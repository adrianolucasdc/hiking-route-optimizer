package com.example.hiking_route.util;

import com.example.hiking_route.model.Grafo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class GrafoVisualizerTest {

    @Nested
    @DisplayName("getVertexLabel")
    class VertexLabelTests {

        @Test
        @DisplayName("Deve retornar A-Z para índices 0-25")
        void shouldReturnAToZFor0To25() {
            assertThat(GrafoVisualizer.getVertexLabel(0)).isEqualTo("A");
            assertThat(GrafoVisualizer.getVertexLabel(1)).isEqualTo("B");
            assertThat(GrafoVisualizer.getVertexLabel(25)).isEqualTo("Z");
        }

        @Test
        @DisplayName("Deve retornar AA, AB... para índices >= 26")
        void shouldReturnAAABFor26Plus() {
            assertThat(GrafoVisualizer.getVertexLabel(26)).isEqualTo("AA");
            assertThat(GrafoVisualizer.getVertexLabel(27)).isEqualTo("AB");
            assertThat(GrafoVisualizer.getVertexLabel(51)).isEqualTo("AZ");
            assertThat(GrafoVisualizer.getVertexLabel(52)).isEqualTo("BA");
        }

        @Test
        @DisplayName("Deve retornar ? para índice negativo")
        void shouldReturnQuestionMarkForNegative() {
            assertThat(GrafoVisualizer.getVertexLabel(-1)).isEqualTo("?");
        }
    }

    @Nested
    @DisplayName("renderMatrizComCaminho")
    class RenderMatrixTests {

        @Test
        @DisplayName("Deve renderizar matriz 3x3 com caminho")
        void shouldRender3x3MatrixWithPath() {
            double[][] matriz = {
                {0, 5, 0},
                {0, 0, 3},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);
            List<Integer> caminho = List.of(0, 1, 2);

            String resultado = GrafoVisualizer.renderMatrizComCaminho(grafo, caminho, 0, 2, false);

            assertThat(resultado).contains("MATRIZ DE ADJACÊNCIA");
            assertThat(resultado).contains("A");
            assertThat(resultado).contains("B");
            assertThat(resultado).contains("C");
            assertThat(resultado).contains("5");
            assertThat(resultado).contains("3");
            assertThat(resultado).contains("A → B → C");
        }

        @Test
        @DisplayName("Deve renderizar matriz com cores ANSI quando habilitado")
        void shouldRenderWithAnsiColorsWhenEnabled() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            String resultado = GrafoVisualizer.renderMatrizComCaminho(grafo, List.of(0, 1), 0, 1, true);

            assertThat(resultado).contains("\u001B["); // Código ANSI
        }

        @Test
        @DisplayName("Deve lidar com grafo nulo")
        void shouldHandleNullGraph() {
            String resultado = GrafoVisualizer.renderMatrizComCaminho(null, null, 0, 0, false);
            assertThat(resultado).contains("nulo ou inválido");
        }

        @Test
        @DisplayName("Deve lidar com matriz vazia")
        void shouldHandleEmptyMatrix() {
            Grafo grafo = new Grafo(new double[0][0]);
            String resultado = GrafoVisualizer.renderMatrizComCaminho(grafo, null, 0, 0, false);
            assertThat(resultado).contains("vazia");
        }

        @Test
        @DisplayName("Deve destacar origem e destino corretamente")
        void shouldHighlightOriginAndDestination() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            String resultado = GrafoVisualizer.renderMatrizComCaminho(grafo, List.of(0, 1), 0, 1, false);

            // Verifica que A (origem) e B (destino) aparecem
            assertThat(resultado).contains("A");
            assertThat(resultado).contains("B");
        }
    }

    @Nested
    @DisplayName("renderCompacto")
    class CompactRenderTests {

        @Test
        @DisplayName("Deve renderizar versão compacta")
        void shouldRenderCompactVersion() {
            double[][] matriz = {
                {0, 5, 0},
                {0, 0, 3},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);
            List<Integer> caminho = List.of(0, 1, 2);

            String resultado = GrafoVisualizer.renderCompacto(grafo, caminho, 0, 2);

            assertThat(resultado).contains("Grafo 3x3");
            assertThat(resultado).contains("Caminho: A → B → C");
            assertThat(resultado).contains("Arestas do caminho");
            assertThat(resultado).contains("Estatísticas");
        }

        @Test
        @DisplayName("Deve lidar com caminho nulo")
        void shouldHandleNullPath() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            String resultado = GrafoVisualizer.renderCompacto(grafo, null, 0, 1);

            assertThat(resultado).contains("Nenhum caminho encontrado");
        }
    }

    @Nested
    @DisplayName("exportarPNG")
    class ExportPngTests {

        @Test
        @DisplayName("Deve exportar PNG sem erros")
        void shouldExportPngWithoutErrors(@TempDir Path tempDir) throws IOException {
            double[][] matriz = {
                {0, 5, 3},
                {0, 0, 1},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);
            List<Integer> caminho = List.of(0, 1, 2);
            File arquivo = tempDir.resolve("grafo-teste.png").toFile();

            GrafoVisualizer.exportarPNG(grafo, caminho, 0, 2, arquivo.getAbsolutePath(), 400, 400);

            assertThat(arquivo).exists();
            assertThat(arquivo.length()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Deve lançar exceção para grafo nulo")
        void shouldThrowForNullGraph(@TempDir Path tempDir) {
            File arquivo = tempDir.resolve("grafo.png").toFile();

            assertThatThrownBy(() -> GrafoVisualizer.exportarPNG(null, null, 0, 0, arquivo.getAbsolutePath(), 400, 400))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inválido");
        }

        @Test
        @DisplayName("Deve exportar PNG sem caminho")
        void shouldExportPngWithoutPath(@TempDir Path tempDir) throws IOException {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);
            File arquivo = tempDir.resolve("grafo-sem-caminho.png").toFile();

            GrafoVisualizer.exportarPNG(grafo, null, 0, 1, arquivo.getAbsolutePath(), 400, 400);

            assertThat(arquivo).exists();
            assertThat(arquivo.length()).isGreaterThan(0);
        }
    }
}