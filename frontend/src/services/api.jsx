import axios from "axios";

const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080';
const PYTHON_BASE = import.meta.env.VITE_PYTHON_BASE || 'http://localhost:5000';

export const api = axios.create({
  baseURL: API_BASE,
  headers: { "Content-Type": "application/json" },
  timeout: 30000,
});

export const pythonApi = axios.create({
  baseURL: PYTHON_BASE,
  headers: { "Content-Type": "application/json" },
  timeout: 30000,
  responseType: 'blob', // Para receber PNG
});

export const graphApi = {
  processarGrafo: (payload) => api.post('/api/grafo/processar', payload),
  health: () => api.get('/actuator/health'),
};

export const pythonGraphApi = {
  generateGraph: (payload) => pythonApi.post('/graph', payload),
  health: () => pythonApi.get('/health'),
};

// Tipos de terreno suportados pelo backend
export const TERRAIN_TYPES = [
  { value: 'TRILHA_BOA', label: 'Trilha Boa (1.0)', cost: 1.0 },
  { value: 'TRILHA_REGULAR', label: 'Trilha Regular (1.5)', cost: 1.5 },
  { value: 'TERRA_BATIDA', label: 'Terra Batida (2.0)', cost: 2.0 },
  { value: 'GRAMA', label: 'Grama (2.5)', cost: 2.5 },
  { value: 'CASCALHO', label: 'Cascalho (3.0)', cost: 3.0 },
  { value: 'ARVORE_ESPARSA', label: 'Árvore Esparsa (4.0)', cost: 4.0 },
  { value: 'AREIA', label: 'Areia (4.0)', cost: 4.0 },
  { value: 'NEVE', label: 'Neve (5.0)', cost: 5.0 },
  { value: 'ROCHA', label: 'Rocha (5.0)', cost: 5.0 },
  { value: 'LAMA', label: 'Lama (6.0)', cost: 6.0 },
  { value: 'GELO', label: 'Gelo (8.0)', cost: 8.0 },
  { value: 'ARVORE_DENSA', label: 'Árvore Densa (10.0)', cost: 10.0 },
  { value: 'AGUA_RASO', label: 'Água Rasa (10.0)', cost: 10.0 },
  { value: 'AGUA_FUNDO', label: 'Água Funda (∞)', cost: Infinity },
  { value: 'PAREDE_ROCHA', label: 'Parede de Rocha (∞)', cost: Infinity },
  { value: 'OBSTACULO', label: 'Obstáculo (∞)', cost: Infinity },
];

export const ALGORITHMS = [
  { value: 'dijkstra', label: 'Dijkstra (Ótimo garantido)', description: 'O((V+E)log V) - Caminho mínimo garantido em grafos ponderados' },
  { value: 'astar', label: 'A* (Heurística Manhattan)', description: 'O((V+E)log V) - Mais rápido em grades com heurística admissível' },
  { value: 'bfs', label: 'BFS (Não ponderado)', description: 'O(V+E) - Menor número de saltos, ignora pesos' },
];

export const HEURISTICS = [
  { value: 'MANHATTAN', label: 'Manhattan (4-dir)', description: 'Ideal para movimento ortogonal apenas' },
  { value: 'EUCLIDEAN', label: 'Euclidiana (qualquer dir)', description: 'Distância em linha reta' },
  { value: 'CHEBYSHEV', label: 'Chebyshev (8-dir)', description: 'Ideal para movimento em 8 direções' },
];

export default api;