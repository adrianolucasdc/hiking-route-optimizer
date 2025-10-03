package com.example.hiking_route.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class GrafoRequest {
    
    @NotNull(message = "A matriz não pode ser nula")
    @Size(min = 1, message = "A matriz deve ter pelo menos uma linha")
    private double[][] matriz;
    
    @NotNull(message = "O vértice de origem é obrigatório")
    private Integer origem;
    
    @NotNull(message = "O vértice de destino é obrigatório")
    private Integer destino;

    public GrafoRequest() {}

    public GrafoRequest(double[][] matriz, Integer origem, Integer destino) {
        this.matriz = matriz;
        this.origem = origem;
        this.destino = destino;
    }

    public double[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(double[][] matriz) {
        this.matriz = matriz;
    }

    public Integer getOrigem() {
        return origem;
    }

    public void setOrigem(Integer origem) {
        this.origem = origem;
    }

    public Integer getDestino() {
        return destino;
    }

    public void setDestino(Integer destino) {
        this.destino = destino;
    }
}
