# Resultados do Benchmark

| Grafo | Algoritmo | Vértices | Arestas | Tempo Médio (ms) | Nós Expandidos | Nós Visitados | Encontrado | Custo |
|-------|-----------|----------|---------|------------------|----------------|---------------|------------|-------|
| Grade 10x10 (densidade=70%) | Dijkstra (Min-Heap) | 100 | 279 | 0,60 | 87 | 88 | ✓ | 63,05 |
| Grade 10x10 (densidade=70%) | BFS (não ponderado) | 100 | 279 | 0,41 | 99 | 100 | ✓ | 18,00 |
| Grade 10x10 (densidade=70%) | A* (heurística nula = Dijkstra) | 100 | 279 | 0,52 | 87 | 88 | ✓ | 63,05 |
| Grade 10x10 (densidade=70%) | A* (MANHATTAN) | 100 | 279 | 0,36 | 78 | 79 | ✓ | 63,05 |
| Grade 10x10 (densidade=70%) | A* (MANHATTAN) | 100 | 279 | 0,38 | 84 | 85 | ✓ | 73,61 |
| Grade 10x10 (densidade=70%) | A* (MANHATTAN) | 100 | 279 | 0,32 | 82 | 83 | ✓ | 66,13 |
| Grade 10x10 (densidade=70%) | A* (MANHATTAN) | 100 | 279 | 0,27 | 78 | 79 | ✓ | 85,12 |
| Grade 10x10 (densidade=70%) | A* (EUCLIDIANA) | 100 | 279 | 0,23 | 79 | 80 | ✓ | 63,05 |
| Grade 10x10 (densidade=70%) | A* (EUCLIDIANA) | 100 | 279 | 0,14 | 84 | 85 | ✓ | 73,61 |
| Grade 10x10 (densidade=70%) | A* (EUCLIDIANA) | 100 | 279 | 0,15 | 83 | 84 | ✓ | 66,13 |
| Grade 10x10 (densidade=70%) | A* (EUCLIDIANA) | 100 | 279 | 0,13 | 78 | 79 | ✓ | 85,12 |
| Grade 20x20 (densidade=70%) | Dijkstra (Min-Heap) | 400 | 1172 | 1,80 | 399 | 400 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | BFS (não ponderado) | 400 | 1172 | 1,74 | 399 | 400 | ✓ | 38,00 |
| Grade 20x20 (densidade=70%) | A* (heurística nula = Dijkstra) | 400 | 1172 | 2,06 | 399 | 400 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (MANHATTAN) | 400 | 1172 | 2,58 | 396 | 397 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (MANHATTAN) | 400 | 1172 | 1,64 | 394 | 395 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (MANHATTAN) | 400 | 1172 | 1,35 | 396 | 397 | ✓ | 156,87 |
| Grade 20x20 (densidade=70%) | A* (MANHATTAN) | 400 | 1172 | 1,17 | 365 | 366 | ✓ | 167,00 |
| Grade 20x20 (densidade=70%) | A* (EUCLIDIANA) | 400 | 1172 | 1,69 | 396 | 397 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (EUCLIDIANA) | 400 | 1172 | 1,73 | 394 | 395 | ✓ | 141,01 |
| Grade 20x20 (densidade=70%) | A* (EUCLIDIANA) | 400 | 1172 | 1,72 | 399 | 400 | ✓ | 145,83 |
| Grade 20x20 (densidade=70%) | A* (EUCLIDIANA) | 400 | 1172 | 0,82 | 365 | 366 | ✓ | 167,00 |
| Grade 30x30 (densidade=70%) | Dijkstra (Min-Heap) | 900 | 2707 | 6,56 | 897 | 898 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | BFS (não ponderado) | 900 | 2707 | 7,25 | 899 | 900 | ✓ | 58,00 |
| Grade 30x30 (densidade=70%) | A* (heurística nula = Dijkstra) | 900 | 2707 | 4,42 | 897 | 898 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (MANHATTAN) | 900 | 2707 | 2,69 | 862 | 863 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (MANHATTAN) | 900 | 2707 | 2,85 | 880 | 881 | ✓ | 198,11 |
| Grade 30x30 (densidade=70%) | A* (MANHATTAN) | 900 | 2707 | 3,10 | 864 | 865 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (MANHATTAN) | 900 | 2707 | 3,93 | 891 | 892 | ✓ | 247,24 |
| Grade 30x30 (densidade=70%) | A* (EUCLIDIANA) | 900 | 2707 | 4,09 | 870 | 871 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (EUCLIDIANA) | 900 | 2707 | 4,04 | 885 | 886 | ✓ | 191,22 |
| Grade 30x30 (densidade=70%) | A* (EUCLIDIANA) | 900 | 2707 | 4,47 | 871 | 872 | ✓ | 190,06 |
| Grade 30x30 (densidade=70%) | A* (EUCLIDIANA) | 900 | 2707 | 4,02 | 891 | 892 | ✓ | 247,24 |
| Grade 50x50 (densidade=50%) | Dijkstra (Min-Heap) | 2500 | 6030 | 25,91 | 2253 | 2254 | ✓ | 373,26 |
| Grade 50x50 (densidade=50%) | BFS (não ponderado) | 2500 | 6030 | 22,92 | 2299 | 2300 | ✓ | 102,00 |
| Grade 50x50 (densidade=50%) | A* (heurística nula = Dijkstra) | 2500 | 6030 | 28,30 | 2253 | 2254 | ✓ | 373,26 |
| Grade 50x50 (densidade=50%) | A* (MANHATTAN) | 2500 | 6030 | 21,50 | 1760 | 1761 | ✓ | 375,28 |
| Grade 50x50 (densidade=50%) | A* (MANHATTAN) | 2500 | 6030 | 22,68 | 2173 | 2174 | ✓ | 390,18 |
| Grade 50x50 (densidade=50%) | A* (MANHATTAN) | 2500 | 6030 | 21,95 | 2225 | 2226 | ✓ | 387,61 |
| Grade 50x50 (densidade=50%) | A* (MANHATTAN) | 2500 | 6030 | 19,05 | 2110 | 2111 | ✓ | 373,26 |
| Grade 50x50 (densidade=50%) | A* (EUCLIDIANA) | 2500 | 6030 | 15,86 | 1862 | 1863 | ✓ | 375,28 |
| Grade 50x50 (densidade=50%) | A* (EUCLIDIANA) | 2500 | 6030 | 19,54 | 2203 | 2204 | ✓ | 373,26 |
| Grade 50x50 (densidade=50%) | A* (EUCLIDIANA) | 2500 | 6030 | 20,26 | 2238 | 2239 | ✓ | 375,03 |
| Grade 50x50 (densidade=50%) | A* (EUCLIDIANA) | 2500 | 6030 | 18,84 | 2136 | 2137 | ✓ | 373,26 |
| Aleatório G(100, 0,10) | Dijkstra (Min-Heap) | 100 | 942 | 0,04 | 41 | 42 | ✓ | 14,97 |
| Aleatório G(100, 0,10) | BFS (não ponderado) | 100 | 942 | 0,03 | 89 | 90 | ✓ | 3,00 |
| Aleatório G(200, 0,10) | Dijkstra (Min-Heap) | 200 | 3941 | 0,24 | 140 | 141 | ✓ | 11,00 |
| Aleatório G(200, 0,10) | BFS (não ponderado) | 200 | 3941 | 0,03 | 38 | 39 | ✓ | 2,00 |
| Aleatório G(500, 0,05) | Dijkstra (Min-Heap) | 500 | 12446 | 1,65 | 310 | 311 | ✓ | 10,31 |
| Aleatório G(500, 0,05) | BFS (não ponderado) | 500 | 12446 | 0,89 | 320 | 321 | ✓ | 3,00 |
| Aleatório G(100, 0,80) | Dijkstra (Min-Heap) | 100 | 7894 | 0,03 | 20 | 21 | ✓ | 3,45 |
| Aleatório G(100, 0,80) | BFS (não ponderado) | 100 | 7894 | 0,03 | 81 | 82 | ✓ | 1,00 |
| Aleatório G(200, 0,80) | Dijkstra (Min-Heap) | 200 | 31824 | 0,28 | 88 | 89 | ✓ | 3,68 |
| Aleatório G(200, 0,80) | BFS (não ponderado) | 200 | 31824 | 0,14 | 160 | 161 | ✓ | 1,00 |
| Esparso (árvore + 50 extras) | Dijkstra (Min-Heap) | 500 | 548 | 1,20 | 458 | 459 | ✓ | 54,61 |
| Esparso (árvore + 50 extras) | BFS (não ponderado) | 500 | 548 | 1,41 | 460 | 461 | ✓ | 8,00 |
| Esparso (árvore + 100 extras) | Dijkstra (Min-Heap) | 1000 | 1099 | 6,92 | 966 | 967 | ✓ | 61,24 |
| Esparso (árvore + 100 extras) | BFS (não ponderado) | 1000 | 1099 | 3,82 | 914 | 915 | ✓ | 10,00 |
| Gargalo (2 clusters de 100) | Dijkstra (Min-Heap) | 200 | 9831 | 0,24 | 189 | 190 | ✓ | 8,87 |
| Gargalo (2 clusters de 100) | BFS (não ponderado) | 200 | 9831 | 0,28 | 191 | 192 | ✓ | 4,00 |
| Gargalo (2 clusters de 250) | Dijkstra (Min-Heap) | 500 | 62119 | 1,87 | 464 | 465 | ✓ | 6,89 |
| Gargalo (2 clusters de 250) | BFS (não ponderado) | 500 | 62119 | 0,67 | 441 | 442 | ✓ | 4,00 |

---

## Análise por Tamanho de Grafo

### Grafo 10x10 (100 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 0,60 | 87 | 88 |
| BFS (não ponderado) | 0,41 | 99 | 100 |
| A* (heurística nula = Dijkstra) | 0,52 | 87 | 88 |
| A* (MANHATTAN) | 0,36 | 78 | 79 |
| A* (MANHATTAN) | 0,38 | 84 | 85 |
| A* (MANHATTAN) | 0,32 | 82 | 83 |
| A* (MANHATTAN) | 0,27 | 78 | 79 |
| A* (EUCLIDIANA) | 0,23 | 79 | 80 |
| A* (EUCLIDIANA) | 0,14 | 84 | 85 |
| A* (EUCLIDIANA) | 0,15 | 83 | 84 |
| A* (EUCLIDIANA) | 0,13 | 78 | 79 |
| Dijkstra (Min-Heap) | 0,04 | 41 | 42 |
| BFS (não ponderado) | 0,03 | 89 | 90 |
| Dijkstra (Min-Heap) | 0,03 | 20 | 21 |
| BFS (não ponderado) | 0,03 | 81 | 82 |

### Grafo 14x14 (200 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 0,24 | 140 | 141 |
| BFS (não ponderado) | 0,03 | 38 | 39 |
| Dijkstra (Min-Heap) | 0,28 | 88 | 89 |
| BFS (não ponderado) | 0,14 | 160 | 161 |
| Dijkstra (Min-Heap) | 0,24 | 189 | 190 |
| BFS (não ponderado) | 0,28 | 191 | 192 |

### Grafo 20x20 (400 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 1,80 | 399 | 400 |
| BFS (não ponderado) | 1,74 | 399 | 400 |
| A* (heurística nula = Dijkstra) | 2,06 | 399 | 400 |
| A* (MANHATTAN) | 2,58 | 396 | 397 |
| A* (MANHATTAN) | 1,64 | 394 | 395 |
| A* (MANHATTAN) | 1,35 | 396 | 397 |
| A* (MANHATTAN) | 1,17 | 365 | 366 |
| A* (EUCLIDIANA) | 1,69 | 396 | 397 |
| A* (EUCLIDIANA) | 1,73 | 394 | 395 |
| A* (EUCLIDIANA) | 1,72 | 399 | 400 |
| A* (EUCLIDIANA) | 0,82 | 365 | 366 |

### Grafo 22x22 (500 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 1,65 | 310 | 311 |
| BFS (não ponderado) | 0,89 | 320 | 321 |
| Dijkstra (Min-Heap) | 1,20 | 458 | 459 |
| BFS (não ponderado) | 1,41 | 460 | 461 |
| Dijkstra (Min-Heap) | 1,87 | 464 | 465 |
| BFS (não ponderado) | 0,67 | 441 | 442 |

### Grafo 30x30 (900 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 6,56 | 897 | 898 |
| BFS (não ponderado) | 7,25 | 899 | 900 |
| A* (heurística nula = Dijkstra) | 4,42 | 897 | 898 |
| A* (MANHATTAN) | 2,69 | 862 | 863 |
| A* (MANHATTAN) | 2,85 | 880 | 881 |
| A* (MANHATTAN) | 3,10 | 864 | 865 |
| A* (MANHATTAN) | 3,93 | 891 | 892 |
| A* (EUCLIDIANA) | 4,09 | 870 | 871 |
| A* (EUCLIDIANA) | 4,04 | 885 | 886 |
| A* (EUCLIDIANA) | 4,47 | 871 | 872 |
| A* (EUCLIDIANA) | 4,02 | 891 | 892 |

### Grafo 31x31 (1000 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 6,92 | 966 | 967 |
| BFS (não ponderado) | 3,82 | 914 | 915 |

### Grafo 50x50 (2500 vértices)

| Algoritmo | Tempo Médio (ms) | Nós Expandidos | Nós Visitados |
|-----------|------------------|----------------|---------------|
| Dijkstra (Min-Heap) | 25,91 | 2253 | 2254 |
| BFS (não ponderado) | 22,92 | 2299 | 2300 |
| A* (heurística nula = Dijkstra) | 28,30 | 2253 | 2254 |
| A* (MANHATTAN) | 21,50 | 1760 | 1761 |
| A* (MANHATTAN) | 22,68 | 2173 | 2174 |
| A* (MANHATTAN) | 21,95 | 2225 | 2226 |
| A* (MANHATTAN) | 19,05 | 2110 | 2111 |
| A* (EUCLIDIANA) | 15,86 | 1862 | 1863 |
| A* (EUCLIDIANA) | 19,54 | 2203 | 2204 |
| A* (EUCLIDIANA) | 20,26 | 2238 | 2239 |
| A* (EUCLIDIANA) | 18,84 | 2136 | 2137 |

