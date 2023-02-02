package tree;

import util.ANSICode;
import util.Log;

public class RBT<T extends Comparable<T>> extends BalancedBST<T, RBTNode<T>> {
    /**
     * Performs a left-left rotation
     *
     * @param p p, the parent of x
     * @param g g, the grandparent of x
     */
    @Override
    protected void LL_Rotate(RBTNode<T> p, RBTNode<T> g) {
        // Call the super rotate method
        super.LL_Rotate(p, g);

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
    protected void RR_Rotate(RBTNode<T> p, RBTNode<T> g) {
        // Call the super rotate method
        super.RR_Rotate(p, g);

        // Recolor the nodes
        g.makeRed();
        p.makeBlack();
    }

    /**
     * @param parent The root node to insert under
     * @param value   The value to insert into the tree
     * @return whether the tree changed as a result of this call
     */
    @Override
    protected boolean add(RBTNode<T> parent, T value) {
        // Color swap if necessary
        if (RBTNode.isRed(parent.getLeftChild()) && RBTNode.isRed(parent.getRightChild())) {
            Log.log("Executing Color Swap with parent = " + parent, Log.NO_DISPLAY);
            if(parent.isNotRoot()) parent.swapColor();
            RBTNode.swapColor(parent.getLeftChild());
            RBTNode.swapColor(parent.getRightChild());
            printTreeToConsole();
            fixInsert(parent);
        }

        return super.add(parent, value);
    }

    /**
     * Fixes red-red violation and root violation, which occur when inserting nodes
     *
     * @param x The node which may be violating the red rule
     */
    protected void fixInsert (RBTNode<T> x) {
        // Check for Red violation
        if (RBTNode.isRed(x.getParent()))   // Do the corresponding rotation
            rotate(x);

        // Make root black
        root.makeBlack();
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
            Log.log("Fix double black null", Log.NO_DISPLAY);
        else Log.log("Fix double black " + sib.getSibling(), Log.NO_DISPLAY);

        if (sib.isRed()) { // Red sibling
            if (sib.isLeftChild()) {
                OO_Rotate(sib);
                fixDoubleBlack(parent.getLeftChild());  // X is parent.getRightChild
            } else {
                OO_Rotate(sib);
                fixDoubleBlack(parent.getRightChild());  // X is parent.getRightChild
            }
        }
        // Black sibling, has red child
        else if (RBTNode.isRed(sib.getLeftChild()) || RBTNode.isRed(sib.getRightChild())) {
            var origColor = RBTNode.getColor(sib.getParent());
            var p = (RBTNode<T>) restructure(
                    sib.isLeftChild()
                            // If sib is left and red on left, do left, else right
                            ? RBTNode.isRed(sib.getLeftChild()) ? sib.getLeftChild() : sib.getRightChild()
                            // If sib is right and red on right, do right, else left
                            : RBTNode.isRed(sib.getRightChild()) ? sib.getRightChild() : sib.getLeftChild()
            );

            RBTNode.setColor(p, origColor);

            RBTNode.makeBlack(p.getLeftChild());
            RBTNode.makeBlack(p.getRightChild());
        } else { // Black sibling, no red child
            sib.makeRed();
            if (parent.isNotRoot() && RBTNode.isBlack(parent))
                fixDoubleBlack(sib.getParent().getSibling());
            else
                RBTNode.makeBlack(parent);
        }
    }

    @Override
    protected void deleteSimple(RBTNode<T> target) {
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
        String s = node.toString();
        int space = targetWidth - 3 - s.length();
        String l = " ".repeat(space / 2);
        String r = " ".repeat(space - space / 2);
        var color = RBTNode.isRed((RBTNode<T>) node) ? ANSICode.RED : ANSICode.PURPLE;

        System.out.print("[" + color + ANSICode.BOLD + l + s + r + ANSICode.CLEAR + "] ");
    }

    @Override
    protected RBTNode<T> constructNode(T value) {
        return new RBTNode<>(value);
    }
}

