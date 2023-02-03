import app.App;
import tree.AVLTree;
import tree.BST;
import tree.RBT;
import util.ANSICode;

import java.util.*;

public class Main {
	public static void main (String[] _args) {
		var args = Arrays.asList(_args);

		if(args.contains("--color"))
			ANSICode.enable();

		new App(args.contains("--rb") ? new RBT<>() : args.contains("--avl") ? new AVLTree<>() : new BST<>());

		/*
		 * insert 221 11 779 73 327 215 422 799 469 159
		 * delete 11 327 422 469
		 * insert 765 306 126 621 130 536 106 496 312 420
		 * delete 215 536 765
		 */
	}
}