package com.haripriya.haripriya_backend.controller;

import com.haripriya.haripriya_backend.dto.ApiResponse;
import com.haripriya.haripriya_backend.dto.PatientRequestDTO;
import com.haripriya.haripriya_backend.dto.PatientResponseDTO;
import com.haripriya.haripriya_backend.dto.PatientUpdateDTO;
import com.haripriya.haripriya_backend.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> createPatient(
            @Valid @RequestBody PatientRequestDTO requestDTO) {
        PatientResponseDTO response = patientService.createPatient(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Patient created successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<PatientResponseDTO>>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) Boolean isActive) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<PatientResponseDTO> patients;
        if (isActive != null) {
            patients = patientService.getActivePatients(isActive, pageable);
        } else {
            patients = patientService.getAllPatients(pageable);
        }

        return ResponseEntity.ok(
                ApiResponse.success("Patients retrieved successfully", patients));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> getPatientById(@PathVariable Long id) {
        PatientResponseDTO response = patientService.getPatientById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Patient retrieved successfully", response));
    }

    @GetMapping("/code/{patientCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> getPatientByCode(@PathVariable String patientCode) {
        PatientResponseDTO response = patientService.getPatientByCode(patientCode);
        return ResponseEntity.ok(
                ApiResponse.success("Patient retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientUpdateDTO updateDTO) {
        PatientResponseDTO response = patientService.updatePatient(id, updateDTO);
        return ResponseEntity.ok(
                ApiResponse.success("Patient updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivatePatient(@PathVariable Long id) {
        patientService.deactivatePatient(id);
        return ResponseEntity.ok(
                ApiResponse.success("Patient deactivated successfully", null));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<PatientResponseDTO>>> searchPatients(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(required = false) Boolean isActive) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<PatientResponseDTO> patients;
        if (isActive != null) {
            patients = patientService.searchPatients(query, isActive, pageable);
        } else {
            patients = patientService.searchPatients(query, pageable);
        }

        return ResponseEntity.ok(
                ApiResponse.success("Search results retrieved successfully", patients));
    }
}
