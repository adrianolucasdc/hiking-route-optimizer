package com.example.hiking_route.util;

import com.example.hiking_route.model.Grafo;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import javax.imageio.ImageIO;

/**
 * Utilitário para visualização de grafos e caminhos.
 * Suporta renderização ASCII/ANSI no terminal e exportação para PNG.
 */
public class GrafoVisualizer {

    // Códigos ANSI para cores
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_YELLOW = "\u001B[43m";

    /**
     * Renderiza a matriz de adjacência com o caminho destacado no terminal.
     *
     * @param grafo        Grafo a ser visualizado
     * @param caminho      Lista de vértices do caminho (pode ser null)
     * @param origem       Vértice de origem
     * @param destino      Vértice de destino
     * @param usarCores    Se true, usa códigos ANSI coloridos
     * @return String formatada da matriz
     */
    public static String renderMatrizComCaminho(Grafo grafo, List<Integer> caminho,
                                                 int origem, int destino, boolean usarCores) {
        if (grafo == null || grafo.getMatrizAdjacencia() == null) {
            return "Grafo nulo ou inválido";
        }

        double[][] matriz = grafo.getMatrizAdjacencia();
        int n = grafo.getNumeroVertices();

        if (n == 0) {
            return "Matriz vazia (0x0)";
        }

        Set<Integer> caminhoSet = new HashSet<>();
        if (caminho != null) {
            caminhoSet.addAll(caminho);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        int larguraBox = Math.max(n * 8 + 2, 20); // Minimum width to fit title
        sb.append(usarCores ? BOLD : "").append("╔").append("═".repeat(larguraBox)).append("╗").append(usarCores ? RESET : "").append("\n");
        int paddingTitle = Math.max(0, larguraBox - "MATRIZ DE ADJACÊNCIA".length() - 2);
        sb.append(usarCores ? BOLD : "").append("║ ").append("MATRIZ DE ADJACÊNCIA").append(" ".repeat(paddingTitle)).append(" ║").append(usarCores ? RESET : "").append("\n");
        sb.append(usarCores ? BOLD : "").append("╠").append("═".repeat(larguraBox)).append("╣").append(usarCores ? RESET : "").append("\n");

        // Cabeçalho das colunas
        sb.append(usarCores ? CYAN : "").append("║   ");
        for (int j = 0; j < n; j++) {
            String colLabel = getVertexLabel(j);
            sb.append(String.format("%-7s", colLabel));
        }
        sb.append(usarCores ? RESET : "").append(" ║\n");

        sb.append(usarCores ? BOLD : "").append("╠").append("═".repeat(n * 8 + 2)).append("╣").append(usarCores ? RESET : "").append("\n");

        // Linhas da matriz
        for (int i = 0; i < n; i++) {
            String rowLabel = getVertexLabel(i);

            // Determinar cor da linha
            String rowColor = "";
            String rowReset = "";
            if (usarCores) {
                if (i == origem && i == destino) {
                    rowColor = BG_YELLOW + WHITE + BOLD;
                } else if (i == origem) {
                    rowColor = BG_GREEN + WHITE + BOLD;
                } else if (i == destino) {
                    rowColor = BG_RED + WHITE + BOLD;
                } else if (caminhoSet.contains(i)) {
                    rowColor = BRIGHT_GREEN;
                }
                rowReset = RESET;
            }

            sb.append(rowColor).append(String.format("║ %s ", rowLabel)).append(rowReset);

            for (int j = 0; j < n; j++) {
                double peso = matriz[i][j];
                String cellStr;
                String cellColor = "";
                String cellReset = "";

                if (usarCores) {
                    boolean isArestaCaminho = caminho != null && caminhoSet.contains(i) && caminhoSet.contains(j) &&
                                             caminho.indexOf(i) >= 0 && caminho.indexOf(j) == caminho.indexOf(i) + 1;
                    boolean isOrigemDestino = (i == origem && j == destino) || (i == destino && j == origem);

                    if (isArestaCaminho) {
                        cellColor = BG_GREEN + WHITE + BOLD;
                    } else if (isOrigemDestino) {
                        cellColor = BG_YELLOW + WHITE + BOLD;
                    } else if (peso > 0) {
                        if (caminhoSet.contains(i) || caminhoSet.contains(j)) {
                            cellColor = YELLOW;
                        } else {
                            cellColor = WHITE;
                        }
                    } else {
                        cellColor = CYAN; // Sem aresta
                    }
                    cellReset = RESET;
                }

                if (peso == 0 && i != j) {
                    cellStr = cellColor + "   ·   " + cellReset; // Sem conexão
                } else if (peso == Double.POSITIVE_INFINITY) {
                    cellStr = cellColor + "  ∞   " + cellReset;
                } else if (peso == (long) peso) {
                    cellStr = cellColor + String.format("%5d  ", (long) peso) + cellReset;
                } else {
                    cellStr = cellColor + String.format("%5.1f ", peso) + cellReset;
                }
                sb.append(cellStr);
            }
            sb.append(usarCores ? CYAN : "").append(" ║").append(usarCores ? RESET : "").append("\n");
        }

        sb.append(usarCores ? BOLD : "").append("╚").append("═".repeat(n * 8 + 2)).append("╝").append(usarCores ? RESET : "").append("\n");

        // Legenda
        if (usarCores) {
            sb.append("\n").append(BOLD).append("Legenda:").append(RESET).append("\n");
            sb.append(BG_GREEN).append(WHITE).append("  ORIGEM  ").append(RESET).append("  Vértice de partida\n");
            sb.append(BG_RED).append(WHITE).append("  DESTINO ").append(RESET).append("  Vértice de chegada\n");
            sb.append(BG_YELLOW).append(WHITE).append("  ORIG=DEST ").append(RESET).append("  Mesmo vértice\n");
            sb.append(BG_GREEN).append(WHITE).append("  CAMINHO ").append(RESET).append("  Aresta no caminho ótimo\n");
            sb.append(YELLOW).append("  AMARELO ").append(RESET).append("  Vértice no caminho\n");
            sb.append(CYAN).append("    ·    ").append(RESET).append("  Sem aresta (peso 0)\n");
        }

        // Info do caminho
        if (caminho != null && !caminho.isEmpty()) {
            sb.append("\n").append(BOLD).append("Caminho: ").append(RESET);
            for (int i = 0; i < caminho.size(); i++) {
                sb.append(usarCores ? BRIGHT_GREEN : "").append(getVertexLabel(caminho.get(i))).append(usarCores ? RESET : "");
                if (i < caminho.size() - 1) {
                    sb.append(usarCores ? CYAN : "").append(" → ").append(usarCores ? RESET : "");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Renderiza visualização compacta para matrizes maiores (>15 vértices).
     */
    public static String renderCompacto(Grafo grafo, List<Integer> caminho, int origem, int destino) {
        if (grafo == null || grafo.getMatrizAdjacencia() == null) {
            return "Grafo nulo";
        }

        double[][] matriz = grafo.getMatrizAdjacencia();
        int n = grafo.getNumeroVertices();
        Set<Integer> caminhoSet = caminho != null ? new HashSet<>(caminho) : new HashSet<>();

        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Grafo ").append(n).append("x").append(n).append(" ===\n");

        // Mostrar apenas arestas do caminho se houver
        if (caminho != null && !caminho.isEmpty()) {
            sb.append("Caminho: ");
            for (int i = 0; i < caminho.size(); i++) {
                sb.append(getVertexLabel(caminho.get(i)));
                if (i < caminho.size() - 1) sb.append(" → ");
            }
            sb.append("\n\nArestas do caminho:\n");

            for (int i = 0; i < caminho.size() - 1; i++) {
                int u = caminho.get(i);
                int v = caminho.get(i + 1);
                double peso = matriz[u][v];
                sb.append(String.format("  %s → %s (peso: %s)\n",
                    getVertexLabel(u), getVertexLabel(v),
                    peso == (long) peso ? String.valueOf((long) peso) : String.valueOf(peso)));
            }
        } else {
            sb.append("Nenhum caminho encontrado.\n");
        }

        // Contar arestas
        int totalArestas = 0;
        double somaPesos = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matriz[i][j] > 0) {
                    totalArestas++;
                    somaPesos += matriz[i][j];
                }
            }
        }
        sb.append(String.format("\nEstatísticas: %d vértices, %d arestas, peso médio: %.2f\n",
            n, totalArestas, totalArestas > 0 ? somaPesos / totalArestas : 0));

        return sb.toString();
    }

    /**
     * Exporta visualização do grafo para arquivo PNG.
     *
     * @param grafo           Grafo a ser exportado
     * @param caminho         Caminho a destacar (opcional)
     * @param origem          Vértice de origem
     * @param destino         Vértice de destino
     * @param arquivoSaida    Caminho do arquivo PNG de saída
     * @param largura         Largura da imagem
     * @param altura          Altura da imagem
     * @throws IOException    Se houver erro ao salvar o arquivo
     */
    public static void exportarPNG(Grafo grafo, List<Integer> caminho, int origem, int destino,
                                    String arquivoSaida, int largura, int altura) throws IOException {
        if (grafo == null || grafo.getMatrizAdjacencia() == null) {
            throw new IllegalArgumentException("Grafo inválido");
        }

        double[][] matriz = grafo.getMatrizAdjacencia();
        int n = grafo.getNumeroVertices();
        Set<Integer> caminhoSet = caminho != null ? new HashSet<>(caminho) : new HashSet<>();

        BufferedImage image = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Configurações de qualidade
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fundo branco
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, largura, altura);

        // Calcular posições dos vértices (layout circular)
        int centroX = largura / 2;
        int centroY = altura / 2;
        int raio = Math.min(largura, altura) / 3;

        Point[] posicoes = new Point[n];
        for (int i = 0; i < n; i++) {
            double angulo = 2 * Math.PI * i / n - Math.PI / 2; // Começa no topo
            int x = centroX + (int) (raio * Math.cos(angulo));
            int y = centroY + (int) (raio * Math.sin(angulo));
            posicoes[i] = new Point(x, y);
        }

        // Desenhar arestas
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double peso = matriz[i][j];
                if (peso > 0) {
                    boolean isCaminho = caminhoSet.contains(i) && caminhoSet.contains(j) &&
                                       caminho.indexOf(i) >= 0 && caminho.indexOf(j) == caminho.indexOf(i) + 1;

                    if (isCaminho) {
                        g2d.setColor(new Color(0, 150, 0)); // Verde para caminho
                        g2d.setStroke(new BasicStroke(3.0f));
                    } else {
                        g2d.setColor(new Color(180, 180, 180)); // Cinza claro para outras
                        g2d.setStroke(new BasicStroke(1.0f));
                    }

                    drawArrow(g2d, posicoes[i], posicoes[j], 20);

                    // Label do peso
                    if (isCaminho || n <= 10) {
                        int midX = (posicoes[i].x + posicoes[j].x) / 2;
                        int midY = (posicoes[i].y + posicoes[j].y) / 2;
                        g2d.setColor(isCaminho ? new Color(0, 100, 0) : Color.DARK_GRAY);
                        g2d.setFont(new Font("SansSerif", Font.BOLD, 10));
                        String pesoStr = peso == (long) peso ? String.valueOf((long) peso) : String.format("%.1f", peso);
                        g2d.drawString(pesoStr, midX + 5, midY - 5);
                    }
                }
            }
        }

        // Desenhar vértices
        for (int i = 0; i < n; i++) {
            Point p = posicoes[i];
            int raioVertice = Math.max(20, Math.min(35, 400 / n));

            // Cor do vértice
            if (i == origem && i == destino) {
                g2d.setColor(new Color(255, 200, 0)); // Amarelo
            } else if (i == origem) {
                g2d.setColor(new Color(0, 180, 0)); // Verde
            } else if (i == destino) {
                g2d.setColor(new Color(220, 50, 50)); // Vermelho
            } else if (caminhoSet.contains(i)) {
                g2d.setColor(new Color(100, 200, 100)); // Verde claro
            } else {
                g2d.setColor(new Color(100, 150, 220)); // Azul
            }

            g2d.fillOval(p.x - raioVertice, p.y - raioVertice, 2 * raioVertice, 2 * raioVertice);

            // Borda
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawOval(p.x - raioVertice, p.y - raioVertice, 2 * raioVertice, 2 * raioVertice);

            // Label do vértice
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, Math.max(12, 240 / n)));
            String label = getVertexLabel(i);
            FontMetrics fm = g2d.getFontMetrics();
            int labelW = fm.stringWidth(label);
            int labelH = fm.getHeight();
            g2d.drawString(label, p.x - labelW / 2, p.y + labelH / 4);
        }

        // Título
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
        String titulo = "Grafo de Rotas - " + n + " vértices";
        if (caminho != null && !caminho.isEmpty()) {
            titulo += " | Caminho: " + caminho.size() + " vértices";
        }
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(titulo, (largura - fm.stringWidth(titulo)) / 2, 30);

        g2d.dispose();
        ImageIO.write(image, "PNG", new File(arquivoSaida));
    }

    /**
     * Desenha uma seta entre dois pontos.
     */
    private static void drawArrow(Graphics2D g2d, Point p1, Point p2, int arrowSize) {
        double dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        double angle = Math.atan2(dy, dx);
        double len = Math.sqrt(dx * dx + dy * dy);

        // Ajustar início e fim para não sobrepor os círculos dos vértices
        int offset = 20;
        int startX = p1.x + (int) (offset * Math.cos(angle));
        int startY = p1.y + (int) (offset * Math.sin(angle));
        int endX = p2.x - (int) (offset * Math.cos(angle));
        int endY = p2.y - (int) (offset * Math.sin(angle));

        g2d.drawLine(startX, startY, endX, endY);

        // Cabeça da seta
        int arrowX1 = endX - (int) (arrowSize * Math.cos(angle - Math.PI / 6));
        int arrowY1 = endY - (int) (arrowSize * Math.sin(angle - Math.PI / 6));
        int arrowX2 = endX - (int) (arrowSize * Math.cos(angle + Math.PI / 6));
        int arrowY2 = endY - (int) (arrowSize * Math.sin(angle + Math.PI / 6));

        g2d.fillPolygon(
            new int[]{endX, arrowX1, arrowX2},
            new int[]{endY, arrowY1, arrowY2},
            3
        );
    }

    /**
     * Converte índice do vértice para label legível (A, B, C... Z, AA, AB...).
     */
    public static String getVertexLabel(int index) {
        if (index < 0) return "?";
        if (index < 26) {
            return String.valueOf((char) ('A' + index));
        }
        // Para > 26: AA, AB, AC...
        int first = index / 26 - 1;
        int second = index % 26;
        return String.valueOf((char) ('A' + first)) + (char) ('A' + second);
    }

    /**
     * Imprime a matriz no console com cores ANSI.
     */
    public static void printMatriz(Grafo grafo, List<Integer> caminho, int origem, int destino) {
        System.out.println(renderMatrizComCaminho(grafo, caminho, origem, destino, true));
    }

    /**
     * Imprime versão compacta (sem cores) para logs/arquivos.
     */
    public static void printCompacto(Grafo grafo, List<Integer> caminho, int origem, int destino) {
        System.out.println(renderCompacto(grafo, caminho, origem, destino));
    }
}