package com.example.hiking_route.controller;

import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.dto.GrafoRequest;
import com.example.hiking_route.dto.GrafoResponse;
import com.example.hiking_route.service.GrafoService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de grafo
 */
@RestController
@RequestMapping("/api/grafo")
@CrossOrigin(origins = "*")
public class GrafoController {

    @Autowired
    private GrafoService grafoService;

    /**
     * Endpoint para processar grafo e retornar ambos os caminhos (mínimo e máximo)
     */
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
     * Endpoint específico para caminho mínimo (Dijkstra)
     */
    @PostMapping("/caminho-minimo")
    public ResponseEntity<CaminhoResponse> encontrarCaminhoMinimo(@Valid @RequestBody GrafoRequest request) {
        try {
            CaminhoResponse response = grafoService.processarCaminhoMinimo(request);
            
            if (!response.isCaminhoEncontrado()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            CaminhoResponse errorResponse = new CaminhoResponse(
                null, 0, "MINIMO", false, "Erro interno do servidor: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint específico para caminho máximo
     */
    @PostMapping("/caminho-maximo")
    public ResponseEntity<CaminhoResponse> encontrarCaminhoMaximo(@Valid @RequestBody GrafoRequest request) {
        try {
            CaminhoResponse response = grafoService.processarCaminhoMaximo(request);
            
            if (!response.isCaminhoEncontrado()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            CaminhoResponse errorResponse = new CaminhoResponse(
                null, 0, "MAXIMO", false, "Erro interno do servidor: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Endpoint para verificar se a API está funcionando
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("API de Grafo está funcionando!");
    }

    /**
     * Endpoint para obter informações sobre a API
     */
    @GetMapping("/info")
    public ResponseEntity<Object> getApiInfo() {
        return ResponseEntity.ok(new Object() {
            public final String nome = "API de Algoritmos de Grafo";
            public final String versao = "1.0.0";
            public final String descricao = "API para encontrar caminhos mínimos e máximos em grafos usando algoritmo de Dijkstra";
            public final String[] endpoints = {
                "POST /api/grafo/processar - Processa grafo completo (ambos os caminhos)",
                "POST /api/grafo/caminho-minimo - Encontra apenas o caminho mínimo",
                "POST /api/grafo/caminho-maximo - Encontra apenas o caminho máximo",
                "GET /api/grafo/health - Verifica status da API",
                "GET /api/grafo/info - Informações da API"
            };
            public final Object formatoRequest = new Object() {
                public final String matriz = "double[][] - Matriz de adjacência do grafo";
                public final String origem = "Integer - Vértice de origem (índice baseado em 0)";
                public final String destino = "Integer - Vértice de destino (índice baseado em 0)";
            };
        });
    }
}
