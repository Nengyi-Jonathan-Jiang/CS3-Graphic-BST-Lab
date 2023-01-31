package tree;

public class BST<T extends Comparable<T>> extends AbstractBST<T, BSTNode<T>> {
	@Override
	protected BSTNode<T> constructNode(T value) {
		return new BSTNode<>(value);
	}
}