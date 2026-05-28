public class CO1Assignment {

    // --- (i) & (iii) Augmented AVL Tree Node Structure ---
    static class Node {
        double rating;      // The key (e.g., 4.2 stars)
        int height;         // For AVL balancing
        int size;           // Augmented field: total nodes in this subtree
        Node left, right;

        Node(double rating) {
            this.rating = rating;
            this.height = 1;
            this.size = 1;
        }
    }

    private Node root;

    // --- Helper Methods ---
    private int height(Node n) { return n == null ? 0 : n.height; }
    private int size(Node n) { return n == null ? 0 : n.size; }

    // Update size and height during maintenance
    private void update(Node n) {
        if (n != null) {
            n.height = 1 + Math.max(height(n.left), height(n.right));
            n.size = 1 + size(n.left) + size(n.right);
        }
    }

    // AVL Rotations (Maintains O(log n) overhead)
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;
        x.right = y;
        y.left = T2;
        update(y);
        update(x);
        return x;
    }

    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;
        y.left = x;
        x.right = T2;
        update(x);
        update(y);
        return y;
    }

    private int getBalance(Node n) { return n == null ? 0 : height(n.left) - height(n.right); }

    // --- (iii) Insert Operation with O(log n) Maintenance Overhead ---
    public void insert(double rating) {
        root = insert(root, rating);
    }

    private Node insert(Node node, double rating) {
        if (node == null) return new Node(rating);

        if (rating < node.rating) node.left = insert(node.left, rating);
        else node.right = insert(node.right, rating);

        // Maintenance: Recalculate size and height while unwinding
        update(node);

        // Check balance factor and perform rotations if needed
        int balance = getBalance(node);
        if (balance > 1 && rating < node.left.rating) return rightRotate(node);
        if (balance < -1 && rating > node.right.rating) return leftRotate(node);
        if (balance > 1 && rating > node.left.rating) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        if (balance < -1 && rating < node.right.rating) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }
        return node;
    }

    // --- (i) & (ii) O(log n) Median Finder (Tree Descent) ---
    public double findMedian() {
        if (root == null) throw new IllegalStateException("Tree is empty");
        int n = root.size;
        
        // Accurate handling for both even and odd sized trees
        if (n % 2 != 0) {
            return findKth(root, n / 2);
        } else {
            return (findKth(root, (n / 2) - 1) + findKth(root, n / 2)) / 2.0;
        }
    }

    // Binary search tree descent using tree size parameters
    private double findKth(Node node, int rank) {
        int leftSize = size(node.left);
        
        if (rank == leftSize)       return node.rating; // Match found
        else if (rank < leftSize)   return findKth(node.left, rank); // Descend left
        else                        return findKth(node.right, rank - leftSize - 1); // Descend right
    }

    // --- Main Method to Test ---
    public static void main(String[] args) {
        CO1Assignment bst = new CO1Assignment();

        // Populate tree
        double[] ratings = {4.5, 3.2, 4.8, 2.1, 4.2, 5.0, 3.8};
        for (double r : ratings) {
            bst.insert(r);
        }

        System.out.println("Total ratings processed: " + bst.root.size);
        System.out.println("Median Rating: " + bst.findMedian() + " stars");
    }
}