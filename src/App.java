import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Scanner;

public class App extends JFrame {
    private static final long LOG_FADE_TIME = 8000;
    private static final long BLINKER_TIME = 800;

    private final BST<Integer> bst = new BST<>();
    private String input = "";
    private final Log log = new Log(LOG_FADE_TIME, 10);

    private static final Font font = FontLoader.load("JBMono.ttf").deriveFont(12f);

    BufferedImage frame = new BufferedImage(1000, 8000, BufferedImage.TYPE_INT_RGB);

    public App(){
        setTitle("Graphics BST Lab");
        setSize(new Dimension(1000, 800));
        setResizable(false);
        setVisible(true);
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
                else{
                    char c = e.getKeyChar();
                    if((c + "").matches("^[ -~]$")) input += c;
                }
            }
        });

        new Timer(10, e -> repaint()).start();

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
        switch(command = scan.next()){
            case "insert" -> {
                while(scan.hasNext()){
                    if(scan.hasNextInt()){
                        int val = scan.nextInt();
                        if(bst.has(val)){
                            log.log(val + " is already in the tree", 1);
                        }
                        else {
                            bst.insert(val);
                            log.log("Added " + val + " to the tree", 1);
                        }
                    }
                    else{
                        log.log("Bad input: \"" + scan.next() + "\" is not an int.", -1);
                    }
                }

                Main.printTree(bst);
            }
            case "delete" -> {
                while(scan.hasNext()){
                    if(scan.hasNextInt()){
                        int val = scan.nextInt();
                        if(!bst.has(val)){
                            log.log(val + " is not in the tree", 1);
                        }
                        else {
                            bst.erase(val);
                            log.log("Deleted " + val + " from the tree", 1);
                        }
                    }
                    else{
                        log.log("Bad input: \"" + scan.next() + "\" is not an int.", -1);
                    }
                }

                Main.printTree(bst);
            }
            case "Hi", "Hello", "hi", "hello" -> {
                log.log("Hi there!");
            }
            case "help" -> {
                for(var line : new String[]{
                    "──── Commands ────",
                    "insert [<number>...] : Adds the values to the BST",
                    "    Example: \"insert 3 1 0 2 6 4 5 9 11\"",
                    "delete [<number>...] : Deletes the values from the BST",
                    "    Example: \"delete 1 2 3 4\"",
                    "help : Displays this list of commands",
                }) log.log(line, 1);
            }
            default -> log.log("Unknown command \"" + command + "\". Type \"help\" to get a list of the commands", -1);
        }
    }

    @Override
    public void paint(Graphics g) {

        Graphics gg = frame.getGraphics();

        ((Graphics2D)gg).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        ((Graphics2D)gg).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        gg.setColor(Color.WHITE);
        gg.fillRect(0, 0, 1000, 800);

        drawStuff((Graphics2D) gg);

        g.drawImage(frame, 0, 0, null);
    }

    private void drawStuff(Graphics2D graphics){
        long currentTime = System.currentTimeMillis();

        graphics.setColor(Color.BLACK);
        graphics.setFont(font);
        graphics.drawString(">>> " + input + (currentTime % BLINKER_TIME > BLINKER_TIME / 3 ? "█" : ""), 20, 780);

        log.forEach((int i, String message, float t, int status) -> {
            float opacity = (1 - t) * (1 - t);
            Color color = status == -1 ? Color.RED : status == 1 ? Color.BLUE : Color.BLACK;
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(Math.min((int)(opacity * 255), 255), 0)));
            graphics.drawString(message, 20, 780 - (font.getSize() * 4 / 3) * (i + 1));
        });

        drawTree(graphics);
    }

    public Dimension getRenderedSize(String text) {
        Rectangle2D size = font.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
        return new Dimension((int) size.getWidth(), font.getSize());
    }

    private void drawTree(Graphics2D graphics){
        if(bst == null || bst.getRoot() == null) return;

        final int height = bst.getHeight();
        final BST.BSTNode<Integer>[][] levels = new BST.BSTNode[height][];
        levels[0] = new BST.BSTNode[]{bst.getRoot()};
        for(int h = 1; h < height; h++){
            int size = 1 << h;
            levels[h] = new BST.BSTNode[size];
            for(int i = 0; i * 2 < size; i++){
                var n = levels[h - 1][i];
                if(n != null){
                    levels[h][i * 2] = n.getLeft();
                    levels[h][i * 2 + 1] = n.getRight();
                }
            }
        }

        final int[][] widths = new int[height][];
        for(int r = height - 1; r >= 0; r--){
            widths[r] = new int[levels[r].length];

            for(int i = 0; i < levels[r].length; i++){
                if(levels[r][i] == null){
                    if(levels[r][i ^ 1] != null){
                        for(int rr = r, ii = i; rr < height; rr++, ii *= 2) {
                            widths[rr][ii] = 20;    // 60px of nothing
                        }
                    }
                }
                else{
                    // Calculate width thing
                    int w = getRenderedSize(levels[r][i].value.toString()).width + 20;  //20px is the padding

                    int width = r == height - 1 ? w : Math.max(w, widths[r + 1][i * 2] + widths[r + 1][i * 2 + 1]);

                    widths[r][i] = width;

                    if(!levels[r][i].hasRight() && !levels[r][i].hasLeft()){
                        for(int rr = r, ii = i; rr < height; rr++, ii *= 2) {
                            widths[rr][ii] = width;
                        }
                    }
                }
            }
        }

        final int[][] x = new int[height][], y = new int[height][];
        for(int r = 0; r < height; r++){
            int sum = 0;
            x[r] = new int[widths[r].length];
            y[r] = new int[widths[r].length];
            for(int i = 0; i < widths[r].length; i++){
                x[r][i] = sum + widths[r][i] / 2 + getWidth() / 2 - widths[0][0];
                y[r][i] = r * 30 + 50;
                sum += widths[r][i];
            }
        }

        for(int r = height - 1; r >= 0; r--){
            for(int i = 0; i < levels[r].length; i++){
                var node = levels[r][i];

                if(node != null) {
                    String text = node.value.toString();
                    var d = getRenderedSize(text);
                    int X = x[r][i], Y = y[r][i];

                    graphics.setColor(Color.BLACK);
                    if(node.getParent() != null){
                        graphics.drawLine(X, Y, x[r - 1][i / 2], y[r - 1][i / 2]);
                    }

                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(X - d.width / 2 - 5, Y - d.height / 2 - 5, d.width + 10, d.height + 10);

                    graphics.setColor(Color.BLACK);
                    graphics.drawString(text, X - d.width / 2, Y + d.height / 2);
                    graphics.drawRect(X - d.width / 2 - 5, Y - d.height / 2 - 5, d.width + 10, d.height + 10);
                }
            }
        }
    }
}
