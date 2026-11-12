package com.example.hiking_route.service;

import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.model.Grafo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class DijkstraServiceTest {

    private DijkstraService dijkstraService;

    @BeforeEach
    void setUp() {
        dijkstraService = new DijkstraService();
    }

    @Nested
    @DisplayName("Casos básicos de caminho mínimo")
    class BasicShortestPathTests {

        @Test
        @DisplayName("Deve encontrar caminho direto entre dois vértices adjacentes")
        void shouldFindDirectPathBetweenAdjacentVertices() {
            double[][] matriz = {
                {0, 5, 0},
                {0, 0, 3},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 1);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            assertThat(response.getCaminho()).containsExactly(0, 1);
            assertThat(response.getCusto()).isEqualTo(5.0);
        }

        @Test
        @DisplayName("Deve encontrar caminho multi-salto escolhendo o menor peso total")
        void shouldFindMultiHopPathChoosingMinimumTotalWeight() {
            double[][] matriz = {
                {0, 10, 3, 0},
                {0, 0, 1, 2},
                {0, 4, 0, 8},
                {0, 0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 3);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            // Caminho: 0 -> 2 (3) -> 1 (4) -> 3 (2) = 9
            // vs 0 -> 1 (10) -> 3 (2) = 12
            assertThat(response.getCaminho()).containsExactly(0, 2, 1, 3);
            assertThat(response.getCusto()).isEqualTo(9.0);
        }

        @Test
        @DisplayName("Deve retornar caminho vazio quando origem igual ao destino")
        void shouldReturnSingleVertexPathWhenOriginEqualsDestination() {
            double[][] matriz = {
                {0, 5},
                {0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 0);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            assertThat(response.getCaminho()).containsExactly(0);
            assertThat(response.getCusto()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Deve retornar sem caminho quando destino inalcançável")
        void shouldReturnNoPathWhenDestinationUnreachable() {
            double[][] matriz = {
                {0, 5, 0},
                {0, 0, 0},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 2);

            assertThat(response.isCaminhoEncontrado()).isFalse();
            assertThat(response.getCaminho()).isEmpty();
            assertThat(response.getCusto()).isEqualTo(Double.POSITIVE_INFINITY);
            assertThat(response.getMensagem()).contains("Não existe caminho");
        }

        @Test
        @DisplayName("Deve funcionar com matriz 1x1")
        void shouldWorkWith1x1Matrix() {
            double[][] matriz = {{0}};
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 0);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            assertThat(response.getCaminho()).containsExactly(0);
            assertThat(response.getCusto()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Grafos com ciclos e estruturas complexas")
    class ComplexGraphTests {

        @Test
        @DisplayName("Deve lidar com ciclos sem loop infinito")
        void shouldHandleCyclesWithoutInfiniteLoop() {
            double[][] matriz = {
                {0, 2, 0, 0},
                {0, 0, 3, 0},
                {1, 0, 0, 4},  // Ciclo: 2 -> 0 -> 1 -> 2
                {0, 0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 3);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            assertThat(response.getCaminho()).containsExactly(0, 1, 2, 3);
            assertThat(response.getCusto()).isEqualTo(9.0); // 2 + 3 + 4
        }

        @Test
        @DisplayName("Deve ignorar arestas de peso zero (sem conexão)")
        void shouldIgnoreZeroWeightEdgesAsNoConnection() {
            double[][] matriz = {
                {0, 0, 5},
                {0, 0, 0},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 1);

            assertThat(response.isCaminhoEncontrado()).isFalse();
        }

        @Test
        @DisplayName("Deve escolher caminho com mais saltos se peso total for menor")
        void shouldPreferMoreHopsIfTotalWeightIsLower() {
            double[][] matriz = {
                {0, 100, 1, 0},
                {0, 0, 0, 1},
                {0, 1, 0, 0},  // Removed 2->3 edge to force path through 1
                {0, 0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 3);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            // 0->2 (1) -> 1 (1) -> 3 (1) = 3 vs 0->1 (100) -> 3 (1) = 101
            assertThat(response.getCaminho()).containsExactly(0, 2, 1, 3);
            assertThat(response.getCusto()).isEqualTo(3.0);
        }

        @Test
        @DisplayName("Deve funcionar com grafo completo pequeno")
        void shouldWorkWithSmallCompleteGraph() {
            double[][] matriz = {
                {0, 4, 2, 7},
                {4, 0, 1, 3},
                {2, 1, 0, 5},
                {7, 3, 5, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 3);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            // 0->2 (2) -> 1 (1) -> 3 (3) = 6
            assertThat(response.getCusto()).isEqualTo(6.0);
        }
    }

    @Nested
    @DisplayName("Validação de vértices inválidos")
    class InvalidVertexTests {

        @Test
        @DisplayName("Deve retornar erro para origem negativa")
        void shouldReturnErrorForNegativeOrigin() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, -1, 1);

            assertThat(response.isCaminhoEncontrado()).isFalse();
            assertThat(response.getMensagem()).contains("inválido");
        }

        @Test
        @DisplayName("Deve retornar erro para origem >= numero de vértices")
        void shouldReturnErrorForOriginOutOfBounds() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 2, 1);

            assertThat(response.isCaminhoEncontrado()).isFalse();
            assertThat(response.getMensagem()).contains("inválido");
        }

        @Test
        @DisplayName("Deve retornar erro para destino negativo")
        void shouldReturnErrorForNegativeDestination() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, -1);

            assertThat(response.isCaminhoEncontrado()).isFalse();
            assertThat(response.getMensagem()).contains("inválido");
        }

        @Test
        @DisplayName("Deve retornar erro para destino >= numero de vértices")
        void shouldReturnErrorForDestinationOutOfBounds() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 2);

            assertThat(response.isCaminhoEncontrado()).isFalse();
            assertThat(response.getMensagem()).contains("inválido");
        }
    }

    @Nested
    @DisplayName("Grafos desconexos")
    class DisconnectedGraphTests {

        @Test
        @DisplayName("Deve detectar grafo completamente desconexo")
        void shouldDetectCompletelyDisconnectedGraph() {
            double[][] matriz = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            CaminhoResponse response = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 2);

            assertThat(response.isCaminhoEncontrado()).isFalse();
        }

        @Test
        @DisplayName("Deve funcionar quando apenas alguns vértices estão conectados")
        void shouldWorkWhenOnlySomeVerticesConnected() {
            double[][] matriz = {
                {0, 5, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 3},
                {0, 0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            // Componente 1: 0 -> 1
            CaminhoResponse r1 = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 1);
            assertThat(r1.isCaminhoEncontrado()).isTrue();
            assertThat(r1.getCusto()).isEqualTo(5.0);

            // Componente 2: 2 -> 3
            CaminhoResponse r2 = dijkstraService.encontrarCaminhoMinimo(grafo, 2, 3);
            assertThat(r2.isCaminhoEncontrado()).isTrue();
            assertThat(r2.getCusto()).isEqualTo(3.0);

            // Entre componentes: sem caminho
            CaminhoResponse r3 = dijkstraService.encontrarCaminhoMinimo(grafo, 0, 3);
            assertThat(r3.isCaminhoEncontrado()).isFalse();
        }
    }

    @Nested
    @DisplayName("Testes parametrizados para múltiplos cenários")
    static class ParametrizedTests {

        static Stream<Arguments> provideGraphScenarios() {
            return Stream.of(
                // (matriz, origem, destino, caminhoEsperado, custoEsperado, encontrado)
                Arguments.of(
                    new double[][]{{0, 1}, {0, 0}},
                    0, 1,
                    List.of(0, 1), 1.0, true
                ),
                Arguments.of(
                    new double[][]{{0, 5, 0}, {0, 0, 3}, {0, 0, 0}},
                    0, 2,
                    List.of(0, 1, 2), 8.0, true
                ),
                Arguments.of(
                    new double[][]{{0, 0}, {0, 0}},
                    0, 1,
                    List.of(), Double.POSITIVE_INFINITY, false
                )
            );
        }

        @ParameterizedTest
        @MethodSource("provideGraphScenarios")
        @DisplayName("Deve resolver cenários variados de grafo")
        void shouldSolveVariedGraphScenarios(double[][] matriz, int origem, int destino,
                                              List<Integer> caminhoEsperado, double custoEsperado, boolean encontrado) {
            Grafo grafo = new Grafo(matriz);
            DijkstraService service = new DijkstraService();
            CaminhoResponse response = service.encontrarCaminhoMinimo(grafo, origem, destino);

            assertThat(response.isCaminhoEncontrado()).isEqualTo(encontrado);
            if (encontrado) {
                assertThat(response.getCaminho()).containsExactlyElementsOf(caminhoEsperado);
                assertThat(response.getCusto()).isEqualTo(custoEsperado);
            }
        }
    }
}