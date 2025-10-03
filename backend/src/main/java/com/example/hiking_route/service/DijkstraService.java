package com.example.hiking_route.service;

import com.example.hiking_route.dto.CaminhoResponse;
import com.example.hiking_route.model.Grafo;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DijkstraService {

    public CaminhoResponse encontrarCaminhoMinimo(Grafo grafo, int origem, int destino) {
        int n = grafo.getNumeroVertices();
        
        if (origem < 0 || origem >= n || destino < 0 || destino >= n) {
            return new CaminhoResponse(
                Collections.emptyList(), 
                Double.POSITIVE_INFINITY, 
                "MINIMO", 
                false, 
                "Vértices de origem ou destino inválidos"
            );
        }

        double[] distancias = new double[n];
        int[] predecessores = new int[n];
        boolean[] visitados = new boolean[n];

        Arrays.fill(distancias, Double.POSITIVE_INFINITY);
        Arrays.fill(predecessores, -1);
        distancias[origem] = 0;

        PriorityQueue<Integer> filaPrioridade = new PriorityQueue<>(
            Comparator.comparingDouble(v -> distancias[v])
        );
        filaPrioridade.offer(origem);

        while (!filaPrioridade.isEmpty()) {
            int u = filaPrioridade.poll();
            
            if (visitados[u]) continue;
            visitados[u] = true;

            if (u == destino) break;

            for (int v = 0; v < n; v++) {
                if (!visitados[v] && grafo.existeAresta(u, v)) {
                    double peso = grafo.getPeso(u, v);
                    double novaDistancia = distancias[u] + peso;
                    
                    if (novaDistancia < distancias[v]) {
                        distancias[v] = novaDistancia;
                        predecessores[v] = u;
                        filaPrioridade.offer(v);
                    }
                }
            }
        }

        if (distancias[destino] == Double.POSITIVE_INFINITY) {
            return new CaminhoResponse(
                Collections.emptyList(), 
                Double.POSITIVE_INFINITY, 
                "MINIMO", 
                false, 
                "Não existe caminho entre os vértices especificados"
            );
        }

        List<Integer> caminho = construirCaminho(predecessores, origem, destino);
        
        return new CaminhoResponse(
            caminho, 
            distancias[destino], 
            "MINIMO", 
            true, 
            "Caminho mínimo encontrado com sucesso"
        );
    }


    private List<Integer> construirCaminho(int[] predecessores, int origem, int destino) {
        List<Integer> caminho = new ArrayList<>();
        int atual = destino;
        
        while (atual != -1) {
            caminho.add(atual);
            atual = predecessores[atual];
        }
        
        Collections.reverse(caminho);
        return caminho;
    }
}
