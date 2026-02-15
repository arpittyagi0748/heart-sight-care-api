package com.haripriya.haripriya_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public ResponseEntity<String> root() {
        return ResponseEntity.ok("Haripriya Backend Running");
    }

    @GetMapping({ "/health", "/healthz" })
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("UP");
    }
}
