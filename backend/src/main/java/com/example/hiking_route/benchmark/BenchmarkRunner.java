package com.example.hiking_route.benchmark;

import com.example.hiking_route.algorithm.*;
import com.example.hiking_route.model.Grafo;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Suite de benchmark para comparar algoritmos de busca de caminho.
 * Executa múltiplas iterações e gera relatórios CSV/Markdown.
 */
public class BenchmarkRunner {

    private static final int WARMUP_ITERATIONS = 3;
    private static final int MEASUREMENT_ITERATIONS = 10;

    private final List<PathFinder> algoritmos;
    private final List<BenchmarkResult> resultados;

    public BenchmarkRunner() {
        this.algoritmos = new ArrayList<>();
        this.resultados = new ArrayList<>();
    }

    public void adicionarAlgoritmo(PathFinder finder) {
        algoritmos.add(finder);
    }

    /**
     * Executa benchmark em uma série de grafos de tamanhos variados.
     */
    public void executarBenchmark(List<GrafoBenchmark> grafos) {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║           BENCHMARK: COMPARAÇÃO DE ALGORITMOS               ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        for (GrafoBenchmark gb : grafos) {
            System.out.printf("▶ Testando: %s (%d vértices, %d arestas)%n",
                gb.nome(), gb.grafo().getNumeroVertices(), gb.numArestas());

            for (PathFinder finder : algoritmos) {
                // Pular A* com heurística se não for grade
                if (finder instanceof AStarFinder astar &&
                    astar.getClass().getSimpleName().contains("heurística nula") == false &&
                    gb.larguraGrade() <= 0) {
                    continue;
                }

                BenchmarkResult resultado = executarAlgoritmo(finder, gb);
                resultados.add(resultado);

                System.out.printf("   %s: %.2fms | expandidos=%d | visitados=%d | %s%n",
                    finder.getNome(),
                    resultado.tempoMedioMs(),
                    resultado.nosExpandidosMedio(),
                    resultado.nosVisitadosMedio(),
                    resultado.encontrado() ? "✓" : "✗");
            }
            System.out.println();
        }
    }

    private BenchmarkResult executarAlgoritmo(PathFinder finder, GrafoBenchmark gb) {
        List<Long> tempos = new ArrayList<>();
        List<Integer> expandidos = new ArrayList<>();
        List<Integer> visitados = new ArrayList<>();
        boolean encontrado = false;
        double custoFinal = 0;
        List<Integer> caminhoFinal = null;

        // Warmup
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            finder.encontrarCaminho(gb.grafo(), gb.origem(), gb.destino());
        }

        // Medições
        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            PathResult resultado = finder.encontrarCaminho(gb.grafo(), gb.origem(), gb.destino());
            tempos.add(resultado.getTempoExecucaoNs());
            expandidos.add(resultado.getNosExpandidos());
            visitados.add(resultado.getNosVisitados());

            if (i == 0) { // Guardar detalhes da primeira execução
                encontrado = resultado.isEncontrado();
                custoFinal = resultado.getCusto();
                caminhoFinal = resultado.getCaminho();
            }
        }

        return new BenchmarkResult(
            gb.nome(),
            finder.getNome(),
            gb.grafo().getNumeroVertices(),
            gb.numArestas(),
            estatisticas(tempos),
            media(expandidos),
            media(visitados),
            encontrado,
            custoFinal,
            caminhoFinal
        );
    }

    private double media(List<? extends Number> lista) {
        return lista.stream().mapToDouble(Number::doubleValue).average().orElse(0);
    }

    private EstatisticasTempo estatisticas(List<Long> temposNs) {
        long[] arr = temposNs.stream().mapToLong(Long::longValue).sorted().toArray();
        return new EstatisticasTempo(
            arr[0] / 1_000_000.0,           // min ms
            arr[arr.length - 1] / 1_000_000.0, // max ms
            arr[arr.length / 2] / 1_000_000.0, // median ms
            Arrays.stream(arr).average().orElse(0) / 1_000_000.0, // mean ms
            desvioPadrao(arr) / 1_000_000.0  // std dev ms
        );
    }

    private double desvioPadrao(long[] arr) {
        double media = Arrays.stream(arr).average().orElse(0);
        double soma = Arrays.stream(arr).mapToDouble(x -> (x - media) * (x - media)).sum();
        return Math.sqrt(soma / arr.length);
    }

    /**
     * Exporta resultados para CSV.
     */
    public void exportarCSV(String arquivo) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
            pw.println("grafo,algoritmo,vertices,arestas,tempo_min_ms,tempo_max_ms,tempo_mediano_ms,tempo_media_ms,tempo_desvio_ms,nos_expandidos_medio,nos_visitados_medio,encontrado,custo");
            for (BenchmarkResult r : resultados) {
                pw.printf("%s,%s,%d,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.0f,%.0f,%b,%.2f%n",
                    r.grafo(), r.algoritmo(), r.vertices(), r.arestas(),
                    r.tempoMinMs(), r.tempoMaxMs(), r.tempoMedianoMs(), r.tempoMedioMs(), r.tempoDesvioMs(),
                    r.nosExpandidosMedio(), r.nosVisitadosMedio(),
                    r.encontrado(), r.custo());
            }
        }
        System.out.println("📊 Resultados exportados para: " + arquivo);
    }

    /**
     * Exporta resultados para Markdown (para README).
     */
    public void exportarMarkdown(String arquivo) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(arquivo))) {
            pw.println("# Resultados do Benchmark\n");
            pw.println("| Grafo | Algoritmo | Vértices | Arestas | Tempo Médio (ms) | Nós Expandidos | Nós Visitados | Encontrado | Custo |");
            pw.println("|-------|-----------|----------|---------|------------------|----------------|---------------|------------|-------|");

            for (BenchmarkResult r : resultados) {
                pw.printf("| %s | %s | %d | %d | %.2f | %.0f | %.0f | %s | %.2f |%n",
                    r.grafo(), r.algoritmo(), r.vertices(), r.arestas(),
                    r.tempoMedioMs(), r.nosExpandidosMedio(), r.nosVisitadosMedio(),
                    r.encontrado() ? "✓" : "✗", r.custo());
            }

            pw.println("\n---\n");
            pw.println("## Análise por Tamanho de Grafo\n");

            // Agrupar por tamanho
            Map<Integer, List<BenchmarkResult>> porTamanho = new TreeMap<>();
            for (BenchmarkResult r : resultados) {
                porTamanho.computeIfAbsent(r.vertices(), k -> new ArrayList<>()).add(r);
            }

            for (var entry : porTamanho.entrySet()) {
                pw.printf("### Grafo %dx%d (%d vértices)\n\n",
                    (int)Math.sqrt(entry.getKey()), (int)Math.sqrt(entry.getKey()), entry.getKey());
                pw.println("| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |");
                pw.println("|-----------|------------------|----------------|---------------|");

                for (BenchmarkResult r : entry.getValue()) {
                    pw.printf("| %s | %.2f | %d | %d |%n",
                        r.algoritmo(), r.tempoMedioMs(), r.nosExpandidosMedio(), r.nosVisitadosMedio());
                }
                pw.println();
            }
        }
        System.out.println("📝 Markdown exportado para: " + arquivo);
    }

    /**
     * Gera gráfico ASCII simples no terminal.
     */
    public void imprimirGraficoASCII() {
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              GRÁFICO DE TEMPO vs TAMANHO                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        // Agrupar por algoritmo
        Map<String, List<BenchmarkResult>> porAlgoritmo = new LinkedHashMap<>();
        for (BenchmarkResult r : resultados) {
            porAlgoritmo.computeIfAbsent(r.algoritmo(), k -> new ArrayList<>()).add(r);
        }

        // Encontrar tempo máximo para escala
        double maxTempo = resultados.stream()
            .mapToDouble(BenchmarkResult::tempoMedioMs)
            .max().orElse(1.0);

        int larguraGrafico = 50;

        for (var entry : porAlgoritmo.entrySet()) {
            String algo = entry.getKey();
            List<BenchmarkResult> dados = entry.getValue();
            dados.sort(Comparator.comparingInt(BenchmarkResult::vertices));

            System.out.printf("%-25s ", algo);

            for (BenchmarkResult r : dados) {
                int barras = (int) ((r.tempoMedioMs() / maxTempo) * larguraGrafico);
                String bar = "█".repeat(Math.max(1, barras));
                System.out.printf("%s %.2fms (V=%d)%n", bar, r.tempoMedioMs(), r.vertices());
                System.out.printf("%25s ", "");
            }
            System.out.println();
        }
    }

    public List<BenchmarkResult> getResultados() {
        return resultados;
    }

    /**
     * Record para estatísticas de tempo.
     */
    public record EstatisticasTempo(
        double minMs, double maxMs, double medianoMs, double mediaMs, double desvioMs
    ) {}

    /**
     * Record para resultado de benchmark.
     */
    public record BenchmarkResult(
        String grafo,
        String algoritmo,
        int vertices,
        int arestas,
        EstatisticasTempo tempo,
        double nosExpandidosMedio,
        double nosVisitadosMedio,
        boolean encontrado,
        double custo,
        List<Integer> caminho
    ) {
        public double tempoMedioMs() { return tempo.mediaMs(); }
        public double tempoMinMs() { return tempo.minMs(); }
        public double tempoMaxMs() { return tempo.maxMs(); }
        public double tempoMedianoMs() { return tempo.medianoMs(); }
        public double tempoDesvioMs() { return tempo.desvioMs(); }
    }

    /**
     * Classe auxiliar para grafo de benchmark com metadados.
     */
    public record GrafoBenchmark(
        String nome,
        Grafo grafo,
        int origem,
        int destino,
        int numArestas,
        int larguraGrade,
        int alturaGrade
    ) {}
}