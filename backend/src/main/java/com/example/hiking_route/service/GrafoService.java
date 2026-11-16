package com.example.hiking_route.service;

import com.example.hiking_route.algorithm.AStarFinder;
import com.example.hiking_route.algorithm.BFSFinder;
import com.example.hiking_route.algorithm.PathResult;
import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.dto.GrafoRequest;
import com.example.hiking_route.dto.GrafoResponse;
import com.example.hiking_route.model.Grafo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GrafoService {

    @Autowired
    private DijkstraService dijkstraService;

    @Autowired
    private CaminhoMaximoService caminhoMaximoService;
    
    @Autowired
    private ValidadorGrafo validadorGrafo;


    public GrafoResponse processarGrafo(GrafoRequest request) {
        try {
            validadorGrafo.validarCompleto(request.getMatriz(), request.getOrigem(), request.getDestino());
            
            Grafo grafo = new Grafo(request.getMatriz());

            CaminhoResponse caminhoMinimo = calcularCaminhoMinimo(grafo, request);

            CaminhoResponse caminhoMaximo = caminhoMaximoService.encontrarCaminhoMaximo(
                grafo, request.getOrigem(), request.getDestino()
            );

            String status = "SUCESSO";
            if (!caminhoMinimo.isCaminhoEncontrado() && !caminhoMaximo.isCaminhoEncontrado()) {
                status = "NENHUM_CAMINHO_ENCONTRADO";
            } else if (!caminhoMinimo.isCaminhoEncontrado() || !caminhoMaximo.isCaminhoEncontrado()) {
                status = "CAMINHO_PARCIAL";
            }

            return new GrafoResponse(
                caminhoMinimo,
                caminhoMaximo,
                grafo.getNumeroVertices(),
                status
            );

        } catch (Exception e) {
            return new GrafoResponse(
                new CaminhoResponse(null, 0, "MINIMO", false, "Erro interno: " + e.getMessage()),
                new CaminhoResponse(null, 0, "MAXIMO", false, "Erro interno: " + e.getMessage()),
                0,
                "ERRO"
            );
        }
    }


    public CaminhoResponse processarCaminhoMinimo(GrafoRequest request) {
        try {
            validadorGrafo.validarCompleto(request.getMatriz(), request.getOrigem(), request.getDestino());
            
            Grafo grafo = new Grafo(request.getMatriz());
            return dijkstraService.encontrarCaminhoMinimo(grafo, request.getOrigem(), request.getDestino());
            
        } catch (Exception e) {
            return new CaminhoResponse(null, 0, "MINIMO", false, "Erro interno: " + e.getMessage());
        }
    }

    public CaminhoResponse processarCaminhoMaximo(GrafoRequest request) {
        try {
            validadorGrafo.validarCompleto(request.getMatriz(), request.getOrigem(), request.getDestino());
            
            Grafo grafo = new Grafo(request.getMatriz());
            return caminhoMaximoService.encontrarCaminhoMaximo(grafo, request.getOrigem(), request.getDestino());
            
        } catch (Exception e) {
            return new CaminhoResponse(null, 0, "MAXIMO", false, "Erro interno: " + e.getMessage());
        }
    }

    /**
     * Escolhe o algoritmo do caminho mínimo conforme o request.
     * Dijkstra é o padrão; A* e BFS são executados pelos PathFinders.
     */
    private CaminhoResponse calcularCaminhoMinimo(Grafo grafo, GrafoRequest request) {
        String algoritmo = request.getAlgoritmo() == null || request.getAlgoritmo().isBlank()
            ? "dijkstra" : request.getAlgoritmo().trim().toLowerCase();
        int origem = request.getOrigem();
        int destino = request.getDestino();

        switch (algoritmo) {
            case "dijkstra" -> {
                CaminhoResponse r = dijkstraService.encontrarCaminhoMinimo(grafo, origem, destino);
                r.setAlgoritmo("Dijkstra");
                return r;
            }
            case "bfs" -> {
                PathResult resultado = new BFSFinder().encontrarCaminho(grafo, origem, destino);
                String aviso = "BFS: menor número de arestas, pesos ignorados na busca; custo = soma dos pesos do caminho";
                return converter(resultado, grafo, aviso, true);
            }
            case "astar" -> {
                ConfigAStar cfg = configurarAStar(grafo, request.getHeuristica());
                AStarFinder finder = new AStarFinder(cfg.lado(), cfg.lado(), cfg.heuristica(), cfg.escala());
                return converter(finder.encontrarCaminho(grafo, origem, destino), grafo, cfg.aviso(), false);
            }
            default -> throw new IllegalArgumentException(
                "Algoritmo inválido: '" + request.getAlgoritmo() + "'. Use dijkstra, astar ou bfs");
        }
    }

    private CaminhoResponse converter(PathResult r, Grafo grafo, String aviso, boolean recalcularCusto) {
        List<Integer> caminho = r.getCaminho() == null ? Collections.emptyList() : r.getCaminho();
        double custo = r.getCusto();
        if (r.isEncontrado() && recalcularCusto) {
            custo = 0;
            for (int i = 0; i < caminho.size() - 1; i++) {
                custo += grafo.getPeso(caminho.get(i), caminho.get(i + 1));
            }
        }
        String mensagem = r.isEncontrado()
            ? "Caminho mínimo encontrado com sucesso (" + aviso + ")"
            : "Não existe caminho entre os vértices especificados";
        CaminhoResponse resposta = new CaminhoResponse(
            caminho, r.isEncontrado() ? custo : Double.POSITIVE_INFINITY, "MINIMO", r.isEncontrado(), mensagem);
        resposta.setAlgoritmo(r.getAlgoritmo());
        resposta.setNosExpandidos(r.getNosExpandidos());
        return resposta;
    }

    private record ConfigAStar(int lado, AStarFinder.Heuristica heuristica, double escala, String aviso) {}

    /**
     * A* só usa heurística de grade quando ela é admissível para o grafo recebido:
     * matriz N = L×L, arestas apenas entre células vizinhas e heurística escalada
     * pela menor aresta. Caso contrário usa heurística nula (equivale a Dijkstra).
     */
    private ConfigAStar configurarAStar(Grafo grafo, String nomeHeuristica) {
        AStarFinder.Heuristica pedida = parseHeuristica(nomeHeuristica);
        int n = grafo.getNumeroVertices();
        int lado = (int) Math.round(Math.sqrt(n));
        ConfigAStar semGrade = new ConfigAStar(0, AStarFinder.Heuristica.NULA, 1.0,
            "A* com heurística nula: o grafo não é uma grade L×L de vizinhos, equivale a Dijkstra");

        if (lado == 0 || lado * lado != n) {
            return semGrade;
        }

        double menorPeso = Double.POSITIVE_INFINITY;
        boolean soOrtogonais = true;
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (u == v || !grafo.existeAresta(u, v)) continue;
                int dx = Math.abs(u % lado - v % lado);
                int dy = Math.abs(u / lado - v / lado);
                if (Math.max(dx, dy) != 1) {
                    return semGrade;
                }
                if (dx + dy != 1) soOrtogonais = false;
                menorPeso = Math.min(menorPeso, grafo.getPeso(u, v));
            }
        }
        if (menorPeso == Double.POSITIVE_INFINITY) {
            return semGrade;
        }

        if (pedida == AStarFinder.Heuristica.MANHATTAN && !soOrtogonais) {
            return new ConfigAStar(0, AStarFinder.Heuristica.NULA, 1.0,
                "A* com heurística nula: Manhattan não é admissível com arestas diagonais, use Chebyshev");
        }
        double escala = (pedida == AStarFinder.Heuristica.EUCLIDIANA && !soOrtogonais)
            ? menorPeso / Math.sqrt(2) : menorPeso;
        return new ConfigAStar(lado, pedida, escala, "A* com heurística " + pedida);
    }

    private AStarFinder.Heuristica parseHeuristica(String nome) {
        if (nome == null || nome.isBlank()) return AStarFinder.Heuristica.MANHATTAN;
        return switch (nome.trim().toUpperCase()) {
            case "MANHATTAN" -> AStarFinder.Heuristica.MANHATTAN;
            case "EUCLIDEAN", "EUCLIDIANA" -> AStarFinder.Heuristica.EUCLIDIANA;
            case "CHEBYSHEV" -> AStarFinder.Heuristica.CHEBYSHEV;
            default -> throw new IllegalArgumentException(
                "Heurística inválida: '" + nome + "'. Use MANHATTAN, EUCLIDEAN ou CHEBYSHEV");
        };
    }
}
