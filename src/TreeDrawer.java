import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Stack;

public abstract class TreeDrawer {

    public static Font font = FontLoader.load("JBMono.ttf").deriveFont(12f);
    public static int getNodePadding(){
        return font.getSize() / 2;
    }

    protected static Dimension getRenderedSize(String text) {
        Rectangle2D size = font.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
        return new Dimension((int) size.getWidth(), (int) size.getHeight());
    }

    protected abstract <T extends Comparable<T>>  int[][] calculatePositions(BSTNode<T>[][] levels, int height, int windowWidth, Graphics2D graphics);

    public final <T extends Comparable<T>> void drawTree(BST<T> bst, int windowWidth, Graphics2D graphics){
        if(bst == null || bst.getRoot() == null) return;

        final int height = bst.countLevels();
        final BSTNode<T>[][] levels = new BSTNode[height][];
        levels[0] = new BSTNode[]{bst.getRoot()};

        for(int h = 1; h < height; h++){
            int size = 1 << h;
            levels[h] = new BSTNode[size];
            for(int i = 0; i * 2 < size; i++){
                var n = levels[h - 1][i];
                if(n != null){
                    levels[h][i * 2] = n.getLeftChild();
                    levels[h][i * 2 + 1] = n.getRightChild();
                }
            }
        }

        int[][] x = calculatePositions(levels, height, windowWidth, graphics);
        int[][] y = new int[height][];
        for(int h = 0; h < height; h++){
            y[h] = new int[1 << h];
            Arrays.fill(y[h],
                    (h * 6 + 1) * getNodePadding() + 40 + getRenderedSize("|").height / 2
            );
        }

        graphics.setFont(font);

        for(int h = height - 1; h >= 0; h--){
            for(int i = 0; i < levels[h].length; i++){
                var node = levels[h][i];

                if(node != null) {
                    String text = node.value.toString();
                    var d = getRenderedSize(text);
                    int X = x[h][i], Y = y[h][i];

                    var fm = graphics.getFontMetrics();

                    graphics.setColor(node instanceof RBTNode ? Color.BLUE : Color.BLACK);
                    if(node.getParent() != null && h > 0){
                        graphics.drawLine(X, Y, x[h - 1][i / 2], y[h - 1][i / 2]);
                    }

                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(X - d.width / 2 - getNodePadding(), Y - d.height / 2 - getNodePadding(), d.width + getNodePadding() * 2, d.height + getNodePadding() * 2);

                    graphics.setColor(node instanceof RBTNode && RBTNode.getColor((RBTNode<T>) node) == RBTNode.Color.RED ? Color.RED : Color.BLACK);
                    graphics.drawRect(X - d.width / 2 - getNodePadding(), Y - d.height / 2 - getNodePadding(), d.width + getNodePadding() * 2, d.height + getNodePadding() * 2);
                    if(node.value instanceof NumberOrString && ((NumberOrString) node.value).isString()){
                        graphics.setColor(Color.GREEN.darker());
                    }
                    graphics.drawString(text, X - d.width / 2, Y - fm.getHeight() / 2 + fm.getAscent());
                }
            }
        }
    }
}

class TreeDrawerOffset extends TreeDrawer {

    @Override
    public <T extends Comparable<T>>  int[][] calculatePositions(BSTNode<T>[][] levels, int height, int windowWidth, Graphics2D graphics) {
        final int[][] widths = new int[height][];
        for(int r = height - 1; r >= 0; r--){
            widths[r] = new int[levels[r].length];

            for(int i = 0; i < levels[r].length; i++){
                if(levels[r][i] == null){
                    if(levels[r][i ^ 1] != null){
                        for(int rr = r, ii = i; rr < height; rr++, ii *= 2) {
                            widths[rr][ii] = getNodePadding() * 4;
                        }
                    }
                }
                else{
                    // Calculate width thing
                    int w = getRenderedSize(levels[r][i].value.toString()).width + getNodePadding() * 4;

                    int width = r == height - 1 ? w : Math.max(w, widths[r + 1][i * 2] + widths[r + 1][i * 2 + 1]);

                    widths[r][i] = width;

                    if(!levels[r][i].hasRightChild() && !levels[r][i].hasLeftChild()){
                        for(int rr = r, ii = i; rr < height; rr++, ii *= 2) {
                            widths[rr][ii] = width;
                        }
                    }
                }
            }
        }

        final int[][] x = new int[height][];
        for(int r = 0; r < height; r++){
            int sum = 0;
            x[r] = new int[widths[r].length];
            for(int i = 0; i < widths[r].length; i++){
                x[r][i] = sum + widths[r][i] / 2 + windowWidth / 2 - widths[0][0] / 2;
                sum += widths[r][i];
            }
        }

        return x;
    }
}

class TreeDrawerStacked extends TreeDrawer {

    @Override
    public <T extends Comparable<T>>  int[][] calculatePositions(BSTNode<T>[][] levels, int height, int windowWidth, Graphics2D graphics) {
        final int LR_OFFSET = getNodePadding() * 2;

        final int[][] widths = new int[height][];
        final int[][] pos = new int[height][];
        final int[][] lPad = new int[height][], rPad = new int[height][];

        for(int h = height - 1; h >= 0; h--) {
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
                    int w = getRenderedSize(levels[h][i].value.toString()).width + getNodePadding() * 4;

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
                    int l = 0, r = 0;
                    if (p - w / 2 < 0) {  // node text extends beyond left edge
                        l = w / 2 - p;
                    } else if (p + w - w / 2 > totalChildWidth) {  // node text extends beyond right edge
                        r = p + w - w / 2 - totalChildWidth;
                    }

                    lPad[h][i] = l; // left padding
                    rPad[h][i] = r; // right padding
                    pos[h][i] = p + l;  // add left padding to distance from left edge
                    widths[h][i] = totalChildWidth + l + r; // width is width of children + padding
                }
            }
        }
        final int[][] x = new int[height][], y = new int[height][];
        for(int h = 0; h < height; h++){
            int left = 0;
            x[h] = new int[widths[h].length];
            y[h] = new int[widths[h].length];
            Arrays.fill(y[h], h * 30 + 50);
            for(int i = 0; i < widths[h].length; i++){
                for(int hh = h, ii = i; hh > 0;){
                    if(ii % 2 == 0) {
                        left += lPad[--hh][ii /= 2];
                    }
                    else {
                        break;
                    }
                }

                x[h][i] = left + pos[h][i] + windowWidth / 2 - widths[0][0] / 2;
                left += widths[h][i];

                for(int hh = h, ii = i; hh > 0;){
                    if(ii % 2 == 1) {
                        left += rPad[--hh][ii /= 2];
                    }
                    else {
                        break;
                    }
                }
            }
        }
        return x;
    }
}

class TreeDrawerStackedCentered extends TreeDrawer {

    @Override
    public <T extends Comparable<T>>  int[][] calculatePositions(BSTNode<T>[][] levels, int height, int windowWidth, Graphics2D graphics) {
        final int LR_OFFSET = getNodePadding() * 2;

        final int[][] widths = new int[height][];
        final int[][] pos = new int[height][];
        final int[][] lPad = new int[height][], rPad = new int[height][];

        for(int h = height - 1; h >= 0; h--) {
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
                    int w = getRenderedSize(levels[h][i].value.toString()).width + getNodePadding() * 4;

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
                        p = (pos[h + 1][i * 2] + pos[h + 1][i * 2 + 1] + widths[h + 1][i * 2]) / 2;
                    }

                    int totalChildWidth = widths[h + 1][i * 2] + widths[h + 1][i * 2 + 1];

                    // Calculate paddings
                    int l = 0, r = 0;
                    if (p - w / 2 < 0) {  // node text extends beyond left edge
                        l = w / 2 - p;
                    } else if (p + w - w / 2 > totalChildWidth) {  // node text extends beyond right edge
                        r = p + w - w / 2 - totalChildWidth;
                    }

                    lPad[h][i] = l; // left padding
                    rPad[h][i] = r; // right padding
                    pos[h][i] = p + l;  // add left padding to distance from left edge
                    widths[h][i] = totalChildWidth + l + r; // width is width of children + padding
                }
            }
        }
        final int[][] x = new int[height][], y = new int[height][];
        for(int h = 0; h < height; h++){
            int left = 0;
            x[h] = new int[widths[h].length];
            y[h] = new int[widths[h].length];
            Arrays.fill(y[h], h * 30 + 50);
            for(int i = 0; i < widths[h].length; i++){
                for(int hh = h, ii = i; hh > 0;){
                    if(ii % 2 == 0) {
                        left += lPad[--hh][ii /= 2];
                    }
                    else {
                        break;
                    }
                }

                x[h][i] = left + pos[h][i] + windowWidth / 2 - widths[0][0] / 2;
                left += widths[h][i];

                for(int hh = h, ii = i; hh > 0;){
                    if(ii % 2 == 1) {
                        left += rPad[--hh][ii /= 2];
                    }
                    else {
                        break;
                    }
                }
            }
        }
        return x;
    }
}

class TreeDrawerInOrder extends TreeDrawer {

    @Override
    public <T extends Comparable<T>> int[][] calculatePositions(BSTNode<T>[][] levels, int height, int windowWidth, Graphics2D graphics) {
        // Inorder traversal

        int[][] x = new int[height][];
        for(int h = 0; h < height; h++) x[h] = new int[1 << h];
        int left = 0;

        Stack<BSTNode<T>> nStk = new Stack<>();
        Stack<Integer> iStk = new Stack<>(), hStk = new Stack<>();
        var currN = levels[0][0];
        int currI = 0;
        int currH = 0;
        while(!nStk.isEmpty() || currN != null){
            if(currN != null) {
                nStk.push(currN);
                iStk.push(currI);
                hStk.push(currH);
                currN = currN.getLeftChild();
                currI *= 2;
                currH++;
            }
            else {
                var node = nStk.pop();
                int i = iStk.pop();
                int h = hStk.pop();

                int w = getRenderedSize(node.value.toString()).width + getNodePadding();
                x[h][i] = left + w / 2;
                left += w;

                currN = node.getRightChild();
                currI = i * 2 + 1;
                currH = h + 1;
            }
        }

        for(int h = 0; h < height; h++){
            for(int i = 0; i < x[h].length; i++){
                x[h][i] += windowWidth/2 - left / 2;
            }
        }
        return x;
    }
}