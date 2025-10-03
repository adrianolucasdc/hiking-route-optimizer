package com.example.hiking_route.dto;

import java.util.List;

public class CaminhoResponse {
    
    private List<Integer> caminho;
    private double custo;
    private String tipo; 
    private boolean caminhoEncontrado;
    private String mensagem;

    public CaminhoResponse() {}

    public CaminhoResponse(List<Integer> caminho, double custo, String tipo, boolean caminhoEncontrado, String mensagem) {
        this.caminho = caminho;
        this.custo = custo;
        this.tipo = tipo;
        this.caminhoEncontrado = caminhoEncontrado;
        this.mensagem = mensagem;
    }

    public List<Integer> getCaminho() {
        return caminho;
    }

    public void setCaminho(List<Integer> caminho) {
        this.caminho = caminho;
    }

    public double getCusto() {
        return custo;
    }

    public void setCusto(double custo) {
        this.custo = custo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isCaminhoEncontrado() {
        return caminhoEncontrado;
    }

    public void setCaminhoEncontrado(boolean caminhoEncontrado) {
        this.caminhoEncontrado = caminhoEncontrado;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
