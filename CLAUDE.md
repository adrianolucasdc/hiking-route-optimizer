# Hiking Route Optimizer - Project Memory

## Project Overview
Full-stack microservice application for finding optimal hiking routes using Dijkstra, A*, and BFS algorithms with realistic terrain modeling.

**Stack**: Spring Boot 3.2.5 (Java 21) + React 19/Vite/TailwindCSS + Python 3.11/Flask/NetworkX/Matplotlib
**Deploy**: Docker Compose with 3 services + healthchecks

## Architecture

| Service | Port | Technology | Purpose |
|---------|------|------------|---------|
| Backend | 8080 | Spring Boot 3.2.5, Java 21 | REST API, pathfinding, terrain modeling |
| Frontend | 3000 | React 19, Vite, TailwindCSS | Web UI for route input/visualization |
| Python | 5000 | Flask, NetworkX, Matplotlib | Graph visualization (PNG export) |

Network: `hiking-network` (bridge)

## Key Algorithms

| Algorithm | Complexity | Use Case |
|-----------|------------|----------|
| Dijkstra (Min-Heap) | O((V+E)log V) | Guaranteed optimal, weighted graphs |
| A* (Manhattan/Euclidean/Chebyshev) | O((V+E)log V) | Grid maps with admissible heuristics |
| BFS | O(V+E) | Unweighted, fewest hops |

## Terrain Modeling (15 Types)
- **Passable**: TRILHA_BOA (1.0) → OBSTACULO (∞)
- **Impassable**: AGUA_FUNDO, PAREDE_ROCHA, OBSTACULO
- **Slope penalties**: +10%/degree uphill, -5%/degree downhill (min 30%)
- **Diagonal**: ×√2 cost multiplier
- **Builder pattern** for configuration + synthetic generators

## API Endpoints

```
Backend:
  GET  /                           → API info
  GET  /actuator/health            → Health check
  POST /api/grafo/processar        → Min/max path (matriz, origem, destino, algoritmo?, heuristica?)

Python:
  GET  /health                     → Health check
  POST /graph                      → PNG visualization (matrix, directed, k, figsize)
```

## Test Suite
**128 tests passing** (JUnit 5 + AssertJ)
- DijkstraServiceTest: 15
- AStarFinderTest: 6
- BFSFinderTest: 6
- DijkstraFinderTest: 8
- CaminhoMaximoServiceTest: 12
- ValidadorGrafoTest: 21
- GrafoTest: 16
- GrafoServiceTest: 13
- GrafoVisualizerTest: 13
- TerrainCostCalculatorTest: 17

Run: `cd backend && ./mvnw test` (requires Java 21)

## Benchmark Suite
`BenchmarkRunner` compares algorithms across graph types/sizes
Outputs: `benchmark-results.csv`, `benchmark-results.md`, ASCII charts
Run: `./mvnw exec:java -Dexec.mainClass="com.example.hiking_route.benchmark.BenchmarkMain"`

## Visualizations
- **Java**: `GrafoVisualizer` - ASCII/ANSI terminal + PNG export (Java 2D)
- **Python**: Flask service - Matplotlib/NetworkX publication-quality PNG

## Critical Fixes Applied
1. **Root endpoint** (`GET /`): Created `RootController` + `spring.web.resources.add-mappings=false`
2. **Docker Compose**: Removed conflicting `backend/compose.yaml`
3. **CORS Python**: Added `flask-cors`, `CORS(app)` for frontend integration
4. **Java 21**: Fixed pom.xml `maven.compiler.release=21`, surefire `useSystemClassLoader=false`
5. **printf formats**: Fixed `%d` → `%.0f` for doubles in BenchmarkRunner/BenchmarkMain
6. **AssertJ 2D arrays**: Changed `.hasLength()` → `.length`

## Quick Start
```bash
# Full stack
docker compose up --build -d

# Local dev
cd backend && ./mvnw spring-boot:run      # Java 21 required
cd frontend && npm install && npm run dev  # Port 5173
cd python-service && pip install -r requirements.txt && python app.py
```

## Key Files
- `backend/pom.xml` - Maven config, Java 21, test plugins
- `backend/src/main/resources/application.properties` - Spring config, static resources disabled
- `docker-compose.yml` - 3 services, healthchecks, shared network
- `python-service/app.py` - Flask + CORS + NetworkX/Matplotlib
- `backend/src/main/java/.../terrain/TerrainCostCalculator.java` - 15 terrains, slopes, diagonals
- `backend/src/main/java/.../util/GrafoVisualizer.java` - ASCII/ANSI + PNG
- `README.md` - Portuguese documentation

## Commands Reference
```bash
# Tests
cd backend && ./mvnw test

# Build JAR
cd backend && ./mvnw clean package -DskipTests

# Benchmarks
cd backend && ./mvnw exec:java -Dexec.mainClass="com.example.hiking_route.benchmark.BenchmarkMain"

# Docker
docker compose up --build -d
docker compose logs -f
docker compose ps
```