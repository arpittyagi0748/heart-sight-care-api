package com.haripriya.haripriya_backend.service.impl;

import com.haripriya.haripriya_backend.dto.PatientRequestDTO;
import com.haripriya.haripriya_backend.dto.PatientResponseDTO;
import com.haripriya.haripriya_backend.dto.PatientUpdateDTO;
import com.haripriya.haripriya_backend.entity.Patient;
import com.haripriya.haripriya_backend.exception.ResourceNotFoundException;
import com.haripriya.haripriya_backend.exception.ValidationException;
import com.haripriya.haripriya_backend.repository.PatientRepository;
import com.haripriya.haripriya_backend.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private static final Logger logger = LoggerFactory.getLogger(PatientServiceImpl.class);
    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO requestDTO) {
        logger.info("Creating new patient: {}", requestDTO.getFullName());

        // Validate unique phone number
        if (patientRepository.existsByPhoneNumber(requestDTO.getPhoneNumber())) {
            throw new ValidationException("Phone number already exists");
        }

        // Validate unique email if provided
        if (requestDTO.getEmail() != null && !requestDTO.getEmail().isEmpty()) {
            if (patientRepository.existsByEmail(requestDTO.getEmail())) {
                throw new ValidationException("Email already exists");
            }
        }

        // Validate date of birth is not in future
        if (requestDTO.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }

        // Generate unique patient code
        String patientCode = generatePatientCode();

        // Build patient entity
        Patient patient = Patient.builder()
                .patientCode(patientCode)
                .fullName(requestDTO.getFullName())
                .gender(requestDTO.getGender())
                .dateOfBirth(requestDTO.getDateOfBirth())
                .phoneNumber(requestDTO.getPhoneNumber())
                .email(requestDTO.getEmail())
                .address(requestDTO.getAddress())
                .bloodGroup(requestDTO.getBloodGroup())
                .chronicDiseases(requestDTO.getChronicDiseases())
                .allergies(requestDTO.getAllergies())
                .emergencyContactName(requestDTO.getEmergencyContactName())
                .emergencyContactPhone(requestDTO.getEmergencyContactPhone())
                .isActive(true)
                .build();

        Patient savedPatient = patientRepository.save(patient);
        logger.info("Patient created successfully with code: {}", patientCode);

        return mapToResponseDTO(savedPatient);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> getAllPatients(Pageable pageable) {
        logger.info("Fetching all patients with pagination");
        return patientRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> getActivePatients(Boolean isActive, Pageable pageable) {
        logger.info("Fetching patients with active status: {}", isActive);
        return patientRepository.findByIsActive(isActive, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientById(Long id) {
        logger.info("Fetching patient by ID: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return mapToResponseDTO(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponseDTO getPatientByCode(String patientCode) {
        logger.info("Fetching patient by code: {}", patientCode);
        Patient patient = patientRepository.findByPatientCode(patientCode)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with code: " + patientCode));
        return mapToResponseDTO(patient);
    }

    @Override
    @Transactional
    public PatientResponseDTO updatePatient(Long id, PatientUpdateDTO updateDTO) {
        logger.info("Updating patient with ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        // Update only non-null fields
        if (updateDTO.getFullName() != null) {
            patient.setFullName(updateDTO.getFullName());
        }
        if (updateDTO.getGender() != null) {
            patient.setGender(updateDTO.getGender());
        }
        if (updateDTO.getDateOfBirth() != null) {
            if (updateDTO.getDateOfBirth().isAfter(LocalDate.now())) {
                throw new ValidationException("Date of birth cannot be in the future");
            }
            patient.setDateOfBirth(updateDTO.getDateOfBirth());
        }
        if (updateDTO.getPhoneNumber() != null) {
            // Check if phone number is being changed and if new number already exists
            if (!patient.getPhoneNumber().equals(updateDTO.getPhoneNumber())) {
                if (patientRepository.existsByPhoneNumber(updateDTO.getPhoneNumber())) {
                    throw new ValidationException("Phone number already exists");
                }
            }
            patient.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if (updateDTO.getEmail() != null) {
            // Check if email is being changed and if new email already exists
            if (!updateDTO.getEmail().equals(patient.getEmail())) {
                if (patientRepository.existsByEmail(updateDTO.getEmail())) {
                    throw new ValidationException("Email already exists");
                }
            }
            patient.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getAddress() != null) {
            patient.setAddress(updateDTO.getAddress());
        }
        if (updateDTO.getBloodGroup() != null) {
            patient.setBloodGroup(updateDTO.getBloodGroup());
        }
        if (updateDTO.getChronicDiseases() != null) {
            patient.setChronicDiseases(updateDTO.getChronicDiseases());
        }
        if (updateDTO.getAllergies() != null) {
            patient.setAllergies(updateDTO.getAllergies());
        }
        if (updateDTO.getEmergencyContactName() != null) {
            patient.setEmergencyContactName(updateDTO.getEmergencyContactName());
        }
        if (updateDTO.getEmergencyContactPhone() != null) {
            patient.setEmergencyContactPhone(updateDTO.getEmergencyContactPhone());
        }

        Patient updatedPatient = patientRepository.save(patient);
        logger.info("Patient updated successfully: {}", id);

        return mapToResponseDTO(updatedPatient);
    }

    @Override
    @Transactional
    public void deactivatePatient(Long id) {
        logger.info("Deactivating patient with ID: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        patient.setIsActive(false);
        patientRepository.save(patient);

        logger.info("Patient deactivated successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> searchPatients(String searchTerm, Pageable pageable) {
        logger.info("Searching patients with term: {}", searchTerm);
        return patientRepository.searchPatients(searchTerm, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> searchPatients(String searchTerm, Boolean isActive, Pageable pageable) {
        logger.info("Searching patients with term: {} and active status: {}", searchTerm, isActive);
        return patientRepository.searchActivePatients(searchTerm, isActive, pageable)
                .map(this::mapToResponseDTO);
    }

    private String generatePatientCode() {
        // Generate patient code in format: PAT-YYYYMMDD-XXXX
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        String patientCode = "PAT-" + datePart + "-" + randomPart;

        // Ensure uniqueness
        while (patientRepository.findByPatientCode(patientCode).isPresent()) {
            randomPart = String.format("%04d", new Random().nextInt(10000));
            patientCode = "PAT-" + datePart + "-" + randomPart;
        }

        return patientCode;
    }

    private PatientResponseDTO mapToResponseDTO(Patient patient) {
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .patientCode(patient.getPatientCode())
                .fullName(patient.getFullName())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .age(patient.getAge())
                .phoneNumber(patient.getPhoneNumber())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .bloodGroup(patient.getBloodGroup())
                .chronicDiseases(patient.getChronicDiseases())
                .allergies(patient.getAllergies())
                .emergencyContactName(patient.getEmergencyContactName())
                .emergencyContactPhone(patient.getEmergencyContactPhone())
                .isActive(patient.getIsActive())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
