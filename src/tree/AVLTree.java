package tree;

public class AVLTree<T extends Comparable<T>> extends BalancedBST<T, AVLNode<T>> {
    @Override
    protected void fixInsert (AVLNode<T> node) {
        rebalance(node);
    }

    private void rebalance(AVLNode<T> g){
        AVLNode<T> res;

        if(g.isUnbalanced()){
            var p = g.isLeftHeavy() ? g.getLeftChild() : g.getRightChild();
            if(p.isLeftHeavy()){
                res = rotate(p.getLeftChild());
            }
            else if(p.isRightHeavy()){
                res = rotate(p.getRightChild());
            }
            else {  // P balanced
                res = OO_Rotate(p);
            }
        }
        else res = g;

        if(res.isNotRoot()) rebalance(res.getParent());
    }

    @Override
    protected void deleteSimple(AVLNode<T> target) {
        var node = target.getParent();
        super.deleteSimple(target);
        fixInsert(node);
    }

    @Override
    protected AVLNode<T> constructNode(T value) {
        return new AVLNode<>(value);
    }
}
