import java.util.*;

public class CO3Assignment {

    // Vertex color constants
    private static final String WHITE = "WHITE";   // Unvisited
    private static final String GREY = "GREY";     // Currently visiting (in recursion stack)
    private static final String BLACK = "BLACK";   // Fully processed

    // Graph representation
    private final Map<String, List<String>> adjList = new TreeMap<>(); // TreeMap ensures alphabetical neighbor order
    private final Map<String, String> colors = new HashMap<>();

    // Initialize graph structure
    public void addEdge(String u, String v) {
        adjList.putIfAbsent(u, new ArrayList<>());
        adjList.putIfAbsent(v, new ArrayList<>());
        adjList.get(u).add(v);
    }

    public void detectCycle(String startNode) {
        System.out.println("=== PART (i) & (ii): DFS CYCLE DETECTION TRACE ===");
        System.out.println("Starting DFS traversal from vertex: " + startNode + "\n");

        // Initialize all vertices to WHITE
        for (String node : adjList.keySet()) {
            colors.put(node, WHITE);
        }

        // Run DFS from the requested starting vertex
        boolean hasCycle = dfsVisit(startNode);

        System.out.println("\n------------------------------------------------");
        if (hasCycle) {
            System.out.println("RESULT: CI rejected the workflow with 'cycle detected'.");
        } else {
            System.out.println("RESULT: No cycle detected.");
        }

        System.out.println("\n=== PART (iii): TIME COMPLEXITY ===");
        System.out.println("Time Complexity: O(V + E)");
        System.out.println("Where V is the number of vertices (jobs) and E is the number of directed edges (dependencies).");
    }

    private boolean dfsVisit(String u) {
        // Color vertex GREY when we first discover it
        colors.put(u, GREY);
        System.out.println("-> Visited " + u + " | Color updated to GREY (added to recursion stack)");

        // Sort neighbors alphabetically to strictly satisfy the prompt requirement
        List<String> neighbors = adjList.getOrDefault(u, new ArrayList<>());
        Collections.sort(neighbors); 

        for (String v : neighbors) {
            System.out.println("   Examining edge: " + u + " -> " + v + " (Destination color: " + colors.get(v) + ")");

            if (colors.get(v).equals(GREY)) {
                // Cycle detection rule matched!
                System.out.println("\n[CYCLE DETECTED!] Found a BACK EDGE to a GREY vertex (" + v + ").");
                System.out.println("Step Breakdown: Job '" + u + "' depends on '" + v + "', which is still actively being processed in the current DFS path.");
                return true; // Cycle detected
            } 
            
            if (colors.get(v).equals(WHITE)) {
                // Recursively visit unvisited neighbors
                if (dfsVisit(v)) {
                    return true;
                }
            }
            
            if (colors.get(v).equals(BLACK)) {
                System.out.println("   Edge " + u + " -> " + v + " points to a BLACK vertex. No action needed.");
            }
        }

        // Color vertex BLACK once all its neighbors are processed
        colors.put(u, BLACK);
        System.out.println("<- Finished " + u + " | Color updated to BLACK (removed from recursion stack)");
        return false;
    }

    public static void main(String[] args) {
        CO3Assignment graph = new CO3Assignment();

        /* Graph Setup based on the prompt's dependency scenario:
           "deploy" needs "build"        -> deploy -> build
           "build" needs "test"          -> build -> test
           "test" needs "integration"    -> test -> integration
           "integration" needs "build"   -> integration -> build
        */
        graph.addEdge("deploy", "build");
        graph.addEdge("build", "test");
        graph.addEdge("test", "integration");
        graph.addEdge("integration", "build");

        // Run cycle detection starting from "deploy"
        graph.detectCycle("deploy");
    }
}