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
}