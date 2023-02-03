package tree;

import java.util.*;
import java.util.stream.Collectors;

public class AVLNode<T extends Comparable<T>> extends BSTNode<T> {
    public AVLNode(T value) {
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
    public boolean isPerfectlyBalanced(){
        return getBalanceFactor() == 0;
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
    public AVLNode<T> getSibling () {
        return (AVLNode<T>) super.getSibling();
    }

    @Override
    public String toString() {
         return super.toString() + "⁻²,⁻,,⁺,⁺²".split(",")[getBalanceFactor() + 2];
    }
}