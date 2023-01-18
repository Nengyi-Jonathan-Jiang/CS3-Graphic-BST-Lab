public class RBT<T extends Comparable<T>> extends BST<T> {
    @Override
    public boolean add(T value) {
        if (root == null) {
            root = new RBTNode<>(value);
            return true;
        } else return super.add(root, value);
    }

    public void LL_Rotation(RBTNode<T> p){
        var x = p.getLeft();
        var s = p.getRight();

        var g = p.getParent();

        var u = g.getRight();

        _LL_Rotation(x, s, p, u, g);

        RBTNode.swapColor(p);
        RBTNode.swapColor(g);
    }
    private void _LL_Rotation(RBTNode<T> x, RBTNode<T> s, RBTNode<T> p, RBTNode<T> u, RBTNode<T> g){
        g.setLeft(s);
        p.setRight(g);
    }

    public void RR_Rotation(RBTNode<T> p){ // Precondition: X is left child, P is left child
        var x = p.getRight();
        var s = p.getLeft();

        var g = p.getParent();

        var u = g.getLeft();

        _RR_Rotation(x, s, p, u, g);

        RBTNode.swapColor(p);
        RBTNode.swapColor(g);
    }

    private void _RR_Rotation(RBTNode<T> x, RBTNode<T> s, RBTNode<T> p, RBTNode<T> u, RBTNode<T> g){
        g.setRight(s);
        p.setLeft(g);
    }
    public void LR_Rotation(RBTNode<T> p){
        var x = p.getRight();
        var s = p.getLeft();

        var g = p.getParent();
        var u = g.getRight();

        _RR_Rotation(x.getRight(), x.getLeft(), x, s, p);
        g.setLeft(x);

        RBTNode.swapColor(x);
        RBTNode.swapColor(g);

        _LL_Rotation(s, p, x.getRight(), g, u);
    }
    public void RL_Rotation(RBTNode<T> p){
        var x = p.getLeft();
        var s = p.getRight();

        var g = p.getParent();
        var u = g.getLeft();

        _LL_Rotation(x.getLeft(), x.getRight(), x, s, p);
        g.setRight(x);

        RBTNode.swapColor(x);
        RBTNode.swapColor(g);

        _RR_Rotation(s, p, x.getLeft(), g, u);
    }

    public RBTNode<T> find(T value){
        return (RBTNode<T>) super.find(value);
    }

    @Override
    protected void deleteDegNotTwo(BSTNode<T> _target) {
        var target = (RBTNode<T>) _target;
        if(target.getDegree() == 1){
            var node = target.hasLeft() ? target.getLeft() : target.getRight();
            if(RBTNode.getColor(node) == RBTNode.Color.RED) {
                super.deleteDegNotTwo(target);
                RBTNode.swapColor(node);
            }
        }
        else {
            // bro idk man
        }
    }
}

class RBTNode<T extends Comparable<T>> extends BSTNode<T> {
    public enum Color {RED, BLACK}
    private Color color = Color.RED;

    public RBTNode(T value) {
        super(value);
    }

    private Color getColor() {
        return color;
    }

    public static <T extends Comparable<T>> Color getColor(RBTNode<T> node) {
        return node == null ? Color.BLACK : node.getColor();
    }


    public static <T extends Comparable<T>> void swapColor(RBTNode<T> node) {
        if(node != null) node.swapColor();
    }

    private void swapColor() {
        color = switch (color) {
            case RED -> Color.BLACK;
            case BLACK -> Color.RED;
        };
    }

    @Override
    public RBTNode<T> getLeft() {
        return (RBTNode<T>) super.getLeft();
    }

    @Override
    public RBTNode<T> getRight() {
        return (RBTNode<T>) super.getRight();
    }

    @Override
    public RBTNode<T> getParent() {
        return (RBTNode<T>) super.getParent();
    }

    @Override
    public void setLeft(T value) {
        setLeft(new RBTNode<>(value));
    }

    @Override
    public void setRight(T value) {
        setRight(new RBTNode<>(value));
    }
}