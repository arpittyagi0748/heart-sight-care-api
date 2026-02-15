package com.haripriya.haripriya_backend.dto;

import com.haripriya.haripriya_backend.enums.BloodGroup;
import com.haripriya.haripriya_backend.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {

    private Long id;
    private String patientCode;
    private String fullName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String phoneNumber;
    private String email;
    private String address;
    private BloodGroup bloodGroup;
    private String chronicDiseases;
    private String allergies;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
