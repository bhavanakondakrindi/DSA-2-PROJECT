import java.util.Locale;

public class CO2Assignment {

    // Scenario Constants
    private static final int TREE_HEIGHT = 5;
    private static final int CACHED_LEVELS = 3; // Top 3 levels (Root, L1, L2) are cached
    private static final int INSERTS_PER_SECOND = 5000;
    
    // Average Disk Seek + Read/Write time for a standard HDD/SSD (in milliseconds)
    // We assume a conservative ~2ms to 5ms for random cloud block storage / SSD I/O.
    private static final double AVG_IO_LATENCY_MS = 2.0; 

    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("   PostgreSQL B-Tree Page Split Analysis & Simulation    ");
        System.out.println("=========================================================\n");

        // (i) Single Page Split Mechanics
        System.out.println("### (i) Single Leaf Page-Split Mechanics");
        System.out.println("---------------------------------------------------------");
        System.out.println("* **Key Distribution:** The full page (127 keys + 1 new key = 128 keys) ");
        System.out.println("  is split roughly 50/50. 64 keys remain in the original page, ");
        System.out.println("  and 64 keys move to a newly allocated right-sibling page.");
        System.out.println("* **Disk I/O Cost for a single isolated split:**");
        System.out.println("  - 1 Read: To fetch the target leaf page (if not already in buffer).");
        System.out.println("  - 2 Writes: Write back the modified original page and write the new page.");
        System.out.println("  - *Total for 1 page split (excluding parent updates):* 3 Disk I/Os.\n");

        // (ii) Cascading Effect
        System.out.println("### (ii) Cascading Splits Explanation");
        System.out.println("---------------------------------------------------------");
        System.out.println("* When a leaf splits, a parent pointer (key + page reference) must be inserted ");
        System.out.println("  into its parent node. If that parent node is also entirely full (127 keys), ");
        System.out.println("  it cannot accept the new pointer and must split as well.");
        System.out.println("* This effect can cascade all the way to the root. If the root splits, a new ");
        System.out.println("  root is allocated, and the tree height grows by 1 (from 5 to 6).\n");

        // (iii) I/O Computation for Worst-Case Cascading Split
        System.out.println("### (iii) Disk I/O Computation & OLTP Feasibility");
        System.out.println("---------------------------------------------------------");

        int totalReads = 0;
        int totalWrites = 0;

        // Level indexing: 1 (Root), 2 (L1), 3 (L2), 4 (L3), 5 (Leaf)
        for (int level = TREE_HEIGHT; level >= 1; level--) {
            boolean isCached = (level <= CACHED_LEVELS);
            
            // For every level that splits:
            // 1. Read the node: If it is cached, 0 Disk I/O. If uncached, 1 Disk Read.
            if (!isCached) {
                totalReads += 1;
            }
            // 2. Write the nodes: Both the modified node and the new split node must be written to disk.
            // (Note: Even for cached levels, dirty blocks must eventually hit the WAL/disk)
            totalWrites += 2; 
        }

        int totalIO = totalReads + totalWrites;

        System.out.printf("Tree Height: %d | Cached Levels (from top): %d%n", TREE_HEIGHT, CACHED_LEVELS);
        System.out.printf("-> Uncached Reads required: %d%n", totalReads);
        System.out.printf("-> Dirty Page Writes required: %d%n", totalWrites);
        System.out.printf("-> **Total Disk I/O for Worst-Case Cascade Split: %d I/Os**%n%n", totalIO);

        // OLTP Analysis
        System.out.println("### OLTP Workload Evaluation");
        System.out.println("---------------------------------------------------------");
        System.out.printf("Target Insert Rate: %,d inserts/sec%n", INSERTS_PER_SECOND);
        
        // Let's assume a standard worst-case scenario where EVERY insert splits (unrealistic, but demonstrates limits)
        double totalIOPerSecondIfAllSplit = INSERTS_PER_SECOND * totalIO;

        System.out.printf(Locale.US, "If *every* insert triggered a worst-case root split, it would require: %,.0f IOPS.%n", totalIOPerSecondIfAllSplit);
        
        System.out.println("\n**Conclusion:**");
        System.out.println("> **YES, this is acceptable for an OLTP workload**, because B-tree page splits ");
        System.out.println("> are mathematically **amortized**. A root-level cascading split is an extremely rare ");
        System.out.println("> event (1 in millions). The vast majority of the 5,000 inserts/sec will result in ");
        System.out.println("> 0 page splits (normal inserts), or a single leaf split requiring only a few asynchronous writes via WAL.");
    }
}