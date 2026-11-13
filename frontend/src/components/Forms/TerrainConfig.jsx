import React, { useState, useEffect, useMemo } from 'react';
import { TERRAIN_TYPES } from '../../services/api';

const getPointName = (index) => String.fromCharCode(65 + index);

export default function TerrainConfig({ config, onConfigChange, vertexCount, vertexLabels }) {
    const [activeSection, setActiveSection] = useState('elevation');
    const [editingCell, setEditingCell] = useState(null);
    const [terrainTypeGrid, setTerrainTypeGrid] = useState([]);

    // Inicializar grids quando vertexCount muda
    useEffect(() => {
        const emptyElevation = Array.from({ length: vertexCount }, () =>
            Array.from({ length: vertexCount }, () => 0)
        );
        const emptyTerrain = Array.from({ length: vertexCount }, () =>
            Array.from({ length: vertexCount }, () => 'TERRA_BATIDA')
        );

        onConfigChange(prev => ({
            ...prev,
            elevation: prev.elevation.length === vertexCount ? prev.elevation : emptyElevation,
            terrainTypes: prev.terrainTypes.length === vertexCount ? prev.terrainTypes : emptyTerrain,
        }));

        setTerrainTypeGrid(emptyTerrain);
    }, [vertexCount, onConfigChange]);

    // Sincronizar terrainTypeGrid com config
    useEffect(() => {
        if (config.terrainTypes.length > 0) {
            setTerrainTypeGrid(config.terrainTypes);
        }
    }, [config.terrainTypes]);

    const handleElevationChange = (row, col, value) => {
        const numValue = value === '' ? 0 : parseFloat(value);
        if (isNaN(numValue)) return;

        const newElevation = config.elevation.map((r, i) =>
            i === row ? r.map((v, j) => j === col ? numValue : v) : r
        );
        onConfigChange({ ...config, elevation: newElevation });
    };

    const handleTerrainTypeChange = (row, col, value) => {
        const newTerrain = terrainTypeGrid.map((r, i) =>
            i === row ? r.map((v, j) => j === col ? value : v) : r
        );
        setTerrainTypeGrid(newTerrain);
        onConfigChange({ ...config, terrainTypes: newTerrain });
    };

    const handleGlobalConfigChange = (key, value) => {
        const numValue = parseFloat(value);
        if (isNaN(numValue)) return;
        onConfigChange({ ...config, [key]: numValue });
    };

    const handleLoadMountainScenario = () => {
        // Cenário de montanha: pico central, vales, trilhas
        const size = vertexCount;
        const center = Math.floor(size / 2);

        const elevation = Array.from({ length: size }, (_, i) =>
            Array.from({ length: size }, (_, j) => {
                const dist = Math.sqrt(Math.pow(i - center, 2) + Math.pow(j - center, 2));
                const maxDist = Math.sqrt(2 * Math.pow(center, 2));
                return Math.round(100 * (1 - dist / maxDist) * 0.8); // Pico 80m no centro
            })
        );

        const terrain = Array.from({ length: size }, (_, i) =>
            Array.from({ length: size }, (_, j) => {
                const dist = Math.sqrt(Math.pow(i - center, 2) + Math.pow(j - center, 2));
                if (dist < 1) return 'TRILHA_BOA';
                if (dist < 2) return 'TERRA_BATIDA';
                if (dist < 3) return 'GRAMA';
                return 'ARVORE_ESPARSA';
            })
        );

        onConfigChange({ ...config, elevation, terrainTypes: terrain });
        setTerrainTypeGrid(terrain);
        toast.success('Cenário de montanha carregado!');
    };

    const handleClearTerrain = () => {
        const emptyElevation = Array.from({ length: vertexCount }, () =>
            Array.from({ length: vertexCount }, () => 0)
        );
        const emptyTerrain = Array.from({ length: vertexCount }, () =>
            Array.from({ length: vertexCount }, () => 'TERRA_BATIDA')
        );
        onConfigChange({ ...config, elevation: emptyElevation, terrainTypes: emptyTerrain });
        setTerrainTypeGrid(emptyTerrain);
    };

    const getTerrainColor = (type) => {
        const terrain = TERRAIN_TYPES.find(t => t.value === type);
        if (!terrain) return '#9ca3af';
        const cost = terrain.cost;
        if (cost === Infinity) return '#1f2937'; // dark
        if (cost <= 2) return '#22c55e'; // green
        if (cost <= 4) return '#84cc16'; // lime
        if (cost <= 6) return '#eab308'; // yellow
        if (cost <= 8) return '#f97316'; // orange
        return '#ef4444'; // red
    };

    const sections = [
        { id: 'elevation', label: 'Elevação (m)', icon: '⛰️' },
        { id: 'terrain', label: 'Tipos de Terreno', icon: '🌲' },
        { id: 'params', label: 'Parâmetros', icon: '⚙️' },
    ];

    return (
        <div className="h-full flex flex-col">
            {/* Section Tabs */}
            <div className="flex gap-2 mb-4 border-b border-gray-200 dark:border-gray-700">
                {sections.map((section) => (
                    <button
                        key={section.id}
                        onClick={() => setActiveSection(section.id)}
                        className={`pb-2 px-3 text-sm font-mono flex items-center gap-1 transition-colors ${
                            activeSection === section.id
                                ? 'border-b-2 border-green-500 text-green-600 dark:text-green-400'
                                : 'border-b-2 border-transparent text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200'
                        }`}
                    >
                        <span>{section.icon}</span>
                        {section.label}
                    </button>
                ))}
            </div>

            {/* Content */}
            <div className="flex-1 overflow-auto space-y-4">
                {/* Elevation Grid */}
                {activeSection === 'elevation' && (
                    <div className="space-y-4">
                        <div className="flex items-center justify-between">
                            <h4 className="font-medium text-gray-900 dark:text-white">Matriz de Elevação (metros)</h4>
                            <div className="flex gap-2">
                                <button
                                    onClick={handleLoadMountainScenario}
                                    className="px-3 py-1 text-xs bg-blue-100 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300 rounded hover:bg-blue-200 dark:hover:bg-blue-900/50"
                                >
                                    Cenário Montanha
                                </button>
                                <button
                                    onClick={handleClearTerrain}
                                    className="px-3 py-1 text-xs bg-gray-100 text-gray-700 dark:bg-gray-700 dark:text-gray-300 rounded hover:bg-gray-200 dark:hover:bg-gray-600"
                                >
                                    Limpar
                                </button>
                            </div>
                        </div>
                        <ElevationGrid
                            vertexCount={vertexCount}
                            vertexLabels={vertexLabels}
                            elevation={config.elevation}
                            onChange={handleElevationChange}
                        />
                        <p className="text-xs text-gray-500 dark:text-gray-400">
                            Elevação relativa em metros. Diferenças positivas = subida, negativas = descida.
                        </p>
                    </div>
                )}

                {/* Terrain Types Grid */}
                {activeSection === 'terrain' && (
                    <div className="space-y-4">
                        <h4 className="font-medium text-gray-900 dark:text-white">Matriz de Tipos de Terreno</h4>
                        <TerrainTypeGrid
                            vertexCount={vertexCount}
                            vertexLabels={vertexLabels}
                            terrainTypes={terrainTypeGrid}
                            onChange={handleTerrainTypeChange}
                            getTerrainColor={getTerrainColor}
                        />
                        <TerrainLegend />
                    </div>
                )}

                {/* Global Parameters */}
                {activeSection === 'params' && (
                    <div className="space-y-4 max-w-md">
                        <h4 className="font-medium text-gray-900 dark:text-white">Parâmetros de Cálculo</h4>

                        <div className="grid grid-cols-2 gap-4">
                            <ParameterInput
                                label="Tamanho da Célula (m)"
                                value={config.cellSizeMeters}
                                onChange={(v) => handleGlobalConfigChange('cellSizeMeters', v)}
                                min={1}
                                max={100}
                                step={1}
                            />
                            <ParameterInput
                                label="Máx. Subida (°)"
                                value={config.maxUphillDegrees}
                                onChange={(v) => handleGlobalConfigChange('maxUphillDegrees', v)}
                                min={10}
                                max={80}
                                step={1}
                            />
                            <ParameterInput
                                label="Máx. Descida (°)"
                                value={config.maxDownhillDegrees}
                                onChange={(v) => handleGlobalConfigChange('maxDownhillDegrees', v)}
                                min={10}
                                max={90}
                                step={1}
                            />
                            <ParameterInput
                                label="Penalidade Subida (%/°)"
                                value={config.uphillPenaltyPerDegree * 100}
                                onChange={(v) => handleGlobalConfigChange('uphillPenaltyPerDegree', v / 100)}
                                min={0}
                                max={50}
                                step={1}
                                suffix="%"
                            />
                            <ParameterInput
                                label="Bônus Descida (%/°)"
                                value={config.downhillBonusPerDegree * 100}
                                onChange={(v) => handleGlobalConfigChange('downhillBonusPerDegree', v / 100)}
                                min={-50}
                                max={0}
                                step={1}
                                suffix="%"
                            />
                            <ParameterInput
                                label="Mín. Custo Descida (%)"
                                value={30}
                                onChange={() => {}}
                                min={10}
                                max={100}
                                step={5}
                                suffix="%"
                                disabled
                                help="Fixo em 30% (fator de segurança)"
                            />
                        </div>

                        <div className="flex items-center gap-3 p-3 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
                            <input
                                type="checkbox"
                                id="allowDiagonals"
                                checked={config.allowDiagonals}
                                onChange={(e) => handleGlobalConfigChange('allowDiagonals', e.target.checked)}
                                className="w-5 h-5 text-green-600 border-gray-300 rounded focus:ring-green-500"
                            />
                            <label htmlFor="allowDiagonals" className="font-mono text-gray-900 dark:text-white cursor-pointer">
                                Permitir movimento diagonal (×√2 ≈ 1.414)
                            </label>
                        </div>

                        <div className="p-3 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg text-sm">
                            <strong className="text-green-800 dark:text-green-200">Fórmulas aplicadas:</strong>
                            <ul className="mt-2 space-y-1 text-green-700 dark:text-green-300 font-mono text-xs">
                                <li>Subida: custo × (1 + inclinação° × {config.uphillPenaltyPerDegree})</li>
                                <li>Descida: custo × max(0.3, 1 + inclinação° × {config.downhillBonusPerDegree})</li>
                                <li>Diagonal: distância × √2 × custo_terreno</li>
                            </ul>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

// Sub-components
function ElevationGrid({ vertexCount, vertexLabels, elevation, onChange }) {
    const maxDisplay = 12;
    const isLarge = vertexCount > maxDisplay;
    const displayCount = isLarge ? maxDisplay : vertexCount;
    const displayLabels = vertexLabels.slice(0, displayCount);

    return (
        <div className="overflow-x-auto overflow-y-auto max-h-[400px] border border-gray-200 dark:border-gray-700 rounded-lg">
            <table className="w-full border-collapse font-mono text-sm">
                <thead className="sticky top-0 bg-gray-50 dark:bg-gray-800">
                    <tr>
                        <th className="w-12 px-2 py-2 text-center border border-gray-200 dark:border-gray-700 font-semibold">m</th>
                        {displayLabels.map((l, j) => (
                            <th key={j} className="w-16 px-2 py-2 text-center border border-gray-200 dark:border-gray-700 font-semibold">{l}</th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {displayLabels.map((label, i) => (
                        <tr key={i}>
                            <th className="w-12 px-2 py-1 text-center border border-gray-200 dark:border-gray-700 font-semibold sticky left-0 bg-white dark:bg-gray-800">{label}</th>
                            {displayLabels.map((_, j) => (
                                <td key={j} className="px-1 py-1 border border-gray-200 dark:border-gray-700">
                                    <input
                                        type="number"
                                        step="1"
                                        value={elevation[i]?.[j] ?? 0}
                                        onChange={(e) => onChange(i, j, e.target.value)}
                                        className="w-full h-10 px-1 text-center border border-gray-300 dark:border-gray-600 rounded focus:ring-2 focus:ring-green-500 bg-white dark:bg-gray-700"
                                    />
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

function TerrainTypeGrid({ vertexCount, vertexLabels, terrainTypes, onChange, getTerrainColor }) {
    const maxDisplay = 10;
    const isLarge = vertexCount > maxDisplay;
    const displayCount = isLarge ? maxDisplay : vertexCount;
    const displayLabels = vertexLabels.slice(0, displayCount);

    return (
        <div className="overflow-x-auto overflow-y-auto max-h-[400px] border border-gray-200 dark:border-gray-700 rounded-lg">
            <table className="w-full border-collapse font-mono text-xs">
                <thead className="sticky top-0 bg-gray-50 dark:bg-gray-800">
                    <tr>
                        <th className="w-12 px-2 py-2 text-center border border-gray-200 dark:border-gray-700 font-semibold">tipo</th>
                        {displayLabels.map((l, j) => (
                            <th key={j} className="w-24 px-2 py-2 text-center border border-gray-200 dark:border-gray-700 font-semibold">{l}</th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {displayLabels.map((label, i) => (
                        <tr key={i}>
                            <th className="w-12 px-2 py-1 text-center border border-gray-200 dark:border-gray-700 font-semibold sticky left-0 bg-white dark:bg-gray-800">{label}</th>
                            {displayLabels.map((_, j) => (
                                <td key={j} className="px-1 py-1 border border-gray-200 dark:border-gray-700">
                                    <select
                                        value={terrainTypes[i]?.[j] ?? 'TERRA_BATIDA'}
                                        onChange={(e) => onChange(i, j, e.target.value)}
                                        className="w-full h-10 px-1 border border-gray-300 dark:border-gray-600 rounded focus:ring-2 focus:ring-green-500 bg-white dark:bg-gray-700 cursor-pointer"
                                        style={{ backgroundColor: getTerrainColor(terrainTypes[i]?.[j] ?? 'TERRA_BATIDA') + '20' }}
                                    >
                                        {TERRAIN_TYPES.slice(0, 8).map(t => (
                                            <option key={t.value} value={t.value} style={{ backgroundColor: getTerrainColor(t.value) + '20' }}>
                                                {t.label}
                                            </option>
                                        ))}
                                    </select>
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

function TerrainLegend() {
    return (
        <div className="flex flex-wrap gap-2 text-xs">
            {TERRAIN_TYPES.slice(0, 8).map(t => (
                <span
                    key={t.value}
                    className="px-2 py-1 rounded text-white font-mono"
                    style={{ backgroundColor: t.cost === Infinity ? '#1f2937' : getTerrainColorHex(t.cost) }}
                    title={t.label}
                >
                    {t.value}
                </span>
            ))}
            <span className="px-2 py-1 rounded bg-gray-700 text-white font-mono" title="E mais 7 tipos (água, rocha, obstáculos)...">
                +7 mais
            </span>
        </div>
    );
}

function getTerrainColorHex(cost) {
    if (cost <= 2) return '#22c55e';
    if (cost <= 4) return '#84cc16';
    if (cost <= 6) return '#eab308';
    if (cost <= 8) return '#f97316';
    return '#ef4444';
}

function ParameterInput({ label, value, onChange, min, max, step, suffix = '', disabled = false, help }) {
    return (
        <div className="space-y-1">
            <label className="block text-xs font-medium text-gray-700 dark:text-gray-300">{label}</label>
            <div className="relative">
                <input
                    type="number"
                    min={min}
                    max={max}
                    step={step}
                    value={value}
                    onChange={(e) => !disabled && onChange(e.target.value)}
                    disabled={disabled}
                    className={`w-full px-3 py-2 pr-8 border rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent text-sm font-mono ${
                        disabled
                            ? 'bg-gray-100 dark:bg-gray-700 text-gray-500 cursor-not-allowed'
                            : 'bg-white dark:bg-gray-700 border-gray-300 dark:border-gray-600 text-gray-900 dark:text-white'
                    }`}
                />
                {suffix && (
                    <span className="absolute right-3 top-1/2 -translate-y-1/2 text-xs text-gray-500 font-mono">{suffix}</span>
                )}
            </div>
            {help && <p className="text-xs text-gray-500 dark:text-gray-400">{help}</p>}
        </div>
    );
}

// Import toast for TerrainConfig
import { toast } from 'react-toastify';