import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { TERRAIN_TYPES } from '../../services/api';

const getPointName = (index) => String.fromCharCode(65 + index);

export default function TerrainConfig({ config, onConfigChange, vertexCount, vertexLabels, t }) {
  const [activeSection, setActiveSection] = useState('elevation');
  const [terrainTypeGrid, setTerrainTypeGrid] = useState([]);

  // Initialize grids when vertexCount changes
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

  // Sync terrainTypeGrid with config
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
    if (key === 'allowDiagonals') {
      onConfigChange({ ...config, [key]: value });
      return;
    }
    const numValue = parseFloat(value);
    if (isNaN(numValue)) return;
    onConfigChange({ ...config, [key]: numValue });
  };

  const handleLoadMountainScenario = () => {
    // Mountain scenario: central peak, valleys, trails
    const size = vertexCount;
    const center = Math.floor(size / 2);

    const elevation = Array.from({ length: size }, (_, i) =>
      Array.from({ length: size }, (_, j) => {
        const dist = Math.sqrt(Math.pow(i - center, 2) + Math.pow(j - center, 2));
        const maxDist = Math.sqrt(2 * Math.pow(center, 2));
        return Math.round(100 * (1 - dist / maxDist) * 0.8); // Peak 80m at center
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
    toast.success(t('mountainLoaded'));
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
    if (!terrain) return '#71717a';
    const cost = terrain.cost;
    if (cost === Infinity) return '#1f2937';
    if (cost <= 2) return '#22c55e';
    if (cost <= 4) return '#84cc16';
    if (cost <= 6) return '#eab308';
    if (cost <= 8) return '#f97316';
    return '#ef4444';
  };

  const sections = [
    { id: 'elevation', label: t('elevationTab') },
    { id: 'terrain', label: t('terrainTab') },
    { id: 'params', label: t('paramsTab') },
  ];

  // Helper to format formulas with current config values
  const formatFormula = (formula) => {
    return formula
      .replace('{uphillPenalty}', (config.uphillPenaltyPerDegree * 100).toFixed(1))
      .replace('{downhillBonus}', (config.downhillBonusPerDegree * 100).toFixed(1));
  };

  return (
    <div className="h-full flex flex-col">
      {/* Section Tabs */}
      <div className="tab-nav mb-4">
        {sections.map((section) => (
          <button
            key={section.id}
            onClick={() => setActiveSection(section.id)}
            className={`tab-item ${activeSection === section.id ? 'tab-item-active' : 'tab-item-inactive'}`}
          >
            {section.label}
          </button>
        ))}
      </div>

      {/* Content */}
      <div className="flex-1 overflow-auto space-y-4">
        {/* Elevation Grid */}
        {activeSection === 'elevation' && (
          <div className="space-y-4 p-4">
            <div className="flex items-center justify-between">
              <h4 className="text-sm font-medium text-text-primary">{t('elevationTab')} (m)</h4>
              <div className="flex gap-2">
                <button
                  onClick={handleLoadMountainScenario}
                  className="btn-secondary text-sm"
                >
                  {t('loadMountainScenario')}
                </button>
                <button
                  onClick={handleClearTerrain}
                  className="btn-ghost text-sm"
                >
                  {t('clearTerrain')}
                </button>
              </div>
            </div>
            <ElevationGrid
              vertexCount={vertexCount}
              vertexLabels={vertexLabels}
              elevation={config.elevation}
              onChange={handleElevationChange}
            />
            <p className="text-xs text-text-muted">{t('elevationHelp')}</p>
          </div>
        )}

        {/* Terrain Types Grid */}
        {activeSection === 'terrain' && (
          <div className="space-y-4 p-4">
            <h4 className="text-sm font-medium text-text-primary">{t('terrainTab')}</h4>
            <TerrainTypeGrid
              vertexCount={vertexCount}
              vertexLabels={vertexLabels}
              terrainTypes={terrainTypeGrid}
              onChange={handleTerrainTypeChange}
              getTerrainColor={getTerrainColor}
            />
            <TerrainLegend t={t} />
          </div>
        )}

        {/* Global Parameters */}
        {activeSection === 'params' && (
          <div className="space-y-4 p-4">
            <h4 className="text-sm font-medium text-text-primary">{t('paramsTab')}</h4>

            <div className="grid grid-cols-2 gap-3">
              <ParameterInput
                label={t('cellSize')}
                value={config.cellSizeMeters}
                onChange={(v) => handleGlobalConfigChange('cellSizeMeters', v)}
                min={1}
                max={100}
                step={1}
              />
              <ParameterInput
                label={t('maxUphill')}
                value={config.maxUphillDegrees}
                onChange={(v) => handleGlobalConfigChange('maxUphillDegrees', v)}
                min={10}
                max={80}
                step={1}
                suffix="°"
              />
              <ParameterInput
                label={t('maxDownhill')}
                value={config.maxDownhillDegrees}
                onChange={(v) => handleGlobalConfigChange('maxDownhillDegrees', v)}
                min={10}
                max={90}
                step={1}
                suffix="°"
              />
              <ParameterInput
                label={t('uphillPenalty')}
                value={config.uphillPenaltyPerDegree * 100}
                onChange={(v) => handleGlobalConfigChange('uphillPenaltyPerDegree', v / 100)}
                min={0}
                max={50}
                step={1}
                suffix="%"
              />
              <ParameterInput
                label={t('downhillBonus')}
                value={config.downhillBonusPerDegree * 100}
                onChange={(v) => handleGlobalConfigChange('downhillBonusPerDegree', v / 100)}
                min={-50}
                max={0}
                step={1}
                suffix="%"
              />
              <ParameterInput
                label={t('minDownhillCost')}
                value={30}
                onChange={() => {}}
                min={10}
                max={100}
                step={5}
                suffix="%"
                disabled
                help={t('minDownhillCostHelp')}
              />
            </div>

            <div className="flex items-center gap-3 p-3 bg-surface-elevated border border-border rounded-lg">
              <input
                type="checkbox"
                id="allowDiagonals"
                checked={config.allowDiagonals}
                onChange={(e) => handleGlobalConfigChange('allowDiagonals', e.target.checked)}
                className="w-4 h-4 text-accent border-border-strong rounded focus:ring-2 focus:ring-accent/30 focus:ring-offset-2 focus:ring-offset-bg"
              />
              <label htmlFor="allowDiagonals" className="text-sm font-medium text-text-primary font-mono cursor-pointer">
                {t('allowDiagonals')}
              </label>
            </div>

            <div className="p-3 bg-surface-elevated border border-border rounded-lg text-sm">
              <strong className="text-text-primary font-mono">{t('formulasTitle')}</strong>
              <ul className="mt-2 space-y-1 text-text-secondary font-mono text-xs">
                {t('formulas').map((formula, idx) => (
                  <li key={idx}>{formatFormula(formula)}</li>
                ))}
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
    <div className="overflow-x-auto overflow-y-auto max-h-[400px] border border-border rounded-lg">
      <table className="w-full border-collapse font-mono text-sm">
        <thead className="sticky top-0 bg-surface">
          <tr>
            <th className="matrix-th-main w-14">m</th>
            {displayLabels.map((l, j) => (
              <th key={j} className="matrix-th w-14">{l}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {displayLabels.map((label, i) => (
            <tr key={i} className="matrix-row">
              <th className="matrix-row-th w-14">{label}</th>
              {displayLabels.map((_, j) => (
                <td key={j} className="matrix-td">
                  <input
                    type="number"
                    step="1"
                    value={elevation[i]?.[j] ?? 0}
                    onChange={(e) => onChange(i, j, e.target.value)}
                    className="input-mono w-10 h-10"
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

  // Only passable terrain types for selection
  const passableTypes = TERRAIN_TYPES.filter(t => t.cost !== Infinity);

  return (
    <div className="overflow-x-auto overflow-y-auto max-h-[400px] border border-border rounded-lg">
      <table className="w-full border-collapse font-mono text-xs">
        <thead className="sticky top-0 bg-surface">
          <tr>
            <th className="matrix-th-main w-14">type</th>
            {displayLabels.map((l, j) => (
              <th key={j} className="matrix-th w-20">{l}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {displayLabels.map((label, i) => (
            <tr key={i} className="matrix-row">
              <th className="matrix-row-th w-14">{label}</th>
              {displayLabels.map((_, j) => (
                <td key={j} className="matrix-td">
                  <select
                    value={terrainTypes[i]?.[j] ?? 'TERRA_BATIDA'}
                    onChange={(e) => onChange(i, j, e.target.value)}
                    className="input-compact w-full h-10 appearance-none bg-no-repeat bg-right bg-center pr-8 cursor-pointer"
                    style={{
                      backgroundColor: getTerrainColor(terrainTypes[i]?.[j] ?? 'TERRA_BATIDA') + '20',
                      backgroundImage: 'url("data:image/svg+xml,%3csvg xmlns=\'http://www.w3.org/2000/svg\' fill=\'none\' viewBox=\'0 0 20 20\'%3e%3cpath stroke=\'%2371717a\' stroke-linecap=\'round\' stroke-linejoin=\'round\' stroke-width=\'1.5\' d=\'M6 8l4 4 4-4\'/%3e%3c/svg%3e")',
                      backgroundSize: '16px 16px'
                    }}
                  >
                    {passableTypes.map(t => (
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

function TerrainLegend({ t }) {
  const passableTypes = TERRAIN_TYPES.filter(t => t.cost !== Infinity);

  return (
    <div className="flex flex-wrap gap-1.5 text-[10px]">
      <span className="text-text-muted font-medium">{t('terrainLegend')}</span>
      {passableTypes.map(t => (
        <span
          key={t.value}
          className="px-2 py-0.5 rounded font-mono text-white"
          style={{ backgroundColor: getTerrainColorHex(t.cost) }}
          title={t.label}
        >
          {t.value}
        </span>
      ))}
      <span className="px-2 py-0.5 rounded bg-border-strong text-text-primary font-mono" title="Plus 7 impassable types (water, rock, obstacles)...">
        +7 impassable
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
      <label className="block text-[10px] font-medium text-text-muted uppercase tracking-wider mb-1.5">{label}</label>
      <div className="relative">
        <input
          type="number"
          min={min}
          max={max}
          step={step}
          value={value}
          onChange={(e) => !disabled && onChange(e.target.value)}
          disabled={disabled}
          className={`input-compact w-full pr-8 ${disabled ? 'opacity-50 cursor-not-allowed' : ''}`}
        />
        {suffix && (
          <span className="absolute right-2 top-1/2 -translate-y-1/2 text-xs text-text-muted font-mono">{suffix}</span>
        )}
      </div>
      {help && <p className="text-[10px] text-text-muted">{help}</p>}
    </div>
  );
}