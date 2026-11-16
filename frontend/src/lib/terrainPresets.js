// Terrain presets used to enrich the graph visualization.
// Each preset generates an N x N elevation grid (meters) and an N x N
// terrain-type grid (same enum names as the backend TerrainCostCalculator).

export const TERRAIN_PRESETS = ['flat', 'mountain', 'trail'];

const grid = (n, fn) =>
  Array.from({ length: n }, (_, i) => Array.from({ length: n }, (_, j) => fn(i, j)));

const distanceToCenter = (i, j, center) => Math.hypot(i - center, j - center);

export function buildTerrainConfig(preset, n) {
  const center = Math.floor(n / 2);
  const maxDist = Math.max(1, Math.hypot(center, center));

  let elevation;
  let terrainTypes;

  switch (preset) {
    case 'mountain':
      // Peak of ~80 m in the middle, forest on the outskirts.
      elevation = grid(n, (i, j) =>
        Math.round(80 * (1 - distanceToCenter(i, j, center) / maxDist)));
      terrainTypes = grid(n, (i, j) => {
        const d = distanceToCenter(i, j, center);
        if (d < 1) return 'TRILHA_BOA';
        if (d < 2) return 'TERRA_BATIDA';
        if (d < 3) return 'GRAMA';
        return 'ARVORE_ESPARSA';
      });
      break;
    case 'trail':
      // Flat ground with a good trail along the diagonal and rough terrain elsewhere.
      elevation = grid(n, () => 0);
      terrainTypes = grid(n, (i, j) => {
        if (Math.abs(i - j) <= 1) return 'TRILHA_BOA';
        if ((i + j) % 3 === 0) return 'CASCALHO';
        return 'GRAMA';
      });
      break;
    case 'flat':
    default:
      elevation = grid(n, () => 0);
      terrainTypes = grid(n, () => 'TERRA_BATIDA');
  }

  return { elevation, terrainTypes, cellSizeMeters: 10, allowDiagonals: true };
}
