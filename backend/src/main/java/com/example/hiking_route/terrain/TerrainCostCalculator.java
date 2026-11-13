package com.example.hiking_route.terrain;

import com.example.hiking_route.model.Grafo;

import java.util.*;

/**
 * Calculadora de custos de movimento baseada em terreno realista.
 * Converte matriz de elevação + tipo de terreno em grafo ponderado para Dijkstra/A*.
 *
 * Modela:
 * - Custo base por tipo de terreno (trilha, terra, rocha, areia, água, etc.)
 * - Penalidade por inclinação (subida custa mais que descida)
 * - Obstáculos intransponíveis
 * - Movimento ortogonal vs diagonal (custo ×√2)
 */
public class TerrainCostCalculator {

    // Tipos de terreno com custos base (por metro)
    public enum TipoTerreno {
        TRILHA_BOA(1.0, "Trilha bem mantida"),
        TRILHA_REGULAR(1.5, "Trilha irregular"),
        TERRA_BATIDA(2.0, "Terra batida"),
        GRAMA(2.5, "Grama/campo aberto"),
        AREIA(4.0, "Areia solta"),
        CASCALHO(3.0, "Cascalho/pedregulho"),
        ROCHA(5.0, "Rocha exposta"),
        LAMA(6.0, "Lama/terreno encharcado"),
        NEVE(5.0, "Neve compactada"),
        GELO(8.0, "Gelo/negelo"),
        ARVORE_DENSA(10.0, "Mata densa"),
        ARVORE_ESPARSA(4.0, "Mata esparsa"),
        AGUA_RASO(10.0, "Água rasa (vadear)"),
        AGUA_FUNDO(Double.POSITIVE_INFINITY, "Água funda (intransponível)"),
        PAREDE_ROCHA(Double.POSITIVE_INFINITY, "Parede de rocha (intransponível)"),
        OBSTACULO(Double.POSITIVE_INFINITY, "Obstáculo artificial");

        private final double custoBase;
        private final String descricao;

        TipoTerreno(double custoBase, String descricao) {
            this.custoBase = custoBase;
            this.descricao = descricao;
        }

        public double getCustoBase() { return custoBase; }
        public String getDescricao() { return descricao; }
        public boolean isTransponivel() { return custoBase < Double.POSITIVE_INFINITY; }
    }

    // Configurações de movimento
    private boolean permitirDiagonais;
    private final double fatorDiagonal; // √2 ≈ 1.414
    private double maxInclinacaoSubida; // graus
    private double maxInclinacaoDescida; // graus
    private double penalidadeSubidaPorGrau;
    private double bonusDescidaPorGrau; // negativo = ganho
    private double tamanhoCelulaMetros; // resolução da grade

    // Tabelas de custo base por tipo
    private final Map<TipoTerreno, Double> custosPersonalizados;

    public TerrainCostCalculator() {
        this(true); // diagonais por padrão
    }

    public TerrainCostCalculator(boolean permitirDiagonais) {
        this.permitirDiagonais = permitirDiagonais;
        this.fatorDiagonal = Math.sqrt(2);
        this.maxInclinacaoSubida = 45.0; // graus
        this.maxInclinacaoDescida = 60.0; // graus
        this.penalidadeSubidaPorGrau = 0.1; // +10% por grau
        this.bonusDescidaPorGrau = -0.05; // -5% por grau (ganho de energia)
        this.tamanhoCelulaMetros = 10.0; // cada célula = 10m

        this.custosPersonalizados = new EnumMap<>(TipoTerreno.class);
        // Usar valores padrão do enum
    }

    // ==================== BUILDER PARA CONFIGURAÇÃO AVANÇADA ====================

    public static class Builder {
        private boolean permitirDiagonais = true;
        private double maxInclinacaoSubida = 45.0;
        private double maxInclinacaoDescida = 60.0;
        private double penalidadeSubidaPorGrau = 0.1;
        private double bonusDescidaPorGrau = -0.05;
        private double tamanhoCelulaMetros = 10.0;
        private Map<TipoTerreno, Double> custosPersonalizados = new EnumMap<>(TipoTerreno.class);

        public Builder permitirDiagonais(boolean v) { this.permitirDiagonais = v; return this; }
        public Builder maxInclinacaoSubida(double graus) { this.maxInclinacaoSubida = graus; return this; }
        public Builder maxInclinacaoDescida(double graus) { this.maxInclinacaoDescida = graus; return this; }
        public Builder penalidadeSubidaPorGrau(double fator) { this.penalidadeSubidaPorGrau = fator; return this; }
        public Builder bonusDescidaPorGrau(double fator) { this.bonusDescidaPorGrau = fator; return this; }
        public Builder tamanhoCelulaMetros(double metros) { this.tamanhoCelulaMetros = metros; return this; }
        public Builder custoPersonalizado(TipoTerreno tipo, double custo) {
            this.custosPersonalizados.put(tipo, custo); return this;
        }

        public TerrainCostCalculator build() {
            TerrainCostCalculator calc = new TerrainCostCalculator(permitirDiagonais);
            calc.maxInclinacaoSubida = this.maxInclinacaoSubida;
            calc.maxInclinacaoDescida = this.maxInclinacaoDescida;
            calc.penalidadeSubidaPorGrau = this.penalidadeSubidaPorGrau;
            calc.bonusDescidaPorGrau = this.bonusDescidaPorGrau;
            calc.tamanhoCelulaMetros = this.tamanhoCelulaMetros;
            calc.custosPersonalizados.putAll(this.custosPersonalizados);
            return calc;
        }
    }

    public static Builder builder() { return new Builder(); }

    // ==================== CÁLCULO PRINCIPAL ====================

    /**
     * Gera grafo de custos a partir de matrizes de elevação e tipo de terreno.
     *
     * @param elevacao     Matriz [linha][coluna] com altitudes em metros
     * @param terrenos     Matriz [linha][coluna] com tipos de terreno (null = TERRA_BATIDA)
     * @return Grafo ponderado pronto para Dijkstra/A*
     */
    public Grafo gerarGrafo(double[][] elevacao, TipoTerreno[][] terrenos) {
        int linhas = elevacao.length;
        int colunas = elevacao[0].length;
        int totalVertices = linhas * colunas;

        double[][] matrizAdj = new double[totalVertices][totalVertices];

        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {
                int u = y * colunas + x;

                // Verificar se célula de origem é transponível
                if (!isTransponivel(y, x, terrenos)) continue;

                // Vizinhos: 4 ortogonais + 4 diagonais (se permitido)
                int[][] direcoes = getDirecoes();

                for (int[] dir : direcoes) {
                    int ny = y + dir[0];
                    int nx = x + dir[1];

                    if (ny < 0 || ny >= linhas || nx < 0 || nx >= colunas) continue;
                    if (!isTransponivel(ny, nx, terrenos)) continue;

                    int v = ny * colunas + nx;
                    double custo = calcularCustoMovimento(y, x, ny, nx, elevacao, terrenos);

                    if (custo < Double.POSITIVE_INFINITY) {
                        matrizAdj[u][v] = custo;
                    }
                }
            }
        }

        return new Grafo(matrizAdj);
    }

    /**
     * Verifica se uma célula é transponível.
     */
    private boolean isTransponivel(int y, int x, TipoTerreno[][] terrenos) {
        if (terrenos == null || terrenos[y] == null || terrenos[y][x] == null) {
            return true; // Default: transponível
        }
        return terrenos[y][x].isTransponivel();
    }

    /**
     * Retorna direções de movimento baseado na configuração.
     */
    private int[][] getDirecoes() {
        if (permitirDiagonais) {
            return new int[][]{
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},     // Ortogonais
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}    // Diagonais
            };
        } else {
            return new int[][]{
                {-1, 0}, {1, 0}, {0, -1}, {0, 1}      // Apenas ortogonais
            };
        }
    }

    /**
     * Calcula custo de movimento entre duas células adjacentes.
     */
    private double calcularCustoMovimento(int y1, int x1, int y2, int x2,
                                           double[][] elevacao, TipoTerreno[][] terrenos) {

        // 1. Custo base = média dos tipos de terreno das duas células
        TipoTerreno t1 = getTerreno(y1, x1, terrenos);
        TipoTerreno t2 = getTerreno(y2, x2, terrenos);
        double custoBase = (getCustoTerreno(t1) + getCustoTerreno(t2)) / 2.0;

        if (custoBase >= Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }

        // 2. Fator de distância (diagonal = √2)
        boolean diagonal = (Math.abs(y2 - y1) == 1 && Math.abs(x2 - x1) == 1);
        double fatorDistancia = diagonal ? fatorDiagonal : 1.0;

        // 3. Diferença de elevação
        double elev1 = elevacao[y1][x1];
        double elev2 = elevacao[y2][x2];
        double deltaElev = elev2 - elev1; // positivo = subida
        double distanciaHorizontal = tamanhoCelulaMetros * fatorDistancia;
        double inclinacaoGraus = Math.toDegrees(Math.atan2(Math.abs(deltaElev), distanciaHorizontal));

        // 4. Verificar limites de inclinação
        if (deltaElev > 0) { // Subida
            if (inclinacaoGraus > maxInclinacaoSubida) {
                return Double.POSITIVE_INFINITY; // Muito íngreme
            }
            // Penalidade progressiva por inclinação
            double fatorInclinacao = 1.0 + (inclinacaoGraus * penalidadeSubidaPorGrau);
            custoBase *= fatorInclinacao;
        } else if (deltaElev < 0) { // Descida
            if (inclinacaoGraus > maxInclinacaoDescida) {
                return Double.POSITIVE_INFINITY; // Muito íngreme (perigo)
            }
            // Bonus por descida (recuperação de energia, mas limitado)
            double fatorInclinacao = 1.0 + (inclinacaoGraus * bonusDescidaPorGrau);
            custoBase *= Math.max(0.3, fatorInclinacao); // Mínimo 30% do custo base
        }
        // deltaElev == 0: plano, sem modificação

        // 5. Custo final = custo base × distância
        return custoBase * distanciaHorizontal;
    }

    private TipoTerreno getTerreno(int y, int x, TipoTerreno[][] terrenos) {
        if (terrenos == null || terrenos[y] == null || terrenos[y][x] == null) {
            return TipoTerreno.TERRA_BATIDA;
        }
        return terrenos[y][x];
    }

    private double getCustoTerreno(TipoTerreno tipo) {
        return custosPersonalizados.getOrDefault(tipo, tipo.getCustoBase());
    }

    // ==================== UTILITÁRIOS ====================

    /**
     * Converte coordenadas (linha, coluna) para índice do vértice.
     */
    public static int toIndex(int linha, int coluna, int numColunas) {
        return linha * numColunas + coluna;
    }

    /**
     * Converte índice do vértice para coordenadas (linha, coluna).
     */
    public static int[] toCoords(int index, int numColunas) {
        return new int[]{index / numColunas, index % numColunas};
    }

    /**
     * Gera matriz de elevação sintética para testes (ruído Perlin simples).
     */
    public static double[][] gerarElevacaoSintetica(int linhas, int colunas, double escala, long seed) {
        Random rand = new Random(seed);
        double[][] elev = new double[linhas][colunas];

        // Gerar alguns "picos" e interpolar
        int numPicos = Math.max(1, (linhas * colunas) / 500);
        double[][] picos = new double[numPicos][3]; // x, y, altura

        for (int i = 0; i < numPicos; i++) {
            picos[i][0] = rand.nextDouble() * colunas;
            picos[i][1] = rand.nextDouble() * linhas;
            picos[i][2] = 100 + rand.nextDouble() * 500; // 100-600m
        }

        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {
                double soma = 0;
                double pesoTotal = 0;

                for (double[] pico : picos) {
                    double dx = x - pico[0];
                    double dy = y - pico[1];
                    double dist = Math.hypot(dx, dy) * escala;
                    double peso = 1.0 / (1.0 + dist * dist); // Inverse distance weighting
                    soma += pico[2] * peso;
                    pesoTotal += peso;
                }

                // Adicionar ruído local
                double ruido = (rand.nextDouble() - 0.5) * 20; // ±10m
                elev[y][x] = (pesoTotal > 0 ? soma / pesoTotal : 100) + ruido;
            }
        }

        return elev;
    }

    /**
     * Gera matriz de terreno aleatória para testes.
     */
    public static TipoTerreno[][] gerarTerrenoAleatorio(int linhas, int colunas, long seed) {
        Random rand = new Random(seed);
        TipoTerreno[][] terr = new TipoTerreno[linhas][colunas];

        TipoTerreno[] tiposComuns = {
            TipoTerreno.TRILHA_BOA, TipoTerreno.TERRA_BATIDA, TipoTerreno.GRAMA,
            TipoTerreno.CASCALHO, TipoTerreno.ARVORE_ESPARSA
        };
        TipoTerreno[] tiposDificeis = {
            TipoTerreno.ROCHA, TipoTerreno.LAMA, TipoTerreno.ARVORE_DENSA
        };
        TipoTerreno[] tiposIntransponiveis = {
            TipoTerreno.AGUA_FUNDO, TipoTerreno.PAREDE_ROCHA
        };

        for (int y = 0; y < linhas; y++) {
            for (int x = 0; x < colunas; x++) {
                double r = rand.nextDouble();
                if (r < 0.70) {
                    terr[y][x] = tiposComuns[rand.nextInt(tiposComuns.length)];
                } else if (r < 0.90) {
                    terr[y][x] = tiposDificeis[rand.nextInt(tiposDificeis.length)];
                } else if (r < 0.95) {
                    terr[y][x] = tiposIntransponiveis[rand.nextInt(tiposIntransponiveis.length)];
                } else {
                    terr[y][x] = TipoTerreno.AGUA_RASO;
                }
            }
        }

        // Garantir que cantos são transponíveis
        terr[0][0] = TipoTerreno.TRILHA_BOA;
        terr[linhas-1][colunas-1] = TipoTerreno.TRILHA_BOA;

        return terr;
    }

    /**
     * Cria cenário de exemplo: trilha de montanha com rio, floresta e pico.
     */
    public static CenarioExemplo criarCenarioMontanha() {
        int size = 30;
        double[][] elev = new double[size][size];
        TipoTerreno[][] terr = new TipoTerreno[size][size];

        // Inicializar tudo como terra batida
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                // Elevação base: vale no centro, montanhas nas bordas
                double cx = x - size/2.0;
                double cy = y - size/2.0;
                double distCentro = Math.hypot(cx, cy);
                elev[y][x] = 1000 + 500 * (1 - Math.exp(-distCentro / 8.0));
                terr[y][x] = TipoTerreno.TERRA_BATIDA;
            }
        }

        // Rio vertical no meio (intransponível exceto em pontes)
        for (int y = 0; y < size; y++) {
            int mid = size / 2;
            elev[y][mid] = 950; // Rio mais baixo
            terr[y][mid] = TipoTerreno.AGUA_FUNDO;
        }
        // Pontes no rio
        terr[5][size/2] = TipoTerreno.TRILHA_BOA;
        terr[15][size/2] = TipoTerreno.TRILHA_BOA;
        terr[25][size/2] = TipoTerreno.TRILHA_BOA;
        elev[5][size/2] = 960;
        elev[15][size/2] = 960;
        elev[25][size/2] = 960;

        // Floresta densa no quadrante superior esquerdo
        for (int y = 0; y < size/2; y++) {
            for (int x = 0; x < size/2; x++) {
                if (Math.random() < 0.3) {
                    terr[y][x] = TipoTerreno.ARVORE_DENSA;
                }
            }
        }

        // Área rochosa no pico (canto superior direito)
        for (int y = 0; y < size/3; y++) {
            for (int x = 2*size/3; x < size; x++) {
                terr[y][x] = TipoTerreno.ROCHA;
                elev[y][x] += 200;
            }
        }

        // Trilha principal serpenteando
        for (int i = 0; i < size; i++) {
            int y = i;
            int x = (int)(size/2 + 5 * Math.sin(i * 0.3));
            if (x >= 0 && x < size) {
                terr[y][x] = TipoTerreno.TRILHA_BOA;
            }
        }

        return new CenarioExemplo(elev, terr,
            toIndex(size-1, 0, size),    // Início: canto inferior esquerdo
            toIndex(0, size-1, size)     // Fim: canto superior direito
        );
    }

    /**
     * Record para cenário de exemplo completo.
     */
    public record CenarioExemplo(
        double[][] elevacao,
        TipoTerreno[][] terrenos,
        int origem,
        int destino
    ) {}
}