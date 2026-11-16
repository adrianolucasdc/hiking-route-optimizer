package com.example.hiking_route.algorithm;

import com.example.hiking_route.model.Grafo;

import java.util.*;

/**
 * Implementação do algoritmo A* (A-Star) para grafos em grade 2D.
 * Usa heurística de Manhattan ou Euclidiana.
 *
 * NOTA: Requer que o grafo tenha estrutura de grade (vértices organizados em linhas/colunas).
 * Para grafos genéricos, usa heurística nula (equivale a Dijkstra).
 */
public class AStarFinder implements PathFinder {

    private final Heuristica heuristica;
    private final int larguraGrade;
    private final int alturaGrade;
    private final double escala;

    public enum Heuristica {
        MANHATTAN,
        EUCLIDIANA,
        CHEBYSHEV,
        NULA // Para grafos não-grade (equivale a Dijkstra)
    }

    /**
     * Construtor para grafo em grade (ex: matriz NxM convertida em grafo).
     */
    public AStarFinder(int larguraGrade, int alturaGrade, Heuristica heuristica) {
        this(larguraGrade, alturaGrade, heuristica, 1.0);
    }

    /**
     * Construtor com fator de escala da heurística. Use a menor aresta (ajustada
     * ao tipo de movimento) para manter a heurística admissível quando os pesos
     * das arestas não são 1.
     */
    public AStarFinder(int larguraGrade, int alturaGrade, Heuristica heuristica, double escala) {
        this.larguraGrade = larguraGrade;
        this.alturaGrade = alturaGrade;
        this.heuristica = heuristica;
        this.escala = escala;
    }

    /**
     * Construtor para grafo genérico (heurística nula = Dijkstra).
     */
    public AStarFinder() {
        this(0, 0, Heuristica.NULA);
    }

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
                .custo(0.0)
                .tempoExecucaoNs(System.nanoTime() - inicio)
                .nosExpandidos(0)
                .nosVisitados(1)
                .build();
        }

        double[] gScore = new double[n]; // Custo real do início até o nó
        double[] fScore = new double[n]; // gScore + heurística
        int[] predecessores = new int[n];
        boolean[] visitados = new boolean[n];

        Arrays.fill(gScore, Double.POSITIVE_INFINITY);
        Arrays.fill(fScore, Double.POSITIVE_INFINITY);
        Arrays.fill(predecessores, -1);

        gScore[origem] = 0;
        fScore[origem] = heuristica(origem, destino);

        // Min-Heap ordenado por fScore
        PriorityQueue<NodeScore> heap = new PriorityQueue<>(Comparator.comparingDouble(NodeScore::fScore));
        heap.offer(new NodeScore(origem, fScore[origem], gScore[origem]));

        int nosExpandidos = 0;
        int nosVisitados = 0;

        while (!heap.isEmpty()) {
            NodeScore atual = heap.poll();
            int u = atual.vertice();

            if (visitados[u]) continue;
            visitados[u] = true;
            nosVisitados++;

            if (u == destino) break;

            nosExpandidos++;

            // Se o gScore atual é maior que o armazenado, pular (entrada obsoleta no heap)
            if (atual.gScore() > gScore[u]) continue;

            for (int v = 0; v < n; v++) {
                if (!visitados[v] && grafo.existeAresta(u, v)) {
                    double peso = grafo.getPeso(u, v);
                    double tentativeGScore = gScore[u] + peso;

                    if (tentativeGScore < gScore[v]) {
                        predecessores[v] = u;
                        gScore[v] = tentativeGScore;
                        fScore[v] = gScore[v] + heuristica(v, destino);
                        heap.offer(new NodeScore(v, fScore[v], gScore[v]));
                    }
                }
            }
        }

        long fim = System.nanoTime();

        if (gScore[destino] == Double.POSITIVE_INFINITY) {
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

        return new PathResult.Builder()
            .algoritmo(getNome())
            .encontrado(true)
            .caminho(caminho)
            .custo(gScore[destino])
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

    /**
     * Calcula heurística baseada na estrutura de grade.
     */
    private double heuristica(int u, int v) {
        if (heuristica == Heuristica.NULA || larguraGrade == 0) {
            return 0; // Dijkstra
        }

        // Converter índice 1D para coordenadas 2D (row-major)
        int ux = u % larguraGrade;
        int uy = u / larguraGrade;
        int vx = v % larguraGrade;
        int vy = v / larguraGrade;

        return escala * switch (heuristica) {
            case MANHATTAN -> Math.abs(ux - vx) + Math.abs(uy - vy);
            case EUCLIDIANA -> Math.hypot(ux - vx, uy - vy);
            case CHEBYSHEV -> Math.max(Math.abs(ux - vx), Math.abs(uy - vy));
            default -> 0;
        };
    }

    @Override
    public String getNome() {
        if (heuristica == Heuristica.NULA || larguraGrade == 0) {
            return "A* (heurística nula = Dijkstra)";
        }
        return "A* (" + heuristica + ")";
    }

    @Override
    public String getComplexidadeTempo() {
        return "O((V + E) log V) * - depende da heurística";
    }

    @Override
    public String getComplexidadeEspaco() {
        return "O(V)";
    }

    private record NodeScore(int vertice, double fScore, double gScore) {}
}