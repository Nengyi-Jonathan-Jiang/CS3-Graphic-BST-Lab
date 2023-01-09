package bst;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

public class BST<T extends Comparable<T>> implements Iterable<T> {
    BSTNode<T> root = null;

    public BST(){ }

    public boolean has(T value){
        return find(value) != null;
    }

    public void insert(T value) {
        if (root == null)
            root = new BSTNode<>(value);
        else
            insert(root, value);
    }

    private void insert(BSTNode<T> parent, T value) {
        int compare = value.compareTo(parent.value);
        if (compare < 0) {
            if (parent.hasLeft())
                insert(parent.left, value);
            else parent.setLeft(value);
        } else if(compare > 0) {
            if (parent.hasRight())
                insert(parent.right, value);
            else parent.setRight(value);
        } else {
            // Node already in tree
        }
    }

    public void erase(T value){
        BSTNode<T> target = find(value);
        if(target == null) return;

        if(target.hasLeft() && target.hasRight()){
            BSTNode<T> node = target.getRight();
            if(!node.hasLeft()){
                // Special case swap

                BSTNode<T> tp = target.getParent(),
                        tl = target.getLeft(),
                        nr = node.getRight();

                target.setRight(nr);
                target.setLeft((BSTNode<T>)null);
                node.setLeft(tl);

                switch (target.type) {
                    case LEFT -> tp.setLeft(node);
                    case RIGHT -> tp.setRight(node);
                    case ROOT -> root = node.makeRoot();
                }

                node.setRight(target);
            }
            else {
                while (node.hasLeft())
                    node = node.getLeft();

                BSTNode<T>
                        tp = target.getParent(),
                        tl = target.getLeft(),
                        tr = target.getRight(),
                        np = node.getParent(),
                        nl = node.getLeft(),
                        nr = node.getRight();

                target.setLeft(nl);
                target.setRight(nr);
                node.setLeft(tl);
                node.setRight(tr);

                BSTNode.ChildType nt = node.type, tt = target.type;

                switch (nt) {
                    case LEFT -> np.setLeft(target);
                    case RIGHT -> np.setRight(target);
                    case ROOT -> throw new Error("Cannot happen");
                }

                switch (tt) {
                    case LEFT -> tp.setLeft(node);
                    case RIGHT -> tp.setRight(node);
                    case ROOT -> root = node.makeRoot();
                }
            }
        }

        if(target.hasLeft()){   // Deg 1, has left
            switch (target.type) {
                case LEFT -> target.parent.setLeft(target.getLeft());
                case RIGHT -> target.parent.setRight(target.getLeft());
                case ROOT -> root = target.getLeft().makeRoot();
            }
        }
        else if(target.hasRight()){   // Deg 1, has right
            switch (target.type) {
                case LEFT -> target.parent.setLeft(target.getRight());
                case RIGHT -> target.parent.setRight(target.getRight());
                case ROOT -> root = target.getRight().makeRoot();
            }
        }
        else {   // Deg 0, has left
            switch (target.type) {
                case LEFT -> target.parent.setLeft((BSTNode<T>)null);
                case RIGHT -> target.parent.setRight((BSTNode<T>)null);
                case ROOT -> root = null;
            }
        }
    }

    private BSTNode<T> find(T value){
        return find(root, value);
    }

    private BSTNode<T> find(BSTNode<T> node, T value){
        if(node == null) return null;

        int compare = value.compareTo(node.value);

        if (compare < 0)
            return find(node.getLeft(), value);
        if (compare > 0)
            return find(node.getRight(), value);
        return node;
    }

    @Override
    public Iterator<T> iterator() {
        if(root == null) return Collections.emptyIterator();

        return new Iterator<>() {
            private final Stack<BSTNode<T>> stk = new Stack<>();
            private BSTNode<T> curr = root;

            @Override
            public boolean hasNext() {
                while(curr != null){
                    stk.push(curr);
                    curr = curr.left;
                }
                return !stk.isEmpty();
            }

            @Override
            public T next() {
                BSTNode<T> res = stk.pop();
                curr = res.right;
                return res.value;
            }
        };
    }

    public int getHeight(){
        return root.getHeight();
    }

    @Override
    public String toString() {
        return root == null ? "bst.BST{}" : "bst.BST" + root;
    }

    public BSTNode<T> getRoot() {
        return root;
    }

    public static class BSTNode<T extends Comparable<T>> {
        private BSTNode<T> left = null, right = null;
        private BSTNode<T> parent = null;
        public final T value;
        private enum ChildType {LEFT, RIGHT, ROOT};
        private ChildType type = ChildType.ROOT;

        public BSTNode(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return toString(this);
        }

        private static <T extends Comparable<T>> String toString(BSTNode<T> node){
            return toString(node, 0);
        }

        private static <T extends Comparable<T>> String toString(BSTNode<T> node, int n){
            if(node == null) return "nil";
            System.out.println(n + ": Calling toString on " + node.value);
            return "{ " + toString(node.left, n + 1) + " " + node.value + " " + toString(node.right, n + 1) + " }";
        }

        public int getHeight(){
            return 1 + Math.max(left == null ? 0 : left.getHeight(), right == null ? 0 : right.getHeight());
        }

        public BSTNode<T> makeRoot(){
            parent = null;
            type = ChildType.ROOT;
            return this;
        }

        public BSTNode<T> getParent(){
            return parent;
        }

        public BSTNode<T> getLeft(){
            return left;
        }
        public BSTNode<T> getRight(){
            return right;
        }
        public void setLeft(BSTNode<T> node){
            left = node;
            if(node == null) return;
            node.parent = this;
            node.type = ChildType.LEFT;
        }
        public void setRight(BSTNode<T> node){
            right = node;
            if(node == null) return;
            node.parent = this;
            node.type = ChildType.RIGHT;
        }
        public void setLeft(T value){
            setLeft(new BSTNode<>(value));
        }
        public void setRight(T value){
            setRight(new BSTNode<>(value));
        }

        public boolean hasLeft() {
            return left != null;
        }
        public boolean hasRight() {
            return right != null;
        }

        public static <T extends Comparable<T>> int getPrintWidth(BSTNode<T> node, Function<BSTNode<T>, Integer> getSelfWidth){
            if(node == null) return 0;
            return Math.max(getSelfWidth.apply(node), getPrintWidth(node.left, getSelfWidth) + getPrintWidth(node.right, getSelfWidth));
        }
    }
}