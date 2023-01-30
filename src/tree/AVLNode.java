package tree;

public class AVLNode<T extends Comparable<T>> extends BSTNode<T> {
    public AVLNode(T value) {
        super(value);
    }

    public int getBalanceFactor(){
        return getHeight(left) - getHeight(right);
    }
}
