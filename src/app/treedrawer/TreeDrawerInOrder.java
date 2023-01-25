package app.treedrawer;

import app.treedrawer.TreeDrawer;
import tree.BSTNode;

import java.awt.*;
import java.util.*;

public class TreeDrawerInOrder extends TreeDrawer {

	@Override
	public int[][] calculatePositions (BSTNode<?>[][] levels, int height, int windowWidth, Graphics2D graphics) {
		// Inorder traversal

		int[][] x = new int[height][];
		for (int h = 0; h < height; h++) x[h] = new int[1 << h];
		int left = 0;

		Stack<BSTNode<?>> nStk = new Stack<>();
		Stack<Integer> iStk = new Stack<>(), hStk = new Stack<>();
		var currN = levels[0][0];
		int currI = 0;
		int currH = 0;
		while (!nStk.isEmpty() || currN != null) {
			if (currN != null) {
				nStk.push(currN);
				iStk.push(currI);
				hStk.push(currH);
				currN = currN.getLeftChild();
				currI *= 2;
				currH++;
			} else {
				var node = nStk.pop();
				int i = iStk.pop();
				int h = hStk.pop();

				int w = getRenderedSize(node.getValue().toString()).width + getNodePadding();
				x[h][i] = left + w / 2;
				left += w;

				currN = node.getRightChild();
				currI = i * 2 + 1;
				currH = h + 1;
			}
		}

		for (int h = 0; h < height; h++)
			for (int i = 0; i < x[h].length; i++)
				x[h][i] -= left / 2;

		return x;
	}
}