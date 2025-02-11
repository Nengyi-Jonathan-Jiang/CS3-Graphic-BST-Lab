package app.treedrawer;

import app.GraphicsTreeNode;
import app.Style;
import graphics.BoundingBox1D;
import graphics.JGraphics;
import tree.AVLNode;
import tree.AbstractBST;
import tree.RBTNode;
import util.GraphicsUtil;

import java.awt.*;

public class TreeDrawer {
    public final void drawTree(AbstractBST<?, ?> bst, JGraphics jGraphics) {
        drawNodes(
            GraphicsTreeNode.create(bst.getRoot()),
            0,
            jGraphics
        );
    }

    private void drawNodes(GraphicsTreeNode<?> node, int yy, JGraphics graphics) {
        if (node == null) return;

        drawNodes(node.left, yy + 1, graphics);
        drawNodes(node.right, yy + 1, graphics);

        double textHeight = GraphicsUtil.getRenderedStringSize("test string", GraphicsTreeNode.font).height;
        double yOffset = GraphicsTreeNode.getNodePadding() + 40 + textHeight / 2;
        double y = yy * 6 * GraphicsTreeNode.getNodePadding() + yOffset;

        BoundingBox1D bounds = node.nodeBounds;
        String text = node.node.toString();

        if (node.parent != null) {
            graphics.setColor(Style.Colors.DM);
            graphics.drawLine(
                bounds.center(), y,
                node.parent.nodeBounds.center(),
                y - 6 * GraphicsTreeNode.getNodePadding()
            );
        }

        var d = GraphicsUtil.getRenderedStringSize(text, GraphicsTreeNode.font);

        var fm = graphics.originalGraphics().getFontMetrics();

        Color backgroundColor, borderColor, textColor;
        if (node.node instanceof RBTNode) {
            if (((RBTNode<? extends Comparable<?>>) node.node).isRed()) {
                backgroundColor = borderColor = Style.Colors.RED;
                textColor = Style.Colors.BLACK;
            } else {
                backgroundColor = Style.Colors.BLACK;
                borderColor = textColor = Style.Colors.GREEN;
            }
        } else if (node.node instanceof AVLNode) {
            backgroundColor = Style.Colors.BLACK;
            if (((AVLNode<? extends Comparable<?>>) node.node).isLeftHeavy()) {
                borderColor = textColor = Style.Colors.RED;
            } else if (((AVLNode<? extends Comparable<?>>) node.node).isRightHeavy()) {
                borderColor = textColor = Style.Colors.BLUE;
            } else {
                borderColor = textColor = Style.Colors.GREEN;
            }
        } else {
            backgroundColor = Style.Colors.BLACK;
            borderColor = textColor = Style.Colors.GREEN;
        }

        double ovalLeft = bounds.left() + GraphicsTreeNode.getNodePadding();
        double ovalTop = y - d.height / 2. - GraphicsTreeNode.getNodePadding();
        double ovalWidth = bounds.width() - GraphicsTreeNode.getNodePadding() * 2;
        float ovalHeight = d.height + GraphicsTreeNode.getNodePadding() * 2;
        double textLeft = bounds.center() - d.width / 2.;
        double textTop = y - fm.getHeight() / 2. + fm.getAscent();
        graphics
            .setColor(backgroundColor).fillOval(ovalLeft, ovalTop, ovalWidth, ovalHeight)
            .setColor(borderColor).drawOval(ovalLeft, ovalTop, ovalWidth, ovalHeight)
            .setColor(textColor).setDrawFont(GraphicsTreeNode.font).drawText(text, textLeft, textTop);

        graphics.setColor(Style.Colors.DM);
        graphics.drawRect(node.containingBounds.left(), ovalTop, node.containingBounds.width(), ovalHeight);
    }

}