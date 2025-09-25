package com.example.hiking_route.service;

import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.model.Grafo;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Serviço para encontrar o caminho máximo em um grafo
 * Utiliza uma variação do algoritmo de Dijkstra com pesos negativos
 */
@Service
public class CaminhoMaximoService {

    /**
     * Encontra o caminho máximo entre dois vértices
     * Usa o algoritmo de Dijkstra modificado (multiplicando pesos por -1)
     */
    public CaminhoResponse encontrarCaminhoMaximo(Grafo grafo, int origem, int destino) {
        int n = grafo.getNumeroVertices();
        
        // Validações básicas
        if (origem < 0 || origem >= n || destino < 0 || destino >= n) {
            return new CaminhoResponse(
                Collections.emptyList(), 
                Double.NEGATIVE_INFINITY, 
                "MAXIMO", 
                false, 
                "Vértices de origem ou destino inválidos"
            );
        }

        // Verificar se há ciclos positivos (que tornariam o caminho máximo infinito)
        if (temCicloPositivo(grafo)) {
            return new CaminhoResponse(
                Collections.emptyList(), 
                Double.POSITIVE_INFINITY, 
                "MAXIMO", 
                false, 
                "Grafo contém ciclo positivo - caminho máximo seria infinito"
            );
        }

        // Arrays para armazenar distâncias e predecessores
        double[] distancias = new double[n];
        int[] predecessores = new int[n];
        boolean[] visitados = new boolean[n];

        // Inicialização (para caminho máximo, começamos com -infinito)
        Arrays.fill(distancias, Double.NEGATIVE_INFINITY);
        Arrays.fill(predecessores, -1);
        distancias[origem] = 0;

        // Fila de prioridade para selecionar o vértice com maior distância
        PriorityQueue<Integer> filaPrioridade = new PriorityQueue<>(
            (v1, v2) -> Double.compare(distancias[v2], distancias[v1]) // Ordem decrescente
        );
        filaPrioridade.offer(origem);

        while (!filaPrioridade.isEmpty()) {
            int u = filaPrioridade.poll();
            
            if (visitados[u]) continue;
            visitados[u] = true;

            // Se chegamos ao destino, podemos parar
            if (u == destino) break;

            // Relaxamento das arestas (para caminho máximo)
            for (int v = 0; v < n; v++) {
                if (!visitados[v] && grafo.existeAresta(u, v)) {
                    double peso = grafo.getPeso(u, v);
                    double novaDistancia = distancias[u] + peso;
                    
                    if (novaDistancia > distancias[v]) {
                        distancias[v] = novaDistancia;
                        predecessores[v] = u;
                        filaPrioridade.offer(v);
                    }
                }
            }
        }

        // Construir o caminho
        if (distancias[destino] == Double.NEGATIVE_INFINITY) {
            return new CaminhoResponse(
                Collections.emptyList(), 
                Double.NEGATIVE_INFINITY, 
                "MAXIMO", 
                false, 
                "Não existe caminho entre os vértices especificados"
            );
        }

        List<Integer> caminho = construirCaminho(predecessores, origem, destino);
        
        return new CaminhoResponse(
            caminho, 
            distancias[destino], 
            "MAXIMO", 
            true, 
            "Caminho máximo encontrado com sucesso"
        );
    }

    /**
     * Verifica se o grafo tem ciclo positivo usando algoritmo de Bellman-Ford modificado
     */
    private boolean temCicloPositivo(Grafo grafo) {
        int n = grafo.getNumeroVertices();
        double[] distancias = new double[n];
        
        // Inicializar distâncias
        Arrays.fill(distancias, Double.NEGATIVE_INFINITY);
        distancias[0] = 0; // Começar do vértice 0
        
        // Relaxar arestas n-1 vezes
        for (int i = 0; i < n - 1; i++) {
            for (int u = 0; u < n; u++) {
                if (distancias[u] != Double.NEGATIVE_INFINITY) {
                    for (int v = 0; v < n; v++) {
                        if (grafo.existeAresta(u, v)) {
                            double peso = grafo.getPeso(u, v);
                            if (distancias[u] + peso > distancias[v]) {
                                distancias[v] = distancias[u] + peso;
                            }
                        }
                    }
                }
            }
        }
        
        // Verificar se ainda é possível relaxar (indica ciclo positivo)
        for (int u = 0; u < n; u++) {
            if (distancias[u] != Double.NEGATIVE_INFINITY) {
                for (int v = 0; v < n; v++) {
                    if (grafo.existeAresta(u, v)) {
                        double peso = grafo.getPeso(u, v);
                        if (distancias[u] + peso > distancias[v]) {
                            return true; // Ciclo positivo encontrado
                        }
                    }
                }
            }
        }
        
        return false;
    }

    /**
     * Constrói o caminho a partir do array de predecessores
     */
    private List<Integer> construirCaminho(int[] predecessores, int origem, int destino) {
        List<Integer> caminho = new ArrayList<>();
        int atual = destino;
        
        while (atual != -1) {
            caminho.add(atual);
            atual = predecessores[atual];
        }
        
        Collections.reverse(caminho);
        return caminho;
    }
}
