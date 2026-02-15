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
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAdminDashboard() {
        Map<String, String> data = new HashMap<>();
        data.put("message", "Welcome to Admin Dashboard");
        data.put("access", "Full system access");

        return ResponseEntity.ok(
                ApiResponse.success("Admin dashboard data retrieved", data));
    }
}
