package com.example.hiking_route.model;

/**
 * Modelo para representar um grafo através de matriz de adjacência
 */
public class Grafo {
    
    private double[][] matrizAdjacencia;
    private int numeroVertices;
    
    public Grafo(double[][] matrizAdjacencia) {
        this.matrizAdjacencia = matrizAdjacencia;
        this.numeroVertices = matrizAdjacencia.length;
    }
    
    public double[][] getMatrizAdjacencia() {
        return matrizAdjacencia;
    }
    
    public void setMatrizAdjacencia(double[][] matrizAdjacencia) {
        this.matrizAdjacencia = matrizAdjacencia;
        this.numeroVertices = matrizAdjacencia.length;
    }
    
    public int getNumeroVertices() {
        return numeroVertices;
    }
    
    /**
     * Verifica se existe uma aresta entre dois vértices
     */
    public boolean existeAresta(int origem, int destino) {
        if (origem < 0 || origem >= numeroVertices || destino < 0 || destino >= numeroVertices) {
            return false;
        }
        return matrizAdjacencia[origem][destino] != 0;
    }
    
    /**
     * Obtém o peso da aresta entre dois vértices
     */
    public double getPeso(int origem, int destino) {
        if (!existeAresta(origem, destino)) {
            return Double.POSITIVE_INFINITY;
        }
        return matrizAdjacencia[origem][destino];
    }
    
    /**
     * Valida se o grafo é válido (matriz quadrada e não nula)
     */
    public boolean isValido() {
        if (matrizAdjacencia == null || numeroVertices == 0) {
            return false;
        }
        
        for (int i = 0; i < numeroVertices; i++) {
            if (matrizAdjacencia[i] == null || matrizAdjacencia[i].length != numeroVertices) {
                return false;
            }
        }
        
        return true;
    }
}
