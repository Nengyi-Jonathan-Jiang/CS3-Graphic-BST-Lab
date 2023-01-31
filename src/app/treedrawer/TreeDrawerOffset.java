package app.treedrawer;

import tree.BSTNode;

import java.awt.Graphics2D;

public class TreeDrawerOffset extends TreeDrawer {

	@Override
	public int[][] calculatePositions (BSTNode<?>[][] levels, int height, int windowWidth, Graphics2D graphics) {
		final int[][] widths = new int[height][];
		for (int r = height - 1; r >= 0; r--) {
			widths[r] = new int[levels[r].length];

			for (int i = 0; i < levels[r].length; i++) {
				if (levels[r][i] == null) {
					if (levels[r][i ^ 1] != null) {
						for (int rr = r, ii = i; rr < height; rr++, ii *= 2) {
							widths[rr][ii] = getNodePadding() * 4;
						}
					}
				} else {
					// Calculate width thing
					int w = getRenderedSize(levels[r][i]);

					int width = r == height - 1 ? w : Math.max(w, widths[r + 1][i * 2] + widths[r + 1][i * 2 + 1]);

					widths[r][i] = width;

					if (!levels[r][i].hasRightChild() && !levels[r][i].hasLeftChild()) {
						for (int rr = r, ii = i; rr < height; rr++, ii *= 2) {
							widths[rr][ii] = width;
						}
					}
				}
			}
		}

		final int[][] x = new int[height][];
		for (int r = 0; r < height; r++) {
			int sum = 0;
			x[r] = new int[widths[r].length];
			for (int i = 0; i < widths[r].length; i++) {
				x[r][i] = sum + widths[r][i] / 2 - widths[0][0] / 2;
				sum += widths[r][i];
			}
		}

		return x;
	}
}
