import app.App;
import util.ANSICode;

import java.util.*;

public class Main {
	public static void main (String[] _args) {
		var args = Arrays.asList(_args);
		if(args.contains("--color"))
			ANSICode.enable();
		//new App(args.contains("--rb"));
		new App(true);
	}
}