package com.example.hiking_route.algorithm;

import com.example.hiking_route.model.Grafo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class DijkstraFinderTest {

    private final DijkstraFinder finder = new DijkstraFinder();

    @Nested
    @DisplayName("Casos básicos")
    class BasicTests {

        @Test
        @DisplayName("Deve encontrar caminho direto")
        void shouldFindDirectPath() {
            double[][] matriz = {{0, 5}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            PathResult result = finder.encontrarCaminho(grafo, 0, 1);

            assertThat(result.isEncontrado()).isTrue();
            assertThat(result.getCaminho()).containsExactly(0, 1);
            assertThat(result.getCusto()).isEqualTo(5.0);
        }

        @Test
        @DisplayName("Deve encontrar caminho multi-salto")
        void shouldFindMultiHopPath() {
            double[][] matriz = {
                {0, 2, 0},
                {0, 0, 3},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            PathResult result = finder.encontrarCaminho(grafo, 0, 2);

            assertThat(result.isEncontrado()).isTrue();
            assertThat(result.getCaminho()).containsExactly(0, 1, 2);
            assertThat(result.getCusto()).isEqualTo(5.0);
        }

        @Test
        @DisplayName("Deve retornar origem=destino com custo 0")
        void shouldReturnZeroCostForSameOriginDestination() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            PathResult result = finder.encontrarCaminho(grafo, 0, 0);

            assertThat(result.isEncontrado()).isTrue();
            assertThat(result.getCaminho()).containsExactly(0);
            assertThat(result.getCusto()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Deve retornar não encontrado para destino inalcançável")
        void shouldReturnNotFoundForUnreachable() {
            double[][] matriz = {{0, 0}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            PathResult result = finder.encontrarCaminho(grafo, 0, 1);

            assertThat(result.isEncontrado()).isFalse();
            assertThat(result.getCusto()).isEqualTo(Double.POSITIVE_INFINITY);
        }
    }

    @Nested
    @DisplayName("Validação de vértices")
    class VertexValidationTests {

        @Test
        @DisplayName("Deve falhar para origem negativa")
        void shouldFailForNegativeOrigin() {
            Grafo grafo = new Grafo(new double[][]{{0, 1}, {0, 0}});
            PathResult result = finder.encontrarCaminho(grafo, -1, 1);
            assertThat(result.isEncontrado()).isFalse();
        }

        @Test
        @DisplayName("Deve falhar para origem fora dos limites")
        void shouldFailForOriginOutOfBounds() {
            Grafo grafo = new Grafo(new double[][]{{0, 1}, {0, 0}});
            PathResult result = finder.encontrarCaminho(grafo, 5, 1);
            assertThat(result.isEncontrado()).isFalse();
        }
    }

    @Nested
    @DisplayName("Metadados do algoritmo")
    class MetadataTests {

        @Test
        @DisplayName("Deve retornar nome correto")
        void shouldReturnCorrectName() {
            assertThat(finder.getNome()).isEqualTo("Dijkstra (Min-Heap)");
        }

        @Test
        @DisplayName("Deve retornar complexidades corretas")
        void shouldReturnCorrectComplexities() {
            assertThat(finder.getComplexidadeTempo()).isEqualTo("O((V + E) log V)");
            assertThat(finder.getComplexidadeEspaco()).isEqualTo("O(V)");
        }
    }
}