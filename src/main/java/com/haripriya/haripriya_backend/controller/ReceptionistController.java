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
@RequestMapping("/api/receptionist")
@PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
public class ReceptionistController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, String>>> getReceptionistDashboard() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "Welcome to Receptionist Dashboard");
        data.put("access", "Appointments and billing");

        return ResponseEntity.ok(
                ApiResponse.success("Receptionist dashboard data retrieved", data));
    }
}
