package com.haripriya.haripriya_backend.service;

import com.haripriya.haripriya_backend.dto.*;
import com.haripriya.haripriya_backend.entity.User;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    UserResponse register(RegisterRequest registerRequest);

    UserResponse getCurrentUser(Long userId);

    User getUserById(Long userId);
}
