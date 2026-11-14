import React, { useState, useEffect, useMemo } from 'react';
import { toast } from 'react-toastify';
import { useI18n } from '../../lib/I18nContext';

const getPointName = (index) => String.fromCharCode(65 + index);

export default function PathVisualization({
  result,
  graphImage,
  vertexLabels,
  algorithm,
  heuristic,
  matrix
}) {
  const { t } = useI18n();
  const [activeView, setActiveView] = useState('summary');
  const [asciiArt, setAsciiArt] = useState('');

  // Generate ASCII art of the path
  useEffect(() => {
    if (result && result.caminhoMinimo && result.caminhoMinimo.caminhoEncontrado) {
      const path = result.caminhoMinimo.caminho;
      const letters = path.map(i => getPointName(i));
      let ascii = '\n';
      ascii += '═'.repeat(50) + '\n';
      ascii += `  ${t('minPathTitle').toUpperCase()}\n`;
      ascii += '═'.repeat(50) + '\n\n';
      ascii += `  Algorithm: ${algorithm.toUpperCase()}${heuristic ? ` (${heuristic})` : ''}\n`;
      ascii += `  Total Cost: ${result.caminhoMinimo.custo}\n`;
      ascii += `  Vertices Visited: ${path.length}\n\n`;
      ascii += `  Route: ${letters.join(' → ')}\n\n`;

      // Show matrix with path highlighted
      ascii += '  Matrix with path (✓ = in path):\n';
      ascii += '     ' + vertexLabels.slice(0, Math.min(10, vertexLabels.length)).join('  ') + '\n';
      for (let i = 0; i < Math.min(10, matrix.length); i++) {
        ascii += `  ${vertexLabels[i]}  `;
        for (let j = 0; j < Math.min(10, matrix[i].length); j++) {
          const inPath = path.includes(i) && path.includes(j);
          const isEdge = matrix[i][j] > 0;
          if (inPath && isEdge) {
            ascii += ' ✓  ';
          } else if (isEdge) {
            ascii += ` ${matrix[i][j]}  `;
          } else {
            ascii += ' ·  ';
          }
        }
        ascii += '\n';
      }
      ascii += '\n' + '═'.repeat(50);

      setAsciiArt(ascii);
    }
  }, [result, algorithm, heuristic, matrix, vertexLabels, t]);

  const views = useMemo(() => [
    { id: 'summary', label: t('summaryTab') },
    { id: 'details', label: t('detailsTab') },
    { id: 'ascii', label: t('asciiTab') },
    { id: 'graph', label: t('graphTab') },
  ], [t]);

  if (!result) return null;

  const minPath = result.caminhoMinimo;
  const maxPath = result.caminhoMaximo;
  const pathFound = minPath?.caminhoEncontrado;

  const minPathLetters = pathFound ? minPath.caminho.map(i => getPointName(i)) : [];
  const maxPathLetters = maxPath?.caminhoEncontrado ? maxPath.caminho.map(i => getPointName(i)) : [];

  return (
    <section id="results" className="space-y-4 animate-fade-in">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-text-primary">{t('resultsTitle')}</h2>
        <span className="text-xs font-mono font-medium text-accent px-2 py-0.5 bg-accent/10 rounded">
          {algorithm.toUpperCase()}{heuristic ? ` (${heuristic})` : ''}
        </span>
      </div>

      {/* View Tabs */}
      <div className="border-b border-border">
        <nav className="tab-nav" aria-label={t('resultsTitle')}>
          {views.map((view) => (
            <button
              key={view.id}
              onClick={() => setActiveView(view.id)}
              disabled={view.id === 'graph' && !graphImage}
              className={`tab-item ${activeView === view.id ? 'tab-item-active' : 'tab-item-inactive'} ${view.id === 'graph' && !graphImage ? 'opacity-50 cursor-not-allowed' : ''}`}
            >
              {view.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Content Panel */}
      <div className="panel p-4">
        {activeView === 'summary' && (
          <PathSummaryView
            minPath={minPath}
            maxPath={maxPath}
            minPathLetters={minPathLetters}
            maxPathLetters={maxPathLetters}
            result={result}
            t={t}
          />
        )}

        {activeView === 'details' && (
          <PathDetailsView
            minPath={minPath}
            maxPath={maxPath}
            result={result}
            vertexLabels={vertexLabels}
            matrix={matrix}
            t={t}
          />
        )}

        {activeView === 'ascii' && (
          <AsciiView asciiArt={asciiArt} pathFound={pathFound} t={t} />
        )}

        {activeView === 'graph' && graphImage && (
          <GraphImageView graphImage={graphImage} t={t} />
        )}

        {activeView === 'graph' && !graphImage && (
          <div className="text-center py-8 text-text-muted">
            <svg className="w-12 h-12 mx-auto mb-3 opacity-40" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <p className="font-mono text-sm">{t('graphUnavailable')}</p>
            <p className="text-xs mt-1">{t('graphUnavailableDesc')}</p>
          </div>
        )}
      </div>

      {/* Export Buttons */}
      <div className="flex flex-wrap gap-2">
        <button
          onClick={() => exportResults(result, algorithm, heuristic, matrix, vertexLabels)}
          className="btn-secondary text-sm"
        >
          <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
          </svg>
          {t('exportJson')}
        </button>
        <button
          onClick={() => exportCSV(result, matrix, vertexLabels)}
          className="btn-secondary text-sm"
        >
          <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
          </svg>
          {t('exportCsv')}
        </button>
        {graphImage && (
          <a
            href={graphImage}
            download="hiking-route-graph.png"
            className="btn-secondary text-sm"
          >
            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            {t('downloadPng')}
          </a>
        )}
      </div>
    </section>
  );
}

function PathSummaryView({ minPath, maxPath, minPathLetters, maxPathLetters, result, t }) {
  const pathFound = minPath?.caminhoEncontrado;

  return (
    <div className="space-y-4">
      {/* Status Grid */}
      <div className="grid grid-cols-2 gap-2 md:grid-cols-4">
        <StatusCard
          title={t('minPathTitle')}
          status={pathFound ? 'success' : 'error'}
          value={pathFound ? t('found') : t('notFound')}
          details={pathFound ? t('cost', { cost: minPath.custo }) : minPath?.mensagem}
        />
        <StatusCard
          title={t('maxPathTitle')}
          status={maxPath?.caminhoEncontrado ? 'success' : 'warning'}
          value={maxPath?.caminhoEncontrado ? t('found') : maxPath?.custo === 'Infinity' ? t('infinite') : t('error')}
          details={maxPath?.caminhoEncontrado ? t('cost', { cost: maxPath.custo }) : maxPath?.mensagem}
        />
        <StatusCard
          title={t('verticesTitle')}
          status="info"
          value={result.numeroVertices}
          details={t('verticesCount', { count: minPathLetters.length, edges: minPathLetters.length - 1 })}
        />
        <StatusCard
          title={t('statusTitle')}
          status={result.status === 'SUCESSO' ? 'success' : result.status === 'CAMINHO_PARCIAL' ? 'warning' : 'error'}
          value={result.status}
          details={result.status === 'SUCESSO' ? t('success') : result.status === 'CAMINHO_PARCIAL' ? t('partial') : t('error')}
        />
      </div>

      {/* Path Visualization */}
      {pathFound && (
        <div className="space-y-3">
          <h3 className="text-sm font-medium text-text-primary">{t('minPathTitle')}: Optimal Route</h3>
          <PathStepper
            steps={minPathLetters}
            highlight={true}
          />

          <div className="p-3 bg-success-bg border border-success/30 rounded-lg">
            <div className="flex items-center justify-between flex-wrap gap-3 text-sm">
              <div className="flex items-center gap-3">
                <span className="font-mono font-bold text-success">
                  {t('totalCost')}: {minPath.custo}
                </span>
                <span className="text-text-secondary font-mono">
                  {t('verticesCount', { count: minPath.caminho.length, edges: minPath.caminho.length - 1 })}
                </span>
              </div>
              <span className="badge badge-success text-[10px]">
                {minPath.mensagem}
              </span>
            </div>
          </div>
        </div>
      )}

      {maxPath?.caminhoEncontrado && (
        <div className="space-y-3 pt-3 border-t border-border">
          <h3 className="text-sm font-medium text-text-primary">{t('maxPathTitle')}</h3>
          <PathStepper steps={maxPathLetters} />
          <div className="p-3 bg-surface-elevated border border-border rounded-lg">
            <div className="font-mono text-sm font-bold text-text-primary">
              {t('totalCost')}: {maxPath.custo}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function PathDetailsView({ minPath, maxPath, result, vertexLabels, matrix, t }) {
  const pathFound = minPath?.caminhoEncontrado;

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        <DetailCard
          title={t('minPathTitle')}
          path={minPath?.caminho}
          cost={minPath?.custo}
          found={pathFound}
          message={minPath?.mensagem}
          vertexLabels={vertexLabels}
          t={t}
        />
        <DetailCard
          title={t('maxPathTitle')}
          path={maxPath?.caminho}
          cost={maxPath?.custo}
          found={maxPath?.caminhoEncontrado}
          message={maxPath?.mensagem}
          vertexLabels={vertexLabels}
          t={t}
        />
      </div>

      {pathFound && (
        <div>
          <h4 className="text-sm font-medium text-text-primary mb-2">Adjacency Matrix with Highlighted Path</h4>
          <div className="overflow-x-auto">
            <table className="w-full border-collapse font-mono text-sm">
              <thead className="bg-surface">
                <tr>
                  <th className="matrix-th-main w-10">↓→</th>
                  {vertexLabels.map((l, j) => (
                    <th key={j} className="matrix-th w-12">{l}</th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {matrix.map((row, i) => (
                  <tr key={i} className="matrix-row">
                    <th className="matrix-row-th w-10">{vertexLabels[i]}</th>
                    {row.map((val, j) => {
                      const inMinPath = minPath.caminho.includes(i) && minPath.caminho.includes(j);
                      const isEdge = val > 0;
                      const isPathEdge = inMinPath && isEdge &&
                          Math.abs(minPath.caminho.indexOf(j) - minPath.caminho.indexOf(i)) === 1;
                      return (
                        <td key={j} className={`matrix-td ${isPathEdge ? 'bg-success-bg text-success font-bold' : inMinPath && i !== j ? 'bg-success-bg/30' : ''}`}>
                          {i === j ? '0' : isEdge ? val : '·'}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

function AsciiView({ asciiArt, pathFound, t }) {
  return (
    <div className="font-mono text-xs bg-bg border border-border rounded-lg p-3 overflow-x-auto whitespace-pre" style={{ fontFamily: "'JetBrains Mono', 'Geist Mono', 'Fira Code', 'Monaco', 'Consolas', monospace" }}>
      {asciiArt || (pathFound === false ? t('notFound') : 'Calculating...')}
    </div>
  );
}

function GraphImageView({ graphImage, t }) {
  return (
    <div className="text-center">
      <img
        src={graphImage}
        alt={t('graphTab')}
        className="max-w-full h-auto rounded-lg border border-border mx-auto"
        style={{ maxHeight: '500px' }}
      />
      <p className="mt-2 text-xs text-text-muted font-mono">
        Generated via Python (NetworkX + Matplotlib)
      </p>
    </div>
  );
}

function StatusCard({ title, status, value, details }) {
  const statusStyles = {
    success: 'bg-success-bg border-success/30',
    warning: 'bg-warning-bg border-warning/30',
    error: 'bg-error-bg border-error/30',
    info: 'bg-surface-elevated border-border',
  };

  const valueColors = {
    success: 'text-success',
    warning: 'text-warning',
    error: 'text-error',
    info: 'text-text-primary',
  };

  return (
    <div className={`p-3 rounded-lg border ${statusStyles[status]}`}>
      <div className="text-[10px] font-medium text-text-muted uppercase tracking-wider mb-1">{title}</div>
      <div className={`font-mono text-sm font-bold ${valueColors[status]}`}>{value}</div>
      <div className="text-[10px] text-text-muted mt-0.5">{details}</div>
    </div>
  );
}

function DetailCard({ title, path, cost, found, message, vertexLabels, t }) {
  const letters = path ? path.map(i => vertexLabels[i]) : [];

  return (
    <div className="p-3 bg-surface-elevated border border-border rounded-lg">
      <h4 className="text-sm font-medium text-text-primary mb-2 flex items-center gap-2">
        {title}
        <span className={`badge ${found ? 'badge-success' : 'badge-error'}`}>
          {found ? t('found') : t('notFound')}
        </span>
      </h4>
      {found && letters.length > 0 && (
        <div className="mb-3">
          <PathStepper steps={letters} compact />
        </div>
      )}
      <div className="text-xs text-text-secondary space-y-0.5">
        {found && <p><span className="font-medium">Cost:</span> <span className="font-mono">{cost}</span></p>}
        <p><span className="font-medium">Vertices:</span> <span className="font-mono">{path?.length || 0}</span></p>
        <p><span className="font-medium">Status:</span> <span className="font-mono">{message}</span></p>
      </div>
    </div>
  );
}

function PathStepper({ steps, costs, highlight = false, compact = false }) {
  return (
    <div className={`flex flex-wrap items-center gap-1.5 ${compact ? 'text-xs' : 'text-sm'}`}>
      {steps.map((step, i) => (
        <React.Fragment key={i}>
          <div className={`flex items-center gap-1 px-2.5 py-1.5 rounded font-mono font-bold transition-all ${
            highlight && i < steps.length - 1
              ? 'bg-success-bg text-success border border-success/30'
              : 'bg-surface-elevated border border-border text-text-primary'
          }`}>
            {step}
          </div>
          {i < steps.length - 1 && (
            <svg className={`w-4 h-4 ${highlight ? 'text-success' : 'text-text-muted'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 8l4 4m0 0l-4 4m4-4H3" />
            </svg>
          )}
        </React.Fragment>
      ))}
    </div>
  );
}

function exportResults(result, algorithm, heuristic, matrix, vertexLabels) {
  const exportData = {
    metadata: {
      timestamp: new Date().toISOString(),
      algorithm,
      heuristic,
      vertexCount: vertexLabels.length,
    },
    result,
    matrix,
    vertexLabels,
  };
  downloadFile(JSON.stringify(exportData, null, 2), 'hiking-route-result.json', 'application/json');
  toast.success('Result exported as JSON');
}

function exportCSV(result, matrix, vertexLabels) {
  let csv = 'Origin,Destination,Weight\n';
  for (let i = 0; i < matrix.length; i++) {
    for (let j = 0; j < matrix[i].length; j++) {
      if (matrix[i][j] > 0) {
        csv += `${vertexLabels[i]},${vertexLabels[j]},${matrix[i][j]}\n`;
      }
    }
  }
  if (result.caminhoMinimo?.caminhoEncontrado) {
    csv += '\n# Min Path\nOrder,Vertex\n';
    result.caminhoMinimo.caminho.forEach((v, i) => {
      csv += `${i + 1},${vertexLabels[v]}\n`;
    });
    csv += `\nTotal Cost,${result.caminhoMinimo.custo}\n`;
  }
  downloadFile(csv, 'hiking-route-matrix.csv', 'text/csv');
  toast.success('Matrix exported as CSV');
}

function downloadFile(content, filename, type) {
  const blob = new Blob([content], { type });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  URL.revokeObjectURL(url);
}