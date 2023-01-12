import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

public abstract class TreeDrawer {

    protected static final Font font = FontLoader.load("JBMono.ttf").deriveFont(12f);

    protected static Dimension getRenderedSize(String text) {
        Rectangle2D size = font.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true));
        return new Dimension((int) size.getWidth(), font.getSize());
    }

    public abstract void drawTree(BST<Integer> bst, int windowWidth, Graphics2D graphics);
}

class TreeDrawer1 extends TreeDrawer {

    @Override
    public void drawTree(BST<Integer> bst, int windowWidth, Graphics2D graphics) {
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
                            widths[rr][ii] = 20;
                        }
                    }
                }
                else{
                    // Calculate width thing
                    int w = getRenderedSize(levels[r][i].value.toString()).width + 20;

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
                x[r][i] = sum + widths[r][i] / 2 + windowWidth / 2 - widths[0][0] / 2;
                y[r][i] = r * 30 + 50;
                sum += widths[r][i];
            }
        }

        graphics.setFont(font);

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

class TreeDrawer2 extends TreeDrawer {

    @Override
    public void drawTree(BST<Integer> bst, int windowWidth, Graphics2D graphics) {
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
                    var left = node.getLeft();
                    var right = node.getRight();

                    // Raw width of node (rect)
                    int w = getRenderedSize(levels[h][i].value.toString()).width + 20;

                    // degree 0: node width is text width, node pos is centered, no padding
                    if (left == null && right == null) {
                        pos[h][i] = w / 2;
                        widths[h][i] = w;
                        lPad[h][i] = w;
                        continue;
                    }

                    int p;
                    final int LR_OFFSET = 15;
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

        for(int h = height - 1; h >= 0; h--){
            for(int i = 0; i < levels[h].length; i++){
                var node = levels[h][i];

                if(node != null) {
                    String text = node.value.toString();
                    var d = getRenderedSize(text);
                    int X = x[h][i], Y = y[h][i];

                    graphics.setColor(Color.BLACK);
                    if(node.getParent() != null){
                        graphics.drawLine(X, Y, x[h - 1][i / 2], y[h - 1][i / 2]);
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

class TreeDrawer3 extends TreeDrawer {

    @Override
    public void drawTree(BST<Integer> bst, int windowWidth, Graphics2D graphics) {
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
                    var left = node.getLeft();
                    var right = node.getRight();

                    // Raw width of node (rect)
                    int w = getRenderedSize(levels[h][i].value.toString()).width + 20;

                    // degree 0: node width is text width, node pos is centered, no padding
                    if (left == null && right == null) {
                        pos[h][i] = w / 2;
                        widths[h][i] = w;
                        lPad[h][i] = w;
                        continue;
                    }

                    int p;
                    final int LR_OFFSET = 15;
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

        for(int h = height - 1; h >= 0; h--){
            for(int i = 0; i < levels[h].length; i++){
                var node = levels[h][i];

                if(node != null) {
                    String text = node.value.toString();
                    var d = getRenderedSize(text);
                    int X = x[h][i], Y = y[h][i];

                    graphics.setColor(Color.BLACK);
                    if(node.getParent() != null){
                        graphics.drawLine(X, Y, x[h - 1][i / 2], y[h - 1][i / 2]);
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