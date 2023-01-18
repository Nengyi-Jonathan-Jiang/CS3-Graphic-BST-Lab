import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;

public class App extends JFrame {
    private static final long LOG_FADE_TIME = 16000;
    private static final long BLINKER_TIME = 800;

    private final BST<Integer> bst;
    private String input = "";
    private final Log log = new Log(LOG_FADE_TIME, 30);

    private static final Font font = FontLoader.load("JBMono.ttf").deriveFont(12f);

    BufferedImage frame = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

    private static final TreeDrawer[] styles = new TreeDrawer[]{
            new TreeDrawerInOrder(),
            new TreeDrawerOffset(),
            new TreeDrawerStacked(),
            new TreeDrawerStackedCentered(),
    };
    private int currStyle = 0;

    public App(boolean isRedBlackTree){
        bst = isRedBlackTree ? new RBT<>() : new BST<>();

        setTitle("Graphics BST Lab");
        setMinimumSize(new Dimension(50, 50));
        setSize(new Dimension(1000, 800));
        setVisible(true);
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    if(input.length() > 0) {
                        input = input.substring(0, input.length() - 1);
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    evaluateCommand();
                }
                else if(e.getKeyCode() == KeyEvent.VK_MINUS && e.isControlDown()){
                    TreeDrawer.font = TreeDrawer.font.deriveFont(TreeDrawer.font.getSize2D() * 0.9f);
                }
                else if(e.getKeyCode() == KeyEvent.VK_EQUALS && e.isControlDown()){
                    TreeDrawer.font = TreeDrawer.font.deriveFont(TreeDrawer.font.getSize2D() * 1.1f);
                }
                else{
                    char c = e.getKeyChar();
                    if((c + "").matches("^[ -~]$")) input += c;
                }
            }
        });

        new Timer(10, e -> repaint()).start();

        evaluateCommand(new Scanner("insert 3 1 0 2 6 4 5 9 11"));
        // First thing to do is help
        evaluateCommand(new Scanner("help"));

        // Also, STDIN loop
        Scanner scan = new Scanner(System.in);
        while(scan.hasNextLine()){
            String line = scan.nextLine();
            log.log(">>> " + line);
            evaluateCommand(new Scanner(line));
        }
    }

    private void evaluateCommand(){
        Scanner scan = new Scanner(input);
        log.log(">>> " + input);
        evaluateCommand(scan);
        // Reset input
        input = "";
    }

    private void evaluateCommand(Scanner scan){
        if(!scan.hasNext()) return;
        String command;
        switch(command = scan.next().toLowerCase()){
            case "insert" -> {
                while(scan.hasNext()){
                    if(scan.hasNextInt()){
                        int val = scan.nextInt();
//                        if(bst.contains(val))
//                            log.log(val + " is already in the tree", 1);
//                        else {
                            bst.add(val);
                            log.log("Added " + val + " to the tree", 1);
//                        }
                    }
                    else{
                        log.log("Bad input: \"" + scan.next() + "\" is not an int.", -1);
                    }
                }

                Main.printTree(bst);
            }
            case "insertrand" -> {
                if(scan.hasNextInt()){
                    int lo = scan.nextInt();
                    if(scan.hasNextInt()){
                        int hi = scan.nextInt();
                        int num = 1;
                        if(scan.hasNextInt()) num = scan.nextInt();
                        StringBuilder c = new StringBuilder("insert");
                        for(int i = 0; i < num; i++){
                            c.append(" ").append((int)(Math.random() * (hi - lo + 1)) + lo);
                        }
                        evaluateCommand(new Scanner(c.toString()));
                    }
                    else{
                        log.log("Bad input: insertRand should be called with 2-3 int args", -1);
                    }
                }
                else{
                    log.log("Bad input: insertRand should be called with 2-3 int args", -1);
                }
            }
            case "rotate" -> {
                if(bst instanceof RBT<Integer>){
                    var n = ((RBT<Integer>)bst).find(scan.nextInt());
                    System.out.println(n);
                    (switch (scan.next()) {
                        case "LL" -> (Consumer<RBTNode<Integer>>)((RBT<Integer>)bst)::LL_Rotation;
                        case "LR" -> (Consumer<RBTNode<Integer>>)((RBT<Integer>)bst)::LR_Rotation;
                        case "RR" -> (Consumer<RBTNode<Integer>>)((RBT<Integer>)bst)::RR_Rotation;
                        case "RL" -> (Consumer<RBTNode<Integer>>)((RBT<Integer>)bst)::RL_Rotation;
                        default -> (Consumer<RBTNode<Integer>>)(i -> {
                            log.log("Unknown rotation", -1);
                        });
                    }).accept(n);

                }
            }
            case "clear" -> {
                bst.root = null;
                log.log("Cleared tree", 1);
            }
            case "delete" -> {
                while(scan.hasNext()){
                    if(scan.hasNextInt()){
                        int val = scan.nextInt();
                        if(!bst.contains(val)){
                            log.log(val + " is not in the tree", 1);
                        }
                        else {
                            bst.remove(val);
                            log.log("Deleted " + val + " from the tree", 1);
                        }
                    }
                    else{
                        log.log("Bad input: \"" + scan.next() + "\" is not an int.", -1);
                    }
                }

                Main.printTree(bst);
            }
            case "help" -> {
                for(var line : new String[]{
                    "──── Commands ────",
                    "insert <values:number...> : Adds the values to the BST",
                    "    Example: \"insert 3 1 0 2 6 4 5 9 11\"",
                    "insertRand <low:number> <high:number> [amount:number] : inserts <amount> random numbers in the range [<low>, <high>]",
                    "    Example: \"insertRand 0 99 10\"",
                    "delete <values:number...> : Deletes the values from the BST",
                    "    Example: \"delete 1 2 3 4\"",
                    "style <style: 1|2|3> : Sets the drawing style of the tree",
                    "    Example: \"style 1\"",
                    "traverse <method: preOrder|postOrder|inOrder|reverseOrder|levelOrder> : Traverses the binary search tree using the provided method",
                    "    Example: \"traverse preOrder\"",
                    "query <attribute: numLeaves|numLevels|width|diameter|size|levelWidths|isFull|largest|smallest> : Gets the corresponding attribute of the bst",
                    "    Example: \"traverse preOrder\"",
                    "clear : Deletes the entire tree",
                    "help : Displays this list of commands",
                }) log.log(line, 1);
            }
            case "style" -> {
                int style;
                if(!scan.hasNextInt() || (style = scan.nextInt()) > styles.length || style <= 0)
                    log.log("Invalid parameter to style: Must be an int in the range [1, " + styles.length + "]", -1);
                else
                    currStyle = style - 1;
            }
            case "traverse" -> {
                try {
                    StringBuilder sb = new StringBuilder("[ ");
                    (switch (scan.next().toLowerCase()){
                        case "preorder" -> new Traversal.PreOrder<>(bst);
                        case "postorder" -> new Traversal.PostOrder<>(bst);
                        case "inorder" -> new Traversal.InOrder<>(bst);
                        case "reverseorder" -> new Traversal.ReverseOrder<>(bst);
                        case "levelorder" -> new Traversal.LevelOrder<>(bst);
                        default -> throw new Exception();
                    }).forEach(i -> sb.append(i).append(" "));
                    sb.append("]");
                    log.log(sb.toString(), 1);
                }
                catch (Exception e){
                    log.log("Invalid parameter to traverse: Must be an one of preorder, postorder, inorder, reverseOrder, levelOrder", -1);
                }
            }
            case "query" -> {
                if(scan.hasNext()) switch(scan.next().toLowerCase()) {
                    case "numleaves" -> log.log(bst.countLeaves() + "", 1);
                    case "numlevels" -> log.log(bst.countLevels() + "", 1);
                    case "width" -> log.log(bst.getWidth() + "", 1);
                    case "diameter" -> log.log(bst.getDiameter() + "", 1);
                    case "size" -> log.log(bst.size() + "", 1);
                    case "isfull" -> log.log("Bst is" + (bst.isFullTree() ? " " : " not ") + "full", 1);
                    case "largest" -> log.log(bst.getLargest() + "", 1);
                    case "smallest" -> log.log(bst.getSmallest() + "", 1);
                    case "levelwidths" -> log.log(Arrays.toString(bst.getLevelWidths()), 1);
                    default -> log.log("Invalid parameter to query: Must be one of numLeaves, numLevels, width, diameter, size, levelWidths, isFull, largest, smallest", -1);
                }
                else log.log("Invalid parameter to query: Must be one of numLeaves, numLevels, width, diameter, size, levelWidths, isFull, largest, smallest", -1);
            }
            default -> log.log("Unknown command \"" + command + "\". Type \"help\" to get a list of the commands", -1);
        }
    }

    @Override
    public void paint(Graphics g) {
        if(frame.getWidth() != getWidth() || frame.getHeight() != getHeight())
            frame = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics gg = frame.getGraphics();

        ((Graphics2D)gg).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ((Graphics2D)gg).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, getWidth(), getHeight());

        drawStuff((Graphics2D) gg);

        g.drawImage(frame, 0, 0, null);
    }

    private void drawStuff(Graphics2D graphics){
        styles[currStyle].drawTree(bst, getWidth(), graphics);

        long currentTime = System.currentTimeMillis();

        graphics.setColor(Color.BLACK);
        graphics.setFont(font);
        graphics.drawString(">>> " + input + (currentTime % BLINKER_TIME > BLINKER_TIME / 3 ? "█" : ""), 20, getHeight() - 20);

        log.forEach((int i, String message, float t, int status) -> {
            double opacity = 1 - Math.pow(t, 4);
            Color color = status == -1 ? Color.RED : status == 1 ? Color.BLUE : Color.BLACK;
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(Math.min((int)(opacity * 255), 255), 0)));
            graphics.drawString(message, 20, getHeight() - 20 - (font.getSize() * 4 / 3) * (i + 1));
        });
    }
}
