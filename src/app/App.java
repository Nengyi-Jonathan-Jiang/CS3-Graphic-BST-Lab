package app;

import app.treedrawer.TreeDrawer;
import graphics.Canvas;
import graphics.JGraphics;
import graphics.Window;
import tree.*;
import util.GraphicsUtil;
import util.Log;
import util.files.FontLoader;
import util.math.Vec2;
import values.NumberOrString;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class App {
    private static final long BLINKER_TIME = 800;

    private AbstractBST<NumberOrString, ?> bst;
    private final Canvas canvas;
    private String input = "";
    private static final Font font = FontLoader.load("JBMono.ttf").deriveFont(20f);

    private static final String STRING_MATCHING_REGEX = "\"([^\"\\\\]|\\\\.)*\"|'([^'\\\\]|\\\\.)*'";

    private static final TreeDrawer treeDrawer = new TreeDrawer() {};
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;

    public App() {
        this.bst = new RBT<>();

        canvas = new Window("BST Visualizer", new Vec2(600, 50), new Vec2(WIDTH, HEIGHT)).canvas;

        SwingUtilities.getWindowAncestor(canvas.canvas).addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                App.this.onKeyPressed(e);
            }
        });

        new Timer(16, _ -> canvas.repaint(this::drawStuff)).start();

        evaluateCommand("help");

        // STDIN loop
        Scanner scan = new Scanner(System.in);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            Log.echoInput(">>> " + line, Log.NO_TERMINAL);
            evaluateCommand(new Scanner(line));
        }
    }

    private void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!input.isEmpty()) {
                input = input.substring(0, input.length() - 1);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            evaluateCommand();
        } else if (e.getKeyCode() == KeyEvent.VK_MINUS && e.isControlDown()) {
//            TreeDrawer.setFontSize(TreeDrawer.getFontSize() * 0.9f);
        } else if (e.getKeyCode() == KeyEvent.VK_EQUALS && e.isControlDown()) {
//            TreeDrawer.setFontSize(TreeDrawer.getFontSize() * 1.1f);
        } else {
            char c = e.getKeyChar();
            if ((c + "").matches("^[ -~]$")) input += c;
        }
    }

    private void evaluateCommand() {
        Log.echoInput(">>> " + input);
        evaluateCommand(input);
        input = "";
    }

    private void evaluateCommand(String str) {
        evaluateCommand(new Scanner(str));
    }

    private void evaluateCommand(Scanner scan) {
        if (!scan.hasNext()) return;
        String command;
        switch (command = scan.next().toLowerCase()) {
            case "insert" -> {
                while (scan.hasNext()) {
                    NumberOrString v = NumberOrString.getFromScanner(scan);

                    if (v == null) {
                        Log.err("Bad input: \"" + scan.next() + "\" is not an int, double, or valid string.");
                        break;
                    }

                    Log.log("Adding " + v + " to the tree");
                    bst.printTreeToConsole();
                    bst.add(v);
                    Log.log("Added " + v + " to the tree");
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
                if (scan.hasNext()) {
                    AbstractBST<NumberOrString, ?> newTree = switch (scan.next()) {
                        case "AVL" -> new AVLTree<>();
                        case "BST" -> new BST<>();
                        case "RBT" -> new RBT<>();
                        default -> null;
                    };
                    if (newTree != null) {
                        bst = newTree;
                        Log.log("Cleared tree");
                    } else {
                        Log.err("Unknown tree type: must be AVL, BST, RBT");
                    }
                } else {
                    bst.clear();
                    Log.log("Cleared tree");
                }
            }
            case "delete" -> {
                while (scan.hasNext()) {
                    NumberOrString v = NumberOrString.getFromScanner(scan);

                    if (v == null) {
                        Log.err("Bad input: \"" + scan.next() + "\" is not an int, double, or valid string.");
                        break;
                    }

                    if (!bst.contains(v)) {
                        Log.log(v + " is not in the tree");
                        break;
                    }

                    Log.log("Deleting " + v + " from the tree");
                    bst.printTreeToConsole();
                    bst.remove(v);
                    bst.printTreeToConsole();
                    Log.log("Deleted " + v + " from the tree");
                }
            }
            case "help" -> {
                for (var line : new String[]{
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
                })
                    Log.output(line);
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

    private void drawStuff(JGraphics jGraphics) {
        Graphics2D graphics = jGraphics.originalGraphics();

        jGraphics.setColor(Style.Colors.BLACK);
        jGraphics.clear();

        treeDrawer.drawTree(bst, jGraphics);

        long currentTime = System.currentTimeMillis();

        graphics.setColor(Style.Colors.FG);
        graphics.setFont(font);
        graphics.drawString(
            ">>> " + input + (currentTime % BLINKER_TIME > BLINKER_TIME / 3 ? "█" : ""),
            -WIDTH / 2 + 20,
            HEIGHT / 2 - 20
        );

        Log.forEachLogItem((int i, String message, float t, Log.LogLevel level) -> {
            double opacity = 1 - Math.pow(t, 4);
            Color color = level.gColor;

            var w = GraphicsUtil.getRenderedStringSize(message, font).width;

            graphics.setColor(new Color(
                Style.Colors.BLACK.getRed(),
                Style.Colors.BLACK.getGreen(),
                Style.Colors.BLACK.getBlue(),
                Math.max(Math.min((int) (opacity * 200), 200), 0))
            );

            graphics.fillRect(
                -WIDTH / 2,
                HEIGHT / 2 - 16 - (font.getSize() * 4 / 3) * (i + 2),
                w + 40,
                font.getSize() * 4 / 3
            );

            graphics.setColor(new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                Math.max(Math.min((int) (opacity * 255), 255), 0)
            ));

            graphics.drawString(message, -WIDTH / 2 + 20, HEIGHT / 2 - 20 - (font.getSize() * 4 / 3) * (i + 1));
        });
    }
}