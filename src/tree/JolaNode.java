package tree;

public class JolaNode<T extends Comparable<T>> extends BSTNode<T> {
    public JolaNode(T value) {
        super(value);
    }

    public int getBalanceFactor(){
        return getHeight(left) - getHeight(right);
    }

    // Interestingly, the AVL algorithm still works no matter what max height diff we set. Of course, tolerating larger
    // height differences means that the tree can get more unbalanced.
    // For trees with higher max height difference, a randomly generated tree starts looking like a bunch of grapes :D
    public boolean isUnbalanced(){
        return Math.abs(getBalanceFactor()) > 1;
    }

    public boolean isLeftHeavy(){
        return getBalanceFactor() > 0;
    }
    public boolean isRightHeavy(){
        return getBalanceFactor() < 0;
    }

    @Override
    public JolaNode<T> getLeftChild () {
        return (JolaNode<T>) super.getLeftChild();
    }

    @Override
    public JolaNode<T> getRightChild () {
        return (JolaNode<T>) super.getRightChild();
    }

    @Override
    public JolaNode<T> getParent () {
        return (JolaNode<T>) super.parent;
    }

    @Override
    public JolaNode<T> getSibling () {
        return (JolaNode<T>) super.getSibling();
    }

//    @Override
//    public String toString() {
//         return super.toString() + "⁻²,⁻,,⁺,⁺²".split(",")[getBalanceFactor() + 2];
//    }
}