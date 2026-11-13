package com.example.hiking_route.algorithm;

import com.example.hiking_route.model.Grafo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AStarFinderTest {

    @Nested
    @DisplayName("Heurística nula (equivale a Dijkstra)")
    class NullHeuristicTests {

        private final AStarFinder finder = new AStarFinder();

        @Test
        @DisplayName("Deve funcionar como Dijkstra")
        void shouldWorkLikeDijkstra() {
            double[][] matriz = {
                {0, 2, 5},
                {0, 0, 1},
                {0, 0, 0}
            };
            Grafo grafo = new Grafo(matriz);

            PathResult result = finder.encontrarCaminho(grafo, 0, 2);

            assertThat(result.isEncontrado()).isTrue();
            assertThat(result.getCaminho()).containsExactly(0, 1, 2);
            assertThat(result.getCusto()).isEqualTo(3.0);
        }

        @Test
        @DisplayName("Deve retornar nome correto")
        void shouldReturnCorrectName() {
            assertThat(finder.getNome()).contains("heurística nula");
        }
    }

    @Nested
    @DisplayName("Heurística Manhattan em grade 3x3")
    class ManhattanHeuristicTests {

        // Grade 3x3: índices 0-8
        // 0 1 2
        // 3 4 5
        // 6 7 8
        private final AStarFinder finder = new AStarFinder(3, 3, AStarFinder.Heuristica.MANHATTAN);

        @Test
        @DisplayName("Deve encontrar caminho na grade")
        void shouldFindPathInGrid() {
            // Conexões: 0->1->2->5->8 (canto sup esq -> canto inf dir)
            double[][] matriz = new double[9][9];
            matriz[0][1] = 1;
            matriz[1][2] = 1;
            matriz[2][5] = 1;
            matriz[5][8] = 1;
            // Caminho alternativo mais longo: 0->3->6->7->8
            matriz[0][3] = 1;
            matriz[3][6] = 1;
            matriz[6][7] = 1;
            matriz[7][8] = 1;

            Grafo grafo = new Grafo(matriz);

            PathResult result = finder.encontrarCaminho(grafo, 0, 8);

            assertThat(result.isEncontrado()).isTrue();
            // Ambos caminhos têm custo 4, A* pode retornar qualquer um
            assertThat(result.getCusto()).isEqualTo(4.0);
        }

        @Test
        @DisplayName("Deve expandir menos nós que Dijkstra em grade aberta")
        void shouldExpandFewerNodesThanDijkstraInOpenGrid() {
            // Grade 5x5 totalmente conectada (malha)
            int size = 5;
            double[][] matriz = new double[size * size][size * size];

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    int u = y * size + x;
                    if (x + 1 < size) matriz[u][u + 1] = 1; // direita
                    if (y + 1 < size) matriz[u][u + size] = 1; // baixo
                }
            }

            Grafo grafo = new Grafo(matriz);

            DijkstraFinder dijkstra = new DijkstraFinder();
            AStarFinder astar = new AStarFinder(size, size, AStarFinder.Heuristica.MANHATTAN);

            PathResult rDijkstra = dijkstra.encontrarCaminho(grafo, 0, size * size - 1);
            PathResult rAStar = astar.encontrarCaminho(grafo, 0, size * size - 1);

            assertThat(rDijkstra.isEncontrado()).isTrue();
            assertThat(rAStar.isEncontrado()).isTrue();
            assertThat(rAStar.getCusto()).isEqualTo(rDijkstra.getCusto());
            // A* com Manhattan deve expandir menos ou igual nós
            assertThat(rAStar.getNosExpandidos()).isLessThanOrEqualTo(rDijkstra.getNosExpandidos());
        }
    }

    @Nested
    @DisplayName("Heurística Euclidiana")
    class EuclideanHeuristicTests {

        @Test
        @DisplayName("Deve encontrar caminho com heurística euclidiana")
        void shouldFindPathWithEuclideanHeuristic() {
            int size = 4;
            double[][] matriz = new double[size * size][size * size];

            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    int u = y * size + x;
                    if (x + 1 < size) matriz[u][u + 1] = 1;
                    if (y + 1 < size) matriz[u][u + size] = 1;
                }
            }

            Grafo grafo = new Grafo(matriz);
            AStarFinder finder = new AStarFinder(size, size, AStarFinder.Heuristica.EUCLIDIANA);

            PathResult result = finder.encontrarCaminho(grafo, 0, size * size - 1);

            assertThat(result.isEncontrado()).isTrue();
            assertThat(result.getCusto()).isEqualTo(6.0); // 3 direita + 3 baixo
        }
    }

    @Nested
    @DisplayName("Metadados")
    class MetadataTests {

        @Test
        @DisplayName("Deve retornar complexidades")
        void shouldReturnComplexities() {
            AStarFinder finder = new AStarFinder(10, 10, AStarFinder.Heuristica.MANHATTAN);
            assertThat(finder.getComplexidadeTempo()).contains("log V");
            assertThat(finder.getComplexidadeEspaco()).isEqualTo("O(V)");
        }
    }
}