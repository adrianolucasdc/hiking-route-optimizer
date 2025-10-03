package com.example.hiking_route.dto;

public class GrafoResponse {
    
    private CaminhoResponse caminhoMinimo;
    private CaminhoResponse caminhoMaximo;
    private int numeroVertices;
    private String status;

    public GrafoResponse() {}

    public GrafoResponse(CaminhoResponse caminhoMinimo, CaminhoResponse caminhoMaximo, int numeroVertices, String status) {
        this.caminhoMinimo = caminhoMinimo;
        this.caminhoMaximo = caminhoMaximo;
        this.numeroVertices = numeroVertices;
        this.status = status;
    }

    public CaminhoResponse getCaminhoMinimo() {
        return caminhoMinimo;
    }

    public void setCaminhoMinimo(CaminhoResponse caminhoMinimo) {
        this.caminhoMinimo = caminhoMinimo;
    }

    public CaminhoResponse getCaminhoMaximo() {
        return caminhoMaximo;
    }

    public void setCaminhoMaximo(CaminhoResponse caminhoMaximo) {
        this.caminhoMaximo = caminhoMaximo;
    }

    public int getNumeroVertices() {
        return numeroVertices;
    }

    public void setNumeroVertices(int numeroVertices) {
        this.numeroVertices = numeroVertices;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
