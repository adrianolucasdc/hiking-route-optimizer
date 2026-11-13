package com.example.hiking_route.algorithm;

import com.example.hiking_route.model.Grafo;

import java.util.*;

/**
 * Implementação de BFS (Busca em Largura) para grafos não ponderados.
 * Encontra o caminho com menor número de arestas (saltos).
 * Complexidade: O(V + E)
 */
public class BFSFinder implements PathFinder {

    @Override
    public PathResult encontrarCaminho(Grafo grafo, int origem, int destino) {
        long inicio = System.nanoTime();

        int n = grafo.getNumeroVertices();

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
                .custo(0.0) // BFS não considera pesos
                .tempoExecucaoNs(System.nanoTime() - inicio)
                .nosExpandidos(0)
                .nosVisitados(1)
                .build();
        }

        int[] predecessores = new int[n];
        boolean[] visitados = new boolean[n];
        int[] distancia = new int[n]; // Número de saltos

        Arrays.fill(predecessores, -1);
        Arrays.fill(distancia, -1);

        Queue<Integer> fila = new LinkedList<>();
        fila.offer(origem);
        visitados[origem] = true;
        distancia[origem] = 0;

        int nosExpandidos = 0;
        int nosVisitados = 0;

        while (!fila.isEmpty()) {
            int u = fila.poll();
            nosVisitados++;

            if (u == destino) break;

            nosExpandidos++;

            for (int v = 0; v < n; v++) {
                if (!visitados[v] && grafo.existeAresta(u, v)) {
                    visitados[v] = true;
                    predecessores[v] = u;
                    distancia[v] = distancia[u] + 1;
                    fila.offer(v);
                }
            }
        }

        long fim = System.nanoTime();

        if (!visitados[destino]) {
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

        List<Integer> caminho = reconstruirCaminho(predecessores, origem, destino);

        // BFS retorna número de saltos como "custo" (cada aresta = custo 1)
        double custoSaltos = distancia[destino];

        return new PathResult.Builder()
            .algoritmo(getNome())
            .encontrado(true)
            .caminho(caminho)
            .custo(custoSaltos)
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
        return "BFS (não ponderado)";
    }

    @Override
    public String getComplexidadeTempo() {
        return "O(V + E)";
    }

    @Override
    public String getComplexidadeEspaco() {
        return "O(V)";
    }
}