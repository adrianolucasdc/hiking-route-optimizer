from flask import Flask, request, send_file, jsonify
from flask_cors import CORS
import io
import networkx as nx
import matplotlib
matplotlib.use('Agg')  # Non-interactive backend
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
import numpy as np
import math

app = Flask(__name__)
CORS(app)

# ============================================================
# TERRAIN COLOR PALETTE (matches backend TerrainCostCalculator)
# ============================================================
TERRAIN_COLORS = {
    'TRILHA_BOA': '#4ade80',        # green-400
    'TRILHA_REGULAR': '#86efac',    # green-300
    'TERRA_BATIDA': '#fde047',      # yellow-300
    'GRAMA': '#a3e635',             # lime-400
    'CASCALHO': '#fbbf24',          # amber-400
    'ARVORE_ESPARSA': '#fb923c',    # orange-400
    'AREIA': '#fcd34d',             # yellow-300
    'NEVE': '#f3f4f6',              # gray-100
    'ROCHA': '#9ca3af',             # gray-400
    'LAMA': '#a16207',              # amber-800
    'GELO': '#bfdbfe',              # blue-200
    'ARVORE_DENSA': '#166534',      # green-900
    'AGUA_RASO': '#60a5fa',         # blue-400
    'AGUA_FUNDO': '#1e3a8a',        # blue-900
    'PAREDE_ROCHA': '#374151',      # gray-700
    'OBSTACULO': '#111827',         # gray-900
}

TERRAIN_COSTS = {
    'TRILHA_BOA': 1.0,
    'TRILHA_REGULAR': 1.5,
    'TERRA_BATIDA': 2.0,
    'GRAMA': 2.5,
    'CASCALHO': 3.0,
    'ARVORE_ESPARSA': 4.0,
    'AREIA': 4.0,
    'NEVE': 5.0,
    'ROCHA': 5.0,
    'LAMA': 6.0,
    'GELO': 8.0,
    'ARVORE_DENSA': 10.0,
    'AGUA_RASO': 10.0,
    'AGUA_FUNDO': float('inf'),
    'PAREDE_ROCHA': float('inf'),
    'OBSTACULO': float('inf'),
}

def get_terrain_color(terrain_type):
    """Get color for terrain type, with fallback."""
    return TERRAIN_COLORS.get(terrain_type, '#71717a')  # zinc-500

def get_terrain_cost(terrain_type):
    """Get cost for terrain type."""
    return TERRAIN_COSTS.get(terrain_type, 1.0)


# ============================================================
# HEALTH CHECK
# ============================================================
@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "healthy", "service": "python-graph-visualization"}), 200


# ============================================================
# GRAPH CONSTRUCTION
# ============================================================
def matrix_to_graph(matrix, directed=True, terrain_types=None, elevations=None, cell_size_meters=10, allow_diagonals=True):
    """Convert adjacency matrix to NetworkX graph with terrain metadata."""
    mat = np.array(matrix, dtype=float)
    n = mat.shape[0]
    if mat.shape[0] != mat.shape[1]:
        raise ValueError("A matriz precisa ser quadrada (n x n).")

    G = nx.DiGraph() if directed else nx.Graph()

    # Add nodes with position hints (grid layout)
    for i in range(n):
        G.add_node(i)

    # Grid dimensions (assume square grid)
    grid_size = int(math.sqrt(n))
    if grid_size * grid_size != n:
        grid_size = None  # Not a perfect grid, use spring layout

    for i in range(n):
        for j in range(n):
            w = mat[i, j]
            if w is None:
                continue
            try:
                if np.isnan(w):
                    continue
            except:
                pass
            try:
                weight = float(w)
                if weight != 0.0:
                    # Add edge with metadata
                    edge_data = {'weight': weight}

                    # Add terrain info if available
                    if terrain_types and i < len(terrain_types) and j < len(terrain_types[i]):
                        terrain = terrain_types[i][j]
                        edge_data['terrain'] = terrain
                        edge_data['color'] = get_terrain_color(terrain)
                        edge_data['cost'] = get_terrain_cost(terrain)

                    # Add elevation info if available
                    if elevations and i < len(elevations) and j < len(elevations[i]):
                        edge_data['elevation_diff'] = elevations[i][j]

                    G.add_edge(i, j, **edge_data)
            except:
                G.add_edge(i, j, weight=w)

    return G, grid_size


def compute_grid_positions(G, grid_size):
    """Compute grid-based positions for nodes (like a trail map)."""
    pos = {}
    n = G.number_of_nodes()
    for i in range(n):
        if grid_size:
            row = i // grid_size
            col = i % grid_size
            pos[i] = (col, -row)  # Negative row for top-to-bottom
        else:
            # Fallback to spring layout
            pos = nx.spring_layout(G, seed=42)
            break
    return pos


def draw_hiking_trail_graph(G, labels=None, path=None, terrain_types=None, elevations=None,
                             grid_size=None, figsize=(12, 10), show_edge_weights=False,
                             cell_size_meters=10, allow_diagonals=True, matrix=None):
    """
    Draw a hiking trail style graph visualization.

    Features:
    - Grid-based layout (trail map style)
    - Path highlighted in bright orange/red
    - Edge colors by terrain type
    - Node labels with elevation markers
    - Trail-like styling (curved paths, terrain legend)
    """
    fig, ax = plt.subplots(figsize=figsize, facecolor='#09090b')  # zinc-950 dark bg
    ax.set_facecolor('#09090b')

    # Compute positions
    if grid_size:
        pos = compute_grid_positions(G, grid_size)
    else:
        pos = nx.spring_layout(G, seed=42, k=1.5)

    if labels:
        node_labels = {i: str(labels[i]) for i in G.nodes()}
    else:
        node_labels = {i: str(i) for i in G.nodes()}

    # ============================================================
    # DRAW EDGES (BACKGROUND - all edges)
    # ============================================================
    edges = list(G.edges(data=True))

    # Separate path edges from non-path edges
    path_edges = set()
    if path and len(path) > 1:
        for i in range(len(path) - 1):
            u, v = path[i], path[i + 1]
            if G.has_edge(u, v):
                path_edges.add((u, v))
            elif G.has_edge(v, u):  # Undirected
                path_edges.add((v, u))

    # Draw non-path edges first (subtle)
    for u, v, data in edges:
        is_path_edge = (u, v) in path_edges
        terrain = data.get('terrain', 'TERRA_BATIDA')
        color = data.get('color', get_terrain_color(terrain))
        weight = data.get('weight', 1.0)

        if is_path_edge:
            continue  # Draw path edges later on top

        # Style for non-path edges
        edge_color = color + '60'  # 40% opacity
        width = max(0.5, min(3.0, 1.0 / max(weight, 0.1) * 2))

        nx.draw_networkx_edges(
            G, pos,
            edgelist=[(u, v)],
            edge_color=edge_color,
            width=width,
            arrows=G.is_directed(),
            arrowstyle='-|>',
            arrowsize=10,
            min_source_margin=12,
            min_target_margin=12,
            alpha=0.4,
            ax=ax
        )

    # ============================================================
    # DRAW PATH EDGES (HIGHLIGHTED - thick, bright)
    # ============================================================
    if path_edges:
        path_edge_list = list(path_edges)
        nx.draw_networkx_edges(
            G, pos,
            edgelist=path_edge_list,
            edge_color='#ea580c',  # orange-600 - accent color
            width=4.0,
            arrows=G.is_directed(),
            arrowstyle='-|>',
            arrowsize=18,
            min_source_margin=15,
            min_target_margin=15,
            alpha=1.0,
            ax=ax
        )

        # Add glow effect (draw twice with blur)
        nx.draw_networkx_edges(
            G, pos,
            edgelist=path_edge_list,
            edge_color='#f97316',  # orange-500
            width=6.0,
            arrows=False,
            alpha=0.15,
            ax=ax
        )

    # ============================================================
    # DRAW NODES
    # ============================================================
    node_colors = []
    node_sizes = []

    for node in G.nodes():
        if path and node in path:
            idx = path.index(node)
            if idx == 0:
                node_colors.append('#16a34a')  # green-600 - START
                node_sizes.append(1000)
            elif idx == len(path) - 1:
                node_colors.append('#ef4444')  # red-500 - END
                node_sizes.append(1000)
            else:
                node_colors.append('#ea580c')  # orange-600 - PATH
                node_sizes.append(800)
        else:
            node_colors.append('#27272a')  # zinc-800 - NORMAL
            node_sizes.append(500)

    nx.draw_networkx_nodes(
        G, pos,
        node_color=node_colors,
        node_size=node_sizes,
        edgecolors='#3f3f46',  # zinc-700
        linewidths=2,
        ax=ax
    )

    # Node labels
    nx.draw_networkx_labels(
        G, pos,
        labels=node_labels,
        font_size=12,
        font_weight='bold',
        font_color='#fafafa',  # zinc-50
        font_family='monospace',
        ax=ax
    )

    # ============================================================
    # EDGE WEIGHTS (optional, only for path edges to avoid clutter)
    # ============================================================
    if show_edge_weights and path_edges:
        edge_labels = {}
        for u, v in path_edges:
            if G.has_edge(u, v):
                w = G[u][v].get('weight', 0)
                edge_labels[(u, v)] = f'{w:.1f}'

        if edge_labels:
            nx.draw_networkx_edge_labels(
                G, pos,
                edge_labels=edge_labels,
                font_size=9,
                font_color='#ea580c',
                font_weight='bold',
                bbox=dict(boxstyle='round,pad=0.2', facecolor='#09090b', edgecolor='#ea580c', alpha=0.9),
                ax=ax
            )

    # ============================================================
    # ADD ELEVATION MARKERS (if available)
    # ============================================================
    if elevations:
        for node in G.nodes():
            if node in pos:
                x, y = pos[node]
                # Find elevation for this node (approximate from incoming edges)
                elev = 0
                for pred in G.predecessors(node):
                    if elevations and pred < len(elevations) and node < len(elevations[pred]):
                        elev = elevations[pred][node]
                        break
                if elev != 0:
                    # Small elevation marker
                    ax.text(x, y + 0.15, f'{elev:+.0f}m',
                           fontsize=7, ha='center', va='bottom',
                           color='#f59e0b', fontweight='bold',
                           fontfamily='monospace',
                           bbox=dict(boxstyle='round,pad=0.1', facecolor='#09090b', edgecolor='#f59e0b', alpha=0.8))

    # ============================================================
    # LEGEND
    # ============================================================
    legend_elements = []

    # Path legend
    if path:
        legend_elements.append(mpatches.Patch(color='#16a34a', label='Início (Start)'))
        legend_elements.append(mpatches.Patch(color='#ea580c', label='Rota Ótima (Optimal Path)'))
        legend_elements.append(mpatches.Patch(color='#ef4444', label='Fim (Destination)'))
        legend_elements.append(mpatches.Patch(color='#27272a', label='Outros Nós'))

    # Terrain legend (only show terrain types present in graph)
    if terrain_types:
        present_terrains = set()
        for i in range(len(terrain_types)):
            for j in range(len(terrain_types[i])):
                if i < len(matrix) and j < len(matrix[i]) and matrix[i][j] > 0:
                    present_terrains.add(terrain_types[i][j])

        for terrain in sorted(present_terrains):
            if terrain in TERRAIN_COLORS:
                legend_elements.append(
                    mpatches.Patch(color=TERRAIN_COLORS[terrain], label=terrain.replace('_', ' ').title())
                )

    if legend_elements:
        leg = ax.legend(
            handles=legend_elements,
            loc='upper right',
            fontsize=9,
            facecolor='#111113',  # zinc-900
            edgecolor='#3f3f46',  # zinc-700
            labelcolor='#fafafa', # zinc-50
            framealpha=0.9,
            title='Legenda / Legend',
            title_fontsize=10
        )
        leg.get_title().set_color('#ea580c')

    # ============================================================
    # TITLE AND INFO
    # ============================================================
    title_text = 'Mapa da Trilha / Trail Map'
    if path:
        title_text += f'  —  Caminho: {" → ".join([node_labels.get(n, str(n)) for n in path])}'

    ax.set_title(title_text, fontsize=14, fontweight='bold', color='#fafafa', pad=20, fontfamily='monospace')

    # Subtitle with stats
    if path:
        path_cost = sum(G[path[i]][path[i+1]].get('weight', 0) for i in range(len(path)-1) if G.has_edge(path[i], path[i+1]))
        subtitle = f'Distância: {path_cost:.1f}  |  Nós visitados: {len(path)}  |  Algoritmo: Dijkstra/A*/BFS'
        ax.text(0.5, 0.95, subtitle, transform=ax.transAxes, ha='center', va='top',
               fontsize=10, color='#a1a1aa', fontfamily='monospace')

    ax.axis('off')
    plt.tight_layout()

    buf = io.BytesIO()
    plt.savefig(buf, format='png', dpi=200, facecolor='#09090b', edgecolor='none', bbox_inches='tight')
    plt.close()
    buf.seek(0)
    return buf


# ============================================================
# MAIN GRAPH ENDPOINT
# ============================================================
@app.route('/graph', methods=['POST'])
def graph_endpoint():
    try:
        data = request.get_json(force=True)
    except Exception as e:
        return jsonify({"error": "JSON inválido ou ausente", "details": str(e)}), 400

    if not data or 'matrix' not in data:
        return jsonify({"error": "chave 'matrix' obrigatória no JSON"}), 400

    matrix = data['matrix']
    directed = data.get('directed', True)

    # Optional parameters
    labels = data.get('labels', None)
    path = data.get('path', None)  # The optimal path to highlight
    terrain_types = data.get('terrainTypes', None)  # 2D array of terrain types
    elevations = data.get('elevations', None)  # 2D array of elevation diffs
    cell_size_meters = data.get('cellSizeMeters', 10)
    allow_diagonals = data.get('allowDiagonals', True)
    k = data.get('k', None)
    figsize = tuple(data.get('figsize', [12, 10]))
    show_edge_weights = data.get('showEdgeWeights', True)

    try:
        G, grid_size = matrix_to_graph(
            matrix,
            directed=directed,
            terrain_types=terrain_types,
            elevations=elevations,
            cell_size_meters=cell_size_meters,
            allow_diagonals=allow_diagonals
        )
    except Exception as e:
        return jsonify({"error": "erro ao converter matriz em grafo", "details": str(e)}), 400

    if labels is not None:
        if len(labels) != G.number_of_nodes():
            return jsonify({"error": "tamanho de 'labels' diferente do número de nós"}), 400

    try:
        buf = draw_hiking_trail_graph(
            G,
            labels=labels,
            path=path,
            terrain_types=terrain_types,
            elevations=elevations,
            grid_size=grid_size,
            figsize=figsize,
            show_edge_weights=show_edge_weights,
            cell_size_meters=cell_size_meters,
            allow_diagonals=allow_diagonals,
            matrix=matrix
        )
        return send_file(buf, mimetype='image/png', as_attachment=False, download_name='hiking-trail-map.png')
    except Exception as e:
        return jsonify({"error": "erro ao gerar imagem", "details": str(e)}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)