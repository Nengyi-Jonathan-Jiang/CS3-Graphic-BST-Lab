package tree;

import util.Log;

public abstract class BalancedBST<T extends Comparable<T>, Node extends BSTNode<T>> extends AbstractBST<T, Node> {
    /**
     * Performs a left-left rotation
     *
     * @param p p, the parent of x
     * @param g g, the grandparent of x
     */
    protected void LL_Rotate(Node p, Node g) {
		/*-Subtree looks like:----
		 *    ?
		 *    g
		 *  p   u
		 * x s
		 -----------------------*/
        g.setLeftChild(p.getRightChild());
		/*-Subtree looks like:----
		 *  ?
		 *  g
		 * s u
		 *
		 * floating:
		 *  p
		 * x
		 -----------------------*/
        switch (g.getChildType()) {
            case LEFT -> g.getParent().setLeftChild(p);
            case RIGHT -> g.getParent().setRightChild(p);
            case ROOT -> root = (Node) p.makeRoot();
        }
		/*-Subtree looks like:----
		 *  ?
		 *  p
		 * x
		 *
		 * floating:
		 *  g
		 * s u
		 -----------------------*/
        p.setRightChild(g);
		/*-Subtree looks like:----
		 *   ?
		 *   p
		 * x   g
		 *    s u
		 -----------------------*/
    }

    /**
     * Performs a right-right rotation
     *
     * @param p p, the parent of x
     * @param g g, the grandparent of x
     */
    protected void RR_Rotate(Node p, Node g) {
		/*-Subtree looks like:----
		 *    ?
		 *    g
		 *  u   p
		 *     s x
		 -----------------------*/
        g.setRightChild(p.getLeftChild());
		/*-Subtree looks like:----
		 *  ?
		 *  g
		 * u s
		 *
		 * floating:
		 *  p
		 *   x
		 -----------------------*/
        switch (g.getChildType()) {
            case LEFT -> g.getParent().setLeftChild(p);
            case RIGHT -> g.getParent().setRightChild(p);
            case ROOT -> root = (Node) p.makeRoot();
        }
		/*-Subtree looks like:----
		 *  ?
		 *  p
		 *   x
		 *
		 * floating:
		 *  g
		 * u s
		 -----------------------*/
        p.setLeftChild(g);
		/*-Subtree looks like:----
		 *    ?
		 *    p
		 *  g   x
		 * u s
		 -----------------------*/
    }

    /**
     * Performs a left-left rotation
     *
     * @param p p, the parent of x
     * @return The new grandparent
     */
    protected Node LL_Rotate(Node p) {
        LL_Rotate(p, (Node) p.getParent());
        return p;
    }

    /**
     * Performs a right-right rotation
     *
     * @param p p, the parent of x
     * @return The new grandparent
     */
    protected Node RR_Rotate(Node p) {
        RR_Rotate(p, (Node) p.getParent());
        return p;
    }

    /**
     * Performs a left-right rotation
     *
     * @param p p, the parent of x
     * @return The new grandparent
     */
    protected Node LR_Rotate(Node p) {
        var x = (Node) p.getRightChild();
        var g = (Node) p.getParent();

        RR_Rotate(x, p);
        LL_Rotate(x, g);

        return x;
    }

    /**
     * Performs a right-left rotation
     *
     * @param p p, the parent of x
     * @return The new grandparent
     */
    protected Node RL_Rotate(Node p) {
        var x = (Node) p.getLeftChild();
        var g = (Node) p.getParent();

        LL_Rotate(x, p);
        RR_Rotate(x, g);

        return x;
    }

    /**
     * Automatically performs the correct rotation based on x's role in the tree
     *
     * @return the new grandparent node
     */
    protected Node rotate(Node x) {
        var p = (Node) x.getParent();
        Node res;
        if (p.isLeftChild() && x.isLeftChild()) {
            Log.log("Performing Left-Left Rotation with parent = " + p);
            res = LL_Rotate(p);
        } else if (p.isLeftChild() && x.isRightChild()) {
            Log.log("Performing Left-Right Rotation with parent = " + p);
            res = LR_Rotate(p);
        } else if (p.isRightChild() && x.isRightChild()) {
            Log.log("Performing Right-Right Rotation with parent = " + p);
            res = RR_Rotate(p);
        } else if (p.isRightChild() && x.isLeftChild()) {
            Log.log("Performing Right-Left Rotation with parent = " + p);
            res = RL_Rotate(p);
        } else throw new Error("This should never happen");

        printTreeToConsole();
        return res;
    }

    /**
     * Automatically performs the correct outside-outside rotation based on p's role in the tree
     *
     * @return the new grandparent node
     */
    protected Node OO_Rotate(Node p) {
        Node res;
        if (p.isLeftChild()) {
            Log.log("Performing Left-Left Rotation with parent = " + p);
            res = LL_Rotate(p);
        } else if (p.isRightChild()) {
            Log.log("Performing Right-Right Rotation with parent = " + p);
            res = RR_Rotate(p);
        } else throw new Error("This should never happen");

        printTreeToConsole();
        return res;
    }

    /**
     * Automatically performs the correct restructure based on x's role in the tree
     *
     * @return The new grandparent
     */
    protected Node restructure(Node x) {
        Node p = (Node) x.getParent();
        Node res;
        if (p.isLeftChild() && x.isLeftChild()) {
            Log.log("Performing Left-Left Restructure with parent = " + p);
            res = LL_Rotate(p);
        } else if (p.isLeftChild() && x.isRightChild()) {
            Log.log("Performing Left-Right Restructure with parent = " + p);
            res = LR_Rotate(p);
        } else if (p.isRightChild() && x.isRightChild()) {
            Log.log("Performing Right-Right Restructure with parent = " + p);
            res = RR_Rotate(p);
        } else if (p.isRightChild() && x.isLeftChild()) {
            Log.log("Performing Right-Left Restructure with parent = " + p);
            res = RL_Rotate(p);
        } else throw new Error("This should never happen");

        printTreeToConsole();
        return res;
    }

    @Override
    public void insertAsRoot(T value) {
        super.insertAsRoot(value);
        fixInsert(root);
    }

    /**
     * @param parent The root node to insert under
     * @param value  The value to insert into the tree
     * @return whether the tree changed as a result of this call
     */
    @Override
    protected boolean add(Node parent, T value) {
        int compare = value.compareTo(parent.getValue());

        if (compare < 0) {
            if (parent.hasLeftChild()) {
                add((Node) parent.getLeftChild(), value);
            } else {
                Log.log("Inserting " + value + " as left child of " + parent);
                var n = constructNode(value);
                parent.setLeftChild(n);
                printTreeToConsole();
                fixInsert(n);
            }
        } else {
            if (parent.hasRightChild()) {
                add((Node) parent.getRightChild(), value);
            } else {
                Log.log("Inserting " + value + " as right child of " + parent);
                var n = constructNode(value);
                parent.setRightChild(n);
                printTreeToConsole();
                fixInsert(n);
            }
        }

        return true;
    }

    /**
     * Should be overridden by subclasses to fix imbalance after insertion.
     *
     * @param node The changed node
     */
    protected abstract void fixInsert(Node node);
}