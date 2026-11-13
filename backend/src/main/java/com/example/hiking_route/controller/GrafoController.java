package com.example.hiking_route.controller;

import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.dto.GrafoRequest;
import com.example.hiking_route.dto.GrafoResponse;
import com.example.hiking_route.service.GrafoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/grafo")
@CrossOrigin(origins = "*")
public class GrafoController {

    @Autowired
    private GrafoService grafoService;

    @PostMapping("/processar")
    public ResponseEntity<GrafoResponse> processarGrafo(@Valid @RequestBody GrafoRequest request) {
        try {
            GrafoResponse response = grafoService.processarGrafo(request);
            
            if ("ERRO".equals(response.getStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            GrafoResponse errorResponse = new GrafoResponse(
                new CaminhoResponse(null, 0, "MINIMO", false, "Erro interno do servidor"),
                new CaminhoResponse(null, 0, "MAXIMO", false, "Erro interno do servidor"),
                0,
                "ERRO"
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Root endpoint - API info
     */
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
