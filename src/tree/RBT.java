package tree;

import util.ANSICode;

public class RBT<T extends Comparable<T>> extends BalancedBST<T, RBTNode<T>> {
    /**
     * Performs a left-left rotation
     *
     * @param p p, the parent of x
     * @param g g, the grandparent of x
     */
    @Override
    protected void LL_Rotation(RBTNode<T> p, RBTNode<T> g) {
        // Call the super rotate method
        super.LL_Rotation(p, g);

        // Recolor the nodes
        g.makeRed();
        p.makeBlack();
    }

    /**
     * Performs a right-right rotation
     *
     * @param p p, the parent of x
     * @param g g, the grandparent of x
     */
    @Override
    protected void RR_Rotation(RBTNode<T> p, RBTNode<T> g) {
        // Call the super rotate method
        super.RR_Rotation(p, g);

        // Recolor the nodes
        g.makeRed();
        p.makeBlack();
    }

    /**
     * @param value The value to insert into the tree
     * @return whether the tree changed as a result of this call
     */
    @Override
    public boolean add(T value) {
        if (root == null) {
            root = new RBTNode<>(value).makeRoot();
            return true;
        }
        return add(root, value);
    }

    /**
     * @param _parent The root node to insert under
     * @param value   The value to insert into the tree
     * @return whether the tree changed as a result of this call
     */
    @Override
    protected boolean add(BSTNode<T> _parent, T value) {
        var parent = (RBTNode<T>) _parent;

        // Color swap if necessary
        if (RBTNode.isRed(parent.getLeftChild()) && RBTNode.isRed(parent.getRightChild())) {
            if (parent == root) {
                RBTNode.swapColor(parent.getLeftChild());
                RBTNode.swapColor(parent.getRightChild());
            } else {
                RBTNode.swapColor(parent);
                RBTNode.swapColor(parent.getLeftChild());
                RBTNode.swapColor(parent.getRightChild());
                fix(parent);
            }
        }

        int compare = value.compareTo(parent.getValue());

        if (compare < 0)
            if (parent.hasLeftChild())
                return add(parent.getLeftChild(), value);
            else
                fix(parent.insertLeft(value));
        else if (parent.hasRightChild())
            return add(parent.getRightChild(), value);
        else
            fix(parent.insertRight(value));
        return true;
    }

    /**
     * Fixes red-red violation and root violation, which occur when inserting nodes
     *
     * @param x The node which may be violating the red rule
     */
    protected void fix(RBTNode<T> x) {
        // Check for Red violation
        var p = x.getParent();
        if (RBTNode.isRed(p))   // Do the corresponding rotation
            rotate(x);

        // Make root black
        ((RBTNode<?>) root).makeBlack();
    }

    /**
     * @param value the value to search for
     * @return the node with that value, or null if the value is not in the tree
     */
    public RBTNode<T> find(T value) {
        return (RBTNode<T>) super.find(value);
    }

    /**
     * @param sib The sibling of the double-black node (We pass in the sibling because node itself may be null).
     */
    private void fixDoubleBlack(RBTNode<T> sib) {
        var parent = sib.getParent();

        if (sib.getSibling() == null)
            System.out.println("Fix double black null");
        else System.out.println("Fix double black " + sib.getSibling().getValue());

        if (sib.isRed()) { // Red sibling
            if (sib.isLeftChild()) {
                LL_Rotation(sib);
                fixDoubleBlack(parent.getLeftChild());
            } else {
                RR_Rotation(sib);
                fixDoubleBlack(parent.getRightChild());
            }
        }
        // Black sibling, has red child
        else if (RBTNode.isRed(sib.getLeftChild()) || RBTNode.isRed(sib.getRightChild())) {
            var origColor = RBTNode.getColor(sib.getParent());
            var p = (RBTNode<T>) rotate(
                    sib.isLeftChild() ?    // If sib is left and red on left do left else right
                            RBTNode.isRed(sib.getLeftChild()) ? sib.getLeftChild() : sib.getRightChild()
                            :                   // Else if sib is right and red on right do right else left
                            RBTNode.isRed(sib.getRightChild()) ? sib.getRightChild() : sib.getLeftChild()
            );

            RBTNode.setColor(p, origColor);

            RBTNode.makeBlack(p.getLeftChild());
            RBTNode.makeBlack(p.getRightChild());
        } else { // Black sibling, no red child
            sib.makeRed();
            if (parent.isNotRoot() && RBTNode.isBlack(parent)) {
                fixDoubleBlack(sib.getParent().getSibling());
            } else RBTNode.makeBlack(parent);
        }
    }

    @Override
    protected void deleteSimple(BSTNode<T> _target) {
        var target = (RBTNode<T>) _target;
        var node = target.hasLeftChild() ? target.getLeftChild() : target.getRightChild();

        if (RBTNode.isRed(target) || RBTNode.isRed(node)) {
            super.deleteSimple(target);
            RBTNode.makeBlack(node);
        } else { // Double black
            var sib = target.hasSibling() ? target.getSibling() : target.getParent().getSibling();
            super.deleteSimple(target);
            fixDoubleBlack(sib);
        }
    }

    protected void _printNode(BSTNode<T> node, int targetWidth) {
        System.out.print("[" + (RBTNode.isRed((RBTNode<T>) node) ? ANSICode.RED : ANSICode.PURPLE ) + ANSICode.BOLD);

        String s = node.getValue().toString();
        int length = s.length();
        int lp = (targetWidth - 3 - length) / 2;
        int rp = targetWidth - 3 - lp - length;
        System.out.print(" ".repeat(lp) + s + " ".repeat(rp) + ANSICode.CLEAR + "] ");
    }

    @Override
    protected RBTNode<T> constructNode(T value) {
        return new RBTNode<>(value);
    }
}

