import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CO6Assignment {

    /**
     * Part (b): Computes the edit distance using the standard 2D DP matrix
     * and displays the step-by-step back-tracking of operations.
     */
    public static void solveAndTrace(String a, String b) {
        int m = a.length();
        int n = b.length();
        int[][] dp = new int[m + 1][n + 1];

        // Boundary: edit-distance to empty string
        for (int i = 0; i <= m; i++) dp[i][0] = i;  // delete i chars
        for (int j = 0; j <= n; j++) dp[0][j] = j;  // insert j chars

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // no edit needed
                } else {
                    int deleteCost = dp[i - 1][j];
                    int insertCost = dp[i][j - 1];
                    int substituteCost = dp[i - 1][j - 1];
                    
                    dp[i][j] = 1 + Math.min(deleteCost, Math.min(insertCost, substituteCost));
                }
            }
        }

        // Print the DP Matrix (Part a)
        System.out.println("--- Edit Distance DP Table ---");
        System.out.print("      O ");
        for (int j = 0; j < n; j++) {
            System.out.print(" " + b.charAt(j) + " ");
        }
        System.out.println();

        for (int i = 0; i <= m; i++) {
            if (i == 0) System.out.print("O   ");
            else System.out.print(a.charAt(i - 1) + "   ");
            
            for (int j = 0; j <= n; j++) {
                System.out.printf("%3d", dp[i][j]);
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("Final dp[" + m + "][" + n + "] = " + dp[m][n]);
        System.out.println();

        // Part (a): Back-trace from dp[m][n] to dp[0][0]
        System.out.println("--- Back-tracked Edit Operations ---");
        List<String> operations = new ArrayList<>();
        int i = m;
        int j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && a.charAt(i - 1) == b.charAt(j - 1)) {
                i--;
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                operations.add("substitute " + a.charAt(i - 1) + " -> " + b.charAt(j - 1));
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                operations.add("delete " + a.charAt(i - 1));
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                operations.add("insert " + b.charAt(j - 1));
                j--;
            } else {
                if (i > 0) {
                    operations.add("delete " + a.charAt(i - 1));
                    i--;
                } else {
                    operations.add("insert " + b.charAt(j - 1));
                    j--;
                }
            }
        }

        // Reverse the operations list to see them from beginning to end
        Collections.reverse(operations);
        for (String op : operations) {
            System.out.println("- " + op);
        }
        System.out.println();
    }

    /**
     * Part (b) iii: Standard 1D-array Space Optimised Approach
     */
    public static int editDistanceOptimized(String a, String b) {
        int m = a.length();
        int n = b.length();
        
        int[] prevRow = new int[n + 1];
        int[] currRow = new int[n + 1];

        for (int j = 0; j <= n; j++) {
            prevRow[j] = j;
        }

        for (int i = 1; i <= m; i++) {
            currRow[0] = i; 
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    currRow[j] = prevRow[j - 1];
                } else {
                    int deleteCost = prevRow[j];
                    int insertCost = currRow[j - 1];
                    int substituteCost = prevRow[j - 1];
                    currRow[j] = 1 + Math.min(deleteCost, Math.min(insertCost, substituteCost));
                }
            }
            System.arraycopy(currRow, 0, prevRow, 0, n + 1);
        }

        return prevRow[n];
    }

    public static void main(String[] args) {
        String word1 = "kitten";
        String word2 = "sitting";

        System.out.println("Processing: '" + word1 + "' vs '" + word2 + "'\n");
        
        solveAndTrace(word1, word2);

        int optResult = editDistanceOptimized(word1, word2);
        System.out.println("--- Space-Optimized Execution ---");
        System.out.println("Optimized 1D Space Method Result: " + optResult);
        System.out.println();
        
        System.out.println("--- Complexity Breakdown ---");
        System.out.println("(i)  Time Complexity:  O(m x n) - Loops through both string lengths.");
        System.out.println("(ii) Space Complexity: O(m x n) - Standard approach stores the full matrix.");
        System.out.println("(iii)Optimized Space:  O(n)       - Storing only the preceding structural array layer.");
    }
}