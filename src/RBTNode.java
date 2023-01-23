class RBTNode<T extends Comparable<T>> extends BSTNode<T> {
    public enum Color {RED, BLACK}

    private Color color = Color.RED;

    public RBTNode(T value) {
        super(value);
    }

    private Color getColor() {
        return color;
    }

    public static Color getColor(RBTNode<?> node) {
        return node == null ? Color.BLACK : node.getColor();
    }

    public static boolean isRed(RBTNode<?> node) {
        return getColor(node) == Color.RED;
    }

    public static boolean isBlack(RBTNode<?> node) {
        return getColor(node) == Color.BLACK;
    }

    public static void swapColor(RBTNode<?> node) {
        if (node != null) node.swapColor();
    }

    public static void makeRed(RBTNode<?> node) {
        if (node != null) node.color = Color.RED;
    }

    public static void makeBlack(RBTNode<?> node) {
        if (node != null) node.color = Color.BLACK;
    }

    public static void setColor(RBTNode<?> node, Color color) {
        if (node != null) node.color = color;
    }

    private void swapColor() {
        color = switch (color) {
            case RED -> Color.BLACK;
            case BLACK -> Color.RED;
        };
    }

    @Override
    public RBTNode<T> getLeftChild() {
        return (RBTNode<T>) super.getLeftChild();
    }

    @Override
    public RBTNode<T> getRightChild() {
        return (RBTNode<T>) super.getRightChild();
    }

    @Override
    public RBTNode<T> getParent() {
        return (RBTNode<T>) super.getParent();
    }

    @Override
    public RBTNode<T> insertLeft(T value) {
        RBTNode<T> res = new RBTNode<>(value);
        setLeftChild(res);
        return res;
    }

    @Override
    public RBTNode<T> insertRight(T value) {
        RBTNode<T> res = new RBTNode<>(value);
        setRightChild(res);
        return res;
    }

    @Override
    public RBTNode<T> getSibling() {
        return (RBTNode<T>) super.getSibling();
    }
}
