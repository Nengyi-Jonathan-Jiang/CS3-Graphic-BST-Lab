package app;

import graphics.BoundingBox1D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tree.BSTNode;
import util.GraphicsUtil;
import util.files.FontLoader;

import java.awt.*;

public class GraphicsTreeNode<Node extends BSTNode<?>> {
    public static final Font font = FontLoader.load("JBMono.ttf").deriveFont(20f);
    public @Nullable GraphicsTreeNode<Node> parent;
    public final @NotNull Node node;
    public final @Nullable GraphicsTreeNode<Node> left;
    public final @Nullable GraphicsTreeNode<Node> right;

    public BoundingBox1D containingBounds;
    public BoundingBox1D nodeBounds;
    public BoundingBox1D childrenBounds;

    public static <Node extends BSTNode<?>> GraphicsTreeNode<Node> create(@Nullable Node node) {
        return create(node, null);
    }

    public static <Node extends BSTNode<?>> GraphicsTreeNode<Node> create(@Nullable Node node, @Nullable GraphicsTreeNode<Node> parent) {
        if (node == null) return null;
        GraphicsTreeNode<Node> left = create((Node) node.getLeftChild(), null);
        GraphicsTreeNode<Node> right = create((Node) node.getRightChild(), null);
        GraphicsTreeNode<Node> gNode = new GraphicsTreeNode<>(parent, node, left, right);
        if (left != null) left.parent = gNode;
        if (right != null) right.parent = gNode;
        return gNode;
    }

    public GraphicsTreeNode(@Nullable GraphicsTreeNode<Node> parent, @NotNull Node node, @Nullable GraphicsTreeNode<Node> left, @Nullable GraphicsTreeNode<Node> right) {
        this.parent = parent;
        this.node = node;
        this.left = left;
        this.right = right;

        computePositions();
    }

    public static float getNodePadding() {
        return font.getSize2D() / 2;
    }

    public float selfWidth() {
        return Math.max(
            GraphicsUtil.getRenderedStringSize(node.toString(), font).width,
            GraphicsUtil.getRenderedStringSize(node.toString(), font).height
        ) + getNodePadding() * 4;
    }

    private void shiftBy(double offset) {
        containingBounds = containingBounds.shiftedBy(offset);
        nodeBounds = nodeBounds.shiftedBy(offset);
        childrenBounds = childrenBounds.shiftedBy(offset);
        if (left != null) left.shiftBy(offset);
        if (right != null) right.shiftBy(offset);
    }

    private void computePositions() {

        // Initialize selfBounds to a bounding box centered on zero;
        nodeBounds = BoundingBox1D.centered(selfWidth(), 0);

        computeChildrenBounds(getNodePadding() * 2);

        containingBounds = BoundingBox1D.containing(nodeBounds, childrenBounds);
//        shiftBy(-containingBounds.center());
    }

    private void computeChildrenBounds(float singleNodeOffset) {
        if (left != null && right != null) {
            // Put left and right bounding boxes side-by-side, with the shared boundary at x = 0:
            left.shiftBy(-left.containingBounds.width() / 2);
            right.shiftBy(right.containingBounds.width() / 2);

            // Shift left and right so that the point halfway between the node centers is at x = 0
            double neededSelfOffset = (left.nodeBounds.center() + right.nodeBounds.center()) / 2;
//            left.shiftBy(-neededSelfOffset);
//            right.shiftBy(-neededSelfOffset);

            // Compute children bounds
            childrenBounds = BoundingBox1D.containing(left.containingBounds, right.containingBounds);
        } else if (left != null) {
            // Move the left node's center to -singleNodeOffset
            left.shiftBy(-singleNodeOffset - left.nodeBounds.center());
            childrenBounds = left.containingBounds;
        } else if (right != null) {
            // Move the right node's center to +singleNodeOffset
            right.shiftBy(singleNodeOffset - right.nodeBounds.center());
            childrenBounds = right.containingBounds;
        } else {
            // No children, easiest way is to just set childrenBounds = nodeBounds
            childrenBounds = nodeBounds;
        }
    }
}
