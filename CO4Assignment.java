import java.util.*;

public class CO4Assignment {

    // Simple Edge class as given in the boilerplate
    static class Edge {
        int from, to, weight;
        
        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    /**
     * Helper method implementing Dijkstra's algorithm using an adjacency list.
     * Operates on non-negative reweighted edges.
     */
    private static int[] dijkstra(int n, List<Edge>[] adj, int src) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Priority queue storing pairs of [node, distance]
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{src, 0});

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];
            int d = curr[1];

            if (d > dist[u]) continue;

            for (Edge e : adj[u]) {
                if (dist[u] != Integer.MAX_VALUE && dist[u] + e.weight < dist[e.to]) {
                    dist[e.to] = dist[u] + e.weight;
                    pq.add(new int[]{e.to, dist[e.to]});
                }
            }
        }
        return dist;
    }

    /** * Returns dist[u][v] = shortest path from u to v for all pairs. 
     * Integer.MAX_VALUE (or INF) if unreachable. 
     */
    static int[][] johnson(int n, List<Edge> edges) {
        // Step 1: add virtual source q (index n).
        List<Edge> augmented = new ArrayList<>(edges);
        for (int i = 0; i < n; i++) {
            augmented.add(new Edge(n, i, 0));
        }

        // Step 2: Bellman-Ford from q to compute h[].
        int[] h = new int[n + 1];
        Arrays.fill(h, Integer.MAX_VALUE);
        h[n] = 0;

        int numVerticesAugmented = n + 1;

        // --- V-1 iterations of edge relaxation ---
        for (int i = 1; i <= numVerticesAugmented - 1; i++) {
            for (Edge e : augmented) {
                if (h[e.from] != Integer.MAX_VALUE && h[e.from] + e.weight < h[e.to]) {
                    h[e.to] = h[e.from] + e.weight;
                }
            }
        }

        // --- V-th pass: detect negative cycle ---
        for (Edge e : augmented) {
            if (h[e.from] != Integer.MAX_VALUE && h[e.from] + e.weight < h[e.to]) {
                throw new RuntimeException("Graph contains a negative weight cycle!");
            }
        }

        // --- reweight each edge ---
        @SuppressWarnings("unchecked")
        List<Edge>[] reweightedAdj = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            reweightedAdj[i] = new ArrayList<>();
        }

        for (Edge e : edges) {
            int reweightedCost = e.weight + h[e.from] - h[e.to];
            reweightedAdj[e.from].add(new Edge(e.from, e.to, reweightedCost));
        }

        // Step 4: run Dijkstra V times on reweighted graph; un-reweight final distances
        int[][] dist = new int[n][n];
        for (int u = 0; u < n; u++) {
            int[] d = dijkstra(n, reweightedAdj, u);
            for (int v = 0; v < n; v++) {
                if (d[v] == Integer.MAX_VALUE) {
                    dist[u][v] = Integer.MAX_VALUE;
                } else {
                    dist[u][v] = d[v] - h[u] + h[v];
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) {
        // Number of original vertices (c1=0, c2=1, c3=2, c4=3, c5=4)
        int n = 5; 

        // Directed edges setup
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1, 4));  // c1 -> c2 (4)
        edges.add(new Edge(0, 2, 8));  // c1 -> c3 (8)
        edges.add(new Edge(1, 2, -3)); // c2 -> c3 (-3)
        edges.add(new Edge(1, 3, 5));  // c2 -> c4 (5)
        edges.add(new Edge(1, 4, -2)); // c2 -> c5 (-2)
        edges.add(new Edge(2, 3, 2));  // c3 -> c4 (2)
        edges.add(new Edge(3, 4, 6));  // c4 -> c5 (6)

        System.out.println("--- Running Johnson's Algorithm ---");
        try {
            int[][] shortestPaths = johnson(n, edges);

            // Print All-Pairs Shortest Path Matrix
            System.out.println("\nAll-Pairs Shortest Paths Matrix (c1 to c5):");
            System.out.print("     ");
            for (int j = 0; j < n; j++) System.out.printf("c%d   ", j + 1);
            System.out.println("\n-------------------------------------");

            for (int i = 0; i < n; i++) {
                System.out.printf("c%d | ", i + 1);
                for (int j = 0; j < n; j++) {
                    if (shortestPaths[i][j] == Integer.MAX_VALUE) {
                        System.out.printf("%-4s ", "INF");
                    } else {
                        System.out.printf("%-4d ", shortestPaths[i][j]);
                    }
                }
                System.out.println();
            }
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}