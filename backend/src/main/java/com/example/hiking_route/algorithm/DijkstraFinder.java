package com.example.hiking_route.algorithm;

import com.example.hiking_route.model.Grafo;

import java.util.*;

/**
 * Implementação do algoritmo de Dijkstra com Min-Heap (PriorityQueue).
 * Complexidade: O((V + E) log V) teórico, O(V² log V) com matriz de adjacência densa.
 */
public class DijkstraFinder implements PathFinder {

    @Override
    public PathResult encontrarCaminho(Grafo grafo, int origem, int destino) {
        long inicio = System.nanoTime();

        int n = grafo.getNumeroVertices();

        // Validação
        if (origem < 0 || origem >= n || destino < 0 || destino >= n) {
            return new PathResult.Builder()
                .algoritmo(getNome())
                .encontrado(false)
                .custo(Double.POSITIVE_INFINITY)
                .tempoExecucaoNs(System.nanoTime() - inicio)
                .nosExpandidos(0)
                .nosVisitados(0)
                .mensagem("Vértices inválidos")
                .build();
        }

        if (origem == destino) {
            return new PathResult.Builder()
                .algoritmo(getNome())
                .encontrado(true)
                .caminho(List.of(origem))
                .custo(0.0)
                .tempoExecucaoNs(System.nanoTime() - inicio)
                .nosExpandidos(0)
                .nosVisitados(1)
                .build();
        }

        double[] distancias = new double[n];
        int[] predecessores = new int[n];
        boolean[] visitados = new boolean[n];

        Arrays.fill(distancias, Double.POSITIVE_INFINITY);
        Arrays.fill(predecessores, -1);
        distancias[origem] = 0;

        // Min-Heap: (distância, vértice)
        PriorityQueue<NodeDist> heap = new PriorityQueue<>(Comparator.comparingDouble(NodeDist::distancia));
        heap.offer(new NodeDist(origem, 0.0));

        int nosExpandidos = 0;
        int nosVisitados = 0;

        while (!heap.isEmpty()) {
            NodeDist atual = heap.poll();
            int u = atual.vertice();

            if (visitados[u]) continue;
            visitados[u] = true;
            nosVisitados++;

            if (u == destino) break;

            nosExpandidos++;

            // Relaxar todas as arestas de u
            for (int v = 0; v < n; v++) {
                if (!visitados[v] && grafo.existeAresta(u, v)) {
                    double peso = grafo.getPeso(u, v);
                    double novaDistancia = distancias[u] + peso;

                    if (novaDistancia < distancias[v]) {
                        distancias[v] = novaDistancia;
                        predecessores[v] = u;
                        heap.offer(new NodeDist(v, novaDistancia));
                    }
                }
            }
        }

        long fim = System.nanoTime();

        if (distancias[destino] == Double.POSITIVE_INFINITY) {
            return new PathResult.Builder()
                .algoritmo(getNome())
                .encontrado(false)
                .custo(Double.POSITIVE_INFINITY)
                .tempoExecucaoNs(fim - inicio)
                .nosExpandidos(nosExpandidos)
                .nosVisitados(nosVisitados)
                .mensagem("Destino inalcançável")
                .build();
        }

        // Reconstruir caminho
        List<Integer> caminho = reconstruirCaminho(predecessores, origem, destino);

        return new PathResult.Builder()
            .algoritmo(getNome())
            .encontrado(true)
            .caminho(caminho)
            .custo(distancias[destino])
            .tempoExecucaoNs(fim - inicio)
            .nosExpandidos(nosExpandidos)
            .nosVisitados(nosVisitados)
            .build();
    }

    private List<Integer> reconstruirCaminho(int[] predecessores, int origem, int destino) {
        List<Integer> caminho = new ArrayList<>();
        int atual = destino;

        while (atual != -1) {
            caminho.add(atual);
            atual = predecessores[atual];
        }

        Collections.reverse(caminho);
        return caminho;
    }

    @Override
    public String getNome() {
        return "Dijkstra (Min-Heap)";
    }

    @Override
    public String getComplexidadeTempo() {
        return "O((V + E) log V)";
    }

    @Override
    public String getComplexidadeEspaco() {
        return "O(V)";
    }

    /**
     * Record para armazenar vértice e sua distância no heap.
     */
    private record NodeDist(int vertice, double distancia) {}
}