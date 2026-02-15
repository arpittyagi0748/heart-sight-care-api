package com.haripriya.haripriya_backend.repository;

import com.haripriya.haripriya_backend.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByPatientCode(String patientCode);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Page<Patient> findByIsActive(Boolean isActive, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE " +
            "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.patientCode) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Patient> searchPatients(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.isActive = :isActive AND " +
            "(LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.patientCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Patient> searchActivePatients(@Param("searchTerm") String searchTerm,
            @Param("isActive") Boolean isActive,
            Pageable pageable);
}
