package tree;

public class AVLNode<T extends Comparable<T>> extends BSTNode<T> {
    public AVLNode(T value) {
        super(value);
    }

    public int getBalanceFactor(){
        return getHeight(left) - getHeight(right);
    }

    @Override
    public AVLNode<T> getLeftChild () {
        return (AVLNode<T>) super.getLeftChild();
    }

    @Override
    public AVLNode<T> getRightChild () {
        return (AVLNode<T>) super.getRightChild();
    }

    @Override
    public AVLNode<T> getParent () {
        return (AVLNode<T>) super.parent;
    }

    @Override
    public String toString() {
        return super.toString() + (getBalanceFactor() == 1 ? " (+1)" : getBalanceFactor() == -1 ? " (-1)" : " (+0)");
    }
}