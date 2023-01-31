package tree;

public class AVLT<T extends Comparable<T>> extends BalancedBST<T, AVLNode<T>> {
    @Override
    protected void fixInsert (AVLNode<T> node) {
        // Do not fix root or child of root, no need
        if(!(node.isNotRoot() && node.getParent().isNotRoot())) return;

        int bf = node.getParent().getParent().getBalanceFactor();

        if(Math.abs(bf) > 1){
            rotate(node);
            fixInsert(node.getParent());
        }
    }
    
    @Override
    protected AVLNode<T> constructNode(T value) {
        return new AVLNode<>(value);
    }
}
