package app.treedrawer;

import tree.BSTNode;

import java.awt.Graphics2D;
import java.util.*;

public class TreeDrawerStacked extends TreeDrawer {

	@Override
	public int[][] calculatePositions (BSTNode<?>[][] levels, int height) {
		final int LR_OFFSET = getNodePadding() * 2;

		final int[][] widths = new int[height][];
		final int[][] pos = new int[height][];
		final int[][] lPad = new int[height][], rPad = new int[height][];

		for (int h = height - 1; h >= 0; h--) {
			widths[h] = new int[levels[h].length];
			pos[h] = new int[levels[h].length];
			lPad[h] = new int[levels[h].length];
			rPad[h] = new int[levels[h].length];

			for (int i = 0; i < levels[h].length; i++) {
				// Null nodes contribute zero width (obviously)
				if (levels[h][i] != null) {
					var node = levels[h][i];
					var left = node.getLeftChild();
					var right = node.getRightChild();

					// Raw width of node (rect)
					int w = getRenderedSize(levels[h][i]);

					// degree 0: node width is text width, node pos is centered, no padding
					if (left == null && right == null) {
						pos[h][i] = w / 2;
						widths[h][i] = w;
						lPad[h][i] = w;
						continue;
					}

					int p;
					if (left == null) { // right is not null
						p = pos[h + 1][i * 2 + 1] - LR_OFFSET;  // pos is a bit to the left of the right child pos
					} else if (right == null) { // left is not null
						p = pos[h + 1][i * 2] + LR_OFFSET;  // pos is a bit to the right of the left child pos
					} else {  // degree 2
						p = widths[h + 1][i * 2];   // pos is at l-r boundary
					}

					int totalChildWidth = widths[h + 1][i * 2] + widths[h + 1][i * 2 + 1];

					// Calculate paddings
					int l = Math.max(0, w / 2 - p),
						r = Math.max(0, p + w - w / 2 - totalChildWidth);

					lPad[h][i] = l; // left padding
					rPad[h][i] = r; // right padding
					pos[h][i] = p + l;  // add left padding to distance from left edge
					widths[h][i] = totalChildWidth + l + r; // width is width of children + padding
				}
			}
		}
		final int[][] x = new int[height][], y = new int[height][];
		for (int h = 0; h < height; h++) {
			int left = 0;
			x[h] = new int[widths[h].length];
			y[h] = new int[widths[h].length];
			Arrays.fill(y[h], h * 30 + 50);
			for (int i = 0; i < widths[h].length; i++) {
				for (int hh = h, ii = i; hh > 0; ) {
					if (ii % 2 == 0) {
						left += lPad[--hh][ii /= 2];
					} else {
						break;
					}
				}

				x[h][i] = left + pos[h][i] - widths[0][0] / 2;
				left += widths[h][i];

				for (int hh = h, ii = i; hh > 0; ) {
					if (ii % 2 == 1) {
						left += rPad[--hh][ii /= 2];
					} else {
						break;
					}
				}
			}
		}
		return x;
	}
}
