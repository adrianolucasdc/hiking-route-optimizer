package com.example.hiking_route.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GrafoTest {

    @Nested
    @DisplayName("Construtor e getters básicos")
    class ConstructorAndGettersTests {

        @Test
        @DisplayName("Deve criar grafo com matriz válida")
        void shouldCreateGraphWithValidMatrix() {
            double[][] matriz = {
                {0, 5, 3},
                {2, 0, 1},
                {4, 6, 0}
            };
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.getNumeroVertices()).isEqualTo(3);
            assertThat(grafo.getMatrizAdjacencia()).isEqualTo(matriz);
        }

        @Test
        @DisplayName("Deve retornar matriz defensiva (não a mesma referência)")
        void shouldReturnDefensiveCopyOfMatrix() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);
            double[][] retornada = grafo.getMatrizAdjacencia();

            // Modificar a retornada não deve afetar o original
            retornada[0][1] = 999;
            assertThat(grafo.getMatrizAdjacencia()[0][1]).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("existeAresta")
    class ExisteArestaTests {

        @Test
        @DisplayName("Deve retornar true para aresta existente com peso > 0")
        void shouldReturnTrueForExistingEdgeWithPositiveWeight() {
            double[][] matriz = {{0, 5}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.existeAresta(0, 1)).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false para peso zero (sem aresta)")
        void shouldReturnFalseForZeroWeight() {
            double[][] matriz = {{0, 0}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.existeAresta(0, 1)).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false para índices negativos")
        void shouldReturnFalseForNegativeIndices() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.existeAresta(-1, 0)).isFalse();
            assertThat(grafo.existeAresta(0, -1)).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false para índices fora dos limites")
        void shouldReturnFalseForOutOfBoundsIndices() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.existeAresta(2, 0)).isFalse();
            assertThat(grafo.existeAresta(0, 2)).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false para auto-loop com peso zero")
        void shouldReturnFalseForSelfLoopWithZeroWeight() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.existeAresta(0, 0)).isFalse();
        }
    }

    @Nested
    @DisplayName("getPeso")
    class GetPesoTests {

        @Test
        @DisplayName("Deve retornar peso da aresta existente")
        void shouldReturnWeightOfExistingEdge() {
            double[][] matriz = {{0, 7.5}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.getPeso(0, 1)).isEqualTo(7.5);
        }

        @Test
        @DisplayName("Deve retornar INFINITY para aresta inexistente")
        void shouldReturnInfinityForNonExistentEdge() {
            double[][] matriz = {{0, 0}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.getPeso(0, 1)).isEqualTo(Double.POSITIVE_INFINITY);
        }

        @Test
        @DisplayName("Deve retornar INFINITY para índices inválidos")
        void shouldReturnInfinityForInvalidIndices() {
            double[][] matriz = {{0, 1}, {0, 0}};
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.getPeso(-1, 0)).isEqualTo(Double.POSITIVE_INFINITY);
            assertThat(grafo.getPeso(0, 5)).isEqualTo(Double.POSITIVE_INFINITY);
        }
    }

    @Nested
    @DisplayName("isValido")
    class IsValidoTests {

        @Test
        @DisplayName("Deve retornar true para matriz quadrada válida")
        void shouldReturnTrueForValidSquareMatrix() {
            double[][] matriz = {
                {0, 1, 2},
                {3, 0, 4},
                {5, 6, 0}
            };
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.isValido()).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false para matriz nula (construtor trata como vazia)")
        void shouldReturnFalseForNullMatrix() {
            Grafo grafo = new Grafo(null);

            assertThat(grafo.isValido()).isFalse();
            assertThat(grafo.getNumeroVertices()).isEqualTo(0);
        }

        @Test
        @DisplayName("Deve retornar false para matriz vazia")
        void shouldReturnFalseForEmptyMatrix() {
            Grafo grafo = new Grafo(new double[0][0]);

            assertThat(grafo.isValido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false para matriz não quadrada")
        void shouldReturnFalseForNonSquareMatrix() {
            double[][] matriz = {
                {0, 1, 2},
                {0, 1}
            };
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.isValido()).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false para linha nula")
        void shouldReturnFalseForNullRow() {
            double[][] matriz = {
                {0, 1},
                null
            };
            Grafo grafo = new Grafo(matriz);

            assertThat(grafo.isValido()).isFalse();
        }
    }

    @Nested
    @DisplayName("Setter de matriz")
    class SetterTests {

        @Test
        @DisplayName("Deve atualizar matriz e número de vértices")
        void shouldUpdateMatrixAndVertexCount() {
            Grafo grafo = new Grafo(new double[][]{{0, 1}, {0, 0}});
            assertThat(grafo.getNumeroVertices()).isEqualTo(2);

            double[][] novaMatriz = {
                {0, 1, 2},
                {3, 0, 4},
                {5, 6, 0}
            };
            grafo.setMatrizAdjacencia(novaMatriz);

            assertThat(grafo.getNumeroVertices()).isEqualTo(3);
            assertThat(grafo.getMatrizAdjacencia()).isEqualTo(novaMatriz);
        }
    }
}