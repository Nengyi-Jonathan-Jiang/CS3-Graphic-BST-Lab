public class RBT<T extends Comparable<T>> extends BST<T> {
    @Override
    public boolean add(T value) {
        if (root == null) {
            root = new RBTNode<>(value);
            RBTNode.swapColor((RBTNode<T>) root);
            return true;
        } else return add(root, value);
    }

    public RBTNode<T> LL_Rotation(RBTNode<T> p){
        var x = p.getLeftChild();
        var s = p.getRightChild();

        var g = p.getParent();

        var u = g.getRightChild();

        var gp = g.getParent();
        var gt = g.getChildType();

        Main.printTree(this);

        _LL_Rotation(x, s, p, u, g);

        RBTNode.makeBlack(p);
        RBTNode.makeRed(g);

        switch (gt){
            case LEFT -> gp.setLeftChild(p);
            case RIGHT -> gp.setRightChild(p);
            case ROOT -> root = p.makeRoot();
        }

        return p;
    }
    private void _LL_Rotation(RBTNode<T> x, RBTNode<T> s, RBTNode<T> p, RBTNode<T> u, RBTNode<T> g){
        g.setLeftChild(s);
        p.setRightChild(g);
    }
    public RBTNode<T> RR_Rotation(RBTNode<T> p){ // Precondition: X is left child, P is left child
        var x = p.getRightChild();
        var s = p.getLeftChild();

        var g = p.getParent();

        var u = g.getLeftChild();

        var gp = g.getParent();
        var gt = g.getChildType();

        Main.printTree(this);

        _RR_Rotation(x, s, p, u, g);

        RBTNode.makeBlack(p);
        RBTNode.makeRed(g);

        switch (gt){
            case LEFT -> gp.setLeftChild(p);
            case RIGHT -> gp.setRightChild(p);
            case ROOT -> root = p.makeRoot();
        }

        return p;
    }

    private void _RR_Rotation(RBTNode<T> x, RBTNode<T> s, RBTNode<T> p, RBTNode<T> u, RBTNode<T> g){
        g.setRightChild(s);
        p.setLeftChild(g);
    }
    public RBTNode<T> LR_Rotation(RBTNode<T> p){
        var x = p.getRightChild();
        var s = p.getLeftChild();

        var g = p.getParent();
        var u = g.getRightChild();

        var gp = g.getParent();
        var gt = g.getChildType();

        Main.printTree(this);

        _RR_Rotation(x.getRightChild(), x.getLeftChild(), x, s, p);
        g.setLeftChild(x);

        Main.printTree(this);

        _LL_Rotation(p, x.getRightChild(), x, u, g);

        RBTNode.makeBlack(x);
        RBTNode.makeRed(g);

        switch (gt){
            case LEFT -> gp.setLeftChild(x);
            case RIGHT -> gp.setRightChild(x);
            case ROOT -> root = x.makeRoot();
        }

        return p.getParent();
    }
    public RBTNode<T> RL_Rotation(RBTNode<T> p){
        var x = p.getLeftChild();
        var s = p.getRightChild();

        var g = p.getParent();
        var u = g.getLeftChild();

        var gp = g.getParent();
        var gt = g.getChildType();

        Main.printTree(this);

        _LL_Rotation(x.getLeftChild(), x.getRightChild(), x, s, p);
        g.setRightChild(x);

        Main.printTree(this);

        _RR_Rotation(p, x.getLeftChild(), x, u, g);

        RBTNode.makeBlack(x);
        RBTNode.makeRed(g);

        switch (gt){
            case LEFT -> gp.setLeftChild(x);
            case RIGHT -> gp.setRightChild(x);
            case ROOT -> root = x.makeRoot();
        }

        return p.getParent();
    }

    private RBTNode<T> rotate(RBTNode<T> x){
        var p = x.getParent();
        if(p.getChildType() == BSTNode.ChildType.LEFT)
            if(x.getChildType() == BSTNode.ChildType.LEFT)
                return LL_Rotation(p);
            else
                return LR_Rotation(p);
        else
            if(x.getChildType() == BSTNode.ChildType.LEFT)
                return RL_Rotation(p);
            else
                return RR_Rotation(p);
    }

    @Override
    protected boolean add(BSTNode<T> _parent, T value) {
        var parent = (RBTNode<T>) _parent;

        // Color swap if necessary
        if(RBTNode.isRed(parent.getLeftChild()) && RBTNode.isRed(parent.getRightChild())){
            if(parent == root){
                RBTNode.swapColor(parent.getLeftChild());
                RBTNode.swapColor(parent.getRightChild());
            }
            else {
                RBTNode.swapColor(parent);
                RBTNode.swapColor(parent.getLeftChild());
                RBTNode.swapColor(parent.getRightChild());
                fix(parent);
            }
        }

        int compare = value.compareTo(parent.value);

        if (compare < 0)
            if (parent.hasLeftChild())
                return add(parent.getLeftChild(), value);
            else
                fix(parent.insertLeft(value));
        else
            if (parent.hasRightChild())
                return add(parent.getRightChild(), value);
            else
                fix(parent.insertRight(value));
        //else return false; // else: Node already in tree, do nothing :)

        return true;
    }

    /**
     * Fixes red-red violation and root violation, which occur when inserting nodes
     * @param x The node which may be violating the red rule
     */
    protected void fix(RBTNode<T> x){
        // Check for Red violation
        var p = x.getParent();
        if(RBTNode.isRed(p))   // Do the corresponding rotation
            rotate(x);

        // Make root black
        RBTNode.makeBlack((RBTNode<?>) root);
    }

    public RBTNode<T> find(T value){
        return (RBTNode<T>) super.find(value);
    }

    /**
     * @param sib The sibling of the double-black node (We use sibling because node itself may be null).
     */
    private void fixDoubleBlack(RBTNode<T> sib){
        var parent = sib.getParent();

        if(sib.getSibling() == null)
            System.out.println("Fix double black null");
        else System.out.println("Fix double black " + sib.getSibling().value);

        if(RBTNode.isRed(sib)){ // Red sibling
            if(sib.getChildType() == BSTNode.ChildType.RIGHT){
                RR_Rotation(sib);
                fixDoubleBlack(parent.getRightChild());
            }
            else{
                LL_Rotation(sib);
                fixDoubleBlack(parent.getLeftChild());
            }
        }
        // Black sibling, has red child
        else if(RBTNode.isRed(sib.getLeftChild()) || RBTNode.isRed(sib.getRightChild())) {
            Main.printTree(this);

            RBTNode.Color origColor = RBTNode.getColor(sib.getParent());
            RBTNode<T> p = rotate(RBTNode.isRed(sib.getLeftChild()) ? sib.getLeftChild() : sib.getRightChild());

            RBTNode.setColor(p, origColor);

            Main.printTree(this);

            RBTNode.makeBlack(p.getLeftChild());
            RBTNode.makeBlack(p.getRightChild());
        }
        else { // Black sibling, no red child
            Main.printTree(this);

            RBTNode.makeRed(sib);
            if(parent.isNotRoot() && RBTNode.isBlack(parent)) {
                fixDoubleBlack(sib.getParent().getSibling());
            }
            else RBTNode.makeBlack(parent);
        }
    }

    @Override
    protected void deleteSimple(BSTNode<T> _target) {
        var target = (RBTNode<T>) _target;
        var node = target.hasLeftChild() ? target.getLeftChild() : target.getRightChild();

        if(RBTNode.isRed(target) || RBTNode.isRed(node)) {
            super.deleteSimple(target);
            RBTNode.makeBlack(node);
        }
        else { // Double black
            var sib = target.hasSibling() ? target.getSibling() : target.getParent().getSibling();
            super.deleteSimple(target);
            fixDoubleBlack(sib);
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

    public static void setColor(RBTNode<?> node, Color color){
        if(node != null) node.color = color;
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
        return (RBTNode<T>)super.getSibling();
    }
}