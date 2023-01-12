import java.util.Scanner;

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
    }

    private static void printCentered(Object x, int w){
        String s = x.toString();
        int length = s.length();
        int l = (w - length) / 2;
        int r = w - l - length;
        System.out.print(" ".repeat(l) + "\u001B[1m" + x + "\u001B[0m" + " ".repeat(r));
    }
}