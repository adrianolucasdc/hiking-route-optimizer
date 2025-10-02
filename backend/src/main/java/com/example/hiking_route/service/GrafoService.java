package com.example.hiking_route.service;

import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.dto.GrafoRequest;
import com.example.hiking_route.dto.GrafoResponse;
import com.example.hiking_route.model.Grafo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrafoService {

    @Autowired
    private DijkstraService dijkstraService;

    @Autowired
    private CaminhoMaximoService caminhoMaximoService;
    
    @Autowired
    private ValidadorGrafo validadorGrafo;


    public GrafoResponse processarGrafo(GrafoRequest request) {
        try {
            validadorGrafo.validarCompleto(request.getMatriz(), request.getOrigem(), request.getDestino());
            
            Grafo grafo = new Grafo(request.getMatriz());

            CaminhoResponse caminhoMinimo = dijkstraService.encontrarCaminhoMinimo(
                grafo, request.getOrigem(), request.getDestino()
            );

            CaminhoResponse caminhoMaximo = caminhoMaximoService.encontrarCaminhoMaximo(
                grafo, request.getOrigem(), request.getDestino()
            );

            String status = "SUCESSO";
            if (!caminhoMinimo.isCaminhoEncontrado() && !caminhoMaximo.isCaminhoEncontrado()) {
                status = "NENHUM_CAMINHO_ENCONTRADO";
            } else if (!caminhoMinimo.isCaminhoEncontrado() || !caminhoMaximo.isCaminhoEncontrado()) {
                status = "CAMINHO_PARCIAL";
            }

            return new GrafoResponse(
                caminhoMinimo,
                caminhoMaximo,
                grafo.getNumeroVertices(),
                status
            );

        } catch (Exception e) {
            return new GrafoResponse(
                new CaminhoResponse(null, 0, "MINIMO", false, "Erro interno: " + e.getMessage()),
                new CaminhoResponse(null, 0, "MAXIMO", false, "Erro interno: " + e.getMessage()),
                0,
                "ERRO"
            );
        }
    }


    public CaminhoResponse processarCaminhoMinimo(GrafoRequest request) {
        try {
            validadorGrafo.validarCompleto(request.getMatriz(), request.getOrigem(), request.getDestino());
            
            Grafo grafo = new Grafo(request.getMatriz());
            return dijkstraService.encontrarCaminhoMinimo(grafo, request.getOrigem(), request.getDestino());
            
        } catch (Exception e) {
            return new CaminhoResponse(null, 0, "MINIMO", false, "Erro interno: " + e.getMessage());
        }
    }

    public CaminhoResponse processarCaminhoMaximo(GrafoRequest request) {
        try {
            validadorGrafo.validarCompleto(request.getMatriz(), request.getOrigem(), request.getDestino());
            
            Grafo grafo = new Grafo(request.getMatriz());
            return caminhoMaximoService.encontrarCaminhoMaximo(grafo, request.getOrigem(), request.getDestino());
            
        } catch (Exception e) {
            return new CaminhoResponse(null, 0, "MAXIMO", false, "Erro interno: " + e.getMessage());
        }
    }
}
