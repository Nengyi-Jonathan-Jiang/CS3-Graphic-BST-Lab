package app.treedrawer;

import app.Style;
import tree.AVLNode;
import tree.AbstractBST;
import tree.BSTNode;
import tree.RBTNode;
import util.FontLoader;
import util.GraphicsUtil;

import java.awt.*;
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

	public static int getNodePadding () {
		return font.getSize() / 2;
	}

	protected static int getRenderedSize(BSTNode<?> node){
		return
			Math.max(
				GraphicsUtil.getRenderedStringSize(node.toString(), font).width,
				GraphicsUtil.getRenderedStringSize(node.toString(), font).height
			)
			+ getNodePadding() * 4;
	}

	protected abstract int[][] calculatePositions (BSTNode<?>[][] levels, int height);

	public final void drawTree (AbstractBST<?, ?> bst, int windowWidth, int windowHeight, Graphics2D graphics) {
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
		{
			int textHeight = GraphicsUtil.getRenderedStringSize("test string", font).height;

			// Black magic. Calculates a nice position for the tree to center it
			// int yOffset =  (windowHeight + textHeight) / 2 - (height - 1) * 3 * getNodePadding() + 4;
			int yOffset = getNodePadding() + 40 + textHeight / 2;

			// Now the actual positions
			for (int h = 0; h < height; h++) {
				y[h] = new int[1 << h];
				Arrays.fill(y[h],
					h * 6 * getNodePadding() + yOffset
				);
			}
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
					var d = GraphicsUtil.getRenderedStringSize(text, font);
					var w = getRenderedSize(node) - getNodePadding() * 4;
					int X = x[h][i], Y = y[h][i];

					var fm = graphics.getFontMetrics();

					graphics.setColor(Style.Colors.DM);
					if (node.getParent() != null && h > 0) {
						graphics.drawLine(X, Y, x[h - 1][i / 2], y[h - 1][i / 2]);
					}

					Color bg, bd, fg;
					if(node instanceof RBTNode){
						if(((RBTNode<? extends Comparable<?>>) node).isRed()){
							bg = bd = Style.Colors.RED;
							fg = Style.Colors.BLACK;
						}
						else {
							bg = Style.Colors.BLACK;
							bd = fg = Style.Colors.GREEN;
						}
					}
					else if(node instanceof AVLNode){
						bg = Style.Colors.BLACK;
						if(((AVLNode<? extends Comparable<?>>) node).isLeftHeavy()){
							bd = fg = Style.Colors.RED;
						}
						else if(((AVLNode<? extends Comparable<?>>) node).isRightHeavy()){
							bd = fg = Style.Colors.BLUE;
						}
						else {
							bd = fg = Style.Colors.GREEN;
						}
					}
					else {
						bg = Style.Colors.BLACK;
						bd = fg = Style.Colors.GREEN;
					}

					graphics.setColor(bg);
					graphics.fillOval(X - w / 2 - getNodePadding(), Y - d.height / 2 - getNodePadding(), w + getNodePadding() * 2, d.height + getNodePadding() * 2);

					graphics.setColor(bd);
					graphics.drawOval(X - w / 2 - getNodePadding(), Y - d.height / 2 - getNodePadding(), w + getNodePadding() * 2, d.height + getNodePadding() * 2);

					graphics.setColor(fg);
					graphics.drawString(
						GraphicsUtil.withFallbackFont(text, font, ffont).getIterator(),
						//text,
						X - d.width / 2,
						Y - fm.getHeight() / 2 + fm.getAscent()
					);
				}
			}
		}
	}
}