public class RBT<T extends Comparable<T>> extends BST<T> {
    @Override
    public boolean add(T value) {
        if (root == null) {
            root = new RBTNode<>(value);
            RBTNode.swapColor((RBTNode<T>) root);
            return true;
        } else return add(root, value);
    }

    public void LL_Rotation(RBTNode<T> p){
        var x = p.getLeft();
        var s = p.getRight();

        var g = p.getParent();

        var u = g.getRight();

        var gp = g.getParent();
        var gt = g.getChildType();

        _LL_Rotation(x, s, p, u, g);

        RBTNode.makeBlack(p);
        RBTNode.makeRed(g);

        switch (gt){
            case LEFT -> gp.setLeft(p);
            case RIGHT -> gp.setRight(p);
            case ROOT -> root = p.makeRoot();
        }
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

        var gp = g.getParent();
        var gt = g.getChildType();

        _RR_Rotation(x, s, p, u, g);

        RBTNode.makeBlack(p);
        RBTNode.makeRed(g);

        switch (gt){
            case LEFT -> gp.setLeft(p);
            case RIGHT -> gp.setRight(p);
            case ROOT -> root = p.makeRoot();
        }
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

        var gp = g.getParent();
        var gt = g.getChildType();

        _RR_Rotation(x.getRight(), x.getLeft(), x, s, p);
        g.setLeft(x);

        Main.printTree(this);

        _LL_Rotation(p, x.getRight(), x, u, g);

        RBTNode.makeBlack(x);
        RBTNode.makeRed(g);

        switch (gt){
            case LEFT -> gp.setLeft(x);
            case RIGHT -> gp.setRight(x);
            case ROOT -> root = x.makeRoot();
        }
    }
    public void RL_Rotation(RBTNode<T> p){
        var x = p.getLeft();
        var s = p.getRight();

        var g = p.getParent();
        var u = g.getLeft();

        var gp = g.getParent();
        var gt = g.getChildType();

        _LL_Rotation(x.getLeft(), x.getRight(), x, s, p);
        g.setRight(x);

        Main.printTree(this);

        _RR_Rotation(p, x.getLeft(), x, u, g);

        RBTNode.makeBlack(x);
        RBTNode.makeRed(g);

        switch (gt){
            case LEFT -> gp.setLeft(x);
            case RIGHT -> gp.setRight(x);
            case ROOT -> root = x.makeRoot();
        }
    }

    @Override
    protected boolean add(BSTNode<T> _parent, T value) {
        var parent = (RBTNode<T>) _parent;

        // Color swap if necessary
        if(RBTNode.isRed(parent.getLeft()) && RBTNode.isRed(parent.getRight())){
            if(parent == root){
                RBTNode.swapColor(parent.getLeft());
                RBTNode.swapColor(parent.getRight());
            }
            else {
                RBTNode.swapColor(parent);
                RBTNode.swapColor(parent.getLeft());
                RBTNode.swapColor(parent.getRight());
                fix(parent);
            }
        }

        int compare = value.compareTo(parent.value);

        if (compare < 0) {
            if (parent.hasLeft())
                add(parent.getLeft(), value);
            else {
                fix(parent.setLeft(value));
            }
        } else {
            if (parent.hasRight())
                add(parent.getRight(), value);
            else {
                fix(parent.setRight(value));
            }
        }
        //else return false; // else: Node already in tree, do nothing :)

        return true;
    }

    protected void fix(RBTNode<T> x){
        // Check for Red violation
        var p = x.getParent();
        if(RBTNode.isRed(p)){   // Do the corresponding rotation
            if(p.getChildType() == BSTNode.ChildType.LEFT){
                if(x.getChildType() == BSTNode.ChildType.LEFT)
                    LL_Rotation(p);
                else LR_Rotation(p);
            }
            else {
                if(x.getChildType() == BSTNode.ChildType.LEFT)
                    RL_Rotation(p);
                else RR_Rotation(p);
            }
        }

        // Make root black
        RBTNode.makeBlack((RBTNode<?>) root);
    }

    public RBTNode<T> find(T value){
        return (RBTNode<T>) super.find(value);
    }

    private void restructure(RBTNode<T> sib){
        if(RBTNode.isRed(sib.getLeft()) || RBTNode.isRed(sib.getRight())) {
            Main.printTree(this);

            RBTNode<T> p2;

            if (sib.getChildType() == BSTNode.ChildType.RIGHT) {
                if(RBTNode.isRed(sib.getRight())) {
                    RR_Rotation(sib);
                    p2 = sib;
                }
                else {
                    RL_Rotation(sib);
                    p2 = sib.getParent();
                }
            } else {
                if(RBTNode.isRed(sib.getLeft())) {
                    LL_Rotation(sib);
                    p2 = sib;
                }
                else {
                    LR_Rotation(sib);
                    p2 = sib.getParent();
                }
            }

            Main.printTree(this);

            RBTNode.makeBlack(p2.getLeft());
            RBTNode.makeBlack(p2.getRight());
        }
        else {
            Main.printTree(this);

            RBTNode.makeRed(sib);
            if(sib.getParent().getChildType() != BSTNode.ChildType.ROOT) {
                var p = sib.getParent();
                var sib2 = p.getChildType() == BSTNode.ChildType.LEFT ? p.getParent().getRight() : p.getParent().getLeft();
                restructure(sib2);
            }
        }
    }

    @Override
    protected void deleteSimple(BSTNode<T> _target) {
        var target = (RBTNode<T>) _target;
        var node = target.hasLeft() ? target.getLeft() : target.getRight();

        if(RBTNode.isRed(target) || RBTNode.isRed(node)) {
            super.deleteSimple(target);
            RBTNode.makeBlack(node);
        }
        else { // Double black
            var sib = target.getChildType() == BSTNode.ChildType.LEFT ? target.getParent().getRight() : target.getParent().getLeft();
            super.deleteSimple(target);
            restructure(sib);
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

    public static Color getColor(RBTNode<?> node) {
        return node == null ? Color.BLACK : node.getColor();
    }

    public static boolean isRed(RBTNode<?> node) {
        return getColor(node) == Color.RED;
    }

    public static boolean isBlack(RBTNode<?> node){
        return getColor(node) == Color.BLACK;
    }

    public static void swapColor(RBTNode<?> node) {
        if(node != null) node.swapColor();
    }

    public static void makeRed(RBTNode<?> node){
        if(node != null) node.color = Color.RED;
    }

    public static void makeBlack(RBTNode<?> node){
        if(node != null) node.color = Color.BLACK;
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
    public RBTNode<T> setLeft(T value) {
        RBTNode<T> res = new RBTNode<>(value);
        setLeft(res);
        return res;
    }

    @Override
    public RBTNode<T> setRight(T value) {
        RBTNode<T> res = new RBTNode<>(value);
        setRight(res);
        return res;
    }
}