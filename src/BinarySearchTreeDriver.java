import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BinarySearchTreeDriver {
    public static void main(String[] args) {
        Scanner scan = new Scanner("""
                4
                us and them
                after all we're only ordinary men
                me you
                god knows it's not what
                2
                them
                every
                4
                us
                them
                yesterday
                only""");

        BST<String> tree = new BST<>();
        int N = scan.nextInt();
        scan.nextLine();
        while(N --> 0) tree.addAll(List.of(scan.nextLine().split(" ")));

        printTree(tree);

        System.out.println("Tree-->" + String.join(" ", tree) + " ");

        System.out.println("PRE ORDER");
        System.out.println(String.join(" ", new Traversal.PreOrder<>(tree)) + " ");
        System.out.println("POST ORDER");
        System.out.println(String.join(" ", new Traversal.PostOrder<>(tree)) + " ");
        System.out.println("IN ORDER");
        System.out.println(String.join(" ", new Traversal.InOrder<>(tree)) + " ");
        System.out.println("REVERSE ORDER");
        System.out.println(String.join(" ", new Traversal.ReverseOrder<>(tree)) + " ");
        System.out.println("LEVEL ORDER");
        System.out.println("[" + String.join(", ", new Traversal.LevelOrder<>(tree)) + "]");

        System.out.println("Number of leaves: " + tree.countLeaves());
        System.out.println("Number of levels: " + tree.countLevels());
        System.out.println("The Tree width: " + tree.getWidth());
        System.out.println("The Tree height: " + tree.getHeight());
        System.out.println("The Tree diameter: " + tree.getDiameter());
        System.out.println("Number of nodes: " + tree.size());

        printTreeWidths(tree);

        System.out.println(tree.isFullTree() ? "Tree is full." : "Tree is not full.");

        N = scan.nextInt();
        while (N --> 0){
            String s = scan.next();
            System.out.println("Tree " + (tree.contains(s) ? "contains " : "does not contain ") + s);
        }

        System.out.println("Largest value: " + tree.getLargest());
        System.out.println("Smallest value: " + tree.getSmallest());

        N = scan.nextInt();
        while (N --> 0) tree.remove(scan.next());

        printTree(tree);
    }

    private static void printTreeWidths(BST<?> tree){
        var w = tree.getLevelWidths();
        for(int i = 0; i < w.length; i++){
            System.out.println("width at level " + i + " " + w[i]);
        }
    }

    private static <T extends Comparable<T>> void printTree(BST<T> tree) {
        System.out.println("Proper Tree Display");

        var copy = tree.getLevels();

        Comparable<T>[][] levels = new Comparable[6][];
        for(int h = 0; h < 6; h++){
            levels[h] = h < copy.length ? copy[h] : new Comparable[1 << h];
        }

        Arrays.stream(levels).forEach(row -> System.out.println(Arrays.stream(row).map(i -> i == null ? "--" : i.toString()).collect(Collectors.joining("|"))));
    }
}
