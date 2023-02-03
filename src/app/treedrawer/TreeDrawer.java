package app.treedrawer;

import tree.AVLNode;
import tree.AbstractBST;
import tree.BSTNode;
import tree.RBTNode;
import util.FallBackFont;
import util.FontLoader;
import values.NumberOrString;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

public abstract class TreeDrawer {
	public static Font font = FontLoader.load("JBMono.ttf").deriveFont(12f);
	public static Font ffont = FontLoader.load("Consolas.ttf").deriveFont(12f);

	public static void setFontSize(float size) {
		font = font.deriveFont(size);
		ffont = ffont.deriveFont(size);
	}
	public static float getFontSize(){
		return font.getSize2D();
	}

	private static final Graphics dummyGraphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();

	public static int getNodePadding () {
		return font.getSize() / 2;
	}

	protected static Dimension getRenderedStringSize (String text) {
		Rectangle2D size = dummyGraphics.getFontMetrics(font).getStringBounds(text, dummyGraphics);
		return new Dimension((int) size.getWidth(), (int) size.getHeight());
	}

	protected static int getRenderedSize(BSTNode<?> node){
		return getRenderedStringSize(node.toString()).width + getNodePadding() * 4;
	}

	protected abstract int[][] calculatePositions (BSTNode<?>[][] levels, int height);

	public final void drawTree (AbstractBST<?, ?> bst, int windowWidth, Graphics2D graphics) {
		final int height = bst.countLevels();
		BSTNode<?>[][] levels;
		int[][] x;

		if (bst.isEmpty()){
			levels = new BSTNode<?>[][]{};
			x = new int[][]{};
		}
		else {
			levels = bst.getNodesAtLevels();

			x = calculatePositions(levels, height);
		}

		int[][] y = new int[height][];
		for (int h = 0; h < height; h++) {
			y[h] = new int[1 << h];
			Arrays.fill(y[h],
				(h * 6 + 1) * getNodePadding() + 40 + getRenderedStringSize("foo").height / 2
			);
		}

		for(int i = 0; i < height; i++){
			for(int j = 0; j < (1 << i); j++){
				x[i][j] += windowWidth / 2;
			}
		}

		graphics.setFont(font);

		for (int h = height - 1; h >= 0; h--) {
			for (int i = 0; i < levels[h].length; i++) {
				var node = levels[h][i];

				if (node != null) {
					String text = node.toString();
					var d = getRenderedStringSize(text);
					int X = x[h][i], Y = y[h][i];

					var fm = graphics.getFontMetrics();

					graphics.setColor(Color.GRAY);
					if (node.getParent() != null && h > 0) {
						graphics.drawLine(X, Y, x[h - 1][i / 2], y[h - 1][i / 2]);
					}

					graphics.setColor(Color.WHITE);
					graphics.fillRect(X - d.width / 2 - getNodePadding(), Y - d.height / 2 - getNodePadding(), d.width + getNodePadding() * 2, d.height + getNodePadding() * 2);

					graphics.setColor(
						node instanceof RBTNode ?
							RBTNode.getColor((RBTNode<?>) node) == RBTNode.Color.RED ? Color.RED : Color.BLACK
						: node instanceof AVLNode ?
						    ((AVLNode<? extends Comparable<?>>) node).isLeftHeavy() ? Color.RED
						    : ((AVLNode<? extends Comparable<?>>) node).isRightHeavy() ? Color.BLUE
							: Color.BLACK
						  : Color.BLACK
					);
					graphics.drawRect(X - d.width / 2 - getNodePadding(), Y - d.height / 2 - getNodePadding(), d.width + getNodePadding() * 2, d.height + getNodePadding() * 2);
					if (node.getValue() instanceof NumberOrString && ((NumberOrString) node.getValue()).isString()) {
						graphics.setColor(Color.GREEN.darker());
					}
					graphics.drawString(
						FallBackFont.createFallbackString(text, font, ffont).getIterator(),
						//text,
						X - d.width / 2,
						Y - fm.getHeight() / 2 + fm.getAscent()
					);
				}
			}
		}
	}
}