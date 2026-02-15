package com.haripriya.haripriya_backend.service.impl;

import com.haripriya.haripriya_backend.dto.*;
import com.haripriya.haripriya_backend.entity.User;
import com.haripriya.haripriya_backend.enums.AuthProvider;
import com.haripriya.haripriya_backend.enums.Role;
import com.haripriya.haripriya_backend.exception.AuthenticationException;
import com.haripriya.haripriya_backend.exception.ResourceNotFoundException;
import com.haripriya.haripriya_backend.exception.ValidationException;
import com.haripriya.haripriya_backend.repository.UserRepository;
import com.haripriya.haripriya_backend.security.JwtTokenProvider;
import com.haripriya.haripriya_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("Login attempt for email: {}", loginRequest.getEmail());

        // Check if user exists
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", loginRequest.getEmail());
                    return new AuthenticationException("Invalid email or password");
                });

        logger.info("Found user: {}, Role: {}, Active: {}", user.getEmail(), user.getRole(), user.getIsActive());
        logger.info("Stored password hash: {}", user.getPassword());

        // Check password explicitly for debugging
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.error("Password mismatch for user: {}", loginRequest.getEmail());
            throw new AuthenticationException("Invalid email or password");
        }

        // Check if user is active
        if (!user.getIsActive()) {
            logger.error("User account is inactive: {}", loginRequest.getEmail());
            throw new AuthenticationException("User account is inactive");
        }

        // Check if user uses INTERNAL auth provider
        if (user.getAuthProvider() != AuthProvider.INTERNAL) {
            logger.error("Invalid auth provider for user: {}", loginRequest.getEmail());
            throw new AuthenticationException("This account uses a different authentication method");
        }

        // Authenticate
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);

            // Build response
            UserResponse userResponse = mapToUserResponse(user);

            logger.info("User logged in successfully: {}", user.getEmail());
            return new AuthResponse(jwt, userResponse);

        } catch (org.springframework.security.core.AuthenticationException ex) {
            logger.error("Authentication failed for user: {}", loginRequest.getEmail(), ex);
            throw new AuthenticationException("Invalid email or password");
        }
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        logger.info("Registration attempt for email: {}", registerRequest.getEmail());

        // Validate role - only internal users can be registered
        if (registerRequest.getRole() == Role.PATIENT) {
            throw new ValidationException("Patient registration is not allowed through this endpoint");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ValidationException("Email already exists");
        }

        // Check if phone already exists (if provided)
        if (registerRequest.getPhone() != null && !registerRequest.getPhone().isEmpty()) {
            if (userRepository.existsByPhone(registerRequest.getPhone())) {
                throw new ValidationException("Phone number already exists");
            }
        }

        // Create new user
        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .authProvider(AuthProvider.INTERNAL)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);

        logger.info("User registered successfully: {}", savedUser.getEmail());
        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        User user = getUserById(userId);
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .authProvider(user.getAuthProvider())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
