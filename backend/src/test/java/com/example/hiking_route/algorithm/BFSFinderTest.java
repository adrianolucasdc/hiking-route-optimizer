package com.example.hiking_route.algorithm;

import com.example.hiking_route.model.Grafo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class BFSFinderTest {

    private final BFSFinder finder = new BFSFinder();

    @Nested
    @DisplayName("Casos básicos")
    class BasicTests {

        @Test
        @DisplayName("Deve encontrar caminho com menor número de saltos")
        void shouldFindPathWithFewestHops() {
            // A -> B (peso 100) -> D
            // A -> C (peso 1) -> D
            double[][] matriz = {
                {0, 100, 1, 0},
                {0, 0, 0, 1},
                {0, 0, 0, 1},
                {0, 0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            PathResult result = finder.encontrarCaminho(grafo, 0, 3);

            assertThat(result.isEncontrado()).isTrue();
            // BFS ignora pesos, escolhe menor número de arestas
            // 0->1->3 (2 saltos) vs 0->2->3 (2 saltos) - ambos 2 saltos
            assertThat(result.getCaminho()).hasSize(3);
            assertThat(result.getCusto()).isEqualTo(2.0); // 2 saltos
        }

        @Test
        @DisplayName("Deve preferir caminho direto se existir")
        void shouldPreferDirectPathIfExists() {
            double[][] matriz = {
                {0, 5, 10},
                {0, 0, 3},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            PathResult result = finder.encontrarCaminho(grafo, 0, 2);

            assertThat(result.isEncontrado()).isTrue();
            assertThat(result.getCaminho()).containsExactly(0, 2); // Direto = 1 salto
            assertThat(result.getCusto()).isEqualTo(1.0);
        }

        @Test
        @DisplayName("Deve retornar origem=destino")
        void shouldReturnSameOriginDestination() {
            Grafo grafo = new Grafo(new double[][]{{0, 1}, {0, 0}});
            PathResult result = finder.encontrarCaminho(grafo, 0, 0);

            assertThat(result.isEncontrado()).isTrue();
            assertThat(result.getCaminho()).containsExactly(0);
            assertThat(result.getCusto()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Deve retornar não encontrado para desconexo")
        void shouldReturnNotFoundForDisconnected() {
            Grafo grafo = new Grafo(new double[][]{{0, 0}, {0, 0}});
            PathResult result = finder.encontrarCaminho(grafo, 0, 1);

            assertThat(result.isEncontrado()).isFalse();
        }
    }

    @Nested
    @DisplayName("Comparação com Dijkstra")
    class ComparisonTests {

        @Test
        @DisplayName("BFS encontra menos saltos, Dijkstra encontra menor peso")
        void bfsFindsFewestHopsDijkstraFindsLowestWeight() {
            // Grafo onde caminho com MAIS saltos tem menor peso total
            // 0->1 (100) -> 3 (1) = 101 (2 saltos)
            // 0->2 (1) -> 1 (1) -> 3 (1) = 3 (3 saltos) ← MENOR PESO
            // 0->2 (1) -> 3 (10) = 11 (2 saltos)
            double[][] matriz = {
                {0, 100, 1, 0},
                {0, 0, 0, 1},
                {0, 1, 0, 10},
                {0, 0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            PathResult rBFS = finder.encontrarCaminho(grafo, 0, 3);
            PathResult rDijkstra = new DijkstraFinder().encontrarCaminho(grafo, 0, 3);

            assertThat(rBFS.isEncontrado()).isTrue();
            assertThat(rDijkstra.isEncontrado()).isTrue();

            // BFS: 0->1->3 (2 saltos) ou 0->2->3 (2 saltos) - ambos 2 saltos
            // Dijkstra: 0->2 (1) -> 1 (1) -> 3 (1) = 3 (3 saltos, menor peso)
            assertThat(rBFS.getCusto()).isEqualTo(2.0); // saltos
            assertThat(rDijkstra.getCusto()).isEqualTo(3.0); // peso total
            assertThat(rBFS.getCaminho().size()).isLessThanOrEqualTo(rDijkstra.getCaminho().size());
        }
    }

    @Nested
    @DisplayName("Metadados")
    class MetadataTests {

        @Test
        @DisplayName("Deve retornar nome e complexidades corretas")
        void shouldReturnCorrectMetadata() {
            assertThat(finder.getNome()).isEqualTo("BFS (não ponderado)");
            assertThat(finder.getComplexidadeTempo()).isEqualTo("O(V + E)");
            assertThat(finder.getComplexidadeEspaco()).isEqualTo("O(V)");
        }
    }
}