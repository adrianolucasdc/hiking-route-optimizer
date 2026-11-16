import React, { useState, useCallback, useEffect, useMemo, useRef } from 'react';
import { toast } from 'react-toastify';
import { graphApi, pythonGraphApi } from '../../services/api';
import PathVisualization from '../Visualization/PathVisualization';
import { buildTerrainConfig, TERRAIN_PRESETS } from '../../lib/terrainPresets';
import { useI18n } from '../../lib/I18nContext';

const getPointName = (index) => String.fromCharCode(65 + index);

const ALGORITHM_OPTIONS = [
  { value: 'dijkstra', label: 'Dijkstra', heuristic: null },
  { value: 'astar', label: 'A*', heuristic: 'MANHATTAN' },
  { value: 'bfs', label: 'BFS', heuristic: null },
];

export default function HikingRouteForm() {
  const { t, getAlgorithmComplexity } = useI18n();

  const [algorithm, setAlgorithm] = useState('dijkstra');
  const [heuristic, setHeuristic] = useState('MANHATTAN');
  const [vertexCount, setVertexCount] = useState(5);
  const [vertexLabels, setVertexLabels] = useState([]);
  const [matrix, setMatrix] = useState([]);
  const pendingMatrix = useRef(null); // example loaded together with a vertex-count change
  const [origin, setOrigin] = useState('');
  const [destination, setDestination] = useState('');
  const [showTerrainConfig, setShowTerrainConfig] = useState(false);
  const [terrainPreset, setTerrainPreset] = useState('flat');
  const terrainConfig = useMemo(
    () => buildTerrainConfig(terrainPreset, vertexCount),
    [terrainPreset, vertexCount]
  );
  const [result, setResult] = useState(null);
  const [graphImage, setGraphImage] = useState(null);
  const [loading, setLoading] = useState(false);
  const [showMatrixHelp, setShowMatrixHelp] = useState(false);

  // Initialize labels and matrix when vertexCount changes
  useEffect(() => {
    const newLabels = Array.from({ length: vertexCount }, (_, i) => getPointName(i));
    setVertexLabels(newLabels);

    const newMatrix = Array.from({ length: vertexCount }, (_, i) =>
      Array.from({ length: vertexCount }, (_, j) =>
        i === j ? 0 : ''
      )
    );
    setMatrix(pendingMatrix.current ?? newMatrix);
    pendingMatrix.current = null;

    if (origin && origin.charCodeAt(0) - 65 >= vertexCount) setOrigin('');
    if (destination && destination.charCodeAt(0) - 65 >= vertexCount) setDestination('');
  }, [vertexCount]);

  // Update heuristic when algorithm changes
  useEffect(() => {
    const algo = ALGORITHM_OPTIONS.find(a => a.value === algorithm);
    if (algo?.heuristic) {
      setHeuristic(algo.heuristic);
    }
  }, [algorithm]);

  const handleMatrixChange = useCallback((row, col, value) => {
    const numValue = value === '' ? 0 : parseFloat(value);
    if (isNaN(numValue)) return;

    setMatrix(prev => prev.map((r, i) =>
      i === row ? r.map((v, j) => j === col ? numValue : v) : r
    ));
  }, []);

  const handleOriginChange = useCallback((e) => setOrigin(e.target.value), []);
  const handleDestinationChange = useCallback((e) => setDestination(e.target.value), []);

  const validateForm = useCallback(() => {
    if (!origin || !destination) {
      toast.error(t('selectOriginDest'));
      return false;
    }
    if (origin === destination) {
      toast.error(t('originDestDifferent'));
      return false;
    }
    const originIdx = origin.charCodeAt(0) - 65;
    const destIdx = destination.charCodeAt(0) - 65;

    // Check if path is possible
    const hasOutgoing = matrix[originIdx].some((v, i) => i !== originIdx && v > 0);
    const hasIncoming = matrix.some((row, i) => i !== originIdx && row[originIdx] > 0);

    if (!hasOutgoing && !hasIncoming) {
      toast.warn(t('isolatedOrigin'));
    }
    return true;
  }, [origin, destination, matrix, t]);

  const buildPayload = useCallback(() => {
    const payload = {
      matriz: matrix,
      origem: origin.charCodeAt(0) - 65,
      destino: destination.charCodeAt(0) - 65,
      algoritmo: algorithm,
    };

    if (algorithm === 'astar') {
      payload.heuristica = heuristic;
    }

    return payload;
  }, [matrix, origin, destination, algorithm, heuristic]);

  const handleSubmit = async () => {
    if (!validateForm()) return;

    setLoading(true);
    setResult(null);
    setGraphImage(null);

    try {
      const payload = buildPayload();
      console.log('Sending payload:', payload);

      const response = await graphApi.processarGrafo(payload);
      console.log('Backend response:', response.data);

      setResult(response.data);
      toast.success(t('routeCalculated'));

      // Generate graph visualization
      try {
        const graphPayload = {
          matrix: matrix.map(row => row.map(v => (v === '' || v == null || isNaN(Number(v)) ? 0 : Number(v)))),
          directed: true,
          labels: vertexLabels,
          path: response.data.caminhoMinimo?.caminhoEncontrado ? response.data.caminhoMinimo.caminho : null,
          // Include terrain data if advanced config is enabled
          terrainTypes: showTerrainConfig ? terrainConfig.terrainTypes : null,
          elevations: showTerrainConfig ? terrainConfig.elevation : null,
          cellSizeMeters: terrainConfig.cellSizeMeters,
          allowDiagonals: terrainConfig.allowDiagonals,
          showEdgeWeights: true,
          figsize: [12, 10],
        };
        const graphResponse = await pythonGraphApi.generateGraph(graphPayload);
        const imageUrl = URL.createObjectURL(graphResponse.data);
        setGraphImage(imageUrl);
      } catch (graphError) {
        console.warn('Error generating visualization:', graphError);
        toast.info(t('graphFailed'));
      }
    } catch (error) {
      console.error('Error calculating route:', error);
      const message = error.response?.data?.message || error.message || 'Unknown error';
      toast.error(`Error: ${message}`);
    } finally {
      setLoading(false);
    }
  };

  const handleClear = useCallback(() => {
    setMatrix(Array.from({ length: vertexCount }, (_, i) =>
      Array.from({ length: vertexCount }, (_, j) => i === j ? 0 : '')
    ));
    setOrigin('');
    setDestination('');
    setResult(null);
    setGraphImage(null);
  }, [vertexCount]);

  const handleLoadExample = useCallback(() => {
    // 3x3 grid (A B C / D E F / G H I), 4-neighbour edges, symmetric weights.
    // Dijkstra/A* find A-D-E-H-I (cost 4); BFS finds a 4-edge path of higher cost.
    const side = 3;
    const size = side * side;
    const weights = {
      '0-1': 1, '1-2': 9, '0-3': 1, '1-4': 8, '2-5': 1,
      '3-4': 1, '4-5': 9, '3-6': 9, '4-7': 1, '5-8': 1,
      '6-7': 1, '7-8': 1,
    };
    const exampleMatrix = Array.from({ length: size }, () => Array(size).fill(0));
    Object.entries(weights).forEach(([edge, w]) => {
      const [a, b] = edge.split('-').map(Number);
      exampleMatrix[a][b] = w;
      exampleMatrix[b][a] = w;
    });

    if (vertexCount !== size) {
      pendingMatrix.current = exampleMatrix; // applied by the vertexCount effect
      setVertexCount(size);
    } else {
      setMatrix(exampleMatrix);
    }
    setOrigin('A');
    setDestination('I');
    setResult(null);
    setGraphImage(null);
    toast.success(t('exampleLoaded'));
  }, [vertexCount, t]);

  const pointOptions = useMemo(() => vertexLabels, [vertexLabels]);
  const complexity = getAlgorithmComplexity(algorithm);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      {/* Algorithm Selector */}
      <section className="mb-6 animate-fade-in">
        <h2 className="text-sm font-medium text-text-primary mb-3">{t('searchAlgorithm')}</h2>

        <div className="segmented-control" role="radiogroup" aria-label={t('searchAlgorithm')}>
          {ALGORITHM_OPTIONS.map((algo) => (
            <button
              key={algo.value}
              role="radio"
              aria-checked={algorithm === algo.value}
              onClick={() => setAlgorithm(algo.value)}
              className={`segmented-item ${algorithm === algo.value ? 'segmented-item-active' : 'segmented-item-inactive'}`}
            >
              {algo.label}
            </button>
          ))}
        </div>

        {/* Complexity text - left aligned below segmented control */}
        <p className="mt-2 text-xs text-text-muted font-mono leading-relaxed">
          {complexity}
        </p>

        {algorithm === 'astar' && (
          <div className="mt-4 animate-slide-in">
            <label className="block text-sm font-medium text-text-secondary mb-1.5">{t('heuristicLabel')}</label>
            <select
              value={heuristic}
              onChange={(e) => setHeuristic(e.target.value)}
              className="input-compact w-full md:w-64 appearance-none bg-no-repeat bg-right bg-center pr-10"
              style={{
                backgroundImage: 'url("data:image/svg+xml,%3csvg xmlns=\'http://www.w3.org/2000/svg\' fill=\'none\' viewBox=\'0 0 20 20\'%3e%3cpath stroke=\'currentColor\' stroke-linecap=\'round\' stroke-linejoin=\'round\' stroke-width=\'1.5\' d=\'M6 8l4 4 4-4\'/%3e%3c/svg%3e")',
                backgroundSize: '16px 16px'
              }}
            >
              <option value="MANHATTAN">{t('heuristicManhattan')}</option>
              <option value="EUCLIDEAN">{t('heuristicEuclidean')}</option>
              <option value="CHEBYSHEV">{t('heuristicChebyshev')}</option>
            </select>
          </div>
        )}
      </section>

      {/* Tabs */}
      <div className="mb-4 animate-fade-in">
        <nav className="tab-nav" aria-label={t('matrixTab')}>
          <button className="tab-item tab-item-active">
            {t('adjacencyMatrix')}
          </button>
        </nav>
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-[1fr_280px] gap-4">
        {/* Left Panel: Matrix Input */}
        <div className="animate-fade-in">
          <div className="panel overflow-hidden">
            <MatrixInput
              vertexCount={vertexCount}
              vertexLabels={vertexLabels}
              matrix={matrix}
              onMatrixChange={handleMatrixChange}
              t={t}
              showHelp={showMatrixHelp}
              onToggleHelp={setShowMatrixHelp}
            />
          </div>
        </div>

        {/* Right Panel: Single Continuous Properties Panel */}
        <div className="animate-slide-in">
          <div className="panel p-4 space-y-0">
            {/* Vertex Count */}
            <div className="pt-2 pb-4 border-b border-border">
              <label className="block text-xs font-medium text-text-muted uppercase tracking-wider mb-2">{t('vertexCount')}</label>
              <input
                type="number"
                min="2"
                max="26"
                value={vertexCount}
                onChange={(e) => setVertexCount(Math.max(2, Math.min(26, parseInt(e.target.value) || 2)))}
                className="input-compact w-full md:w-24 font-mono text-center"
              />
            </div>

            {/* Origin/Destination */}
            <div className="py-4 border-b border-border space-y-4">
              <div>
                <label className="block text-xs font-medium text-text-muted uppercase tracking-wider mb-2">{t('origin')}</label>
                <select
                  value={origin}
                  onChange={handleOriginChange}
                  className="input-compact w-full appearance-none bg-no-repeat bg-right bg-center pr-10"
                  style={{
                    backgroundImage: 'url("data:image/svg+xml,%3csvg xmlns=\'http://www.w3.org/2000/svg\' fill=\'none\' viewBox=\'0 0 20 20\'%3e%3cpath stroke=\'currentColor\' stroke-linecap=\'round\' stroke-linejoin=\'round\' stroke-width=\'1.5\' d=\'M6 8l4 4 4-4\'/%3e%3c/svg%3e")',
                    backgroundSize: '16px 16px'
                  }}
                >
                  <option value="">{t('selectPlaceholder')}</option>
                  {pointOptions.map(p => (
                    <option key={p} value={p}>{p}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-xs font-medium text-text-muted uppercase tracking-wider mb-2">{t('destination')}</label>
                <select
                  value={destination}
                  onChange={handleDestinationChange}
                  className="input-compact w-full appearance-none bg-no-repeat bg-right bg-center pr-10"
                  style={{
                    backgroundImage: 'url("data:image/svg+xml,%3csvg xmlns=\'http://www.w3.org/2000/svg\' fill=\'none\' viewBox=\'0 0 20 20\'%3e%3cpath stroke=\'currentColor\' stroke-linecap=\'round\' stroke-linejoin=\'round\' stroke-width=\'1.5\' d=\'M6 8l4 4 4-4\'/%3e%3c/svg%3e")',
                    backgroundSize: '16px 16px'
                  }}
                >
                  <option value="">{t('selectPlaceholder')}</option>
                  {pointOptions.map(p => (
                    <option key={p} value={p}>{p}</option>
                  ))}
                </select>
              </div>
            </div>

            {/* Terrain Toggle */}
            <div className="py-4 border-b border-border">
              <label className="flex items-center gap-3 cursor-pointer">
                <input
                  type="checkbox"
                  checked={showTerrainConfig}
                  onChange={(e) => setShowTerrainConfig(e.target.checked)}
                  className="w-4 h-4 text-accent border-border-strong rounded focus:ring-2 focus:ring-accent/30 focus:ring-offset-2 focus:ring-offset-bg"
                />
                <span className="text-sm font-medium text-text-primary font-mono">{t('terrainConfig')}</span>
              </label>
              <p className="text-xs text-text-muted mt-1.5 ml-7">{t('terrainConfigDesc')}</p>
              {showTerrainConfig && (
                <select
                  value={terrainPreset}
                  onChange={(e) => setTerrainPreset(e.target.value)}
                  className="input-compact w-full mt-3"
                  aria-label={t('terrainConfig')}
                >
                  {TERRAIN_PRESETS.map((p) => (
                    <option key={p} value={p}>{t(`preset_${p}`)}</option>
                  ))}
                </select>
              )}
            </div>

            {/* Action Buttons */}
            <div className="py-4 space-y-2">
              <button
                onClick={handleSubmit}
                disabled={loading || !origin || !destination}
                className="btn-primary w-full"
              >
                {loading ? (
                  <>
                    <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24" aria-hidden="true">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                    </svg>
                    {t('calculating')}
                  </>
                ) : (
                  <>
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 9l3 3m0 0l-3 3m3-3H8m13 0a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    {t('calculateRoute')}
                  </>
                )}
              </button>

              <div className="grid grid-cols-2 gap-2">
                <button
                  onClick={handleLoadExample}
                  disabled={loading}
                  className="btn-secondary whitespace-nowrap"
                >
                  {t('loadExample')}
                </button>
                <button
                  onClick={handleClear}
                  disabled={loading}
                  className="btn-ghost"
                >
                  {t('clear')}
                </button>
              </div>
            </div>

            {/* Quick Stats - only show when result exists */}
            {result && (
              <div className="pt-4 border-t border-border-strong animate-fade-in">
                <div className="grid grid-cols-2 gap-2">
                  <div className="p-3 bg-surface-elevated border border-border rounded-lg">
                    <div className="text-[10px] font-medium text-text-muted uppercase tracking-wider mb-1">{t('minPathTitle')}</div>
                    <div className="font-mono text-sm font-bold text-text-primary">
                      {result.caminhoMinimo?.caminhoEncontrado ? '✓ Found' : '✗ Not found'}
                    </div>
                    <div className="font-mono text-sm font-bold text-accent mt-0.5">
                      {result.caminhoMinimo?.custo !== undefined ? result.caminhoMinimo.custo : 'N/A'}
                    </div>
                  </div>
                  <div className="p-3 bg-surface-elevated border border-border rounded-lg">
                    <div className="text-[10px] font-medium text-text-muted uppercase tracking-wider mb-1">{t('maxPathTitle')}</div>
                    <div className="font-mono text-sm font-bold text-text-primary">
                      {result.caminhoMaximo?.caminhoEncontrado ? '✓ Found' : '∞ Infinite'}
                    </div>
                    <div className="font-mono text-sm font-bold text-text-secondary mt-0.5">
                      {result.caminhoMaximo?.custo !== undefined && result.caminhoMaximo.custo !== 'Infinity' ? result.caminhoMaximo.custo : '—'}
                    </div>
                  </div>
                  <div className="p-3 bg-surface-elevated border border-border rounded-lg col-span-2">
                    <div className="text-[10px] font-medium text-text-muted uppercase tracking-wider mb-1">{t('verticesTitle')}</div>
                    <div className="font-mono text-sm font-bold text-text-primary">
                      {result.numeroVertices || vertexCount}
                    </div>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Results Section */}
      {(result || graphImage) && (
        <div className="mt-6 animate-fade-in">
          <PathVisualization
            result={result}
            graphImage={graphImage}
            vertexLabels={vertexLabels}
            algorithm={algorithm}
            heuristic={algorithm === 'astar' ? heuristic : null}
            matrix={matrix}
          />
        </div>
      )}
    </div>
  );
}

// Sub-component: Matrix Input with Tooltip Help
function MatrixInput({ vertexCount, vertexLabels, matrix, onMatrixChange, t, showHelp, onToggleHelp }) {
  const maxDisplaySize = 12;
  const isLarge = vertexCount > maxDisplaySize;
  const displayCount = isLarge ? maxDisplaySize : vertexCount;
  const displayLabels = vertexLabels.slice(0, displayCount);

  return (
    <div className="p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-text-primary flex items-center gap-2">
          {t('adjacencyMatrix')}
          <span className="text-[11px] font-normal text-text-muted">({t('matrixSize', { size: vertexCount })})</span>
        </h3>

        <div className="flex items-center gap-2">
          {isLarge && (
            <span className="text-[10px] font-medium text-text-muted px-2 py-0.5 bg-surface-elevated border border-border rounded">
              {t('showing', { display: maxDisplaySize, total: vertexCount })}
            </span>
          )}
          <div className="relative inline-flex items-center tooltip-trigger">
            <button
              type="button"
              onClick={() => onToggleHelp(!showHelp)}
              className="p-1.5 rounded bg-surface-elevated border border-border hover:border-accent/50 hover:bg-surface transition-all duration-150 text-text-muted"
              aria-label={t('matrixHelpTitle')}
              aria-expanded={showHelp}
            >
              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.228 9c.549-1.165 2.03-2 3.772-2 2.21 0 4 1.343 4 3 0 1.4-1.278 2.575-3.006 2.907-.542.104-.994.54-.994 1.093m0 3h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </button>
            <div className="tooltip-content">
              <strong className="block mb-1 text-text-primary">{t('matrixHelpTitle')}</strong>
              <ul className="space-y-1 text-left">
                {t('matrixHelpItems').map((item, idx) => (
                  <li key={idx} className="flex items-start gap-1.5 text-text-secondary">
                    <span className="flex-shrink-0 text-accent">•</span>
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      </div>

      <div className="overflow-x-auto overflow-y-auto max-h-[500px] border border-border rounded-lg">
        <table className="matrix-table">
          <thead className="sticky top-0 bg-surface">
            <tr>
              <th className="matrix-th-main">
                ↓→
              </th>
              {displayLabels.map((label, j) => (
                <th key={j} className="matrix-th w-12">{label}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {displayLabels.map((label, i) => (
              <tr key={i} className="matrix-row">
                <th className="matrix-row-th w-12">{label}</th>
                {displayLabels.map((_, j) => (
                  <td key={j} className="matrix-td p-1">
                    {i === j ? (
                      // Diagonal cells - just text, no input
                      <div className="w-full h-10 flex items-center justify-center font-mono text-text-disabled text-sm">
                        0
                      </div>
                    ) : (
                      <input
                        type="number"
                        min="0"
                        step="0.1"
                        value={matrix[i]?.[j] === 0 ? '' : matrix[i]?.[j] ?? ''}
                        onChange={(e) => onMatrixChange(i, j, e.target.value)}
                        className={`input-mono w-full h-10 ${matrix[i]?.[j] > 0 ? 'bg-cell-filled border-cell-filled-border text-accent' : ''}`}
                        placeholder={i < j ? 'weight' : '0'}
                        title={`${label}→${displayLabels[j]}`}
                        aria-label={`${label} to ${displayLabels[j]}`}
                      />
                    )}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showHelp && (
        <div className="mt-3 animate-fade-in p-3 bg-surface-elevated border border-border rounded-lg">
          <ul className="space-y-1">
            {t('matrixHelpItems').map((item, idx) => (
              <li key={idx} className="flex items-start gap-2 text-xs text-text-secondary">
                <span className="flex-shrink-0 text-accent">•</span>
                <span>{item}</span>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}