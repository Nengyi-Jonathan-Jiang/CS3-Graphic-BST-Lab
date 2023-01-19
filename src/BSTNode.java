public class BSTNode<T extends Comparable<T>> {
    private BSTNode<T> left = null, right = null, parent = null;
    public T value;

    public enum ChildType {LEFT, RIGHT, ROOT}

    public ChildType getChildType() {
        return parent == null ? ChildType.ROOT : this == parent.getLeft() ? ChildType.LEFT : ChildType.RIGHT;
    }

    public BSTNode(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return toString(this);
    }

    private static <T extends Comparable<T>> String toString(BSTNode<T> node) {
        return toString(node, 0);
    }

    private static <T extends Comparable<T>> String toString(BSTNode<T> node, int n) {
        if (node == null) return "nil";
        System.out.println(n + ": Calling toString on " + node.value);
        return "{ " + toString(node.getLeft(), n + 1) + " " + node.value + " " + toString(node.getRight(), n + 1) + " }";
    }

    public int getHeight() {
        return 1 + Math.max(left == null ? -1 : left.getHeight(), right == null ? -1 : right.getHeight());
    }

    public BSTNode<T> makeRoot() {
        parent = null;
        return this;
    }

    public BSTNode<T> getParent() {
        return parent;
    }

    public BSTNode<T> getLeft() {
        return left;
    }

    public BSTNode<T> getRight() {
        return right;
    }

    public void setLeft(BSTNode<T> node) {
        left = node;
        if (node == null) return;
        node.parent = this;
    }

    public void setRight(BSTNode<T> node) {
        right = node;
        if (node == null) return;
        node.parent = this;
    }

    public BSTNode<T> setLeft(T value) {
        var res = new BSTNode<>(value);
        setLeft(res);
        return res;
    }

    public BSTNode<T> setRight(T value) {
        var res = new BSTNode<>(value);
        setRight(res);
        return res;
    }

    public boolean hasLeft() {
        return left != null;
    }

    public boolean hasRight() {
        return right != null;
    }

    public boolean isLeaf() {
        return getDegree() == 0;
    }

    public int getDegree() {
        return (hasLeft() ? 1 : 0) + (hasRight() ? 1 : 0);
    }
}