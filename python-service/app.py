from flask import Flask, request, send_file, jsonify
import io
import networkx as nx
import matplotlib.pyplot as plt
import numpy as np

app = Flask(__name__)

def matrix_to_graph(matrix, directed=True):
    """
    Converte matriz (lista de listas ou numpy array) em um NetworkX graph.
    Assume: matrix[i][j] == weight (0 ou None -> sem aresta)
    """
    mat = np.array(matrix)
    n = mat.shape[0]
    if mat.shape[0] != mat.shape[1]:
        raise ValueError("A matriz precisa ser quadrada (n x n).")
    G = nx.DiGraph() if directed else nx.Graph()
    # adicionar nós
    for i in range(n):
        G.add_node(i)
    # adicionar arestas
    for i in range(n):
        for j in range(n):
            w = mat[i, j]
            # considera aresta se não for zero, não-nan e não None
            if w is None:
                continue
            try:
                if np.isnan(w):
                    continue
            except:
                pass
            try:
                if float(w) != 0.0:
                    G.add_edge(i, j, weight=float(w))
            except:
                # se não numérico, adiciona aresta sem peso
                G.add_edge(i, j, weight=w)
    return G

def draw_graph_to_png_bytes(G, labels=None, k=None, figsize=(8,6), show_edge_weights=True):
    """
    Desenha o grafo e retorna bytes PNG.
    - labels: lista de labels para cada nó (opcional)
    - k: parâmetro do spring_layout (float) — distância "ideal" entre nós
    """
    plt.figure(figsize=figsize)
    # layout
    if k is not None:
        pos = nx.spring_layout(G, k=float(k), seed=42)
    else:
        pos = nx.spring_layout(G, seed=42)

    # node labels
    if labels:
        node_labels = {i: str(labels[i]) for i in G.nodes()}
    else:
        node_labels = {i: str(i) for i in G.nodes()}

    # draw
    nx.draw_networkx_nodes(G, pos, node_size=700)
    nx.draw_networkx_edges(
        G,
        pos,
        arrows=G.is_directed(),
        arrowstyle='-|>',
        arrowsize=12,
        min_source_margin=15,   # distância da seta em relação ao nó de origem
        min_target_margin=15    # distância da seta em relação ao nó de destino
    )
    nx.draw_networkx_labels(G, pos, labels=node_labels, font_size=10)

    if show_edge_weights:
        edge_labels = nx.get_edge_attributes(G, 'weight')
        if edge_labels:
            nx.draw_networkx_edge_labels(G, pos, edge_labels=edge_labels, font_size=9)

    plt.axis('off')
    buf = io.BytesIO()
    plt.tight_layout()
    plt.savefig(buf, format='png', dpi=150)
    plt.close()
    buf.seek(0)
    return buf

@app.route('/graph', methods=['POST'])
def graph_endpoint():
    """
    Espera JSON com pelo menos:
    {
      "matrix": [[0, 1, 0], [0, 0, 2], [3, 0, 0]],
      "directed": true,           # opcional, default true
      "labels": ["A","B","C"],    # opcional
      "k": 0.5,                   # opcional, valor para spring_layout
      "figsize": [8,6]            # opcional
    }
    Retorna: image/png
    """
    try:
        data = request.get_json(force=True)
    except Exception as e:
        return jsonify({"error": "JSON inválido ou ausente", "details": str(e)}), 400

    if not data or 'matrix' not in data:
        return jsonify({"error": "chave 'matrix' obrigatória no JSON"}), 400

    matrix = data['matrix']
    directed = data.get('directed', True)
    labels = data.get('labels', None)
    k = data.get('k', None)
    figsize = tuple(data.get('figsize', [8,6]))

    try:
        G = matrix_to_graph(matrix, directed=directed)
    except Exception as e:
        return jsonify({"error": "erro ao converter matriz em grafo", "details": str(e)}), 400

    # validação simples de labels
    if labels is not None:
        if len(labels) != G.number_of_nodes():
            return jsonify({"error": "tamanho de 'labels' diferente do número de nós"}), 400

    buf = draw_graph_to_png_bytes(G, labels=labels, k=k, figsize=figsize)
    return send_file(buf, mimetype='image/png', as_attachment=False, download_name='graph.png')

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
