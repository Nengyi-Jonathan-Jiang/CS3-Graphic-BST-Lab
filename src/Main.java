import java.util.Arrays;

public class Main {
    static boolean useColor = false;

    static class ANSI_CODES {
        static ANSI_CODES
            CLEAR = new ANSI_CODES(),
            BLACK = new ANSI_CODES(30),
            RED = new ANSI_CODES(31),
            GREEN = new ANSI_CODES(32),
            YELLOW = new ANSI_CODES(33),
            BLUE = new ANSI_CODES(34),
            PURPLE = new ANSI_CODES(35),
            CYAN = new ANSI_CODES(36),
            WHITE = new ANSI_CODES(37),
            BOLD = new ANSI_CODES(1, 0),
            UNDERLINE = new ANSI_CODES(4, 0);

        private final int c, d;
        ANSI_CODES(){
            this(0, 0);
        }
        ANSI_CODES(int c){
            this(0, c);
        }
        ANSI_CODES(int d, int c){
            this.d = d;
            this.c = c;
        }

        @Override
        public String toString() {
            return useColor ? "\u001B[" + (c == 0 ? d : d + ";" + c) + "m" : "";
        }

        public ANSI_CODES and(ANSI_CODES o){
            return new ANSI_CODES(d == 0 ? o.d : d, c == 0 ? o.c : c);
        }
    }

    public static void main(String[] args){
        var largs = Arrays.asList(args);
        //if(largs.contains("--color"))
            useColor = true;
        new App(
//                true
                largs.contains("--rb")
        );
        new App(true);
    }

    public static <T extends Comparable<T>> void printTree(BST<T> bst){
        if(bst == null || bst.getRoot() == null){
            System.out.println("Empty Tree");
            return;
        }

        final int height = bst.countLevels();
        final BSTNode<T>[][] levels = new BSTNode[height][];
        levels[0] = new BSTNode[]{bst.getRoot()};
        for(int h = 1; h < height; h++){
            int size = 1 << h;
            levels[h] = new BSTNode[size];
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
                            widths[rr][ii] = 4;
                        }
                    }
                }
                else{
                    int w = levels[r][i].value.toString().length() + 3;
                    int width = r == height - 1
                            ? w
                            : Math.max(w, widths[r + 1][i * 2] + widths[r + 1][i * 2 + 1]);
                    widths[r][i] = width;

                    if(!levels[r][i].hasRight() && !levels[r][i].hasLeft()){
                        for(int rr = r, ii = i; rr < height; rr++, ii *= 2) {
                            widths[rr][ii] = width;
                        }
                    }
                }
            }
        }

        for(int r = 0; r < height; r++){
            for(int i = 0; i < levels[r].length; i++){
                var val = levels[r][i];
                int w = widths[r][i];

                if(w == 0) continue;

                if(val == null) {
                    System.out.print(ANSI_CODES.WHITE + "[");
                    System.out.print("-".repeat(w - 3));
                    System.out.print("] " + ANSI_CODES.CLEAR);
                }
                else {
                    System.out.print("[");
                    if(val instanceof RBTNode)
                        System.out.print(RBTNode.isRed((RBTNode<T>) val)
                            ? ANSI_CODES.RED
                            : ANSI_CODES.PURPLE
                        );
                    System.out.print(ANSI_CODES.BOLD);
                    printCentered(val.value, w - 3);
                    System.out.print(ANSI_CODES.CLEAR + "] ");
                }
            }
            System.out.println();
        }
    }

    private static void printCentered(Object x, int w){
        String s = x.toString();
        int length = s.length();
        int l = (w - length) / 2;
        int r = w - l - length;
        System.out.print(" ".repeat(l) + x + " ".repeat(r));
    }
}