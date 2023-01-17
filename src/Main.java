public class Main {
    public static void main(String[] args){
        new App();
    }

    @SuppressWarnings("rawtypes")
    public static void printTree(BST bst){
        if(bst == null || bst.getRoot() == null){
            System.out.println("Empty Tree");
            return;
        }

        final int height = bst.getHeight();
        final BSTNode<Integer>[][] levels = new BSTNode[height][];
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
                    System.out.print("[");
                    System.out.print("-".repeat(w - 3));
                    System.out.print("] ");
                }
                else {
                    System.out.print("[");
                    printCentered(val.value, w - 3);
                    System.out.print("] ");
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
        System.out.print(" ".repeat(l) + "\u001B[1m" + x + "\u001B[0m" + " ".repeat(r));
    }
}