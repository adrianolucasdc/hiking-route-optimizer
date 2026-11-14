/**
 * Internationalization system for Hiking Route Optimizer
 * Default language: English (en)
 * Supported: English (en), Portuguese (pt)
 */

export const SUPPORTED_LANGUAGES = [
  { code: 'en', name: 'EN', nativeName: 'English' },
  { code: 'pt', name: 'PT', nativeName: 'Português' },
];

export const DEFAULT_LANGUAGE = 'en';

// Translation dictionary
export const translations = {
  en: {
    // Header
    appTitle: 'Hiking Route Optimizer',
    themeToggleLight: 'Light mode',
    themeToggleDark: 'Dark mode',
    languageSelector: 'Language',
    github: 'GitHub Repository',

    // Algorithm Selector
    searchAlgorithm: 'Search Algorithm',
    algorithmDijkstra: 'Dijkstra',
    algorithmAStar: 'A* (Manhattan)',
    algorithmBFS: 'BFS',
    algorithmComplexity: {
      dijkstra: 'O((V+E)log V) — Guaranteed optimal path in weighted graphs',
      astar: 'O((V+E)log V) — Heuristic-guided, faster on grids',
      bfs: 'O(V+E) — Fewest hops, ignores weights',
    },
    heuristicLabel: 'A* Heuristic',
    heuristicManhattan: 'Manhattan (4-dir)',
    heuristicEuclidean: 'Euclidean (any dir)',
    heuristicChebyshev: 'Chebyshev (8-dir)',

    // Matrix Input
    adjacencyMatrix: 'Adjacency Matrix',
    matrixSize: '({size}×{size})',
    matrixHelpTitle: 'Matrix Rules',
    matrixHelpItems: [
      'Main diagonal (origin = destination) is always 0',
      'Leave empty or 0 for no edge',
      'Positive values = edge weight/cost',
      'Directed graph: fill both directions if bidirectional',
    ],
    vertexCount: 'Vertex Count (2–26)',
    origin: 'Origin',
    destination: 'Destination',
    selectPlaceholder: 'Select...',

    // Terrain Config
    terrainConfig: 'Advanced Terrain Config',
    terrainConfigDesc: 'Elevation, 15 terrain types, slope penalties, diagonals',
    elevationTab: 'Elevation (m)',
    terrainTab: 'Terrain Types',
    paramsTab: 'Parameters',
    loadMountainScenario: 'Load Mountain Scenario',
    clearTerrain: 'Clear',
    elevationHelp: 'Relative elevation in meters. Positive = uphill, negative = downhill.',
    terrainLegend: 'Legend',
    cellSize: 'Cell Size (m)',
    maxUphill: 'Max Uphill (°)',
    maxDownhill: 'Max Downhill (°)',
    uphillPenalty: 'Uphill Penalty (%/°)',
    downhillBonus: 'Downhill Bonus (%/°)',
    minDownhillCost: 'Min Downhill Cost (%)',
    minDownhillCostHelp: 'Fixed at 30% (safety factor)',
    allowDiagonals: 'Allow diagonal movement (×√2 ≈ 1.414)',
    formulasTitle: 'Applied Formulas',
    formulas: [
      'Uphill: cost × (1 + slope° × {uphillPenalty})',
      'Downhill: cost × max(0.3, 1 + slope° × {downhillBonus})',
      'Diagonal: distance × √2 × terrain_cost',
    ],

    // Actions
    calculateRoute: 'Calculate Optimal Route',
    calculating: 'Calculating...',
    loadExample: 'Load Example',
    clear: 'Clear',

    // Results
    resultsTitle: 'Search Results',
    summaryTab: 'Summary',
    detailsTab: 'Details',
    asciiTab: 'ASCII/Terminal',
    graphTab: 'Graph',
    graphUnavailable: 'Graph unavailable',
    graphUnavailableDesc: 'Python visualization service may be offline',
    exportJson: 'Export JSON',
    exportCsv: 'Export CSV',
    downloadPng: 'Download PNG',

    // Status Cards
    minPathTitle: 'Min Path',
    maxPathTitle: 'Max Path',
    verticesTitle: 'Vertices',
    statusTitle: 'Status',
    found: 'Found',
    notFound: 'Not found',
    infinite: 'Infinite (positive cycle)',
    error: 'Error',
    partial: 'Partial (min only)',
    success: 'Success (both paths)',
    cost: 'Cost: {cost}',
    totalCost: 'Total Cost',
    verticesCount: '{count} vertices • {edges} edges',

    // Toasts
    routeCalculated: 'Route calculated successfully!',
    graphFailed: 'Route calculated, but failed to generate graph',
    exampleLoaded: 'Example loaded: 5-vertex graph (A–B–C–D–E)',
    mountainLoaded: 'Mountain scenario loaded!',
    selectOriginDest: 'Select origin and destination',
    originDestDifferent: 'Origin and destination must be different',
    isolatedOrigin: 'Isolated origin — no outgoing or incoming edges',
    exportJsonSuccess: 'Result exported as JSON',
    exportCsvSuccess: 'Matrix exported as CSV',

    // Footer
    footerCopyright: '© 2025 Hiking Route Optimizer. Academic open-source project.',
    footerGithub: 'GitHub',
    footerApiDocs: 'API Docs',
    footerLicense: 'MIT License',
    footerStack: 'Spring Boot 3.2.5 • React 19 • Python 3.11 • Docker • 122 tests',
  },

  pt: {
    // Header
    appTitle: 'Otimizador de Rotas de Trilha',
    themeToggleLight: 'Modo claro',
    themeToggleDark: 'Modo escuro',
    languageSelector: 'Idioma',
    github: 'Repositório no GitHub',

    // Algorithm Selector
    searchAlgorithm: 'Algoritmo de Busca',
    algorithmDijkstra: 'Dijkstra',
    algorithmAStar: 'A* (Manhattan)',
    algorithmBFS: 'BFS',
    algorithmComplexity: {
      dijkstra: 'O((V+E)log V) — Caminho ótimo garantido em grafos ponderados',
      astar: 'O((V+E)log V) — Guiado por heurística, mais rápido em grades',
      bfs: 'O(V+E) — Menor número de saltos, ignora pesos',
    },
    heuristicLabel: 'Heurística A*',
    heuristicManhattan: 'Manhattan (4-dir)',
    heuristicEuclidean: 'Euclidiana (qualquer dir)',
    heuristicChebyshev: 'Chebyshev (8-dir)',

    // Matrix Input
    adjacencyMatrix: 'Matriz de Adjacência',
    matrixSize: '({size}×{size})',
    matrixHelpTitle: 'Regras da Matriz',
    matrixHelpItems: [
      'Diagonal principal (origem = destino) é sempre 0',
      'Deixe vazio ou 0 para ausência de aresta',
      'Valores positivos = peso/custo da aresta',
      'Grafo direcionado: preencha ambas direções se bidirecional',
    ],
    vertexCount: 'Número de Vértices (2–26)',
    origin: 'Origem',
    destination: 'Destino',
    selectPlaceholder: 'Selecione...',

    // Terrain Config
    terrainConfig: 'Configuração Avançada de Terreno',
    terrainConfigDesc: 'Elevação, 15 tipos de terreno, penalidades de inclinação, diagonais',
    elevationTab: 'Elevação (m)',
    terrainTab: 'Tipos de Terreno',
    paramsTab: 'Parâmetros',
    loadMountainScenario: 'Carregar Cenário Montanha',
    clearTerrain: 'Limpar',
    elevationHelp: 'Elevação relativa em metros. Positivo = subida, negativo = descida.',
    terrainLegend: 'Legenda',
    cellSize: 'Tamanho da Célula (m)',
    maxUphill: 'Máx. Subida (°)',
    maxDownhill: 'Máx. Descida (°)',
    uphillPenalty: 'Penalidade Subida (%/°)',
    downhillBonus: 'Bônus Descida (%/°)',
    minDownhillCost: 'Mín. Custo Descida (%)',
    minDownhillCostHelp: 'Fixo em 30% (fator de segurança)',
    allowDiagonals: 'Permitir movimento diagonal (×√2 ≈ 1.414)',
    formulasTitle: 'Fórmulas Aplicadas',
    formulas: [
      'Subida: custo × (1 + inclinação° × {uphillPenalty})',
      'Descida: custo × max(0.3, 1 + inclinação° × {downhillBonus})',
      'Diagonal: distância × √2 × custo_terreno',
    ],

    // Actions
    calculateRoute: 'Calcular Rota Ótima',
    calculating: 'Calculando...',
    loadExample: 'Carregar Exemplo',
    clear: 'Limpar',

    // Results
    resultsTitle: 'Resultados da Busca',
    summaryTab: 'Resumo',
    detailsTab: 'Detalhes',
    asciiTab: 'ASCII/Terminal',
    graphTab: 'Gráfico',
    graphUnavailable: 'Gráfico indisponível',
    graphUnavailableDesc: 'Serviço de visualização Python pode estar offline',
    exportJson: 'Exportar JSON',
    exportCsv: 'Exportar CSV',
    downloadPng: 'Baixar PNG',

    // Status Cards
    minPathTitle: 'Caminho Mínimo',
    maxPathTitle: 'Caminho Máximo',
    verticesTitle: 'Vértices',
    statusTitle: 'Status',
    found: 'Encontrado',
    notFound: 'Não encontrado',
    infinite: 'Infinito (ciclo positivo)',
    error: 'Erro',
    partial: 'Parcial (apenas mínimo)',
    success: 'Sucesso (ambos os caminhos)',
    cost: 'Custo: {cost}',
    totalCost: 'Custo Total',
    verticesCount: '{count} vértices • {edges} arestas',

    // Toasts
    routeCalculated: 'Rota calculada com sucesso!',
    graphFailed: 'Rota calculada, mas falha ao gerar gráfico',
    exampleLoaded: 'Exemplo carregado: Grafo 5 vértices (A–B–C–D–E)',
    mountainLoaded: 'Cenário de montanha carregado!',
    selectOriginDest: 'Selecione origem e destino',
    originDestDifferent: 'Origem e destino devem ser diferentes',
    isolatedOrigin: 'Origem isolada — sem conexões de saída ou entrada',
    exportJsonSuccess: 'Resultado exportado como JSON',
    exportCsvSuccess: 'Matriz exportada como CSV',

    // Footer
    footerCopyright: '© 2025 Otimizador de Rotas de Trilha. Projeto acadêmico open source.',
    footerGithub: 'GitHub',
    footerApiDocs: 'Documentação API',
    footerLicense: 'Licença MIT',
    footerStack: 'Spring Boot 3.2.5 • React 19 • Python 3.11 • Docker • 122 testes',
  },
};

/**
 * Get translation for current language with fallback to English
 */
export function t(lang, key, params = {}) {
  const langDict = translations[lang] || translations[DEFAULT_LANGUAGE];
  const keys = key.split('.');
  let value = langDict;

  for (const k of keys) {
    if (value && typeof value === 'object' && k in value) {
      value = value[k];
    } else {
      // Fallback to English
      value = translations[DEFAULT_LANGUAGE];
      for (const fk of keys) {
        if (value && typeof value === 'object' && fk in value) {
          value = value[fk];
        } else {
          return key; // Return key as last resort
        }
      }
      break;
    }
  }

  // Interpolate parameters
  if (typeof value === 'string') {
    return value.replace(/\{(\w+)\}/g, (match, paramKey) => params[paramKey] ?? match);
  }

  return value;
}

/**
 * Get algorithm complexity string for current language
 */
export function getAlgorithmComplexity(lang, algorithm) {
  const complexities = translations[lang]?.algorithmComplexity || translations[DEFAULT_LANGUAGE].algorithmComplexity;
  return complexities[algorithm] || complexities.dijkstra;
}

/**
 * Format matrix size string
 */
export function formatMatrixSize(lang, size) {
  return t(lang, 'matrixSize', { size });
}