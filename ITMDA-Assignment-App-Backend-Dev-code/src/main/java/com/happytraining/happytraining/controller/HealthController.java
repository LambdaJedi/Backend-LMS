package com.happytraining.happytraining.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of(
                "status", "UP",
                "message", "Backend is running",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
    }

    @GetMapping("/public/health")
    public Map<String, String> publicHealthCheck() {
        return Map.of(
                "status", "UP",
                "message", "Public health endpoint - no auth required",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
    }
}
