import java.util.*;

public abstract class Traversal<T extends Comparable<T>> implements Iterable<T> {
    protected final BST<T> tree;

    protected Traversal(BST<T> tree) {
        this.tree = tree;
    }

    public static class PreOrder<T extends Comparable<T>> extends Traversal<T> {
        public PreOrder(BST<T> tree) {
            super(tree);
        }

        public Iterator<T> iterator() {
            return tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
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
                    if (res.hasRightChild()) nodes.push(res.getRightChild());
                    if (res.hasLeftChild()) nodes.push(res.getLeftChild());
                    return res.getValue();
                }
            };
        }
    }

    public static class PostOrder<T extends Comparable<T>> extends Traversal<T> {
        public PostOrder(BST<T> tree) {
            super(tree);
        }

        public Iterator<T> iterator() {
            return tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Stack<BSTNode<T>> nodes = new Stack<>();
                private BSTNode<T> curr = tree.getRoot();

                @Override
                public boolean hasNext() {
                    while (curr != null) {
                        nodes.push(curr);
                        if (curr.hasLeftChild())
                            curr = curr.getLeftChild();
                        else
                            curr = curr.getRightChild();
                    }
                    return !nodes.empty();
                }

                public T next() {
                    var node = nodes.pop();
                    if (node.getChildType() == BSTNode.ChildType.LEFT) {
                        curr = node.getParent().getRightChild();
                    }
                    return node.getValue();
                }
            };
        }
    }

    public static class InOrder<T extends Comparable<T>> extends Traversal<T> {

        public InOrder(BST<T> tree) {
            super(tree);
        }

        public Iterator<T> iterator() {
            return tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Stack<BSTNode<T>> nodes = new Stack<>();
                private BSTNode<T> curr = tree.getRoot();

                @Override
                public boolean hasNext() {
                    while (curr != null) {
                        nodes.push(curr);
                        curr = curr.getLeftChild();
                    }
                    return !nodes.isEmpty();
                }

                public T next() {
                    var res = nodes.pop();
                    curr = res.getRightChild();
                    return res.value;
                }
            };
        }
    }

    public static class ReverseOrder<T extends Comparable<T>> extends Traversal<T> {

        public ReverseOrder(BST<T> tree) {
            super(tree);
        }

        public Iterator<T> iterator() {
            return tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Stack<BSTNode<T>> nodes = new Stack<>();
                private BSTNode<T> curr = tree.getRoot();

                @Override
                public boolean hasNext() {
                    while (curr != null) {
                        nodes.push(curr);
                        curr = curr.getRightChild();
                    }
                    return !nodes.isEmpty();
                }

                public T next() {
                    var res = nodes.pop();
                    curr = res.getLeftChild();
                    return res.value;
                }
            };
        }
    }

    public static class LevelOrder<T extends Comparable<T>> extends Traversal<T> {

        public LevelOrder(BST<T> tree) {
            super(tree);
        }

        public Iterator<T> iterator() {
            return tree.getRoot() == null ? Collections.emptyIterator() : new Iterator<>() {
                private final Queue<BSTNode<T>> nodes = new LinkedList<>(Collections.singleton(tree.getRoot()));

                @Override
                public boolean hasNext() {
                    return !nodes.isEmpty();
                }

                @Override
                public T next() {
                    var node = nodes.remove();
                    if (node.hasLeftChild())
                        nodes.add(node.getLeftChild());
                    if (node.hasRightChild())
                        nodes.add(node.getRightChild());
                    return node.value;
                }
            };
        }
    }
}