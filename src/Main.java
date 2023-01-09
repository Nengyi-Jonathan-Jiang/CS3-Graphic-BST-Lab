import bst.BST;

import java.util.Scanner;

import static bst.BST.BSTNode;

public class Main {
    public static void main(String[] args){
        BST<Integer> tree = new BST<>();
        for(int el : new int[]{7, 9, 3, 4, 8, 2, 1, 5, 6}) tree.insert(el);
        printTree(tree);

        Scanner scan = new Scanner(System.in);
        String line;
        System.out.print(">>> ");
        while((line = scan.nextLine()).length() > 0){
            Scanner l = new Scanner(line);
            String op = l.next();
            switch (op) {
                case "print" -> printTree(tree);
                case "add" -> {
                    do {
                        int num = l.nextInt();
                        tree.insert(num);
                    } while(l.hasNextInt());
                }
                case "del" -> {
                    do {
                        int num = l.nextInt();
                        tree.erase(num);
                    } while(l.hasNextInt());
                }
                case "quit" -> {
                    return;
                }
                case "help" -> {
                    System.out.println(
                            """
                                    \u001B[4mAvailable commands:\u001B[0m
                                        \u001B[0;32madd\u001B[0m [\u001B[0;35mnumber\u001B[0m, ]: Insert the values into the tree
                                        \u001B[0;32mdel\u001B[0m [\u001B[0;35mnumber\u001B[0m, ]: Removes the values from the tree
                                        \u001B[0;32mprint\u001B[0m: Prints the current state of the tree
                                        \u001B[0;32mquit\u001B[0m: Exits the program"""
                    );
                }
                default -> System.out.println("\u001B[31mUnknown command. Type \"help\" to see a list of the commands\u001B[0m");
            }
            System.out.print(">>> ");
        }
    }

    @SuppressWarnings("rawtypes")
    private static void printTree(BST bst){
        if(bst == null){
            System.out.println("Empty Tree");
            return;
        }

        final int height = bst.getHeight();
        final BSTNode[][] levels = new BSTNode[height][];
        levels[0] = new BSTNode[]{bst.getRoot()};
        for(int h = 1; h < height; h++){
            int size = 1 << h;
            levels[h] = new BSTNode[size];
            for(int i = 0; i * 2 < size; i++){
                BSTNode n = levels[h - 1][i];
                if(n != null){
                    levels[h][i * 2] = n.getLeft();
                    levels[h][i * 2 + 1] = n.getRight();
                }
            }
        }

        for(int i = height; i --> 0; ){
            var row = levels[height - i - 1];

            if(i != height - 1){
                System.out.print(" ".repeat((2 << i) + 3));
                for(int j = 0; j < row.length; j += 2){
                    System.out.print((row[j] != null ? "┌" + "─".repeat((2 << i) - 1) : " ".repeat(2 << i)));
                    System.out.print((row[j] != null && row[j + 1] != null ? "┴" : row[j] != null ? "┘" : row[j + 1] != null ? "└" : " "));
                    System.out.print((row[j + 1] != null ? "─".repeat((2 << i) - 1) + "┐" : " ".repeat(2 << i)));
                    System.out.print(" ".repeat((4 << i) - 1));
                }
                System.out.println();
            }

            System.out.print(" ".repeat(2 << i));
            for(var val : row) {
                if(val == null) System.out.print("\u001B[37m nil\u001B[0m");
                else printVal(val.value);

                System.out.print("    ".repeat((1 << i) - 1));
            }
            System.out.println();
        }
    }

    private static void printVal(Object x){
        String s = x.toString();
        System.out.print(" ".repeat(4 - s.length()) + "\u001B[1m" + x + "\u001B[0m");
    }
}