package com.example.hiking_route.benchmark;

import com.example.hiking_route.model.Grafo;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Gerador de grafos para benchmarking.
 * Suporta: grade 2D, grafo aleatório, grafo denso, grafo esparso.
 */
public class GrafoGenerator {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * Gera grafo em grade 2D (N x N) com pesos aleatórios.
     * Vértices organizados row-major: índice = y * N + x
     * Arestas: 4-vizinhança (cima, baixo, esquerda, direita) + opcional diagonais.
     */
    public static BenchmarkRunner.GrafoBenchmark gerarGrade2D(
            int tamanho, double densidade, boolean diagonais, int seed) {

        Random rand = new Random(seed);
        int n = tamanho * tamanho;
        double[][] matriz = new double[n][n];
        int arestas = 0;

        for (int y = 0; y < tamanho; y++) {
            for (int x = 0; x < tamanho; x++) {
                int u = y * tamanho + x;

                // Vizinhos ortogonais
                int[][] vizinhos = {
                    {x - 1, y}, {x + 1, y}, {x, y - 1}, {x, y + 1}
                };

                if (diagonais) {
                    int[][] diags = {
                        {x - 1, y - 1}, {x + 1, y - 1},
                        {x - 1, y + 1}, {x + 1, y + 1}
                    };
                    vizinhos = Arrays.copyOf(vizinhos, vizinhos.length + diags.length);
                    System.arraycopy(diags, 0, vizinhos, 4, diags.length);
                }

                for (int[] v : vizinhos) {
                    int vx = v[0], vy = v[1];
                    if (vx >= 0 && vx < tamanho && vy >= 0 && vy < tamanho) {
                        int vIdx = vy * tamanho + vx;
                        if (rand.nextDouble() < densidade) {
                            double peso = 1 + rand.nextDouble() * 9; // 1-10
                            matriz[u][vIdx] = peso;
                            arestas++;
                        }
                    }
                }
            }
        }

        // Garantir conectividade básica (caminho da borda esquerda para direita)
        for (int y = 0; y < tamanho; y++) {
            int u = y * tamanho;
            int v = y * tamanho + tamanho - 1;
            if (matriz[u][v] == 0 && u != v) {
                // Conectar via caminho horizontal
                for (int x = 0; x < tamanho - 1; x++) {
                    int a = y * tamanho + x;
                    int b = y * tamanho + x + 1;
                    if (matriz[a][b] == 0) {
                        matriz[a][b] = 1 + rand.nextDouble() * 3;
                        arestas++;
                    }
                }
            }
        }

        Grafo grafo = new Grafo(matriz);
        return new BenchmarkRunner.GrafoBenchmark(
            String.format("Grade %dx%d (densidade=%.0f%%)", tamanho, tamanho, densidade * 100),
            grafo,
            0, // origem: canto superior esquerdo
            n - 1, // destino: canto inferior direito
            arestas,
            tamanho,
            tamanho
        );
    }

    /**
     * Gera grafo aleatório (Erdős–Rényi G(n, p)).
     */
    public static BenchmarkRunner.GrafoBenchmark gerarAleatorio(
            int vertices, double probabilidadeAresta, int seed) {

        Random rand = new Random(seed);
        double[][] matriz = new double[vertices][vertices];
        int arestas = 0;

        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                if (i != j && rand.nextDouble() < probabilidadeAresta) {
                    matriz[i][j] = 1 + rand.nextDouble() * 20; // 1-20
                    arestas++;
                }
            }
        }

        // Garantir que origem e destino estão conectados
        int origem = 0;
        int destino = vertices - 1;
        if (!estaConectado(matriz, origem, destino)) {
            // Adicionar caminho direto
            matriz[origem][destino] = 10 + rand.nextDouble() * 10;
            arestas++;
        }

        Grafo grafo = new Grafo(matriz);
        return new BenchmarkRunner.GrafoBenchmark(
            String.format("Aleatório G(%d, %.2f)", vertices, probabilidadeAresta),
            grafo,
            origem,
            destino,
            arestas,
            0, 0
        );
    }

    /**
     * Gera grafo denso (quase completo).
     */
    public static BenchmarkRunner.GrafoBenchmark gerarDenso(int vertices, int seed) {
        return gerarAleatorio(vertices, 0.8, seed);
    }

    /**
     * Gera grafo esparso (árvore + algumas arestas extras).
     */
    public static BenchmarkRunner.GrafoBenchmark gerarEsparso(int vertices, int seed) {
        Random rand = new Random(seed);
        double[][] matriz = new double[vertices][vertices];
        int arestas = 0;

        // Criar árvore geradora (garante conectividade)
        List<Integer> verticesRestantes = new ArrayList<>();
        for (int i = 1; i < vertices; i++) verticesRestantes.add(i);
        Collections.shuffle(verticesRestantes, rand);

        int[] pai = new int[vertices];
        for (int v : verticesRestantes) {
            int p = rand.nextInt(v); // Conecta a um vértice anterior
            double peso = 1 + rand.nextDouble() * 10;
            matriz[p][v] = peso;
            arestas++;
        }

        // Adicionar algumas arestas extras (10% dos vértices)
        int extras = vertices / 10;
        for (int i = 0; i < extras; i++) {
            int u = rand.nextInt(vertices);
            int v = rand.nextInt(vertices);
            if (u != v && matriz[u][v] == 0) {
                matriz[u][v] = 1 + rand.nextDouble() * 20;
                arestas++;
            }
        }

        Grafo grafo = new Grafo(matriz);
        return new BenchmarkRunner.GrafoBenchmark(
            String.format("Esparso (árvore + %d extras)", extras),
            grafo,
            0,
            vertices - 1,
            arestas,
            0, 0
        );
    }

    /**
     * Gera grafo com "gargalo" (dois clusters conectados por uma única aresta).
     */
    public static BenchmarkRunner.GrafoBenchmark gerarComGargalo(int verticesPorCluster, int seed) {
        Random rand = new Random(seed);
        int total = verticesPorCluster * 2;
        double[][] matriz = new double[total][total];
        int arestas = 0;

        // Cluster 1: denso
        for (int i = 0; i < verticesPorCluster; i++) {
            for (int j = 0; j < verticesPorCluster; j++) {
                if (i != j && rand.nextDouble() < 0.5) {
                    matriz[i][j] = 1 + rand.nextDouble() * 5;
                    arestas++;
                }
            }
        }

        // Cluster 2: denso
        for (int i = verticesPorCluster; i < total; i++) {
            for (int j = verticesPorCluster; j < total; j++) {
                if (i != j && rand.nextDouble() < 0.5) {
                    matriz[i][j] = 1 + rand.nextDouble() * 5;
                    arestas++;
                }
            }
        }

        // Gargalo: única aresta entre clusters
        int saidaCluster1 = rand.nextInt(verticesPorCluster);
        int entradaCluster2 = verticesPorCluster + rand.nextInt(verticesPorCluster);
        matriz[saidaCluster1][entradaCluster2] = 1;
        arestas++;

        Grafo grafo = new Grafo(matriz);
        return new BenchmarkRunner.GrafoBenchmark(
            String.format("Gargalo (2 clusters de %d)", verticesPorCluster),
            grafo,
            0,
            total - 1,
            arestas,
            0, 0
        );
    }

    /**
     * Gera suite padrão de grafos para benchmark.
     */
    public static List<BenchmarkRunner.GrafoBenchmark> gerarSuitePadrao() {
        List<BenchmarkRunner.GrafoBenchmark> suite = new ArrayList<>();

        // Grades 2D (ideais para A*)
        suite.add(gerarGrade2D(10, 0.7, false, 42));    // 100 vértices
        suite.add(gerarGrade2D(20, 0.7, false, 42));    // 400 vértices
        suite.add(gerarGrade2D(30, 0.7, false, 42));    // 900 vértices
        suite.add(gerarGrade2D(50, 0.5, false, 42));    // 2500 vértices

        // Grafos aleatórios
        suite.add(gerarAleatorio(100, 0.1, 42));        // Esparso
        suite.add(gerarAleatorio(200, 0.1, 42));
        suite.add(gerarAleatorio(500, 0.05, 42));

        // Grafos densos
        suite.add(gerarDenso(100, 42));
        suite.add(gerarDenso(200, 42));

        // Grafos esparsos (árvore-like)
        suite.add(gerarEsparso(500, 42));
        suite.add(gerarEsparso(1000, 42));

        // Grafo com gargalo
        suite.add(gerarComGargalo(100, 42));  // 200 vértices
        suite.add(gerarComGargalo(250, 42));  // 500 vértices

        return suite;
    }

    /**
     * Verifica se dois vértices estão conectados (BFS simples).
     */
    private static boolean estaConectado(double[][] matriz, int origem, int destino) {
        int n = matriz.length;
        boolean[] visitado = new boolean[n];
        Queue<Integer> fila = new LinkedList<>();
        fila.offer(origem);
        visitado[origem] = true;

        while (!fila.isEmpty()) {
            int u = fila.poll();
            if (u == destino) return true;
            for (int v = 0; v < n; v++) {
                if (!visitado[v] && matriz[u][v] > 0) {
                    visitado[v] = true;
                    fila.offer(v);
                }
            }
        }
        return false;
    }
}