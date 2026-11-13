package com.example.hiking_route.algorithm;

import com.example.hiking_route.model.Grafo;
import com.example.hiking_route.util.GrafoVisualizer;

import java.util.*;

/**
 * Interface comum para algoritmos de busca de caminho.
 */
public interface PathFinder {

    /**
     * Encontra o caminho entre origem e destino.
     *
     * @param grafo   Grafo de busca
     * @param origem  Vértice de origem
     * @param destino Vértice de destino
     * @return Resultado da busca
     */
    PathResult encontrarCaminho(Grafo grafo, int origem, int destino);

    /**
     * @return Nome do algoritmo para exibição
     */
    String getNome();

    /**
     * @return Complexidade de tempo teórica
     */
    String getComplexidadeTempo();

    /**
     * @return Complexidade de espaço teórica
     */
    String getComplexidadeEspaco();
}