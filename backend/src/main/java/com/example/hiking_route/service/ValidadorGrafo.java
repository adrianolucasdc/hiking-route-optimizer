package com.example.hiking_route.service;

import com.example.hiking_route.exception.MatrizInvalidaException;
import com.example.hiking_route.exception.VerticeInvalidoException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorGrafo {

    public void validarMatriz(double[][] matriz) {
        if (matriz == null) {
            throw new MatrizInvalidaException("Matriz não pode ser nula");
        }
        
        if (matriz.length == 0) {
            throw new MatrizInvalidaException("Matriz deve ter pelo menos uma linha");
        }
        
        int n = matriz.length;
        
        for (int i = 0; i < n; i++) {
            if (matriz[i] == null) {
                throw new MatrizInvalidaException("Linha " + i + " da matriz é nula");
            }
            
            if (matriz[i].length != n) {
                throw new MatrizInvalidaException(
                    "Matriz deve ser quadrada. Linha " + i + " tem " + matriz[i].length + 
                    " colunas, mas esperado " + n
                );
            }
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double valor = matriz[i][j];
                
                if (Double.isNaN(valor)) {
                    throw new MatrizInvalidaException(
                        "Valor NaN encontrado na posição [" + i + "][" + j + "]"
                    );
                }
                
                if (valor < 0) {
                    throw new MatrizInvalidaException(
                        "Valores negativos não são permitidos para algoritmo de Dijkstra. " +
                        "Valor " + valor + " encontrado na posição [" + i + "][" + j + "]"
                    );
                }
            }
        }
    }

    public void validarVertices(int origem, int destino, int numeroVertices) {
        if (origem < 0 || origem >= numeroVertices) {
            throw new VerticeInvalidoException(
                "Vértice de origem " + origem + " é inválido. " +
                "Deve estar entre 0 e " + (numeroVertices - 1)
            );
        }
        
        if (destino < 0 || destino >= numeroVertices) {
            throw new VerticeInvalidoException(
                "Vértice de destino " + destino + " é inválido. " +
                "Deve estar entre 0 e " + (numeroVertices - 1)
            );
        }
    }

    public void validarConectividade(double[][] matriz, int origem, int destino) {
        int n = matriz.length;
        boolean[][] alcancavel = new boolean[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                alcancavel[i][j] = (i == j) || (matriz[i][j] > 0);
            }
        }
        
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    alcancavel[i][j] = alcancavel[i][j] || (alcancavel[i][k] && alcancavel[k][j]);
                }
            }
        }
        
        if (!alcancavel[origem][destino]) {
            throw new VerticeInvalidoException(
                "Não existe caminho do vértice " + origem + " para o vértice " + destino
            );
        }
    }


    public void validarCompleto(double[][] matriz, int origem, int destino) {
        validarMatriz(matriz);
        validarVertices(origem, destino, matriz.length);
    }
}
