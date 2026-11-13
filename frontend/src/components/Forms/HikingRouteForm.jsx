import React, { useState, useCallback, useEffect } from 'react';
import { toast } from 'react-toastify';
import { graphApi, pythonGraphApi, ALGORITHMS, HEURISTICS, TERRAIN_TYPES } from '../../services/api';
import PathVisualization from '../Visualization/PathVisualization';
import TerrainConfig from '../Forms/TerrainConfig';

const getPointName = (index) => String.fromCharCode(65 + index);

export default function HikingRouteForm() {
    const [algorithm, setAlgorithm] = useState('dijkstra');
    const [heuristic, setHeuristic] = useState('MANHATTAN');
    const [vertexCount, setVertexCount] = useState(5);
    const [vertexLabels, setVertexLabels] = useState([]);
    const [matrix, setMatrix] = useState([]);
    const [origin, setOrigin] = useState('');
    const [destination, setDestination] = useState('');
    const [showTerrainConfig, setShowTerrainConfig] = useState(false);
    const [terrainConfig, setTerrainConfig] = useState({
        elevation: [],
        terrainTypes: [],
        cellSizeMeters: 10,
        allowDiagonals: true,
        maxUphillDegrees: 45,
        maxDownhillDegrees: 60,
        uphillPenaltyPerDegree: 0.1,
        downhillBonusPerDegree: -0.05,
    });
    const [result, setResult] = useState(null);
    const [graphImage, setGraphImage] = useState(null);
    const [loading, setLoading] = useState(false);
    const [activeTab, setActiveTab] = useState('matrix');

    // Inicializar labels e matriz quando vertexCount mudar
    useEffect(() => {
        const newLabels = Array.from({ length: vertexCount }, (_, i) => getPointName(i));
        setVertexLabels(newLabels);

        const newMatrix = Array.from({ length: vertexCount }, (_, i) =>
            Array.from({ length: vertexCount }, (_, j) =>
                i === j ? 0 : (i < j ? '' : 0)
            )
        );
        setMatrix(newMatrix);

        if (origin && origin.charCodeAt(0) - 65 >= vertexCount) setOrigin('');
        if (destination && destination.charCodeAt(0) - 65 >= vertexCount) setDestination('');
    }, [vertexCount]);

    const handleMatrixChange = (row, col, value) => {
        const numValue = value === '' ? 0 : parseFloat(value);
        if (isNaN(numValue)) return;

        const newMatrix = matrix.map((r, i) =>
            i === row ? r.map((v, j) => j === col ? numValue : v) : r
        );
        setMatrix(newMatrix);

        // Manter matriz simétrica para grafos não direcionados (opcional)
        // if (i !== j) newMatrix[col][row] = numValue;
    };

    const handleOriginChange = (e) => setOrigin(e.target.value);
    const handleDestinationChange = (e) => setDestination(e.target.value);

    const validateForm = () => {
        if (!origin || !destination) {
            toast.error('Selecione origem e destino');
            return false;
        }
        if (origin === destination) {
            toast.error('Origem e destino devem ser diferentes');
            return false;
        }
        const originIdx = origin.charCodeAt(0) - 65;
        const destIdx = destination.charCodeAt(0) - 65;

        // Verificar se há caminho possível (pelo menos uma conexão da origem)
        const hasOutgoing = matrix[originIdx].some((v, i) => i !== originIdx && v > 0);
        const hasIncoming = matrix.some((row, i) => i !== originIdx && row[originIdx] > 0);

        if (!hasOutgoing && !hasIncoming) {
            toast.warn('Origem isolada - sem conexões de saída ou entrada');
        }
        return true;
    };

    const buildPayload = () => {
        const payload = {
            matriz: matrix,
            origem: origin.charCodeAt(0) - 65,
            destino: destination.charCodeAt(0) - 65,
            algoritmo: algorithm,
        };

        if (algorithm === 'astar') {
            payload.heuristica = heuristic;
        }

        if (showTerrainConfig) {
            payload.terreno = terrainConfig;
        }

        return payload;
    };

    const handleSubmit = async () => {
        if (!validateForm()) return;

        setLoading(true);
        setResult(null);
        setGraphImage(null);

        try {
            const payload = buildPayload();
            console.log('Enviando payload:', payload);

            const response = await graphApi.processarGrafo(payload);
            console.log('Resposta backend:', response.data);

            setResult(response.data);
            toast.success('Rota calculada com sucesso!');

            // Gerar visualização do grafo
            try {
                const graphPayload = {
                    matrix: matrix,
                    directed: true,
                    labels: vertexLabels,
                    path: response.data.caminhoMinimo?.caminhoEncontrado ? response.data.caminhoMinimo.caminho : null,
                };
                const graphResponse = await pythonGraphApi.generateGraph(graphPayload);
                const imageUrl = URL.createObjectURL(graphResponse.data);
                setGraphImage(imageUrl);
            } catch (graphError) {
                console.warn('Erro ao gerar visualização:', graphError);
                toast.info('Rota calculada, mas falha ao gerar gráfico');
            }
        } catch (error) {
            console.error('Erro ao calcular rota:', error);
            const message = error.response?.data?.message || error.message || 'Erro desconhecido';
            toast.error(`Erro: ${message}`);
        } finally {
            setLoading(false);
        }
    };

    const handleClear = () => {
        setMatrix(Array.from({ length: vertexCount }, (_, i) =>
            Array.from({ length: vertexCount }, (_, j) => i === j ? 0 : (i < j ? '' : 0))
        ));
        setOrigin('');
        setDestination('');
        setResult(null);
        setGraphImage(null);
    };

    const handleLoadExample = () => {
        // Exemplo: grafo simples A-B-C-D-E com pesos variados
        const exampleMatrix = [
            [0, 10, 0, 0, 15],  // A
            [10, 0, 5, 0, 0],   // B
            [0, 5, 0, 8, 0],    // C
            [0, 0, 8, 0, 12],   // D
            [15, 0, 0, 12, 0],  // E
        ];
        setMatrix(exampleMatrix);
        setOrigin('A');
        setDestination('D');
        toast.success('Exemplo carregado: Grafo 5 vértices (A-B-C-D-E)');
    };

    const pointOptions = vertexLabels;

    return (
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
            {/* Algorithm Selection */}
            <section className="mb-8">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
                    <svg className="w-5 h-5 text-green-600 dark:text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                    </svg>
                    Algoritmo de Busca
                </h2>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {ALGORITHMS.map((algo) => (
                        <label
                            key={algo.value}
                            className={`relative cursor-pointer p-4 rounded-xl border-2 transition-all ${
                                algorithm === algo.value
                                    ? 'border-green-500 bg-green-50 dark:bg-green-900/20'
                                    : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600'
                            }`}
                        >
                            <input
                                type="radio"
                                name="algorithm"
                                value={algo.value}
                                checked={algorithm === algo.value}
                                onChange={(e) => setAlgorithm(e.target.value)}
                                className="sr-only"
                            />
                            <div className="font-mono font-semibold text-gray-900 dark:text-white">
                                {algo.label}
                            </div>
                            <div className="text-sm text-gray-600 dark:text-gray-400 mt-1">
                                {algo.description}
                            </div>
                            {algorithm === algo.value && (
                                <div className="absolute top-2 right-2 w-5 h-5 bg-green-500 text-white rounded-full flex items-center justify-center text-xs">
                                    ✓
                                </div>
                            )}
                        </label>
                    ))}
                </div>

                {algorithm === 'astar' && (
                    <div className="mt-4">
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                            Heurística A*
                        </label>
                        <select
                            value={heuristic}
                            onChange={(e) => setHeuristic(e.target.value)}
                            className="w-full md:w-64 px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                        >
                            {HEURISTICS.map((h) => (
                                <option key={h.value} value={h.value}>
                                    {h.label} - {h.description}
                                </option>
                            ))}
                        </select>
                    </div>
                )}
            </section>

            {/* Tabs */}
            <div className="mb-6 border-b border-gray-200 dark:border-gray-700">
                <nav className="flex gap-8" aria-label="Tabs">
                    <button
                        onClick={() => setActiveTab('matrix')}
                        className={`pb-3 px-1 border-b-2 font-mono text-sm transition-colors ${
                            activeTab === 'matrix'
                                ? 'border-green-500 text-green-600 dark:text-green-400'
                                : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
                        }`}
                    >
                        Matriz de Adjacência
                    </button>
                    {showTerrainConfig && (
                        <button
                            onClick={() => setActiveTab('terrain')}
                            className={`pb-3 px-1 border-b-2 font-mono text-sm transition-colors ${
                                activeTab === 'terrain'
                                    ? 'border-green-500 text-green-600 dark:text-green-400'
                                    : 'border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
                            }`}
                        >
                            Config. Terreno
                        </button>
                    )}
                </nav>
            </div>

            {/* Main Content */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                {/* Left Panel: Matrix Input */}
                <div className="lg:col-span-2">
                    <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-6">
                        {activeTab === 'matrix' && (
                            <MatrixInput
                                vertexCount={vertexCount}
                                vertexLabels={vertexLabels}
                                matrix={matrix}
                                onMatrixChange={handleMatrixChange}
                                onVertexCountChange={setVertexCount}
                            />
                        )}
                        {activeTab === 'terrain' && (
                            <TerrainConfig
                                config={terrainConfig}
                                onConfigChange={setTerrainConfig}
                                vertexCount={vertexCount}
                                vertexLabels={vertexLabels}
                            />
                        )}
                    </div>
                </div>

                {/* Right Panel: Controls & Results */}
                <div className="space-y-4">
                    {/* Vertex Count */}
                    <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-4">
                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                            Número de Vértices (2-26)
                        </label>
                        <input
                            type="number"
                            min="2"
                            max="26"
                            value={vertexCount}
                            onChange={(e) => setVertexCount(parseInt(e.target.value) || 2)}
                            className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                        />
                    </div>

                    {/* Origin/Destination */}
                    <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-4 space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                Origem
                            </label>
                            <select
                                value={origin}
                                onChange={handleOriginChange}
                                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                            >
                                <option value="">Selecione...</option>
                                {pointOptions.map(p => (
                                    <option key={p} value={p}>{p}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                Destino
                            </label>
                            <select
                                value={destination}
                                onChange={handleDestinationChange}
                                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white focus:ring-2 focus:ring-green-500 focus:border-transparent"
                            >
                                <option value="">Selecione...</option>
                                {pointOptions.map(p => (
                                    <option key={p} value={p}>{p}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    {/* Terrain Toggle */}
                    <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-4">
                        <label className="flex items-center gap-3 cursor-pointer">
                            <input
                                type="checkbox"
                                checked={showTerrainConfig}
                                onChange={(e) => setShowTerrainConfig(e.target.checked)}
                                className="w-5 h-5 text-green-600 border-gray-300 rounded focus:ring-green-500"
                            />
                            <span className="font-mono text-gray-900 dark:text-white">
                                Configuração Avançada de Terreno
                            </span>
                        </label>
                        <p className="text-sm text-gray-500 dark:text-gray-400 mt-2 ml-8">
                            Elevação, 15 tipos de terreno, inclinações, diagonais
                        </p>
                    </div>

                    {/* Action Buttons */}
                    <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-4 space-y-3">
                        <button
                            onClick={handleSubmit}
                            disabled={loading || !origin || !destination}
                            className="w-full bg-green-600 hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white font-bold font-mono py-3 px-4 rounded-lg transition-colors flex items-center justify-center gap-2"
                        >
                            {loading ? (
                                <>
                                    <svg className="animate-spin h-5 w-5" fill="none" viewBox="0 0 24 24">
                                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                                        <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                                    </svg>
                                    Calculando...
                                </>
                            ) : (
                                <>
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 9l3 3m0 0l-3 3m3-3H8m13 0a9 9 0 11-18 0 9 9 0 0118 0z" />
                                    </svg>
                                    Calcular Rota Ótima
                                </>
                            )}
                        </button>

                        <div className="grid grid-cols-2 gap-2">
                            <button
                                onClick={handleLoadExample}
                                disabled={loading}
                                className="bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 text-white font-mono py-2 px-4 rounded-lg transition-colors text-sm"
                            >
                                Carregar Exemplo
                            </button>
                            <button
                                onClick={handleClear}
                                disabled={loading}
                                className="bg-gray-600 hover:bg-gray-700 disabled:bg-gray-400 text-white font-mono py-2 px-4 rounded-lg transition-colors text-sm"
                            >
                                Limpar
                            </button>
                        </div>
                    </div>

                    {/* Quick Stats */}
                    {result && (
                        <div className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-4">
                            <h3 className="font-semibold text-gray-900 dark:text-white mb-3 flex items-center gap-2">
                                <svg className="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
                                Resumo Rápido
                            </h3>
                            <div className="grid grid-cols-2 gap-3 text-sm">
                                <div className="p-2 bg-green-50 dark:bg-green-900/20 rounded-lg">
                                    <div className="text-green-700 dark:text-green-300 font-mono font-bold">
                                        {result.caminhoMinimo?.caminhoEncontrado ? '✓ Encontrado' : '✗ Não encontrado'}
                                    </div>
                                    <div className="text-gray-600 dark:text-gray-400">Caminho Mínimo</div>
                                </div>
                                <div className="p-2 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                                    <div className="text-blue-700 dark:text-blue-300 font-mono font-bold">
                                        {result.caminhoMinimo?.custo !== undefined ? result.caminhoMinimo.custo : 'N/A'}
                                    </div>
                                    <div className="text-gray-600 dark:text-gray-400">Custo Total</div>
                                </div>
                                <div className="p-2 bg-purple-50 dark:bg-purple-900/20 rounded-lg">
                                    <div className="text-purple-700 dark:text-purple-300 font-mono font-bold">
                                        {result.caminhoMaximo?.caminhoEncontrado ? '✓ Encontrado' : '✗ Infinito/Erro'}
                                    </div>
                                    <div className="text-gray-600 dark:text-gray-400">Caminho Máximo</div>
                                </div>
                                <div className="p-2 bg-orange-50 dark:bg-orange-900/20 rounded-lg">
                                    <div className="text-orange-700 dark:text-orange-300 font-mono font-bold">
                                        {result.numeroVertices || vertexCount}
                                    </div>
                                    <div className="text-gray-600 dark:text-gray-400">Vértices</div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Results Section */}
            {(result || graphImage) && (
                <div className="mt-8">
                    <PathVisualization
                        result={result}
                        graphImage={graphImage}
                        vertexLabels={vertexLabels}
                        algorithm={algorithm}
                        heuristic={heuristic}
                        matrix={matrix}
                    />
                </div>
            )}
        </div>
    );
}

// Sub-component: Matrix Input
function MatrixInput({ vertexCount, vertexLabels, matrix, onMatrixChange, onVertexCountChange }) {
    const maxDisplaySize = 12;
    const isLarge = vertexCount > maxDisplaySize;
    const displayCount = isLarge ? maxDisplaySize : vertexCount;
    const displayLabels = vertexLabels.slice(0, displayCount);

    return (
        <div>
            <div className="flex items-center justify-between mb-4">
                <h3 className="font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                    <svg className="w-5 h-5 text-green-600 dark:text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
                    </svg>
                    Matriz de Adjacência ({vertexCount}×{vertexCount})
                </h3>
                {isLarge && (
                    <span className="text-xs text-gray-500 dark:text-gray-400 bg-gray-100 dark:bg-gray-800 px-2 py-1 rounded">
                        Mostrando {maxDisplaySize}×{maxDisplaySize} de {vertexCount}×{vertexCount}
                    </span>
                )}
            </div>

            <div className="overflow-x-auto overflow-y-auto max-h-[500px] border border-gray-200 dark:border-gray-700 rounded-lg">
                <table className="w-full border-collapse font-mono text-sm">
                    <thead className="sticky top-0 bg-gray-50 dark:bg-gray-800">
                        <tr>
                            <th className="w-12 px-2 py-2 text-center border border-gray-200 dark:border-gray-700 font-semibold">
                                ↓→
                            </th>
                            {displayLabels.map((label, j) => (
                                <th key={j} className="w-16 px-2 py-2 text-center border border-gray-200 dark:border-gray-700 font-semibold text-gray-700 dark:text-gray-300">
                                    {label}
                                </th>
                            ))}
                        </tr>
                    </thead>
                    <tbody>
                        {displayLabels.map((label, i) => (
                            <tr key={i} className={i % 2 === 0 ? 'bg-white dark:bg-gray-800' : 'bg-gray-50 dark:bg-gray-700/50'}>
                                <th className="w-12 px-2 py-1 text-center border border-gray-200 dark:border-gray-700 font-semibold text-gray-700 dark:text-gray-300 sticky left-0 bg-white dark:bg-gray-800">
                                    {label}
                                </th>
                                {displayLabels.map((_, j) => (
                                    <td key={j} className="px-1 py-1 border border-gray-200 dark:border-gray-700">
                                        {i === j ? (
                                            <input
                                                type="text"
                                                value="0"
                                                readOnly
                                                className="w-full h-10 px-1 text-center text-gray-400 bg-gray-100 dark:bg-gray-700 dark:text-gray-500 border-none focus:outline-none rounded"
                                                title="Diagonal principal = 0"
                                            />
                                        ) : (
                                            <input
                                                type="number"
                                                min="0"
                                                step="0.1"
                                                value={matrix[i]?.[j] === 0 ? '' : matrix[i]?.[j] ?? ''}
                                                onChange={(e) => onMatrixChange(i, j, e.target.value)}
                                                className="w-full h-10 px-1 text-center border border-gray-300 dark:border-gray-600 rounded focus:ring-2 focus:ring-green-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white placeholder-gray-400"
                                                placeholder={i < j ? 'peso' : '0'}
                                                title={`Peso ${label}→${displayLabels[j]}`}
                                            />
                                        )}
                                    </td>
                                ))}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            <div className="mt-3 text-sm text-gray-500 dark:text-gray-400 space-y-1">
                <p>• Diagonal principal (origem = destino) é sempre 0</p>
                <p>• Deixe vazio ou 0 para indicar ausência de aresta</p>
                <p>• Valores positivos = peso/custo da aresta</p>
                <p>• Grafo direcionado: preencha ambas direções se bidirecional</p>
            </div>
        </div>
    );
}