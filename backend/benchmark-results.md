# Resultados do Benchmark

| Grafo | Algoritmo | Vértices | Arestas | Tempo Médio (ms) | Nós Expandidos | Nós Visitados | Encontrado | Custo |
|-------|-----------|----------|---------|------------------|----------------|---------------|------------|-------|
| Grade 10x10 (densidade=70%) | Dijkstra (Min-Heap) | 100 | 279 | 0,42 | 87 | 88 | ✓ | 63,05 |
| Grade 10x10 (densidade=70%) | BFS (não ponderado) | 100 | 279 | 0,44 | 99 | 100 | ✓ | 18,00 |
| Grade 10x10 (densidade=70%) | A* (heurística nula = Dijkstra) | 100 | 279 | 0,44 | 87 | 88 | ✓ | 63,05 |
| Grade 10x10 (densidade=70%) | A* (MANHATTAN) | 100 | 279 | 0,09 | 78 | 79 | ✓ | 63,05 |
| Grade 10x10 (densidade=70%) | A* (MANHATTAN) | 100 | 279 | 0,11 | 84 | 85 | ✓ | 73,61 |
| Grade 10x10 (densidade=70%) | A* (MANHATTAN) | 100 | 279 | 0,14 | 82 | 83 | ✓ | 66,13 |
| Grade 10x10 (densidade=70%) | A* (MANHATTAN) | 100 | 279 | 0,15 | 78 | 79 | ✓ | 85,12 |
| Grade 10x10 (densidade=70%) | A* (EUCLIDIANA) | 100 | 279 | 0,14 | 79 | 80 | ✓ | 63,05 |
| Grade 10x10 (densidade=70%) | A* (EUCLIDIANA) | 100 | 279 | 0,08 | 84 | 85 | ✓ | 73,61 |
| Grade 10x10 (densidade=70%) | A* (EUCLIDIANA) | 100 | 279 | 0,10 | 83 | 84 | ✓ | 66,13 |
| Grade 10x10 (densidade=70%) | A* (EUCLIDIANA) | 100 | 279 | 0,11 | 78 | 79 | ✓ | 85,12 |
| Grade 20x20 (densidade=70%) | Dijkstra (Min-Heap) | 400 | 1172 | 1,57 | 399 | 400 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | BFS (não ponderado) | 400 | 1172 | 1,66 | 399 | 400 | ✓ | 38,00 |
| Grade 20x20 (densidade=70%) | A* (heurística nula = Dijkstra) | 400 | 1172 | 1,48 | 399 | 400 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (MANHATTAN) | 400 | 1172 | 1,69 | 396 | 397 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (MANHATTAN) | 400 | 1172 | 0,71 | 394 | 395 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (MANHATTAN) | 400 | 1172 | 1,09 | 396 | 397 | ✓ | 156,87 |
| Grade 20x20 (densidade=70%) | A* (MANHATTAN) | 400 | 1172 | 0,77 | 365 | 366 | ✓ | 167,00 |
| Grade 20x20 (densidade=70%) | A* (EUCLIDIANA) | 400 | 1172 | 0,82 | 396 | 397 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (EUCLIDIANA) | 400 | 1172 | 0,71 | 394 | 395 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (EUCLIDIANA) | 400 | 1172 | 0,74 | 399 | 400 | ✓ | 145,83 |
| Grade 20x20 (densidade=70%) | A* (EUCLIDIANA) | 400 | 1172 | 0,42 | 365 | 366 | ✓ | 167,00 |
| Grade 30x30 (densidade=70%) | Dijkstra (Min-Heap) | 900 | 2707 | 3,53 | 897 | 898 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | BFS (não ponderado) | 900 | 2707 | 3,24 | 899 | 900 | ✓ | 58,00 |
| Grade 30x30 (densidade=70%) | A* (heurística nula = Dijkstra) | 900 | 2707 | 2,43 | 897 | 898 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (MANHATTAN) | 900 | 2707 | 2,69 | 862 | 863 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (MANHATTAN) | 900 | 2707 | 2,25 | 880 | 881 | ✓ | 198,11 |
| Grade 30x30 (densidade=70%) | A* (MANHATTAN) | 900 | 2707 | 2,39 | 864 | 865 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (MANHATTAN) | 900 | 2707 | 2,12 | 891 | 892 | ✓ | 247,24 |
| Grade 30x30 (densidade=70%) | A* (EUCLIDIANA) | 900 | 2707 | 2,22 | 870 | 871 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (EUCLIDIANA) | 900 | 2707 | 2,04 | 885 | 886 | ✓ | 191,22 |
| Grade 30x30 (densidade=70%) | A* (EUCLIDIANA) | 900 | 2707 | 2,37 | 871 | 872 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (EUCLIDIANA) | 900 | 2707 | 2,32 | 891 | 892 | ✓ | 247,24 |
| Grade 50x50 (densidade=50%) | Dijkstra (Min-Heap) | 2500 | 6030 | 14,90 | 2253 | 2254 | ✓ | 373,26 |
| Grade 50x50 (densidade=50%) | BFS (não ponderado) | 2500 | 6030 | 18,91 | 2299 | 2300 | ✓ | 102,00 |
| Grade 50x50 (densidade=50%) | A* (heurística nula = Dijkstra) | 2500 | 6030 | 14,63 | 2253 | 2254 | ✓ | 373,26 |
| Grade 50x50 (densidade=50%) | A* (MANHATTAN) | 2500 | 6030 | 11,23 | 1760 | 1761 | ✓ | 375,28 |
| Grade 50x50 (densidade=50%) | A* (MANHATTAN) | 2500 | 6030 | 14,31 | 2173 | 2174 | ✓ | 390,18 |
| Grade 50x50 (densidade=50%) | A* (MANHATTAN) | 2500 | 6030 | 14,71 | 2225 | 2226 | ✓ | 387,61 |
| Grade 50x50 (densidade=50%) | A* (MANHATTAN) | 2500 | 6030 | 13,50 | 2110 | 2111 | ✓ | 373,26 |
| Grade 50x50 (densidade=50%) | A* (EUCLIDIANA) | 2500 | 6030 | 11,63 | 1862 | 1863 | ✓ | 375,28 |
| Grade 50x50 (densidade=50%) | A* (EUCLIDIANA) | 2500 | 6030 | 14,83 | 2203 | 2204 | ✓ | 373,26 |
| Grade 50x50 (densidade=50%) | A* (EUCLIDIANA) | 2500 | 6030 | 16,88 | 2238 | 2239 | ✓ | 375,03 |
| Grade 50x50 (densidade=50%) | A* (EUCLIDIANA) | 2500 | 6030 | 14,73 | 2136 | 2137 | ✓ | 373,26 |
| Aleatório G(100, 0,10) | Dijkstra (Min-Heap) | 100 | 942 | 0,02 | 41 | 42 | ✓ | 14,97 |
| Aleatório G(100, 0,10) | BFS (não ponderado) | 100 | 942 | 0,03 | 89 | 90 | ✓ | 3,00 |
| Aleatório G(200, 0,10) | Dijkstra (Min-Heap) | 200 | 3941 | 0,14 | 140 | 141 | ✓ | 11,00 |
| Aleatório G(200, 0,10) | BFS (não ponderado) | 200 | 3941 | 0,03 | 38 | 39 | ✓ | 2,00 |
| Aleatório G(500, 0,05) | Dijkstra (Min-Heap) | 500 | 12446 | 0,73 | 310 | 311 | ✓ | 10,31 |
| Aleatório G(500, 0,05) | BFS (não ponderado) | 500 | 12446 | 0,42 | 320 | 321 | ✓ | 3,00 |
| Aleatório G(100, 0,80) | Dijkstra (Min-Heap) | 100 | 7894 | 0,02 | 20 | 21 | ✓ | 3,45 |
| Aleatório G(100, 0,80) | BFS (não ponderado) | 100 | 7894 | 0,02 | 81 | 82 | ✓ | 1,00 |
| Aleatório G(200, 0,80) | Dijkstra (Min-Heap) | 200 | 31824 | 0,13 | 88 | 89 | ✓ | 3,68 |
| Aleatório G(200, 0,80) | BFS (não ponderado) | 200 | 31824 | 0,08 | 160 | 161 | ✓ | 1,00 |
| Esparso (árvore + 50 extras) | Dijkstra (Min-Heap) | 500 | 548 | 0,63 | 458 | 459 | ✓ | 54,61 |
| Esparso (árvore + 50 extras) | BFS (não ponderado) | 500 | 548 | 0,80 | 460 | 461 | ✓ | 8,00 |
| Esparso (árvore + 100 extras) | Dijkstra (Min-Heap) | 1000 | 1099 | 2,68 | 966 | 967 | ✓ | 61,24 |
| Esparso (árvore + 100 extras) | BFS (não ponderado) | 1000 | 1099 | 3,38 | 914 | 915 | ✓ | 10,00 |
| Gargalo (2 clusters de 100) | Dijkstra (Min-Heap) | 200 | 9831 | 0,18 | 189 | 190 | ✓ | 8,87 |
| Gargalo (2 clusters de 100) | BFS (não ponderado) | 200 | 9831 | 0,10 | 191 | 192 | ✓ | 4,00 |
| Gargalo (2 clusters de 250) | Dijkstra (Min-Heap) | 500 | 62119 | 1,21 | 464 | 465 | ✓ | 6,89 |
| Gargalo (2 clusters de 250) | BFS (não ponderado) | 500 | 62119 | 0,54 | 441 | 442 | ✓ | 4,00 |

---

## Análise por Tamanho de Grafo

### Grafo 10x10 (100 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 0,42 | 87 | 88 |
| BFS (não ponderado) | 0,44 | 99 | 100 |
| A* (heurística nula = Dijkstra) | 0,44 | 87 | 88 |
| A* (MANHATTAN) | 0,09 | 78 | 79 |
| A* (MANHATTAN) | 0,11 | 84 | 85 |
| A* (MANHATTAN) | 0,14 | 82 | 83 |
| A* (MANHATTAN) | 0,15 | 78 | 79 |
| A* (EUCLIDIANA) | 0,14 | 79 | 80 |
| A* (EUCLIDIANA) | 0,08 | 84 | 85 |
| A* (EUCLIDIANA) | 0,10 | 83 | 84 |
| A* (EUCLIDIANA) | 0,11 | 78 | 79 |
| Dijkstra (Min-Heap) | 0,02 | 41 | 42 |
| BFS (não ponderado) | 0,03 | 89 | 90 |
| Dijkstra (Min-Heap) | 0,02 | 20 | 21 |
| BFS (não ponderado) | 0,02 | 81 | 82 |

### Grafo 14x14 (200 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 0,14 | 140 | 141 |
| BFS (não ponderado) | 0,03 | 38 | 39 |
| Dijkstra (Min-Heap) | 0,13 | 88 | 89 |
| BFS (não ponderado) | 0,08 | 160 | 161 |
| Dijkstra (Min-Heap) | 0,18 | 189 | 190 |
| BFS (não ponderado) | 0,10 | 191 | 192 |

### Grafo 20x20 (400 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 1,57 | 399 | 400 |
| BFS (não ponderado) | 1,66 | 399 | 400 |
| A* (heurística nula = Dijkstra) | 1,48 | 399 | 400 |
| A* (MANHATTAN) | 1,69 | 396 | 397 |
| A* (MANHATTAN) | 0,71 | 394 | 395 |
| A* (MANHATTAN) | 1,09 | 396 | 397 |
| A* (MANHATTAN) | 0,77 | 365 | 366 |
| A* (EUCLIDIANA) | 0,82 | 396 | 397 |
| A* (EUCLIDIANA) | 0,71 | 394 | 395 |
| A* (EUCLIDIANA) | 0,74 | 399 | 400 |
| A* (EUCLIDIANA) | 0,42 | 365 | 366 |

### Grafo 22x22 (500 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 0,73 | 310 | 311 |
| BFS (não ponderado) | 0,42 | 320 | 321 |
| Dijkstra (Min-Heap) | 0,63 | 458 | 459 |
| BFS (não ponderado) | 0,80 | 460 | 461 |
| Dijkstra (Min-Heap) | 1,21 | 464 | 465 |
| BFS (não ponderado) | 0,54 | 441 | 442 |

### Grafo 30x30 (900 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 3,53 | 897 | 898 |
| BFS (não ponderado) | 3,24 | 899 | 900 |
| A* (heurística nula = Dijkstra) | 2,43 | 897 | 898 |
| A* (MANHATTAN) | 2,69 | 862 | 863 |
| A* (MANHATTAN) | 2,25 | 880 | 881 |
| A* (MANHATTAN) | 2,39 | 864 | 865 |
| A* (MANHATTAN) | 2,12 | 891 | 892 |
| A* (EUCLIDIANA) | 2,22 | 870 | 871 |
| A* (EUCLIDIANA) | 2,04 | 885 | 886 |
| A* (EUCLIDIANA) | 2,37 | 871 | 872 |
| A* (EUCLIDIANA) | 2,32 | 891 | 892 |

### Grafo 31x31 (1000 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 2,68 | 966 | 967 |
| BFS (não ponderado) | 3,38 | 914 | 915 |

### Grafo 50x50 (2500 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 14,90 | 2253 | 2254 |
| BFS (não ponderado) | 18,91 | 2299 | 2300 |
| A* (heurística nula = Dijkstra) | 14,63 | 2253 | 2254 |
| A* (MANHATTAN) | 11,23 | 1760 | 1761 |
| A* (MANHATTAN) | 14,31 | 2173 | 2174 |
| A* (MANHATTAN) | 14,71 | 2225 | 2226 |
| A* (MANHATTAN) | 13,50 | 2110 | 2111 |
| A* (EUCLIDIANA) | 11,63 | 1862 | 1863 |
| A* (EUCLIDIANA) | 14,83 | 2203 | 2204 |
| A* (EUCLIDIANA) | 16,88 | 2238 | 2239 |
| A* (EUCLIDIANA) | 14,73 | 2136 | 2137 |

