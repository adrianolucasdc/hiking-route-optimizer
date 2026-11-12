package com.example.hiking_route.service;

import com.example.hiking_route.exception.MatrizInvalidaException;
import com.example.hiking_route.exception.VerticeInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ValidadorGrafoTest {

    private ValidadorGrafo validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorGrafo();
    }

    @Nested
    @DisplayName("Validação de matriz")
    class MatrizValidationTests {

        @Test
        @DisplayName("Deve lançar exceção para matriz nula")
        void shouldThrowForNullMatrix() {
            assertThatThrownBy(() -> validador.validarMatriz(null))
                .isInstanceOf(MatrizInvalidaException.class)
                .hasMessageContaining("nula");
        }

        @Test
        @DisplayName("Deve lançar exceção para matriz vazia")
        void shouldThrowForEmptyMatrix() {
            double[][] matriz = {};
            assertThatThrownBy(() -> validador.validarMatriz(matriz))
                .isInstanceOf(MatrizInvalidaException.class)
                .hasMessageContaining("pelo menos uma linha");
        }

        @Test
        @DisplayName("Deve lançar exceção para matriz não quadrada (linhas com tamanhos diferentes)")
        void shouldThrowForNonSquareMatrix() {
            double[][] matriz = {
                {0, 1, 2},
                {0, 1},
                {0, 1, 2}
            };
            assertThatThrownBy(() -> validador.validarMatriz(matriz))
                .isInstanceOf(MatrizInvalidaException.class)
                .hasMessageContaining("quadrada");
        }

        @Test
        @DisplayName("Deve lançar exceção para linha nula")
        void shouldThrowForNullRow() {
            // Matriz 3x3 onde a linha 1 é nula (primeira linha tem 3 elementos para passar no check de quadrada)
            double[][] matriz = {
                {0, 1, 2},
                null,
                {0, 1, 2}
            };
            assertThatThrownBy(() -> validador.validarMatriz(matriz))
                .isInstanceOf(MatrizInvalidaException.class)
                .hasMessageContaining("nula");
        }

        @Test
        @DisplayName("Deve lançar exceção para valor NaN")
        void shouldThrowForNaNValue() {
            double[][] matriz = {
                {0, Double.NaN},
                {0, 0}
            };
            assertThatThrownBy(() -> validador.validarMatriz(matriz))
                .isInstanceOf(MatrizInvalidaException.class)
                .hasMessageContaining("NaN");
        }

        @Test
        @DisplayName("Deve lançar exceção para valor negativo (Dijkstra não suporta)")
        void shouldThrowForNegativeValue() {
            double[][] matriz = {
                {0, -1},
                {0, 0}
            };
            assertThatThrownBy(() -> validador.validarMatriz(matriz))
                .isInstanceOf(MatrizInvalidaException.class)
                .hasMessageContaining("negativos não são permitidos");
        }

        @Test
        @DisplayName("Deve aceitar matriz válida com zeros na diagonal")
        void shouldAcceptValidMatrixWithZeroDiagonal() {
            double[][] matriz = {
                {0, 5, 3},
                {2, 0, 1},
                {4, 6, 0}
            };
            assertThatCode(() -> validador.validarMatriz(matriz)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Deve aceitar matriz com pesos zero (arestas de custo zero)")
        void shouldAcceptMatrixWithZeroWeightEdges() {
            double[][] matriz = {
                {0, 0, 5},
                {0, 0, 0},
                {0, 0, 0}
            };
            assertThatCode(() -> validador.validarMatriz(matriz)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Deve aceitar matriz 1x1")
        void shouldAccept1x1Matrix() {
            double[][] matriz = {{0}};
            assertThatCode(() -> validador.validarMatriz(matriz)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Deve aceitar valores Double.POSITIVE_INFINITY")
        void shouldAcceptPositiveInfinity() {
            double[][] matriz = {
                {0, Double.POSITIVE_INFINITY},
                {0, 0}
            };
            assertThatCode(() -> validador.validarMatriz(matriz)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Validação de vértices")
    class VertexValidationTests {

        @Test
        @DisplayName("Deve lançar exceção para origem negativa")
        void shouldThrowForNegativeOrigin() {
            assertThatThrownBy(() -> validador.validarVertices(-1, 1, 3))
                .isInstanceOf(VerticeInvalidoException.class)
                .hasMessageContaining("origem");
        }

        @Test
        @DisplayName("Deve lançar exceção para origem >= n")
        void shouldThrowForOriginOutOfBounds() {
            assertThatThrownBy(() -> validador.validarVertices(3, 1, 3))
                .isInstanceOf(VerticeInvalidoException.class)
                .hasMessageContaining("origem");
        }

        @Test
        @DisplayName("Deve lançar exceção para destino negativo")
        void shouldThrowForNegativeDestination() {
            assertThatThrownBy(() -> validador.validarVertices(0, -1, 3))
                .isInstanceOf(VerticeInvalidoException.class)
                .hasMessageContaining("destino");
        }

        @Test
        @DisplayName("Deve lançar exceção para destino >= n")
        void shouldThrowForDestinationOutOfBounds() {
            assertThatThrownBy(() -> validador.validarVertices(0, 3, 3))
                .isInstanceOf(VerticeInvalidoException.class)
                .hasMessageContaining("destino");
        }

        @Test
        @DisplayName("Deve aceitar vértices válidos nos limites")
        void shouldAcceptValidVerticesAtBounds() {
            assertThatCode(() -> validador.validarVertices(0, 2, 3)).doesNotThrowAnyException();
            assertThatCode(() -> validador.validarVertices(2, 0, 3)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Validação completa (matriz + vértices)")
    class CompleteValidationTests {

        @Test
        @DisplayName("Deve validar matriz e vértices juntos")
        void shouldValidateMatrixAndVerticesTogether() {
            double[][] matriz = {
                {0, 1, 2},
                {0, 0, 3},
                {0, 0, 0}
            };
            assertThatCode(() -> validador.validarCompleto(matriz, 0, 2)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Deve falhar se matriz inválida")
        void shouldFailIfMatrixInvalid() {
            double[][] matriz = {{0, -1}, {0, 0}};
            assertThatThrownBy(() -> validador.validarCompleto(matriz, 0, 1))
                .isInstanceOf(MatrizInvalidaException.class);
        }

        @Test
        @DisplayName("Deve falhar se vértices inválidos")
        void shouldFailIfVerticesInvalid() {
            double[][] matriz = {{0, 1}, {0, 0}};
            assertThatThrownBy(() -> validador.validarCompleto(matriz, 0, 2))
                .isInstanceOf(VerticeInvalidoException.class);
        }
    }

    @Nested
    @DisplayName("Validação de conectividade (Floyd-Warshall)")
    class ConnectivityValidationTests {

        @Test
        @DisplayName("Deve detectar grafo conexo")
        void shouldDetectConnectedGraph() {
            double[][] matriz = {
                {0, 1, 0},
                {0, 0, 1},
                {1, 0, 0}
            };
            assertThatCode(() -> validador.validarConectividade(matriz, 0, 2)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Deve lançar exceção para grafo desconexo")
        void shouldThrowForDisconnectedGraph() {
            double[][] matriz = {
                {0, 1, 0},
                {0, 0, 0},
                {0, 0, 0}
            };
            assertThatThrownBy(() -> validador.validarConectividade(matriz, 0, 2))
                .isInstanceOf(VerticeInvalidoException.class)
                .hasMessageContaining("Não existe caminho");
        }

        @Test
        @DisplayName("Deve funcionar para origem igual destino (mesmo em grafo desconexo)")
        void shouldWorkForSameOriginDestinationEvenInDisconnectedGraph() {
            double[][] matriz = {
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
            };
            assertThatCode(() -> validador.validarConectividade(matriz, 1, 1)).doesNotThrowAnyException();
        }
    }
}