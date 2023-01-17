import java.util.*;

public abstract class Traversal<T extends Comparable<T>> {
    protected Traversal() { }

    public abstract Iterable<T> traverse(BST<T> tree);

    public static class PreOrder<T extends Comparable<T>> extends Traversal<T> {
        public Iterable<T> traverse(BST<T> tree) {
            return () -> tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Stack<BSTNode<T>> nodes;

                {
                    var res = new Stack<BSTNode<T>>();
                    res.push(tree.getRoot());
                    nodes = res;
                }

                @Override
                public boolean hasNext() {
                    return !nodes.isEmpty();
                }

                public T next() {
                    var res = nodes.pop();
                    if (res.hasRight()) nodes.push(res.getRight());
                    if (res.hasLeft()) nodes.push(res.getLeft());
                    return res.value;
                }
            };
        }
    }

    public static class PostOrder<T extends Comparable<T>> extends Traversal<T> {
        public Iterable<T> traverse(BST<T> tree) {
            return () -> tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Stack<BSTNode<T>> nodes = new Stack<>();
                private BSTNode<T> curr = tree.getRoot();

                @Override
                public boolean hasNext() {
                    while (curr != null) {
                        nodes.push(curr);
                        if (curr.hasLeft())
                            curr = curr.getLeft();
                        else
                            curr = curr.getRight();
                    }
                    return !nodes.empty();
                }

                public T next() {
                    var node = nodes.pop();
                    if (node.getChildType() == BSTNode.ChildType.LEFT) {
                        curr = node.getParent().getRight();
                    }
                    return node.value;
                }
            };
        }
    }

    public static class InOrder<T extends Comparable<T>> extends Traversal<T> {

        @Override
        public Iterable<T> traverse(BST<T> tree) {
            return () -> tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Stack<BSTNode<T>> nodes = new Stack<>();
                private BSTNode<T> curr = tree.getRoot();

                @Override
                public boolean hasNext() {
                    while (curr != null) {
                        nodes.push(curr);
                        curr = curr.getLeft();
                    }
                    return !nodes.isEmpty();
                }

                public T next() {
                    var res = nodes.pop();
                    curr = res.getRight();
                    return res.value;
                }
            };
        }
    }

    public static class ReverseOrder<T extends Comparable<T>> extends Traversal<T> {

        @Override
        public Iterable<T> traverse(BST<T> tree) {
            return () -> tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Stack<BSTNode<T>> nodes = new Stack<>();
                private BSTNode<T> curr = tree.getRoot();

                @Override
                public boolean hasNext() {
                    while (curr != null) {
                        nodes.push(curr);
                        curr = curr.getRight();
                    }
                    return !nodes.isEmpty();
                }

                public T next() {
                    var res = nodes.pop();
                    curr = res.getLeft();
                    return res.value;
                }
            };
        }
    }

    public static class LevelOrder<T extends Comparable<T>> extends Traversal<T> {

        @Override
        public Iterable<T> traverse(BST<T> tree) {
            return () -> tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Queue<BSTNode<T>> nodes = new LinkedList<>(Collections.singleton(tree.getRoot()));

                @Override
                public boolean hasNext() {
                    return !nodes.isEmpty();
                }

                @Override
                public T next() {
                    var node = nodes.remove();
                    if (node.hasLeft())
                        nodes.add(node.getLeft());
                    if (node.hasRight())
                        nodes.add(node.getRight());
                    return node.value;
                }
            };
        }
    }
}

