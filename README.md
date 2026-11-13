# 🥾 Hiking Route Optimizer

> **Uma aplicação de microsserviços Spring Boot + React + Python para encontrar rotas de trilha ótimas usando algoritmos Dijkstra, A* e BFS com modelagem realista de terreno.**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-blue.svg)](https://react.dev/)
[![Python](https://img.shields.io/badge/Python-3.11-blue.svg)](https://www.python.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![Testes](https://img.shields.io/badge/Testes-122%20passando-brightgreen.svg)](#-suite-de-testes)
[![Licença](https://img.shields.io/badge/Licença-MIT-green.svg)](LICENSE)

---

## 📋 Sumário

- [Visão Geral](#-visão-geral)
- [Arquitetura](#-arquitetura)
- [Algoritmos e Complexidade](#-algoritmos-e-complexidade)
- [Modelagem de Terreno](#-modelagem-de-terreno)
- [Visualizações](#-visualizações)
- [Resultados de Benchmark](#-resultados-de-benchmark)
- [Início Rápido](#-início-rápido)
- [Referência da API](#-referência-da-api)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Suite de Testes](#-suite-de-testes)
- [Desenvolvimento](#-desenvolvimento)

---

## 🎯 Visão Geral

O **Hiking Route Optimizer** é uma aplicação full-stack que resolve o problema de pathfinding (busca de caminho) para trilhas de caminhada usando múltiplos algoritmos de grafos. Ele modela restrições de terreno do mundo real incluindo:

- **Perfis de elevação** com penalidades de inclinação (subida custa mais, descida recupera energia)
- **15 tipos de terreno** (trilha, rocha, areia, lama, neve, água, floresta densa, etc.)
- **Obstáculos intransponíveis** (água funda, paredes de rocha, barreiras artificiais)
- **Movimento ortogonal + diagonal** (diagonal custa ×√2)

### Principais Funcionalidades

| Funcionalidade | Descrição |
|---------------|-----------|
| **3 Algoritmos de Pathfinding** | Dijkstra (ótimo), A* (guiado por heurística), BFS (não ponderado) |
| **Calculadora de Custo de Terreno** | 15 tipos de terreno + penalidades de inclinação + movimento diagonal |
| **Visualização** | Renderização ASCII/ANSI no terminal + exportação PNG (Java 2D & Python/matplotlib) |
| **Suite de Benchmark** | Comparação de performance automatizada com exportação CSV/Markdown |
| **API REST** | Spring Boot backend com validação & health checks do actuator |
| **Interface Web** | React + Vite + TailwindCSS frontend |
| **Pronto para Docker** | Deploy multi-serviço com health checks |

---

## 🏗 Arquitetura

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        HIKING ROUTE OPTIMIZER                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────────┐     HTTP/JSON      ┌──────────────┐                     │
│  │   FRONTEND   │ ◄─────────────────► │   BACKEND    │                     │
│  │  (React)     │    Porta 3000       │ (Spring Boot)│                     │
│  │  Porta 3000  │                     │  Porta 8080  │                     │
│  └──────────────┘                     └──────┬───────┘                     │
│                                               │                             │
│                                               │ HTTP/JSON                   │
│                                               ▼                             │
│                                    ┌────────────────────┐                  │
│                                    │  PYTHON SERVICE    │                  │
│                                    │   (Flask)          │                  │
│                                    │  Porta 5000        │                  │
│                                    └────────────────────┘                  │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                    REDE COMPARTILHADA (hiking-network)              │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Detalhes dos Serviços

| Serviço | Tecnologia | Porta | Propósito |
|---------|------------|-------|-----------|
| **Backend** | Spring Boot 3.2.5, Java 21 | 8080 | API REST, algoritmos de pathfinding, modelagem de terreno |
| **Frontend** | React 19, Vite, TailwindCSS | 3000 | UI web para entrada de rotas & visualização de resultados |
| **Python Service** | Flask, NetworkX, Matplotlib | 5000 | Visualização de grafos (exportação PNG) |

---

## ⚡ Algoritmos e Complexidade

### Algoritmos Implementados

| Algoritmo | Complexidade de Tempo | Complexidade de Espaço | Melhor Para |
|-----------|----------------------|------------------------|-------------|
| **Dijkstra (Min-Heap)** | O((V + E) log V) | O(V) | Caminhos ótimos garantidos, grafos ponderados |
| **A* (Manhattan/Euclidiana/Chebyshev)** | O((V + E) log V) | O(V) | Mapas baseados em grade com heurísticas admissíveis |
| **BFS** | O(V + E) | O(V) | Grafos não ponderados, menor número de saltos |

### Análise de Complexidade

#### Dijkstra com Min-Heap (PriorityQueue)
```
Tempo:  O((V + E) log V)
         ├─ V extrações do heap: O(V log V)
         └─ E operações decrease-key: O(E log V)
Espaço: O(V) para array de distâncias + heap + conjunto visitado
```

#### Busca A*
```
Tempo:  O((V + E) log V) pior caso, mas tipicamente muito mais rápido
         Com heurística admissível h(n):
         - Expande menos nós que Dijkstra
         - Performance depende da qualidade da heurística
Espaço: O(V) para conjuntos open/closed + g-score + f-score maps

Heurísticas (para grades 2D):
- Manhattan:  h(n) = |x₁ - x₂| + |y₁ - y₂|     (4-direcional)
- Euclidiana: h(n) = √((x₁-x₂)² + (y₁-y₂)²)    (qualquer direção)
- Chebyshev:  h(n) = max(|x₁-x₂|, |y₁-y₂|)     (8-direcional)
```

#### BFS (Busca em Largura)
```
Tempo:  O(V + E) - visita cada vértice e aresta uma vez
Espaço: O(V) - fila + conjunto visitado
Nota:   Ignora pesos das arestas, encontra caminho com menos arestas
```

---

## 🏔 Modelagem de Terreno

O `TerrainCostCalculator` converte matrizes de elevação + matrizes de tipo de terreno em grafos ponderados para pathfinding.

### 15 Tipos de Terreno

| Terreno | Custo Base (por metro) | Transponível | Descrição |
|---------|------------------------|--------------|-----------|
| `TRILHA_BOA` | 1.0 | ✅ | Trilha bem mantida |
| `TRILHA_REGULAR` | 1.5 | ✅ | Trilha irregular |
| `TERRA_BATIDA` | 2.0 | ✅ | Terra batida (padrão) |
| `GRAMA` | 2.5 | ✅ | Grama/campo aberto |
| `CASCALHO` | 3.0 | ✅ | Cascalho/pedregulho |
| `ARVORE_ESPARSA` | 4.0 | ✅ | Floresta esparsa |
| `AREIA` | 4.0 | ✅ | Areia solta |
| `NEVE` | 5.0 | ✅ | Neve compactada |
| `ROCHA` | 5.0 | ✅ | Rocha exposta |
| `LAMA` | 6.0 | ✅ | Lama/encharcado |
| `GELO` | 8.0 | ✅ | Gelo/neve dura |
| `ARVORE_DENSA` | 10.0 | ✅ | Floresta densa |
| `AGUA_RASO` | 10.0 | ✅ | Água rasa (vadeável) |
| `AGUA_FUNDO` | ∞ | ❌ | Água funda (intransponível) |
| `PAREDE_ROCHA` | ∞ | ❌ | Parede de rocha (intransponível) |
| `OBSTACULO` | ∞ | ❌ | Obstáculo artificial |

### Penalidades de Inclinação

```
Subida (Δ elevação > 0):
  - Inclinação máxima: 45° (configurável)
  - Penalidade: +10% por grau (configurável)
  - Multiplicador de custo = 1.0 + (graus_inclinação × 0.1)

Descida (Δ elevação < 0):
  - Inclinação máxima: 60° (configurável) 
  - Bônus: -5% por grau (recuperação de energia)
  - Mínimo: 30% do custo base (fator de segurança)
  - Multiplicador de custo = max(0.3, 1.0 + (graus_inclinação × -0.05))

Plano (Δ elevação = 0): Sem modificação
```

### Movimento Diagonal
```
Ortogonal (N/S/E/W):  distância = tamanho_célula (padrão 10m)
Diagonal (NE/NW/SE/SW): distância = tamanho_célula × √2 ≈ 14.14m
Multiplicador de custo: √2 ≈ 1.414
```

### Padrão Builder para Configuração
```java
TerrainCostCalculator calc = TerrainCostCalculator.builder()
    .permitirDiagonais(true)
    .maxInclinacaoSubida(45.0)
    .maxInclinacaoDescida(60.0)
    .penalidadeSubidaPorGrau(0.1)
    .bonusDescidaPorGrau(-0.05)
    .tamanhoCelulaMetros(10.0)
    .custoPersonalizado(TipoTerreno.TRILHA_BOA, 0.8)  // Custo customizado
    .build();
```

### Geradores de Dados Sintéticos
```java
// Elevação estilo Perlin-noise
double[][] elev = TerrainCostCalculator.gerarElevacaoSintetica(100, 100, 0.1, 42L);

// Terreno aleatório com 70% comum, 20% difícil, 5% intransponível
TipoTerreno[][] terr = TerrainCostCalculator.gerarTerrenoAleatorio(50, 50, 42L);

// Cenário de montanha pré-construído (30×30 com rio, floresta, picos, trilhas)
CenarioExemplo cenario = TerrainCostCalculator.criarCenarioMontanha();
```

---

## 🎨 Visualizações

### Renderização ASCII/ANSI no Terminal (Java)

O `GrafoVisualizer` renderiza matrizes e caminhos coloridos diretamente no terminal:

```
╔══════════════════════════════════════════════════════════════╗
║                    MATRIZ DE ADJACÊNCIA                       ║
╠══════════════════════════════════════════════════════════════╣
║      A       B       C       D       E        ║
╠══════════════════════════════════════════════════════════════╣
║ A  │   ·      10      ·       ·       ·      ║
║ B  │   ·       ·      15       ·       ·      ║
║ C  │   ·       ·       ·      20       ·      ║
║ D  │   ·       ·       ·       ·      25      ║
║ E  │   ·       ·       ·       ·       ·      ║
╚══════════════════════════════════════════════════════════════╝

Legenda:
  ORIGEM    Vértice de partida
  DESTINO   Vértice de chegada
  CAMINHO   Aresta no caminho ótimo
  AMARELO   Vértice no caminho
    ·       Sem aresta (peso 0)

Caminho: A → B → C → D → E
```

#### Funcionalidades
- **Vértices coloridos**: Origem (verde), Destino (vermelho), Caminho (verde brilhante)
- **Destaque de arestas**: Arestas do caminho ótimo em fundo verde
- **Modo compacto**: Para grafos grandes (>15 vértices)
- **Exportação PNG**: Imagens de grafo de alta qualidade via Java 2D

### Visualização Python/Matplotlib

O serviço Flask gera grafos PNG de qualidade de publicação:

```bash
curl -X POST http://localhost:5000/graph \
  -H "Content-Type: application/json" \
  -d '{"matrix": [[0,10,0],[0,0,10],[0,0,0]], "figsize": [10,8]}' \
  -o route.png
```

Funcionalidades:
- Layout spring com parâmetro `k` configurável
- Arestas direcionadas com pontas de seta
- Rótulos de peso das arestas
- Rótulos de nós customizáveis (A, B, C... ou customizados)
- Tamanho de figura e DPI configuráveis (150)

---

## 📊 Resultados de Benchmark

A suite de benchmark (`BenchmarkRunner`) compara todos os algoritmos através de múltiplos tipos e tamanhos de grafos.

### Resumo: Melhor Algoritmo por Tamanho do Grafo

| Vértices | Melhor Algoritmo | Tempo Médio |
|----------|------------------|-------------|
| 100      | BFS              | 0.02 ms     |
| 200      | BFS              | 0.03 ms     |
| 400      | A* (Euclidiana)  | 0.72 ms     |
| 500      | BFS              | 0.42 ms     |
| 900      | A* (Manhattan)   | 1.90 ms     |
| 1000     | Dijkstra         | 2.78 ms     |
| 2500     | A* (Manhattan)   | 11.23 ms    |

### Grafos em Grade: Dijkstra vs A* (Manhattan)

| Tamanho da Grade | Dijkstra | A* Manhattan | Aceleração |
|------------------|----------|--------------|------------|
| 10×10            | 0.42 ms  | 0.09 ms      | **4.7×**   |
| 20×20            | 1.57 ms  | 0.71 ms      | **2.2×**   |
| 30×30            | 3.53 ms  | 2.12 ms      | **1.7×**   |
| 50×50            | 14.90 ms | 11.23 ms     | **1.3×**   |

> **Insight Principal**: A* com heurística Manhattan supera significativamente Dijkstra em grafos de grade, especialmente em tamanhos menores onde a heurística guia efetivamente a busca. A vantagem diminui em tamanhos maiores devido ao overhead de computação da heurística.

### Executando Benchmarks

```bash
cd backend
./mvnw exec:java -Dexec.mainClass="com.example.hiking_route.benchmark.BenchmarkMain"
```

Saídas:
- `benchmark-results.csv` - Dados brutos para análise
- `benchmark-results.md` - Tabelas formatadas para README
- Gráfico ASCII no terminal

---

## 🚀 Início Rápido

### Pré-requisitos
- Docker & Docker Compose v2
- OU: Java 21, Node.js 20, Python 3.11

### Opção 1: Docker Compose (Recomendado)

```bash
# Clone e inicie todos os serviços
git clone https://github.com/seu-usuario/hiking-route-optimizer.git
cd hiking-route-optimizer

# Build e inicie
docker compose up --build -d

# Verifique status
docker compose ps

# Veja logs
docker compose logs -f
```

**Pontos de Acesso:**
- Frontend UI: http://localhost:3000
- Backend API: http://localhost:8080/api/grafo/processar
- Python Visualization: http://localhost:5000/graph
- Health Checks: `/actuator/health` (backend), `/health` (python)

### Opção 2: Desenvolvimento Local

#### Backend (Spring Boot)
```bash
cd backend
# Requer Java 21
./mvnw spring-boot:run
# API em http://localhost:8080
```

#### Frontend (React/Vite)
```bash
cd frontend
npm install
npm run dev
# UI em http://localhost:5173 (proxy para backend)
```

#### Python Service
```bash
cd python-service
pip install -r requirements.txt
python app.py
# Serviço em http://localhost:5000
```

### Opção 3: Executar Apenas Benchmarks

```bash
cd backend
./mvnw exec:java -Dexec.mainClass="com.example.hiking_route.benchmark.BenchmarkMain"
```

---

## 📡 Referência da API

### Backend: Processamento de Grafo

**POST** `/api/grafo/processar`

```json
Request:
{
  "matriz": [
    [0, 10, 0],
    [0, 0, 10],
    [0, 0, 0]
  ],
  "origem": 0,
  "destino": 2
}
```

```json
Response:
{
  "caminhoMinimo": {
    "caminho": [0, 1, 2],
    "custo": 20.0,
    "tipo": "MINIMO",
    "caminhoEncontrado": true,
    "mensagem": "Caminho mínimo encontrado com sucesso"
  },
  "caminhoMaximo": {
    "caminho": [0, 1, 2],
    "custo": 20.0,
    "tipo": "MAXIMO",
    "caminhoEncontrado": true,
    "mensagem": "Caminho máximo encontrado com sucesso"
  },
  "numeroVertices": 3,
  "status": "SUCESSO"
}
```

### Backend: Health Check

**GET** `/actuator/health`

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {"status": "UP", ...},
    "ping": {"status": "UP"}
  }
}
```

### Python Service: Visualização de Grafo

**POST** `/graph`

```json
Request:
{
  "matrix": [[0, 10, 0], [0, 0, 10], [0, 0, 0]],
  "directed": true,
  "k": 1.5,
  "figsize": [8, 6]
}
```

**Response:** Imagem PNG (image/png)

**GET** `/health`

```json
{
  "status": "healthy",
  "service": "python-graph-visualization"
}
```

---

## 📁 Estrutura do Projeto

```
hiking-route-optimizer/
├── backend/                          # Aplicação Spring Boot
│   ├── src/
│   │   ├── main/java/com/example/hiking_route/
│   │   │   ├── algorithm/            # Implementações PathFinder
│   │   │   │   ├── PathFinder.java           # Interface
│   │   │   │   ├── PathResult.java           # Objeto de resultado rico
│   │   │   │   ├── DijkstraFinder.java       # O((V+E)log V)
│   │   │   │   ├── AStarFinder.java          # Guiado por heurística
│   │   │   │   └── BFSFinder.java            # Não ponderado
│   │   │   ├── benchmark/            # Testes de performance
│   │   │   │   ├── BenchmarkRunner.java      # Stats, export CSV, MD
│   │   │   │   ├── BenchmarkMain.java        # Ponto de entrada
│   │   │   │   └── GrafoGenerator.java       # Geração de grafos de teste
│   │   │   ├── controller/           # Endpoints REST
│   │   │   ├── dto/                  # Objetos Request/Response
│   │   │   ├── exception/            # Exceções customizadas
│   │   │   ├── model/
│   │   │   │   └── Grafo.java        # Grafo de matriz de adjacência
│   │   │   ├── service/              # Lógica de negócio
│   │   │   │   ├── DijkstraService.java
│   │   │   │   ├── CaminhoMaximoService.java # Variante Bellman-Ford
│   │   │   │   ├── GrafoService.java
│   │   │   │   └── ValidadorGrafo.java
│   │   │   ├── terrain/              # Modelagem de custo de terreno
│   │   │   │   └── TerrainCostCalculator.java
│   │   │   └── util/
│   │   │       └── GrafoVisualizer.java      # ASCII/ANSI + PNG
│   │   └── test/                     # 122 testes unitários
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/                         # React + Vite + TailwindCSS
│   ├── src/
│   │   ├── components/
│   │   ├── services/
│   │   └── ...
│   ├── package.json
│   └── Dockerfile
│
├── python-service/                   # Flask + NetworkX + Matplotlib
│   ├── app.py
│   ├── requirements.txt
│   └── Dockerfile
│
├── docker-compose.yml                # Orquestração multi-serviço
└── README.md                         # Este arquivo
```

---

## 🧪 Suite de Testes

**122 testes passando** cobrindo:

| Módulo | Testes | Cobertura |
|--------|--------|-----------|
| `DijkstraServiceTest` | 15 | Caminho mínimo, desconexo, vértices inválidos |
| `AStarFinderTest` | 6 | Heurísticas, otimalidade |
| `BFSFinderTest` | 6 | Menor número de saltos, comparação com Dijkstra |
| `DijkstraFinderTest` | 8 | Fila de prioridade, casos de borda |
| `CaminhoMaximoServiceTest` | 12 | Caminho máximo, ciclos positivos, Bellman-Ford |
| `ValidadorGrafoTest` | 21 | Validação de matriz, conectividade, vértices |
| `GrafoTest` | 16 | Construção, getters, cópias defensivas |
| `GrafoServiceTest` | 7 | Integração, processamento min/max path |
| `GrafoVisualizerTest` | 13 | Render ASCII, export PNG, rótulos |
| `TerrainCostCalculatorTest` | 17 | Tipos de terreno, inclinações, diagonais, cenários |

### Executando Testes

```bash
# Testes do backend (Java 21 requerido)
cd backend
./mvnw test

# Ou com Docker
docker compose run --rm backend ./mvnw test
```

---

## 🛠 Desenvolvimento

### Build do JAR

```bash
cd backend
./mvnw clean package -DskipTests
# Saída: target/hiking-route-0.0.1-SNAPSHOT.jar
```

### Qualidade de Código

- **Java 21** com features modernas (records, pattern matching, var)
- **Spring Boot 3.2** com Jakarta EE 9+ (jakarta.validation)
- **AssertJ** para assertions fluentes em testes
- **JUnit 5** com classes de teste aninhadas
- **Maven Wrapper** para builds reproduzíveis

### Adicionando Novos Tipos de Terreno

```java
// Em TerrainCostCalculator.java enum
NOVO_TERRENO(3.5, "Descrição do terreno"),
```

### Adicionando Novas Heurísticas para A*

```java
// Em AStarFinder.java
public enum Heuristica {
    MANHATTAN, EUCLIDIANA, CHEBYSHEV,
    OCTILE // Para grades com movimento diagonal
}
```

---

## 📈 Dicas de Performance

1. **Use A* com Manhattan** para mapas de trilha baseados em grade (melhor aceleração)
2. **Desative diagonais** se não necessárias (reduz arestas em ~50%)
3. **Pré-calcule grafos de terreno** para mapas estáticos
4. **Ajuste heap size** para grafos grandes: `-Xmx2g`
5. **Profile com JFR**: `./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-XX:StartFlightRecording=..."`

---

## 🤝 Contribuindo

1. Faça fork do repositório
2. Crie uma branch de feature (`git checkout -b feature/feature-incrivel`)
3. Execute testes (`./mvnw test`)
4. Commit as mudanças (`git commit -m 'Adiciona feature incrível'`)
5. Push para a branch (`git push origin feature/feature-incrivel`)
6. Abra um Pull Request

---

## 📄 Licença

Este projeto é licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 🙏 Agradecimentos

- **Dijkstra, A*, BFS** - Algoritmos clássicos da teoria dos grafos
- **Spring Boot** - Desenvolvimento rápido de aplicações
- **React + Vite** - Tooling moderno de frontend
- **NetworkX + Matplotlib** - Visualização de grafos em Python
- **JUnit 5 + AssertJ** - Excelente experiência de testes

---

<div align="center">

**Desenvolvido com ❤️ para a comunidade de trilhas**

[⬆ Voltar ao Topo](#-hiking-route-optimizer)

</div>