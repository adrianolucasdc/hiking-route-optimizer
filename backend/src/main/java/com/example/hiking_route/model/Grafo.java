package com.example.hiking_route.model;

public class Grafo {

    private double[][] matrizAdjacencia;
    private int numeroVertices;

    public Grafo(double[][] matrizAdjacencia) {
        if (matrizAdjacencia == null) {
            this.matrizAdjacencia = new double[0][0];
            this.numeroVertices = 0;
        } else {
            this.matrizAdjacencia = matrizAdjacencia;
            this.numeroVertices = matrizAdjacencia.length;
        }
    }

    public double[][] getMatrizAdjacencia() {
        // Return defensive copy to prevent external mutation
        if (matrizAdjacencia == null) {
            return new double[0][0];
        }
        double[][] copy = new double[matrizAdjacencia.length][];
        for (int i = 0; i < matrizAdjacencia.length; i++) {
            if (matrizAdjacencia[i] != null) {
                copy[i] = matrizAdjacencia[i].clone();
            } else {
                copy[i] = new double[0];
            }
        }
        return copy;
    }

    public void setMatrizAdjacencia(double[][] matrizAdjacencia) {
        if (matrizAdjacencia == null) {
            this.matrizAdjacencia = new double[0][0];
            this.numeroVertices = 0;
        } else {
            this.matrizAdjacencia = matrizAdjacencia;
            this.numeroVertices = matrizAdjacencia.length;
        }
    }
    
    public int getNumeroVertices() {
        return numeroVertices;
    }
    
    public boolean existeAresta(int origem, int destino) {
        if (origem < 0 || origem >= numeroVertices || destino < 0 || destino >= numeroVertices) {
            return false;
        }
        return matrizAdjacencia[origem][destino] != 0;
    }
    
    public double getPeso(int origem, int destino) {
        if (!existeAresta(origem, destino)) {
            return Double.POSITIVE_INFINITY;
        }
        return matrizAdjacencia[origem][destino];
    }
    
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
