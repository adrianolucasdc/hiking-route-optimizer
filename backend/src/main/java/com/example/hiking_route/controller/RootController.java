package com.example.hiking_route.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        return ResponseEntity.ok(Map.of(
            "name", "Hiking Route Optimizer API",
            "version", "1.0.0",
            "endpoints", Map.of(
                "POST /api/grafo/processar", "Process graph and find min/max paths",
                "GET /actuator/health", "Health check"
            )
        ));
    }
}