import app.App;
import tree.AVLT;
import tree.BST;
import tree.RBT;
import util.ANSICode;

import java.util.*;

public class Main {
	public static void main (String[] _args) {
		var args = Arrays.asList(_args);

		if(args.contains("--color"))
			ANSICode.enable();

		new App(args.contains("--rb") ? new RBT<>() : args.contains("--avl") ? new AVLT<>() : new BST<>());
	}
}