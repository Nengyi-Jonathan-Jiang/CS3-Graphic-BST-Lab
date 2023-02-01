package tree;

public class AVLTree<T extends Comparable<T>> extends BalancedBST<T, AVLNode<T>> {
    @Override
    protected void fixInsert (AVLNode<T> node) {
        // Do not fix root or child of root, no need
        if(!(node.hasParent() && node.getParent().isNotRoot())) return;

        int bf = node.getParent().getParent().getBalanceFactor();

        var newParent = node.getParent();

        if(Math.abs(bf) > 1){
            newParent = rotate(node.getParent(), node);
        }
        fixInsert(newParent);
    }
    
    @Override
    protected AVLNode<T> constructNode(T value) {
        return new AVLNode<>(value);
    }
}
