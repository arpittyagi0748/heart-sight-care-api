package com.haripriya.haripriya_backend.service;

import com.haripriya.haripriya_backend.dto.PatientRequestDTO;
import com.haripriya.haripriya_backend.dto.PatientResponseDTO;
import com.haripriya.haripriya_backend.dto.PatientUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {

    PatientResponseDTO createPatient(PatientRequestDTO requestDTO);

    Page<PatientResponseDTO> getAllPatients(Pageable pageable);

    Page<PatientResponseDTO> getActivePatients(Boolean isActive, Pageable pageable);

    PatientResponseDTO getPatientById(Long id);

    PatientResponseDTO getPatientByCode(String patientCode);

    PatientResponseDTO updatePatient(Long id, PatientUpdateDTO updateDTO);

    void deactivatePatient(Long id);

    Page<PatientResponseDTO> searchPatients(String searchTerm, Pageable pageable);

    Page<PatientResponseDTO> searchPatients(String searchTerm, Boolean isActive, Pageable pageable);
}
