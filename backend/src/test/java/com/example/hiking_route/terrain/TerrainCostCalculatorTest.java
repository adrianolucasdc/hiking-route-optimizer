package com.example.hiking_route.terrain;

import com.example.hiking_route.model.Grafo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class TerrainCostCalculatorTest {

    private final TerrainCostCalculator calc = new TerrainCostCalculator();

    @Nested
    @DisplayName("Tipos de terreno")
    class TipoTerrenoTests {

        @Test
        @DisplayName("Deve ter custos base definidos")
        void shouldHaveBaseCostsDefined() {
            assertThat(TerrainCostCalculator.TipoTerreno.TRILHA_BOA.getCustoBase()).isEqualTo(1.0);
            assertThat(TerrainCostCalculator.TipoTerreno.ROCHA.getCustoBase()).isEqualTo(5.0);
            assertThat(TerrainCostCalculator.TipoTerreno.AGUA_FUNDO.getCustoBase()).isEqualTo(Double.POSITIVE_INFINITY);
        }

        @Test
        @DisplayName("Deve identificar terrenos intransponíveis")
        void shouldIdentifyImpassableTerrain() {
            assertThat(TerrainCostCalculator.TipoTerreno.AGUA_FUNDO.isTransponivel()).isFalse();
            assertThat(TerrainCostCalculator.TipoTerreno.PAREDE_ROCHA.isTransponivel()).isFalse();
            assertThat(TerrainCostCalculator.TipoTerreno.OBSTACULO.isTransponivel()).isFalse();
            assertThat(TerrainCostCalculator.TipoTerreno.TRILHA_BOA.isTransponivel()).isTrue();
        }
    }

    @Nested
    @DisplayName("Geração de grafo plana")
    class FlatTerrainTests {

        @Test
        @DisplayName("Deve gerar grafo com custos uniformes em terreno plano")
        void shouldGenerateUniformCostsOnFlatTerrain() {
            double[][] elev = {
                {100, 100, 100},
                {100, 100, 100},
                {100, 100, 100}
            };
            TerrainCostCalculator.TipoTerreno[][] terr = new TerrainCostCalculator.TipoTerreno[3][3];
            // Todos null = TERRA_BATIDA (custo 2.0)

            Grafo grafo = calc.gerarGrafo(elev, terr);

            // Verificar arestas ortogonais (distância 10m = 1 célula)
            // Custo = 2.0 (terra batida) * 10m = 20.0
            assertThat(grafo.existeAresta(0, 1)).isTrue(); // (0,0) -> (0,1)
            assertThat(grafo.getPeso(0, 1)).isCloseTo(20.0, within(0.01));

            // Diagonais: √2 * 10m * 2.0 = 28.28
            assertThat(grafo.existeAresta(0, 4)).isTrue(); // (0,0) -> (1,1)
            assertThat(grafo.getPeso(0, 4)).isCloseTo(20.0 * Math.sqrt(2), within(0.01));
        }

        @Test
        @DisplayName("Deve respeitar tipos de terreno diferentes")
        void shouldRespectDifferentTerrainTypes() {
            double[][] elev = {
                {100, 100},
                {100, 100}
            };
            TerrainCostCalculator.TipoTerreno[][] terr = {
                {TerrainCostCalculator.TipoTerreno.TRILHA_BOA, TerrainCostCalculator.TipoTerreno.ROCHA},
                {TerrainCostCalculator.TipoTerreno.TERRA_BATIDA, TerrainCostCalculator.TipoTerreno.GRAMA}
            };

            Grafo grafo = calc.gerarGrafo(elev, terr);

            // Trilha (1.0) -> Rocha (5.0) = média 3.0 * 10m = 30
            double custo01 = grafo.getPeso(0, 1);
            assertThat(custo01).isCloseTo(30.0, within(0.01));
        }

        @Test
        @DisplayName("Deve bloquear movimento para células intransponíveis")
        void shouldBlockMovementToImpassableCells() {
            double[][] elev = {{100, 100}, {100, 100}};
            TerrainCostCalculator.TipoTerreno[][] terr = {
                {TerrainCostCalculator.TipoTerreno.TERRA_BATIDA, TerrainCostCalculator.TipoTerreno.AGUA_FUNDO},
                {TerrainCostCalculator.TipoTerreno.TERRA_BATIDA, TerrainCostCalculator.TipoTerreno.TERRA_BATIDA}
            };

            Grafo grafo = calc.gerarGrafo(elev, terr);

            // (0,0) não deve conectar com (0,1) - água funda
            assertThat(grafo.existeAresta(0, 1)).isFalse();

            // Mas deve conectar com (1,0) - terra batida
            assertThat(grafo.existeAresta(0, 2)).isTrue();
        }
    }

    @Nested
    @DisplayName("Inclinação (subida/descida)")
    class SlopeTests {

        @Test
        @DisplayName("Deve aumentar custo em subida")
        void shouldIncreaseCostOnUphill() {
            // Subida de 10m em 10m horizontal = 45 graus
            double[][] elev = {
                {100, 110},  // delta = +10
                {100, 100}
            };
            TerrainCostCalculator.TipoTerreno[][] terr = new TerrainCostCalculator.TipoTerreno[2][2];

            Grafo grafo = calc.gerarGrafo(elev, terr);

            // Base: 2.0 * 10 = 20
            // Inclinação 45°, penalidade 0.1/° = 1 + 4.5 = 5.5x
            // Esperado: ~110
            double custo = grafo.getPeso(0, 1);
            assertThat(custo).isGreaterThan(50); // Bem maior que plano
        }

        @Test
        @DisplayName("Deve reduzir custo em descida (mas não abaixo de 30%)")
        void shouldReduceCostOnDownhill() {
            double[][] elev = {
                {110, 100},  // delta = -10 (descida)
                {100, 100}
            };
            TerrainCostCalculator.TipoTerreno[][] terr = new TerrainCostCalculator.TipoTerreno[2][2];

            Grafo grafo = calc.gerarGrafo(elev, terr);

            double custo = grafo.getPeso(0, 1);
            double custoPlano = 20.0; // TERRA_BATIDA * 10m

            // Descida deve ser mais barata que plano
            assertThat(custo).isLessThan(custoPlano);
            // Mas não menos que 30% (6.0) - inclusive boundary
            assertThat(custo).isGreaterThanOrEqualTo(6.0);
        }

        @Test
        @DisplayName("Deve bloquear subida muito íngreme (>45°)")
        void shouldBlockTooSteepUphill() {
            // Subida de 100m em 10m = ~84° > 45°
            double[][] elev = {
                {100, 200},
                {100, 100}
            };
            TerrainCostCalculator.TipoTerreno[][] terr = new TerrainCostCalculator.TipoTerreno[2][2];

            Grafo grafo = calc.gerarGrafo(elev, terr);

            assertThat(grafo.existeAresta(0, 1)).isFalse();
        }

        @Test
        @DisplayName("Deve permitir configuração de limites de inclinação")
        void shouldAllowCustomSlopeLimits() {
            TerrainCostCalculator custom = TerrainCostCalculator.builder()
                .maxInclinacaoSubida(80.0) // Permitir até 80°
                .build();

            // Subida de 60m em 10m = ~80.5° - levemente acima
            // Subida de 56m em 10m = ~80° - exatamente no limite
            double[][] elev = {
                {100, 156}, // ~80° (atan(56/10) = 79.8°)
                {100, 100}
            };
            TerrainCostCalculator.TipoTerreno[][] terr = new TerrainCostCalculator.TipoTerreno[2][2];

            Grafo grafo = custom.gerarGrafo(elev, terr);

            // Deve permitir subida até 80°
            assertThat(grafo.existeAresta(0, 1)).isTrue();

            // Mas bloquear acima de 80°
            double[][] elevTooSteep = {
                {100, 160}, // ~81°
                {100, 100}
            };
            Grafo grafo2 = custom.gerarGrafo(elevTooSteep, terr);
            assertThat(grafo2.existeAresta(0, 1)).isFalse();
        }
    }

    @Nested
    @DisplayName("Movimento diagonal")
    class DiagonalMovementTests {

        @Test
        @DisplayName("Deve custar √2 vezes mais que ortogonal")
        void shouldCostSqrt2TimesOrthogonal() {
            double[][] elev = {{100, 100}, {100, 100}};
            TerrainCostCalculator.TipoTerreno[][] terr = new TerrainCostCalculator.TipoTerreno[2][2];

            Grafo grafo = calc.gerarGrafo(elev, terr);

            double ortogonal = grafo.getPeso(0, 1); // (0,0) -> (0,1)
            double diagonal = grafo.getPeso(0, 3);  // (0,0) -> (1,1)

            assertThat(diagonal).isCloseTo(ortogonal * Math.sqrt(2), within(0.01));
        }

        @Test
        @DisplayName("Deve permitir desativar diagonais")
        void shouldAllowDisablingDiagonals() {
            TerrainCostCalculator noDiag = TerrainCostCalculator.builder()
                .permitirDiagonais(false)
                .build();

            double[][] elev = {{100, 100}, {100, 100}};
            TerrainCostCalculator.TipoTerreno[][] terr = new TerrainCostCalculator.TipoTerreno[2][2];

            Grafo grafo = noDiag.gerarGrafo(elev, terr);

            // Ortogonais existem
            assertThat(grafo.existeAresta(0, 1)).isTrue();
            assertThat(grafo.existeAresta(0, 2)).isTrue();
            // Diagonais NÃO existem
            assertThat(grafo.existeAresta(0, 3)).isFalse();
        }
    }

    @Nested
    @DisplayName("Utilitários de coordenadas")
    class CoordinateUtilsTests {

        @Test
        @DisplayName("Deve converter coordenadas para índice")
        void shouldConvertCoordsToIndex() {
            assertThat(TerrainCostCalculator.toIndex(0, 0, 10)).isEqualTo(0);
            assertThat(TerrainCostCalculator.toIndex(0, 5, 10)).isEqualTo(5);
            assertThat(TerrainCostCalculator.toIndex(1, 0, 10)).isEqualTo(10);
            assertThat(TerrainCostCalculator.toIndex(2, 3, 10)).isEqualTo(23);
        }

        @Test
        @DisplayName("Deve converter índice para coordenadas")
        void shouldConvertIndexToCoords() {
            assertThat(TerrainCostCalculator.toCoords(0, 10)).isEqualTo(new int[]{0, 0});
            assertThat(TerrainCostCalculator.toCoords(5, 10)).isEqualTo(new int[]{0, 5});
            assertThat(TerrainCostCalculator.toCoords(10, 10)).isEqualTo(new int[]{1, 0});
            assertThat(TerrainCostCalculator.toCoords(23, 10)).isEqualTo(new int[]{2, 3});
        }
    }

    @Nested
    @DisplayName("Cenário de exemplo: montanha")
    class MountainScenarioTests {

        @Test
        @DisplayName("Deve gerar cenário de montanha válido")
        void shouldGenerateValidMountainScenario() {
            TerrainCostCalculator.CenarioExemplo cenario =
                TerrainCostCalculator.criarCenarioMontanha();

            assertThat(cenario.elevacao()).isNotNull();
            assertThat(cenario.terrenos()).isNotNull();
            assertThat(cenario.elevacao().length).isEqualTo(30);
            assertThat(cenario.terrenos().length).isEqualTo(30);
            assertThat(cenario.origem()).isBetween(0, 899);
            assertThat(cenario.destino()).isBetween(0, 899);
        }

        @Test
        @DisplayName("Deve ter rio intransponível com pontes")
        void shouldHaveImpassableRiverWithBridges() {
            TerrainCostCalculator.CenarioExemplo cenario =
                TerrainCostCalculator.criarCenarioMontanha();

            // Verificar que o meio (coluna 15) tem pontes nas posições definidas
            int mid = 15;
            assertThat(cenario.terrenos()[5][mid]).isEqualTo(TerrainCostCalculator.TipoTerreno.TRILHA_BOA); // Ponte
            assertThat(cenario.terrenos()[15][mid]).isEqualTo(TerrainCostCalculator.TipoTerreno.TRILHA_BOA); // Ponte
            assertThat(cenario.terrenos()[25][mid]).isEqualTo(TerrainCostCalculator.TipoTerreno.TRILHA_BOA); // Ponte

            // Verificar que há água funda no rio (pelo menos algumas posições)
            int aguaCount = 0;
            for (int y = 0; y < 30; y++) {
                if (cenario.terrenos()[y][mid] == TerrainCostCalculator.TipoTerreno.AGUA_FUNDO) {
                    aguaCount++;
                }
            }
            assertThat(aguaCount).isGreaterThan(20); // Maioria do rio é água funda
        }
    }

    @Nested
    @DisplayName("Geração sintética")
    class SyntheticGenerationTests {

        @Test
        @DisplayName("Deve gerar elevação sintética")
        void shouldGenerateSyntheticElevation() {
            double[][] elev = TerrainCostCalculator.gerarElevacaoSintetica(20, 20, 0.1, 42);

            assertThat(elev.length).isEqualTo(20);
            assertThat(elev[0].length).isEqualTo(20);
            // Verificar que há variação
            double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
            for (double[] row : elev) {
                for (double v : row) {
                    min = Math.min(min, v);
                    max = Math.max(max, v);
                }
            }
            // Variação depende dos parâmetros; com scale=0.1 e seed=42 temos ~20m
            assertThat(max - min).isGreaterThan(10); // Pelo menos 10m de variação
        }

        @Test
        @DisplayName("Deve gerar terreno aleatório com tipos variados")
        void shouldGenerateRandomTerrainWithVariedTypes() {
            TerrainCostCalculator.TipoTerreno[][] terr =
                TerrainCostCalculator.gerarTerrenoAleatorio(10, 10, 42);

            assertThat(terr.length).isEqualTo(10);
            assertThat(terr[0].length).isEqualTo(10);

            // Verificar que cantos são transponíveis
            assertThat(terr[0][0].isTransponivel()).isTrue();
            assertThat(terr[9][9].isTransponivel()).isTrue();
        }
    }
}