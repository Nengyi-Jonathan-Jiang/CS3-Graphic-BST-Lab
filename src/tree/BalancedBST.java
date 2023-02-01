package tree;

public abstract class BalancedBST<T extends Comparable<T>, Node extends BSTNode<T>> extends AbstractBST<T, Node> {
    /**
     * Performs a left-left rotation
     *
     * @param p p, the parent of x
     * @param g g, the grandparent of x
     */
    protected void LL_Rotation(Node p, Node g) {
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
    protected void RR_Rotation(Node p, Node g) {
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
    protected Node LL_Rotation(Node p) {
        LL_Rotation(p, (Node) p.getParent());

        return p;
    }

    /**
     * Performs a right-right rotation
     *
     * @param p p, the parent of x
     * @return The new grandparent
     */
    protected Node RR_Rotation(Node p) {
        RR_Rotation(p, (Node) p.getParent());

        return p;
    }

    /**
     * Performs a left-right rotation
     *
     * @param p p, the parent of x
     * @return The new grandparent
     */
    protected Node LR_Rotation(Node p) {
        var x = (Node) p.getRightChild();
        var g = (Node) p.getParent();

        RR_Rotation(x, p);
        LL_Rotation(x, g);

        return x;
    }

    /**
     * Performs a right-left rotation
     *
     * @param p p, the parent of x
     * @return The new grandparent
     */
    protected Node RL_Rotation(Node p) {
        var x = (Node) p.getLeftChild();
        var g = (Node) p.getParent();

        LL_Rotation(x, p);
        RR_Rotation(x, g);

        return x;
    }

    /**
     * Automatically performs the correct rotation based on x's role in the tree
     */
    protected void rotate(Node p, Node x) {
        if (p.isLeftChild() && (x == null || x.isLeftChild())) {
            System.out.println("Performing Left-Left Rotation with parent = " + p.getValue());
            LL_Rotation(p);
        } else if (p.isLeftChild() && x.isRightChild()) {
            System.out.println("Performing Left-Right Rotation with parent = " + p.getValue());
            LR_Rotation(p);
        } else if (p.isRightChild() && (x == null || x.isRightChild())) {
            System.out.println("Performing Right-Right Rotation with parent = " + p.getValue());
            RR_Rotation(p);
        } else if (p.isRightChild() && x.isLeftChild()) {
            System.out.println("Performing Right-Left Rotation with parent = " + p.getValue());
            RL_Rotation(p);
        } else throw new Error("This should never happen");
        printTreeToConsole();
    }

    /**
     * Automatically performs the correct restructure based on x's role in the tree
     *
     * @return The new grandparent
     */
    protected Node restructure(Node p, Node x) {
        Node res;
        if (p.isLeftChild() && (x == null || x.isLeftChild())) {
            System.out.println("Performing Left-Left Restructure with parent = " + p.getValue());
            res = LL_Rotation(p);
        } else if (p.isLeftChild() && x.isRightChild()) {
            System.out.println("Performing Left-Right Restructure with parent = " + p.getValue());
            res = LR_Rotation(p);
        } else if (p.isRightChild() && (x == null || x.isRightChild())) {
            System.out.println("Performing Right-Right Restructure with parent = " + p.getValue());
            res = RR_Rotation(p);
        } else if (p.isRightChild() && x.isLeftChild()) {
            System.out.println("Performing Right-Left Restructure with parent = " + p.getValue());
            res = RL_Rotation(p);
        } else throw new Error("This should never happen");

        printTreeToConsole();
        return res;
    }

    @Override
    public boolean add(T value) {
        if (root == null) {
            System.out.println("Inserting " + value + " as root");
            root = constructNode(value);
            printTreeToConsole();
            fixInsert(root);
            return true;
        } else return add(root, value);
    }

    /**
     * @param parent The root node to insert under
     * @param value   The value to insert into the tree
     * @return whether the tree changed as a result of this call
     */
    @Override
    protected boolean add(Node parent, T value) {
        int compare = value.compareTo(parent.getValue());

        if (compare < 0) {
            if (parent.hasLeftChild()) {
                add((Node) parent.getLeftChild(), value);
            } else {
                System.out.println("Inserting " + value + " as left child of " + parent.getValue());
                var n = constructNode(value);
                parent.setLeftChild(n);
                printTreeToConsole();
                fixInsert(n);
            }
        } else {
            if (parent.hasRightChild()) {
                add((Node) parent.getRightChild(), value);
            } else {
                System.out.println("Inserting " + value + " as right child of " + parent.getValue());
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
     * @param node The changed node
     */
    protected abstract void fixInsert (Node node);

    /**
     * Constructs a {@link Node} to use in the tree. This should be overridden by subclasses to return the right kind of
     * BSTNode to use in the tree
     * @param value The value to put in the node
     */
    protected abstract Node constructNode(T value);
}
