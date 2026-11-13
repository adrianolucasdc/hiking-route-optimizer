package com.example.hiking_route.benchmark;

import com.example.hiking_route.algorithm.*;
import com.example.hiking_route.model.Grafo;

import java.io.IOException;
import java.util.*;

/**
 * Classe principal para executar benchmarks e gerar relatórios.
 *
 * Uso:
 *   java -cp target/classes com.example.hiking_route.benchmark.BenchmarkMain
 */
public class BenchmarkMain {

    public static void main(String[] args) throws IOException {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║      HIKING ROUTE OPTIMIZER - BENCHMARK SUITE               ║");
        System.out.println("║      Dijkstra vs A* vs BFS - Análise de Performance         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // Configurar algoritmos
        BenchmarkRunner runner = new BenchmarkRunner();

        runner.adicionarAlgoritmo(new DijkstraFinder());
        runner.adicionarAlgoritmo(new BFSFinder());
        runner.adicionarAlgoritmo(new AStarFinder()); // heurística nula
        runner.adicionarAlgoritmo(new AStarFinder(10, 10, AStarFinder.Heuristica.MANHATTAN));
        runner.adicionarAlgoritmo(new AStarFinder(20, 20, AStarFinder.Heuristica.MANHATTAN));
        runner.adicionarAlgoritmo(new AStarFinder(30, 30, AStarFinder.Heuristica.MANHATTAN));
        runner.adicionarAlgoritmo(new AStarFinder(50, 50, AStarFinder.Heuristica.MANHATTAN));
        runner.adicionarAlgoritmo(new AStarFinder(10, 10, AStarFinder.Heuristica.EUCLIDIANA));
        runner.adicionarAlgoritmo(new AStarFinder(20, 20, AStarFinder.Heuristica.EUCLIDIANA));
        runner.adicionarAlgoritmo(new AStarFinder(30, 30, AStarFinder.Heuristica.EUCLIDIANA));
        runner.adicionarAlgoritmo(new AStarFinder(50, 50, AStarFinder.Heuristica.EUCLIDIANA));

        // Gerar grafos de teste
        List<BenchmarkRunner.GrafoBenchmark> grafos = GrafoGenerator.gerarSuitePadrao();

        // Executar benchmark
        runner.executarBenchmark(grafos);

        // Mostrar gráfico ASCII
        runner.imprimirGraficoASCII();

        // Exportar resultados
        runner.exportarCSV("benchmark-results.csv");
        runner.exportarMarkdown("benchmark-results.md");

        // Resumo final
        imprimirResumo(runner.getResultados());
    }

    private static void imprimirResumo(List<BenchmarkRunner.BenchmarkResult> resultados) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                         RESUMO                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // Melhor algoritmo por tamanho de grafo
        Map<Integer, BenchmarkRunner.BenchmarkResult> melhorPorTamanho = new TreeMap<>();
        for (BenchmarkRunner.BenchmarkResult r : resultados) {
            if (r.encontrado()) {
                melhorPorTamanho.merge(r.vertices(), r,
                    (a, b) -> a.tempoMedioMs() < b.tempoMedioMs() ? a : b);
            }
        }

        System.out.println("Melhor algoritmo por tamanho de grafo:");
        System.out.println("--------------------------------------");
        for (var entry : melhorPorTamanho.entrySet()) {
            BenchmarkRunner.BenchmarkResult r = entry.getValue();
            System.out.printf("  V=%-5d: %-35s %.2fms%n",
                r.vertices(), r.algoritmo(), r.tempoMedioMs());
        }

        // Comparação Dijkstra vs A* (Manhattan) em grades
        System.out.println("\nComparação Dijkstra vs A* (Manhattan) em Grades:");
        System.out.println("--------------------------------------------------");
        for (BenchmarkRunner.BenchmarkResult r : resultados) {
            if (r.grafo().contains("Grade") &&
                (r.algoritmo().equals("Dijkstra (Min-Heap)") ||
                 r.algoritmo().contains("MANHATTAN"))) {
                System.out.printf("  %s: %-35s %.2fms | expandidos=%.0f%n",
                    r.grafo(), r.algoritmo(), r.tempoMedioMs(), r.nosExpandidosMedio());
            }
        }
    }
}