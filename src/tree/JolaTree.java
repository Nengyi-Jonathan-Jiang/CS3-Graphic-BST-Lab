package tree;

import util.Log;

public class JolaTree<T extends Comparable<T>> extends BalancedBST<T, JolaNode<T>> {

    /**
     * @param parent The root node to insert under
     * @param value   The value to insert into the tree
     * @return whether the tree changed as a result of this call
     */
    @Override
    protected boolean add(JolaNode<T> parent, T value) {
        int compare = value.compareTo(parent.getValue());

        if (compare < 0) {
            if (parent.hasLeftChild()) {
                var leftChild = (JolaNode<T>) parent.getLeftChild();
                if(value.compareTo(leftChild.getValue()) >= 0) {
                    var temporaryInvoluntaryMigrant = leftChild.getRightChild();
                    leftChild.setRightChild(constructNode(value));
                    LR_Rotate(leftChild);
                    leftChild.setRightChild(temporaryInvoluntaryMigrant);
                    fixInsert(leftChild);
                }
                else {
                    add(parent.getLeftChild(), value);
                    fixInsert(parent);
                }
            } else {
                Log.log("Inserting " + value + " as left child of " + parent);
                var n = constructNode(value);
                parent.setLeftChild(n);
                printTreeToConsole();
                fixInsert(parent);
            }
        } else {
            if (parent.hasRightChild()) {
                var rightChild = (JolaNode<T>) parent.getRightChild();
                if(value.compareTo(rightChild.getValue()) < 0) {
                    var temporaryInvoluntaryMigrant = rightChild.getLeftChild();
                    rightChild.setLeftChild(constructNode(value));
                    RL_Rotate(rightChild);
                    rightChild.setLeftChild(temporaryInvoluntaryMigrant);
                    fixInsert(rightChild);
                }
                else {
                    add(parent.getRightChild(), value);
                    fixInsert(parent);
                }
            } else {
                Log.log("Inserting " + value + " as right child of " + parent);
                var n = constructNode(value);
                parent.setRightChild(n);
                printTreeToConsole();
                fixInsert(parent);
            }
        }

        return true;
    }

    @Override
    protected void fixInsert(JolaNode<T> node) {
//        throw new RuntimeException("Not implemented. Who the hell called this?");
        boolean hasLeft = node.hasLeftChild(), hasRight = node.hasRightChild();

        if(hasLeft && !hasRight) {
            fixInsert(OO_Rotate(node));

        }
        else if(hasRight && !hasLeft) {
            fixInsert(OO_Rotate(node));
        }
    }


    @Override
    public void insertAsRoot (T value) {
        assert root == null : "Tried to insert into non-null root";

        root = constructNode(value);
        printTreeToConsole();
    }

    @Override
    protected JolaNode<T> constructNode(T value) {
        return new JolaNode<>(value);
    }
}
