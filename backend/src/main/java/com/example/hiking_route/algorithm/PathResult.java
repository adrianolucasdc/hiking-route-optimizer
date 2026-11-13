package com.example.hiking_route.algorithm;

import java.util.List;

/**
 * Resultado da execução de um algoritmo de busca de caminho.
 */
public class PathResult {

    private final List<Integer> caminho;
    private final double custo;
    private final boolean encontrado;
    private final long tempoExecucaoNs;
    private final int nosExpandidos;
    private final int nosVisitados;
    private final String algoritmo;
    private final String mensagem;

    private PathResult(Builder builder) {
        this.caminho = builder.caminho;
        this.custo = builder.custo;
        this.encontrado = builder.encontrado;
        this.tempoExecucaoNs = builder.tempoExecucaoNs;
        this.nosExpandidos = builder.nosExpandidos;
        this.nosVisitados = builder.nosVisitados;
        this.algoritmo = builder.algoritmo;
        this.mensagem = builder.mensagem;
    }

    public List<Integer> getCaminho() { return caminho; }
    public double getCusto() { return custo; }
    public boolean isEncontrado() { return encontrado; }
    public long getTempoExecucaoNs() { return tempoExecucaoNs; }
    public double getTempoExecucaoMs() { return tempoExecucaoNs / 1_000_000.0; }
    public int getNosExpandidos() { return nosExpandidos; }
    public int getNosVisitados() { return nosVisitados; }
    public String getAlgoritmo() { return algoritmo; }
    public String getMensagem() { return mensagem; }

    @Override
    public String toString() {
        return String.format("%s: %s (custo=%.2f, tempo=%.2fms, expandidos=%d, visitados=%d)",
            algoritmo, encontrado ? "ENCONTRADO" : "NÃO ENCONTRADO", custo, getTempoExecucaoMs(), nosExpandidos, nosVisitados);
    }

    public static class Builder {
        private List<Integer> caminho;
        private double custo;
        private boolean encontrado;
        private long tempoExecucaoNs;
        private int nosExpandidos;
        private int nosVisitados;
        private String algoritmo;
        private String mensagem;

        public Builder caminho(List<Integer> caminho) { this.caminho = caminho; return this; }
        public Builder custo(double custo) { this.custo = custo; return this; }
        public Builder encontrado(boolean encontrado) { this.encontrado = encontrado; return this; }
        public Builder tempoExecucaoNs(long tempoExecucaoNs) { this.tempoExecucaoNs = tempoExecucaoNs; return this; }
        public Builder nosExpandidos(int nosExpandidos) { this.nosExpandidos = nosExpandidos; return this; }
        public Builder nosVisitados(int nosVisitados) { this.nosVisitados = nosVisitados; return this; }
        public Builder algoritmo(String algoritmo) { this.algoritmo = algoritmo; return this; }
        public Builder mensagem(String mensagem) { this.mensagem = mensagem; return this; }

        public PathResult build() {
            return new PathResult(this);
        }
    }
}