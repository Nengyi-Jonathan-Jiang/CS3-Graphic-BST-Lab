package app;

import app.treedrawer.*;
import tree.AbstractBST;
import tree.Traversal;
import util.FontLoader;
import util.Log;
import values.NumberOrString;

import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class App extends JFrame {
	private static final long BLINKER_TIME = 800;

	private final AbstractBST<NumberOrString, ?> bst;
	private String input = "";
	private static final Font font = FontLoader.load("JBMono.ttf").deriveFont(12f);

	private static final String STRING_MATCHING_REGEX = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";

	BufferedImage frame = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

	private static final TreeDrawer[] styles = new TreeDrawer[] {
		new TreeDrawerInOrder(),
		new TreeDrawerOffset(),
		new TreeDrawerStacked(),
		new TreeDrawerStackedCentered(),
	};
	private int currStyle = 3;

	public App (AbstractBST<NumberOrString, ?> bst) {
		this.bst = bst;

		setTitle("Graphics BST Lab");
		setMinimumSize(new Dimension(50, 50));
		setSize(new Dimension(1000, 800));
		setVisible(true);
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		addKeyListener(new KeyAdapter() {
			public void keyPressed (KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					if (input.length() > 0) {
						input = input.substring(0, input.length() - 1);
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					evaluateCommand();
				} else if (e.getKeyCode() == KeyEvent.VK_MINUS && e.isControlDown()) {
					TreeDrawer.font = TreeDrawer.font.deriveFont(TreeDrawer.font.getSize2D() * 0.9f);
				} else if (e.getKeyCode() == KeyEvent.VK_EQUALS && e.isControlDown()) {
					TreeDrawer.font = TreeDrawer.font.deriveFont(TreeDrawer.font.getSize2D() * 1.1f);
				} else {
					char c = e.getKeyChar();
					if ((c + "").matches("^[ -~]$")) input += c;
				}
			}
		});

		new Timer(10, e -> repaint()).start();

		evaluateCommand("help");

		// STDIN loop
		Scanner scan = new Scanner(System.in);
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			Log.echoInput(">>> " + line, Log.NO_TERMINAL);
			evaluateCommand(new Scanner(line));
		}
	}

	private void evaluateCommand () {
		Log.echoInput(">>> " + input);
		evaluateCommand(input);
		input = "";
	}

	private void evaluateCommand (String str) {
		evaluateCommand(new Scanner(str));
	}

	private void evaluateCommand (Scanner scan) {
		if (!scan.hasNext()) return;
		String command;
		switch (command = scan.next().toLowerCase()) {
			case "insert" -> {
				while (scan.hasNext()) {
					NumberOrString v;
					String vs;

					if (scan.hasNextInt()) {
						v = new NumberOrString(scan.nextInt());
						vs = v.toString();
					} else if (scan.hasNextDouble()) {
						v = new NumberOrString(scan.nextDouble());
						vs = v.toString();
					} else if ((v = NumberOrString.fromStringString(scan.findInLine(STRING_MATCHING_REGEX))) != null) {
						vs = "\"" + v + "\"";
					} else {
						Log.err("Bad input: \"" + scan.next() + "\" is not an int, double, or string.");
						break;
					}

					Log.log("Adding " + vs + " to the tree");
					bst.printTreeToConsole();
					bst.add(v);
					Log.log("Added " + vs + " to the tree");
				}
			}
			case "insertrand" -> {
				if (scan.hasNextInt()) {
					int lo = scan.nextInt();
					if (scan.hasNextInt()) {
						int hi = scan.nextInt();
						int num = 1;
						if (scan.hasNextInt()) num = scan.nextInt();
						StringBuilder c = new StringBuilder("insert");
						for (int i = 0; i < num; i++) {
							c.append(" ").append((int) (Math.random() * (hi - lo + 1)) + lo);
						}
						evaluateCommand(new Scanner(c.toString()));
						break;
					}
				}
				Log.err("Bad input: insertRand should be called with 2-3 int args");
			}
			case "clear" -> {
				bst.clear();
				Log.log("Cleared tree");
			}
			case "delete" -> {
				while (scan.hasNext()) {
					Object val;
					if (scan.hasNextInt()) {
						val = new NumberOrString(scan.nextInt());
					} else if (scan.hasNextDouble()) {
						val = new NumberOrString(scan.nextDouble());
					} else if (((NumberOrString) (val = NumberOrString.fromStringString(scan.findInLine(STRING_MATCHING_REGEX)))).value != null)
						;
					else {
						Log.err("Bad input: \"" + scan.next() + "\" is not an int.");
						continue;
					}

					if (!bst.contains(val)) {
						Log.warn(val + " is not in the tree");
					} else {
						Log.log("Deleting " + val + " from the tree");
						bst.printTreeToConsole();
						bst.remove(val);
						bst.printTreeToConsole();
						Log.log("Deleted " + val + " from the tree");
					}
				}
			}
			case "help" -> {
				for (var line : new String[] {
					"──── Commands ────",
					"insert <values:number...> : Adds the values to the bst.BST",
					"    Example: \"insert 3 1 0 2 6 4 5 9 11\"",
					"insertRand <low:number> <high:number> [amount:number] : inserts <amount> random numbers in the range [<low>, <high>]",
					"    Example: \"insertRand 0 99 10\"",
					"delete <values:number...> : Deletes the values from the bst.BST",
					"    Example: \"delete 1 2 3 4\"",
					"style <style: 1|2|3|4> : Sets the drawing style of the tree",
					"    Example: \"style 1\"",
					"traverse <method: preOrder|postOrder|inOrder|reverseOrder|levelOrder> : Traverses the binary search tree using the provided method",
					"    Example: \"traverse preOrder\"",
					"query <attribute: numLeaves|numLevels|height|width|diameter|size|levelWidths|isFull|largest|smallest> : Gets the corresponding attribute of the bst",
					"    Example: \"traverse preOrder\"",
					"clear : Deletes the entire tree",
					"help : Displays this list of commands",
				}) Log.output(line);
			}
			case "style" -> {
				int style;
				if (!scan.hasNextInt() || (style = scan.nextInt()) > styles.length || style <= 0) {
					Log.err("Invalid parameter to style: Must be an int in the range [1, " + styles.length + "]");
				} else {
					Log.log("Set style to " + style);
					currStyle = style - 1;
				}
			}
			case "traverse" -> {
				try {
					StringBuilder sb = new StringBuilder("[ ");
					(switch (scan.next().toLowerCase()) {
						case "preorder" -> new Traversal.PreOrder<>(bst);
						case "postorder" -> new Traversal.PostOrder<>(bst);
						case "inorder" -> new Traversal.InOrder<>(bst);
						case "reverseorder" -> new Traversal.ReverseOrder<>(bst);
						case "levelorder" -> new Traversal.LevelOrder<>(bst);
						default -> throw new Exception();
					}).forEach(i -> sb.append(i).append(" "));
					sb.append("]");
					Log.output(sb.toString());
				} catch (Exception e) {
					Log.err("Invalid parameter to traverse: Must be an one of preorder, postorder, inorder, reverseOrder, levelOrder");
				}
			}
			case "query" -> {
				if (scan.hasNext()) switch (scan.next().toLowerCase()) {
					case "numleaves" -> Log.output(bst.countLeaves() + "");
					case "numlevels" -> Log.output(bst.countLevels() + "");
					case "height" -> Log.output(bst.getHeight() + "");
					case "width" -> Log.output(bst.getWidth() + "");
					case "diameter" -> Log.output(bst.getDiameter() + "");
					case "size" -> Log.output(bst.size() + "");
					case "isfull" -> Log.output("Bst is" + (bst.isFullTree() ? " " : " not ") + "full");
					case "largest" -> Log.output(bst.getLargest() + "");
					case "smallest" -> Log.output(bst.getSmallest() + "");
					case "levelwidths" -> Log.output(Arrays.toString(bst.getLevelWidths()));
					default ->
						Log.err("Invalid parameter to query: Must be one of numLeaves, numLevels, width, height, diameter, size, levelWidths, isFull, largest, smallest");
				}
				else
					Log.err("Invalid parameter to query: Must be one of numLeaves, numLevels, height, width, diameter, size, levelWidths, isFull, largest, smallest");
			}
			case "intersect" -> {
				List<NumberOrString> values = new ArrayList<>();
				while (scan.hasNext()) {
					NumberOrString v;

					if (scan.hasNextInt()) {
						v = new NumberOrString(scan.nextInt());
					} else if (scan.hasNextDouble()) {
						v = new NumberOrString(scan.nextDouble());
					} else if ((v = NumberOrString.fromStringString(scan.findInLine(STRING_MATCHING_REGEX))) != null) {

					} else {
						Log.err("Bad input: \"" + scan.next() + "\" is not an int, double, or string.");
						break;
					}

					values.add(v);
				}

				List<NumberOrString> intersection = new ArrayList<>(bst.intersection(values));
				Log.output(intersection.toString());
			}
			default -> Log.err("Unknown command \"" + command + "\". Type \"help\" to get a list of the commands");
		}
	}

	@Override
	public void paint (Graphics g) {
		if (frame.getWidth() != getWidth() || frame.getHeight() != getHeight())
			frame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

		Graphics gg = frame.getGraphics();

		((Graphics2D) gg).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		((Graphics2D) gg).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		gg.setColor(Color.WHITE);
		gg.fillRect(0, 0, getWidth(), getHeight());

		drawStuff((Graphics2D) gg);

		g.drawImage(frame, 0, 0, null);
	}

	private void drawStuff (Graphics2D graphics) {
		styles[currStyle].drawTree(bst, getWidth(), graphics);

		long currentTime = System.currentTimeMillis();

		graphics.setColor(Color.BLACK);
		graphics.setFont(font);
		graphics.drawString(">>> " + input + (currentTime % BLINKER_TIME > BLINKER_TIME / 3 ? "█" : ""), 20, getHeight() - 20);

		Log.forEachLogItem((int i, String message, float t, Log.LogLevel level) -> {
			double opacity = 1 - Math.pow(t, 4);
			Color color = level.gColor;
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(Math.min((int) (opacity * 255), 255), 0)));
			graphics.drawString(message, 20, getHeight() - 20 - (font.getSize() * 4 / 3) * (i + 1));
		});
	}
}