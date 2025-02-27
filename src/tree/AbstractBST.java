package tree;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ANSICode;
import util.Log;

import java.util.*;

public abstract class AbstractBST<T extends Comparable<T>, Node extends BSTNode<T>> implements Collection<T> {
    protected Node root = null;

    /**
     * Constructs a {@link Node} to use in the tree. This should be overridden by subclasses to return the right kind of
     * BSTNode to use in the tree
     *
     * @param value The value to put in the node
     */
    protected abstract Node constructNode(T value);

    /**
     * Constructs a {@link AbstractBST} with the same value type and node type as this tree. Used in algorithms
     */
    private AbstractBST<T, Node> makeEmptyTree() {
        try {
            return this.getClass().getConstructor(new Class[]{}).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected synchronized void insertAsRoot(T value) {
        assert root == null : "Tried to insert into non-null root";

        root = constructNode(value);
        printTreeToConsole();
    }

    /**
     * @param parent The root node to insert under
     * @param value  The value to insert into the tree
     * @return whether the tree changed as a result of this call
     */
    protected synchronized boolean add(Node parent, T value) {
        int compare = value.compareTo(parent.getValue());

        if (compare < 0) {
            if (parent.hasLeftChild()) {
                add((Node) parent.getLeftChild(), value);
            } else {
                System.out.println("Inserting " + value + " as left child of " + parent);
                parent.setLeftChild(constructNode(value));
                printTreeToConsole();
            }
        } else {
            if (parent.hasRightChild()) {
                add((Node) parent.getRightChild(), value);
            } else {
                System.out.println("Inserting " + value + " as right child of " + parent);
                parent.setRightChild(constructNode(value));
                printTreeToConsole();
            }
        }

        return true;
    }

    /**
     * @param value The value to search for
     * @return whether the value exists in the tree
     * @throws ClassCastException when value is not a {@link Comparable}
     */
    public final synchronized boolean contains(Object value) {
        if (!(value instanceof Comparable<?>)) throw new ClassCastException();
        return find((T) value) != null;
    }

    /**
     * @param values The values to search for
     * @return whether all the values exists in the tree
     * @throws ClassCastException when any of the values is not a {@link Comparable}
     */
    public final synchronized boolean containsAll(Collection<?> values) {
        return values.stream().allMatch(this::contains);
    }

    /**
     * @param value The value to insert into the tree
     * @return whether the tree changed as a result of this call
     */
    public final synchronized boolean add(T value) {
        if (root == null) {
            System.out.println("Inserting " + value + " as root");
            insertAsRoot(value);
            return true;
        } else return add(root, value);
    }

    /**
     * @param values the values to be inserted into the tree.
     * @return whether the collection was changed as a result of this operation
     */
    public final synchronized boolean addAll(Collection<? extends T> values) {
        return values.stream().map(this::add).toList().contains(true);
    }

    /**
     * @param value the value to erase from the tree
     * @return whether a value was removed as a result of this call
     * @throws ClassCastException when value is not a {@link Comparable}
     */
    @Override
    public final synchronized boolean remove(Object value) {
        if (!(value instanceof Comparable<?>)) return false;    // Can't remove a value that isn't of the right type

        // Find the node to be deleted
        var target = find((T) value);

        // If target not exist in the tree don't do anything
        if (target == null) return false;

        if (target == root && target.isLeaf()) {
            root = null;
        } else if (target.getDegree() == 2) {  // If deg 2, must first find inorder successor n and swap
            // The node to swap with (the inorder successor).
            // Guaranteed to exist because target must have a right child to be deg 2
            var swap = (Node) target.getInorderSuccessor();

            // Swap the values of the nodes (but not any other information)
            BSTNode.swapValues(target, swap);

            deleteSimple(swap);
        } else deleteSimple(target);

        return true;
    }

    protected void deleteSimple(Node target) {
        Log.log("Removing " + target + (
            target.hasParent()
                ? " (parent is " + target.getParent() + ")"
                : " (root)"
        ));
        printTreeToConsole();
        var node = target.hasLeftChild() ? target.getLeftChild() : target.hasRightChild() ? target.getRightChild() : null;
        switch (target.getChildType()) {
            case LEFT -> target.getParent().setLeftChild(node);
            case RIGHT -> target.getParent().setRightChild(node);
            case ROOT -> root = (Node) target.getLeftChild().makeRoot();
        }
    }

    /**
     * @param values The values to remove from the tree
     * @return whether the collection was changed as a result of this operation
     */
    public final synchronized boolean removeAll(Collection<?> values) {
        return values.stream().map(this::remove).toList().contains(true);
    }

    /**
     * @param c A collection of values to intersect with this tree
     * @return A new bst.BST containing the elements common to this tree and the given values
     */
    @Contract(pure = true)
    public final @NotNull AbstractBST<T, Node> intersection(@NotNull Collection<?> c) {
        AbstractBST<T, Node> res = makeEmptyTree();
        c.stream()
            .filter(this::contains) // Filter out the elements that are not in this tree
            .map(i -> (T) i)    // Cast the elements to type T (to Comparable, after erasure)
            .map(this::find)    // Get the actual element in the tree
            .filter(Objects::nonNull)
            .map(BSTNode::getValue)
            .forEach(res::add);

        return res;
    }

    /**
     * @param c The values to keep in the tree
     * @return A new bst.BST containing the elements common to this tree and the given values
     */
    @Override
    public final synchronized boolean retainAll(@NotNull Collection<?> c) {
        var temp = intersection(c);
        boolean res = !new ArrayList<>(this).equals(new ArrayList<>(temp)); // This operation should be O(N) I think
        root = temp.getRoot();  // Just copy the temp tree to this tree
        return res;
    }

    /**
     * Removes all items from the tree
     */
    @Override
    public final synchronized void clear() {
        root = null;
    }

    /**
     * @param value the value to search for
     * @return the node with that value, or null if the value is not in the tree
     */
    @Contract(pure = true)
    protected final @Nullable Node find(T value) {
        return find(root, value);
    }

    /**
     * @param node  The root node to search from
     * @param value The value to search for
     * @return the {@link BSTNode} with that value, or null if the value is not in the tree
     */
    @Contract(pure = true)
    protected final @Nullable Node find(Node node, T value) {
        if (node == null) return null;

        int compare = value.compareTo(node.getValue());

        if (compare < 0)
            return find((Node) node.getLeftChild(), value);
        if (compare > 0)
            return find((Node) node.getRightChild(), value);
        return node;
    }

    @Override
    @Contract(pure = true)
    public final int size() {
        return countNodes(root);
    }

    @Contract(pure = true)
    protected final int countNodes(BSTNode<T> node) {
        return node == null ? 0 : 1 + countNodes(node.getLeftChild()) + countNodes(node.getRightChild());
    }

    /**
     * @return whether the tree is empty
     */
    @Contract(pure = true)
    public final synchronized boolean isEmpty() {
        return root == null;
    }

    @Override
    @Contract(pure = true)
    public final synchronized @NotNull Iterator<T> iterator() {
        return root == null ? Collections.emptyIterator() : new Iterator<>() {
            private final Stack<BSTNode<T>> nodes = new Stack<>();
            private BSTNode<T> curr = root;

            @Override
            public boolean hasNext() {
                while (curr != null) {
                    nodes.push(curr);
                    curr = curr.getLeftChild();
                }
                return !nodes.isEmpty();
            }

            public T next() {
                var res = nodes.pop();
                curr = res.getRightChild();
                return res.getValue();
            }
        };
    }

    @Contract(pure = true)
    public final synchronized Object @NotNull [] toArray() {
        // IntelliJ suggests that I replace stream().toArray() with this.toArray()
        // Wow, that would definitely work!
        // Yeah, I want to call toArray() in the toArray() method!
        // No.
        // Just no.

        //noinspection SimplifyStreamApiCallChains
        return stream().toArray();
    }

    @Contract(pure = true)
    public final synchronized <U> U @NotNull [] toArray(U[] a) {
        // IDK how hacky this is, I never understood the purpose of this method overload
        return (U[]) toArray();
    }

    @Override
    public final synchronized String toString() {
        return root == null ? "bst.BST{}" : "bst.BST" + root;
    }

    /**
     * @return The height of the tree. This is the number of edges from the root to the deepest leaf
     */
    @Contract(pure = true)
    public final synchronized int getHeight() {
        return root == null ? -1 : root.getHeight();
    }

    /**
     * @return The root node of the tree. If the tree is empty, this is null
     */
    @Contract(pure = true)
    public final synchronized @Nullable Node getRoot() {
        return root;
    }

    /**
     * @return the number of leaves in the tree
     */
    @Contract(pure = true)
    public final synchronized int countLeaves() {
        return countLeaves(root);
    }

    /**
     * @param node the root of the tree to search
     * @return the number of leaves in the tree
     */
    private synchronized int countLeaves(BSTNode<T> node) {
        return node == null ? 0 : node.isLeaf() ? 1 : countLeaves(node.getLeftChild()) + countLeaves(node.getRightChild());
    }

    /**
     * @return The number of levels in the tree.
     */
    @Contract(pure = true)
    public final synchronized int countLevels() {
        return getHeight() + 1;
    }

    /**
     * @return The maximum number of nodes on any level of the tree
     */
    @Contract(pure = true)
    public final synchronized int getWidth() {
        return Arrays.stream(getLevelWidths()).reduce(0, Math::max);
    }

    /**
     * @return The number of the nodes in the longest path from any node in the left subtree to any node in the right subtree
     */
    @Contract(pure = true)
    public final synchronized int getDiameter() {
        return root == null ? 0 : 3 + (root.hasLeftChild() ? root.getLeftChild().getHeight() : 0) + (root.hasRightChild() ? root.getRightChild().getHeight() : 0);
    }

    /**
     * @return Whether the tree is a full BST. A full BST has no nodes with degree 1. An empty tree is not full
     */
    @Contract(pure = true)
    public final synchronized boolean isFullTree() {
        return root == null || isFull(root);
    }

    private boolean isFull(BSTNode<T> node) {
        return node.getDegree() == 0 || node.getDegree() == 2 && isFull(node.getLeftChild()) && isFull(node.getRightChild());
    }

    @Contract(pure = true)
    public final T getLargest() {
        return root == null ? null : getLargest(root);
    }

    private T getLargest(BSTNode<T> node) {
        return node.hasRightChild() ? getLargest(node.getRightChild()) : node.getValue();
    }

    @Contract(pure = true)
    public final T getSmallest() {
        return root == null ? null : getSmallest(root);
    }

    private T getSmallest(BSTNode<T> node) {
        return node.hasLeftChild() ? getSmallest(node.getLeftChild()) : node.getValue();
    }

    public final T[][] getLevels() {
        return (T[][]) Arrays.stream(getNodesAtLevels())
            .map(Arrays::stream)
            .map(i -> i.map(BSTNode::getValue).toArray(Comparable[]::new))
            .toArray(Comparable[][]::new);
    }

    public final BSTNode<T>[][] getNodesAtLevels() {
        if (root == null) return (BSTNode<T>[][]) new BSTNode[][]{};

        final int levels = countLevels();
        final BSTNode<T>[][] res = new BSTNode[levels][];
        res[0] = new BSTNode[]{root};

        for (int h = 1; h < levels; h++) {
            int size = 1 << h;
            res[h] = new BSTNode[size];
            for (int i = 0; i * 2 < size; i++) {
                var n = res[h - 1][i];
                if (n != null) {
                    res[h][i * 2] = n.getLeftChild();
                    res[h][i * 2 + 1] = n.getRightChild();
                }
            }
        }
        return res;
    }

    public final int[] getLevelWidths() {
        // Java FP is nice, but verbose
        // Whatever, I will use it because I am lazy
        return Arrays.stream(getLevels()).mapToInt(i -> (int) Arrays.stream(i).filter(Objects::nonNull).count()).toArray();
    }

    public void printTreeToConsole() {
        if (isEmpty()) {
            System.out.println(ANSICode.WHITE + "[Empty Tree]" + ANSICode.CLEAR);
            return;
        }

        final int height = countLevels();
        final BSTNode<T>[][] levels = getNodesAtLevels();

        final int[][] widths = new int[height][];
        for (int r = height - 1; r >= 0; r--) {
            widths[r] = new int[levels[r].length];

            for (int i = 0; i < levels[r].length; i++) {
                if (levels[r][i] == null) {
                    if (levels[r][i ^ 1] != null) {
                        for (int rr = r, ii = i; rr < height; rr++, ii *= 2) {
                            widths[rr][ii] = 4;
                        }
                    }
                } else {
                    int w = levels[r][i].toString().length() + 3;
                    int width = r == height - 1
                        ? w
                        : Math.max(w, widths[r + 1][i * 2] + widths[r + 1][i * 2 + 1]);
                    widths[r][i] = width;

                    if (!levels[r][i].hasRightChild() && !levels[r][i].hasLeftChild()) {
                        for (int rr = r, ii = i; rr < height; rr++, ii *= 2) {
                            widths[rr][ii] = width;
                        }
                    }
                }
            }
        }

        for (int r = 0; r < height; r++) {
            for (int i = 0; i < levels[r].length; i++) {
                var node = levels[r][i];
                int w = widths[r][i];

                if (w == 0) continue;

                if (node == null) {
                    System.out.print(ANSICode.WHITE + "[");
                    System.out.print("-".repeat(w - 3));
                    System.out.print("] " + ANSICode.CLEAR);
                } else {
                    _printNode(node, w);
                }
            }
            System.out.println();
        }
    }

    protected void _printNode(BSTNode<T> node, int targetWidth) {
        String s = node.toString();
        int space = targetWidth - 3 - s.length();
        String l = " ".repeat(space / 2);
        String r = " ".repeat(space - space / 2);

        System.out.print("[" + ANSICode.BOLD + l + s + r + ANSICode.CLEAR + "] ");
    }
}