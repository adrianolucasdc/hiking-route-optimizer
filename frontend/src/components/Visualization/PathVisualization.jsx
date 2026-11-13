import React, { useState, useEffect, useMemo } from 'react';
import { toast } from 'react-toastify';

const getPointName = (index) => String.fromCharCode(65 + index);

export default function PathVisualization({
    result,
    graphImage,
    vertexLabels,
    algorithm,
    heuristic,
    matrix
}) {
    const [activeView, setActiveView] = useState('summary');
    const [asciiArt, setAsciiArt] = useState('');

    // Gerar ASCII art do caminho
    useEffect(() => {
        if (result && result.caminhoMinimo && result.caminhoMinimo.caminhoEncontrado) {
            const path = result.caminhoMinimo.caminho;
            const letters = path.map(i => getPointName(i));
            let ascii = '\n';
            ascii += '═'.repeat(50) + '\n';
            ascii += '  CAMINHO MÍNIMO ENCONTRADO\n';
            ascii += '═'.repeat(50) + '\n\n';
            ascii += `  Algoritmo: ${algorithm.toUpperCase()}${heuristic ? ` (${heuristic})` : ''}\n`;
            ascii += `  Custo total: ${result.caminhoMinimo.custo}\n`;
            ascii += `  Vértices visitados: ${path.length}\n\n`;
            ascii += '  Rota: ' + letters.join(' → ') + '\n\n';

            // Mostrar matriz com caminho destacado
            ascii += '  Matriz com caminho (✓ = no caminho):\n';
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
    }, [result, algorithm, heuristic, matrix, vertexLabels]);

    const views = [
        { id: 'summary', label: 'Resumo', icon: '📊' },
        { id: 'details', label: 'Detalhes', icon: '📋' },
        { id: 'ascii', label: 'ASCII/Terminal', icon: '💻' },
        { id: 'graph', label: 'Gráfico', icon: '📈' },
    ];

    if (!result) return null;

    const minPath = result.caminhoMinimo;
    const maxPath = result.caminhoMaximo;
    const pathFound = minPath?.caminhoEncontrado;

    const minPathLetters = pathFound ? minPath.caminho.map(i => getPointName(i)) : [];
    const maxPathLetters = maxPath?.caminhoEncontrado ? maxPath.caminho.map(i => getPointName(i)) : [];

    return (
        <section id="results" className="space-y-6 animate-fade-in">
            <div className="flex items-center justify-between">
                <h2 className="text-2xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                    <svg className="w-6 h-6 text-green-600 dark:text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    Resultados da Busca
                </h2>
                <span className="px-3 py-1 text-sm font-mono bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200 rounded-full">
                    {algorithm.toUpperCase()}{heuristic ? ` (${heuristic})` : ''}
                </span>
            </div>

            {/* View Tabs */}
            <div className="border-b border-gray-200 dark:border-gray-700">
                <nav className="flex gap-4 overflow-x-auto" aria-label="Visualizações">
                    {views.map((view) => (
                        <button
                            key={view.id}
                            onClick={() => setActiveView(view.id)}
                            disabled={view.id === 'graph' && !graphImage}
                            className={`pb-3 px-2 border-b-2 font-mono text-sm flex items-center gap-1 whitespace-nowrap transition-colors ${
                                activeView === view.id
                                    ? 'border-green-500 text-green-600 dark:text-green-400'
                                    : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
                            } ${view.id === 'graph' && !graphImage ? 'opacity-50 cursor-not-allowed' : ''}`}
                        >
                            <span>{view.icon}</span>
                            {view.label}
                        </button>
                    ))}
                </nav>
            </div>

            {/* Content Panels */}
            <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-6">
                {activeView === 'summary' && (
                    <PathSummaryView
                        minPath={minPath}
                        maxPath={maxPath}
                        minPathLetters={minPathLetters}
                        maxPathLetters={maxPathLetters}
                        result={result}
                    />
                )}

                {activeView === 'details' && (
                    <PathDetailsView
                        minPath={minPath}
                        maxPath={maxPath}
                        result={result}
                        vertexLabels={vertexLabels}
                        matrix={matrix}
                    />
                )}

                {activeView === 'ascii' && (
                    <AsciiView asciiArt={asciiArt} pathFound={pathFound} />
                )}

                {activeView === 'graph' && graphImage && (
                    <GraphImageView graphImage={graphImage} />
                )}

                {activeView === 'graph' && !graphImage && (
                    <div className="text-center py-12 text-gray-500 dark:text-gray-400">
                        <svg className="w-16 h-16 mx-auto mb-4 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <p className="font-mono">Gráfico não disponível</p>
                        <p className="text-sm mt-1">O serviço de visualização Python pode estar indisponível</p>
                    </div>
                )}
            </div>

            {/* Export Buttons */}
            <div className="flex flex-wrap gap-3">
                <button
                    onClick={() => exportResults(result, algorithm, heuristic, matrix, vertexLabels)}
                    className="px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 font-mono text-sm flex items-center gap-2 transition-colors"
                >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                    Exportar JSON
                </button>
                <button
                    onClick={() => exportCSV(result, matrix, vertexLabels)}
                    className="px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 font-mono text-sm flex items-center gap-2 transition-colors"
                >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                    </svg>
                    Exportar CSV
                </button>
                {graphImage && (
                    <a
                        href={graphImage}
                        download="hiking-route-graph.png"
                        className="px-4 py-2 bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg hover:bg-gray-200 dark:hover:bg-gray-600 font-mono text-sm flex items-center gap-2 transition-colors"
                    >
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        Baixar PNG
                    </a>
                )}
            </div>
        </section>
    );
}

function PathSummaryView({ minPath, maxPath, minPathLetters, maxPathLetters, result }) {
    const pathFound = minPath?.caminhoEncontrado;

    return (
        <div className="space-y-6">
            {/* Status Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <StatusCard
                    title="Caminho Mínimo"
                    status={pathFound ? 'success' : 'error'}
                    value={pathFound ? 'Encontrado' : 'Não encontrado'}
                    icon="🎯"
                    details={pathFound ? `Custo: ${minPath.custo}` : minPath?.mensagem}
                />
                <StatusCard
                    title="Caminho Máximo"
                    status={maxPath?.caminhoEncontrado ? 'success' : 'warning'}
                    value={maxPath?.caminhoEncontrado ? 'Encontrado' : maxPath?.custo === 'Infinity' ? 'Infinito (ciclo positivo)' : 'Erro'}
                    icon="📈"
                    details={maxPath?.caminhoEncontrado ? `Custo: ${maxPath.custo}` : maxPath?.mensagem}
                />
                <StatusCard
                    title="Vértices"
                    status="info"
                    value={result.numeroVertices}
                    icon="🔢"
                    details={`Origem → Destino`}
                />
                <StatusCard
                    title="Status Geral"
                    status={result.status === 'SUCESSO' ? 'success' : result.status === 'CAMINHO_PARCIAL' ? 'warning' : 'error'}
                    value={result.status}
                    icon="📋"
                    details={result.status === 'SUCESSO' ? 'Ambos os caminhos' : result.status === 'CAMINHO_PARCIAL' ? 'Apenas mínimo' : 'Erro'}
                />
            </div>

            {/* Path Visualization */}
            {pathFound && (
                <div className="space-y-4">
                    <h3 className="font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                        <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 18.657A8 8 0 016.343 7.343S7 9 9 10c0-2 .5-5 2.986-7C14 5 16.09 5.777 17.656 7.343A7.975 7.975 0 0120 13a7.975 7.975 0 01-2.343 5.657z" />
                        </svg>
                        Rota Ótima (Mínimo)
                    </h3>
                    <PathStepper
                        steps={minPathLetters}
                        costs={minPath.caminho.map((_, i) => i < minPath.caminho.length - 1 ? '→' : '')}
                        highlight={true}
                    />

                    <div className="bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-4">
                        <div className="flex items-center justify-between flex-wrap gap-4">
                            <div className="flex items-center gap-4 text-sm">
                                <span className="font-mono font-bold text-green-800 dark:text-green-200">
                                    Distância Total: {minPath.custo}
                                </span>
                                <span className="text-green-700 dark:text-green-300">
                                    {minPath.caminho.length} vértices • {minPath.caminho.length - 1} arestas
                                </span>
                            </div>
                            <span className="px-2 py-1 text-xs font-mono bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200 rounded">
                                {minPath.mensagem}
                            </span>
                        </div>
                    </div>
                </div>
            )}

            {maxPath?.caminhoEncontrado && (
                <div className="space-y-4 border-t border-gray-200 dark:border-gray-700 pt-6">
                    <h3 className="font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                        <svg className="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                        </svg>
                        Caminho Máximo
                    </h3>
                    <PathStepper steps={maxPathLetters} />
                    <div className="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
                        <div className="font-mono font-bold text-blue-800 dark:text-blue-200">
                            Custo Total: {maxPath.custo}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

function PathDetailsView({ minPath, maxPath, result, vertexLabels, matrix }) {
    const pathFound = minPath?.caminhoEncontrado;

    return (
        <div className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <DetailCard
                    title="Caminho Mínimo"
                    path={minPath?.caminho}
                    cost={minPath?.custo}
                    found={pathFound}
                    message={minPath?.mensagem}
                    vertexLabels={vertexLabels}
                />
                <DetailCard
                    title="Caminho Máximo"
                    path={maxPath?.caminho}
                    cost={maxPath?.custo}
                    found={maxPath?.caminhoEncontrado}
                    message={maxPath?.mensagem}
                    vertexLabels={vertexLabels}
                />
            </div>

            {pathFound && (
                <div>
                    <h4 className="font-semibold text-gray-900 dark:text-white mb-3">Matriz de Adjacência com Caminho Destacado</h4>
                    <div className="overflow-x-auto">
                        <table className="w-full border-collapse font-mono text-sm">
                            <thead className="bg-gray-50 dark:bg-gray-800">
                                <tr>
                                    <th className="w-10 px-2 py-2 text-center border border-gray-200 dark:border-gray-700">↓→</th>
                                    {vertexLabels.map((l, j) => (
                                        <th key={j} className="w-12 px-2 py-2 text-center border border-gray-200 dark:border-gray-700 font-semibold">{l}</th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {matrix.map((row, i) => (
                                    <tr key={i}>
                                        <th className="w-10 px-2 py-1 text-center border border-gray-200 dark:border-gray-700 font-semibold sticky left-0 bg-white dark:bg-gray-800">{vertexLabels[i]}</th>
                                        {row.map((val, j) => {
                                            const inMinPath = minPath.caminho.includes(i) && minPath.caminho.includes(j);
                                            const isEdge = val > 0;
                                            const isPathEdge = inMinPath && isEdge &&
                                                Math.abs(minPath.caminho.indexOf(j) - minPath.caminho.indexOf(i)) === 1;
                                            return (
                                                <td key={j} className={`px-2 py-1 text-center border border-gray-200 dark:border-gray-700 ${
                                                    isPathEdge ? 'bg-green-100 dark:bg-green-900/30 font-bold text-green-800 dark:text-green-200' :
                                                    inMinPath && i !== j ? 'bg-green-50 dark:bg-green-900/10' : ''
                                                }`}>
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

function AsciiView({ asciiArt, pathFound }) {
    return (
        <div className="font-mono text-sm bg-gray-900 text-green-300 rounded-lg p-4 overflow-x-auto whitespace-pre" style={{ fontFamily: "'Fira Code', 'Monaco', 'Consolas', monospace" }}>
            {asciiArt || (pathFound === false ? 'Nenhum caminho encontrado' : 'Calculando...')}
        </div>
    );
}

function GraphImageView({ graphImage }) {
    return (
        <div className="text-center">
            <img
                src={graphImage}
                alt="Visualização do grafo"
                className="max-w-full h-auto rounded-lg shadow-lg border border-gray-200 dark:border-gray-700 mx-auto"
                style={{ maxHeight: '500px' }}
            />
            <p className="mt-3 text-sm text-gray-500 dark:text-gray-400 font-mono">
                Gerado via Python (NetworkX + Matplotlib)
            </p>
        </div>
    );
}

function StatusCard({ title, status, value, icon, details }) {
    const statusStyles = {
        success: 'bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800 text-green-800 dark:text-green-200',
        warning: 'bg-yellow-50 dark:bg-yellow-900/20 border-yellow-200 dark:border-yellow-800 text-yellow-800 dark:text-yellow-200',
        error: 'bg-red-50 dark:bg-red-900/20 border-red-200 dark:border-red-800 text-red-800 dark:text-red-200',
        info: 'bg-blue-50 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800 text-blue-800 dark:text-blue-200',
    };

    return (
        <div className={`p-4 rounded-xl border ${statusStyles[status]}`}>
            <div className="flex items-start gap-3">
                <span className="text-2xl">{icon}</span>
                <div className="flex-1">
                    <div className="text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wide">{title}</div>
                    <div className="font-mono font-bold text-lg mt-1">{value}</div>
                    <div className="text-xs mt-1 opacity-80">{details}</div>
                </div>
            </div>
        </div>
    );
}

function DetailCard({ title, path, cost, found, message, vertexLabels }) {
    const letters = path ? path.map(i => vertexLabels[i]) : [];

    return (
        <div className="bg-gray-50 dark:bg-gray-700/50 rounded-xl p-4 border border-gray-200 dark:border-gray-600">
            <h4 className="font-semibold text-gray-900 dark:text-white mb-3 flex items-center gap-2">
                {title}
                <span className={`px-2 py-0.5 text-xs font-mono rounded ${found ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200' : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'}`}>
                    {found ? 'Encontrado' : 'Não encontrado'}
                </span>
            </h4>
            {found && letters.length > 0 && (
                <div className="space-y-2 mb-4">
                    <PathStepper steps={letters} compact />
                </div>
            )}
            <div className="text-sm text-gray-600 dark:text-gray-400 space-y-1">
                {found && <p><strong>Custo:</strong> <span className="font-mono">{cost}</span></p>}
                <p><strong>Vértices:</strong> <span className="font-mono">{path?.length || 0}</span></p>
                <p><strong>Status:</strong> <span className="font-mono">{message}</span></p>
            </div>
        </div>
    );
}

function PathStepper({ steps, costs, highlight = false, compact = false }) {
    return (
        <div className={`flex flex-wrap items-center gap-2 ${compact ? 'text-sm' : ''}`}>
            {steps.map((step, i) => (
                <React.Fragment key={i}>
                    <div className={`flex items-center gap-1 px-3 py-2 rounded-lg font-mono font-bold transition-all ${
                        highlight && i < steps.length - 1
                            ? 'bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-200 border border-green-300 dark:border-green-700'
                            : 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-200'
                    }`}>
                        {step}
                    </div>
                    {i < steps.length - 1 && (
                        <svg className={`w-5 h-5 ${highlight ? 'text-green-500' : 'text-gray-400'}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
    toast.success('Resultado exportado como JSON');
}

function exportCSV(result, matrix, vertexLabels) {
    let csv = 'Origem,Destino,Peso\n';
    for (let i = 0; i < matrix.length; i++) {
        for (let j = 0; j < matrix[i].length; j++) {
            if (matrix[i][j] > 0) {
                csv += `${vertexLabels[i]},${vertexLabels[j]},${matrix[i][j]}\n`;
            }
        }
    }
    if (result.caminhoMinimo?.caminhoEncontrado) {
        csv += '\n# Caminho Mínimo\nOrdem,Vértice\n';
        result.caminhoMinimo.caminho.forEach((v, i) => {
            csv += `${i + 1},${vertexLabels[v]}\n`;
        });
        csv += `\nCusto Total,${result.caminhoMinimo.custo}\n`;
    }
    downloadFile(csv, 'hiking-route-matrix.csv', 'text/csv');
    toast.success('Matriz exportada como CSV');
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