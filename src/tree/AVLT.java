package tree;

public class AVLT<T extends Comparable<T>> extends BalancedBST<T, AVLNode<T>> {
    @Override
    protected void fix(AVLNode<T> node) {
    
    }
    
    @Override
    protected AVLNode<T> constructNode(T value) {
        return new AVLNode<>(value);
    }
}
