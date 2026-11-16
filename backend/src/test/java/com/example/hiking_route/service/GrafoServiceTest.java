package com.example.hiking_route.service;

import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.dto.GrafoRequest;
import com.example.hiking_route.dto.GrafoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GrafoServiceTest {

    private GrafoService grafoService;
    private DijkstraService dijkstraService;
    private CaminhoMaximoService caminhoMaximoService;
    private ValidadorGrafo validadorGrafo;

    @BeforeEach
    void setUp() {
        dijkstraService = new DijkstraService();
        caminhoMaximoService = new CaminhoMaximoService();
        validadorGrafo = new ValidadorGrafo();
        grafoService = new GrafoService();
        // Injeção manual já que não estamos usando Spring context nos testes
        try {
            java.lang.reflect.Field dijkstraField = GrafoService.class.getDeclaredField("dijkstraService");
            dijkstraField.setAccessible(true);
            dijkstraField.set(grafoService, dijkstraService);

            java.lang.reflect.Field maximoField = GrafoService.class.getDeclaredField("caminhoMaximoService");
            maximoField.setAccessible(true);
            maximoField.set(grafoService, caminhoMaximoService);

            java.lang.reflect.Field validadorField = GrafoService.class.getDeclaredField("validadorGrafo");
            validadorField.setAccessible(true);
            validadorField.set(grafoService, validadorGrafo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao injetar dependências", e);
        }
    }

    @Nested
    @DisplayName("processarGrafo - fluxo completo")
    class ProcessarGrafoTests {

        @Test
        @DisplayName("Deve processar grafo simples retornando ambos os caminhos")
        void shouldProcessSimpleGraphReturningBothPaths() {
            GrafoRequest request = new GrafoRequest(
                new double[][]{
                    {0, 5, 0},
                    {0, 0, 3},
                    {0, 0, 0}
                },
                0, 2
            );

            GrafoResponse response = grafoService.processarGrafo(request);

            assertThat(response.getStatus()).isEqualTo("SUCESSO");
            assertThat(response.getNumeroVertices()).isEqualTo(3);

            CaminhoResponse minimo = response.getCaminhoMinimo();
            assertThat(minimo.isCaminhoEncontrado()).isTrue();
            assertThat(minimo.getCaminho()).containsExactly(0, 1, 2);
            assertThat(minimo.getCusto()).isEqualTo(8.0);

            CaminhoResponse maximo = response.getCaminhoMaximo();
            assertThat(maximo.isCaminhoEncontrado()).isTrue();
            assertThat(maximo.getCaminho()).containsExactly(0, 1, 2);
            assertThat(maximo.getCusto()).isEqualTo(8.0);
        }

        @Test
        @DisplayName("Deve retornar status CAMINHO_PARCIAL quando apenas mínimo encontrado")
        void shouldReturnPartialStatusWhenOnlyMinimumFound() {
            // Grafo onde mínimo existe mas máximo não (ciclo positivo)
            GrafoRequest request = new GrafoRequest(
                new double[][]{
                    {0, 1, 0},
                    {0, 0, 1},
                    {1, 0, 0}
                },
                0, 2
            );

            GrafoResponse response = grafoService.processarGrafo(request);

            assertThat(response.getStatus()).isEqualTo("CAMINHO_PARCIAL");
            assertThat(response.getCaminhoMinimo().isCaminhoEncontrado()).isTrue();
            assertThat(response.getCaminhoMaximo().isCaminhoEncontrado()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar status NENHUM_CAMINHO_ENCONTRADO quando nenhum caminho existe")
        void shouldReturnNoPathStatusWhenNeitherPathExists() {
            GrafoRequest request = new GrafoRequest(
                new double[][]{
                    {0, 0, 0},
                    {0, 0, 0},
                    {0, 0, 0}
                },
                0, 2
            );

            GrafoResponse response = grafoService.processarGrafo(request);

            assertThat(response.getStatus()).isEqualTo("NENHUM_CAMINHO_ENCONTRADO");
            assertThat(response.getCaminhoMinimo().isCaminhoEncontrado()).isFalse();
            assertThat(response.getCaminhoMaximo().isCaminhoEncontrado()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar erro para matriz inválida")
        void shouldReturnErrorForInvalidMatrix() {
            GrafoRequest request = new GrafoRequest(
                new double[][]{{0, -1}, {0, 0}}, // peso negativo
                0, 1
            );

            GrafoResponse response = grafoService.processarGrafo(request);

            assertThat(response.getStatus()).isEqualTo("ERRO");
            assertThat(response.getCaminhoMinimo().getMensagem()).contains("negativos não são permitidos");
        }

        @Test
        @DisplayName("Deve retornar erro para vértices inválidos")
        void shouldReturnErrorForInvalidVertices() {
            GrafoRequest request = new GrafoRequest(
                new double[][]{{0, 1}, {0, 0}},
                0, 5 // destino fora dos limites
            );

            GrafoResponse response = grafoService.processarGrafo(request);

            assertThat(response.getStatus()).isEqualTo("ERRO");
            assertThat(response.getCaminhoMinimo().getMensagem()).contains("inválido");
        }
    }

    @Nested
    @DisplayName("processarCaminhoMinimo - apenas Dijkstra")
    class ProcessarCaminhoMinimoTests {

        @Test
        @DisplayName("Deve retornar apenas caminho mínimo")
        void shouldReturnOnlyMinimumPath() {
            GrafoRequest request = new GrafoRequest(
                new double[][]{
                    {0, 10, 3},
                    {0, 0, 1},
                    {0, 0, 0}
                },
                0, 2
            );

            CaminhoResponse response = grafoService.processarCaminhoMinimo(request);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            assertThat(response.getCaminho()).containsExactly(0, 2);
            assertThat(response.getCusto()).isEqualTo(3.0);
        }
    }

    @Nested
    @DisplayName("processarCaminhoMaximo - apenas caminho máximo")
    class ProcessarCaminhoMaximoTests {

        @Test
        @DisplayName("Deve retornar apenas caminho máximo")
        void shouldReturnOnlyMaximumPath() {
            GrafoRequest request = new GrafoRequest(
                new double[][]{
                    {0, 2, 10},
                    {0, 0, 3},
                    {0, 0, 0}
                },
                0, 2
            );

            CaminhoResponse response = grafoService.processarCaminhoMaximo(request);

            assertThat(response.isCaminhoEncontrado()).isTrue();
            // Direto 0->2 (10) vs 0->1->2 (5)
            assertThat(response.getCaminho()).containsExactly(0, 2);
            assertThat(response.getCusto()).isEqualTo(10.0);
        }
    }

    @Nested
    @DisplayName("seleção de algoritmo")
    class SelecaoAlgoritmoTests {

        private GrafoRequest request(double[][] m, int o, int d, String algoritmo, String heuristica) {
            GrafoRequest r = new GrafoRequest(m, o, d);
            r.setAlgoritmo(algoritmo);
            r.setHeuristica(heuristica);
            return r;
        }

        // Atalho 0->3 custa 100; caminho 0->1->2->3 custa 3
        private final double[][] atalhoCaro = {
            {0, 1, 0, 100},
            {0, 0, 1, 0},
            {0, 0, 0, 1},
            {0, 0, 0, 0}
        };

        @Test
        @DisplayName("Padrão sem algoritmo informado deve usar Dijkstra")
        void shouldDefaultToDijkstra() {
            GrafoResponse r = grafoService.processarGrafo(new GrafoRequest(atalhoCaro, 0, 3));

            assertThat(r.getCaminhoMinimo().getAlgoritmo()).isEqualTo("Dijkstra");
            assertThat(r.getCaminhoMinimo().getCusto()).isEqualTo(3.0);
        }

        @Test
        @DisplayName("BFS deve minimizar arestas, não o custo")
        void bfsShouldMinimizeEdgesNotCost() {
            GrafoResponse r = grafoService.processarGrafo(request(atalhoCaro, 0, 3, "bfs", null));

            assertThat(r.getCaminhoMinimo().getCaminho()).containsExactly(0, 3);
            assertThat(r.getCaminhoMinimo().getCusto()).isEqualTo(100.0);
            assertThat(r.getCaminhoMinimo().getAlgoritmo()).contains("BFS");
        }

        @Test
        @DisplayName("A* em grade 3x3 deve achar o mesmo custo do Dijkstra")
        void astarShouldMatchDijkstraCostOnGrid() {
            double[][] grade = new double[9][9];
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    int u = r * 3 + c;
                    if (c < 2) { double w = 1 + (u * 7 + 3) % 5; grade[u][u + 1] = w; grade[u + 1][u] = w; }
                    if (r < 2) { double w = 1 + (u * 4 + 1) % 6; grade[u][u + 3] = w; grade[u + 3][u] = w; }
                }
            }

            GrafoResponse dijkstra = grafoService.processarGrafo(request(grade, 0, 8, "dijkstra", null));
            for (String h : new String[]{"MANHATTAN", "EUCLIDEAN", "CHEBYSHEV"}) {
                GrafoResponse astar = grafoService.processarGrafo(request(grade, 0, 8, "astar", h));
                assertThat(astar.getCaminhoMinimo().getCusto())
                    .as("heurística %s", h)
                    .isEqualTo(dijkstra.getCaminhoMinimo().getCusto());
                assertThat(astar.getCaminhoMinimo().getAlgoritmo()).contains("A*");
            }
        }

        @Test
        @DisplayName("A* com pesos menores que 1 permanece ótimo (heurística escalada)")
        void astarShouldStayOptimalWithFractionalWeights() {
            // Grade 2x2: 0 1 / 2 3. Caminho 0->1->3 custa 0.2; 0->2->3 custa 5.1
            double[][] m = new double[4][4];
            m[0][1] = 0.1; m[1][0] = 0.1;
            m[1][3] = 0.1; m[3][1] = 0.1;
            m[0][2] = 5;   m[2][0] = 5;
            m[2][3] = 0.1; m[3][2] = 0.1;

            GrafoResponse r = grafoService.processarGrafo(request(m, 0, 3, "astar", "MANHATTAN"));

            assertThat(r.getCaminhoMinimo().getCaminho()).containsExactly(0, 1, 3);
            assertThat(r.getCaminhoMinimo().getCusto()).isEqualTo(0.2, within(1e-9));
        }

        @Test
        @DisplayName("A* em grafo que não é grade deve cair em heurística nula e continuar correto")
        void astarShouldFallBackOnNonGridGraph() {
            GrafoResponse r = grafoService.processarGrafo(request(atalhoCaro, 0, 3, "astar", "MANHATTAN"));

            assertThat(r.getCaminhoMinimo().getCusto()).isEqualTo(3.0);
            assertThat(r.getCaminhoMinimo().getMensagem()).contains("heurística nula");
        }

        @Test
        @DisplayName("Algoritmo ou heurística inválidos devem retornar ERRO")
        void shouldRejectUnknownAlgorithmOrHeuristic() {
            assertThat(grafoService.processarGrafo(request(atalhoCaro, 0, 3, "floyd", null)).getStatus())
                .isEqualTo("ERRO");
            assertThat(grafoService.processarGrafo(request(atalhoCaro, 0, 3, "astar", "XYZ")).getStatus())
                .isEqualTo("ERRO");
        }
    }
}
