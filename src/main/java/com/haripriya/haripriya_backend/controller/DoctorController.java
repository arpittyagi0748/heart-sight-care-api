package com.haripriya.haripriya_backend.controller;

import com.haripriya.haripriya_backend.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
public class DoctorController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, String>>> getDoctorDashboard() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "Welcome to Doctor Dashboard");
        data.put("access", "Patient records and prescriptions");

        return ResponseEntity.ok(
                ApiResponse.success("Doctor dashboard data retrieved", data));
    }
}
