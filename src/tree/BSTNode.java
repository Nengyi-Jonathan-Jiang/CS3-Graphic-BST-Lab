package tree;

import java.util.function.Function;
import java.util.function.Supplier;

public class BSTNode<T extends Comparable<T>> {
	protected BSTNode<T> left = null, right = null, parent = null;
	protected T value;

	public enum ChildType {
		LEFT, RIGHT, ROOT
	}

	/**
	 * @return One of {@link ChildType#LEFT}, {@link ChildType#RIGHT}, or {@link ChildType#ROOT} depending
	 * on the role of this node in the tree
	 */
	public ChildType getChildType () {
		return isLeftChild() ? ChildType.LEFT : isRightChild() ? ChildType.RIGHT : ChildType.ROOT;
	}

	public BSTNode (T value) {
		this.value = value;
	}

	private BSTNode(T value, BSTNode<T> left, BSTNode<T> right){
		this(value);
		this.setLeftChild(left);
		this.setRightChild(right);
	}

	@Override
	public String toString () {
		return toString(this);
	}

	private static <T extends Comparable<T>> String toString (BSTNode<T> node) {
		if (node == null) return "nil";
		return "{ " + toString(node.getLeftChild()) + " " + node.value + " " + toString(node.getRightChild()) + " }";
	}

	public T getValue () {
		return value;
	}

	/**
	 * @return The number of edges from this node to the lowest child node
	 */
	public int getHeight () {
		return getHeight(this);
	}

	/**
	 * @return The number of edges from the node to its lowest child node
	 */
	public static int getHeight (BSTNode<?> node) {
		return node == null ? -1 : 1 + Math.max(getHeight(node.left), getHeight(node.right));
	}

	/**
	 * Make the node have no parent
	 *
	 * @return itself
	 */
	public BSTNode<T> makeRoot () {
		parent = null;
		return this;
	}

	public BSTNode<T> getParent () {
		return parent;
	}

	public BSTNode<T> getLeftChild () {
		return left;
	}

	public BSTNode<T> getRightChild () {
		return right;
	}

	public void setLeftChild (BSTNode<T> node) {
		left = node;
		if (node == null) return;
		node.setParent(this);
	}

	public void setRightChild (BSTNode<T> node) {
		right = node;
		if (node == null) return;
		node.setParent(this);
	}

	protected void setParent (BSTNode<T> parent) {
		this.parent = parent;
	}

	public BSTNode<T> getSibling () {
		return switch (getChildType()) {
			case LEFT -> parent.getRightChild();
			case RIGHT -> parent.getLeftChild();
			case ROOT -> null;
		};
	}

	public boolean hasSibling () {
		return parent.getDegree() == 2;
	}

	public boolean isNotRoot () {
		return parent != null;
	}

	public boolean isLeftChild () {
		return isNotRoot() && this == parent.getLeftChild();
	}

	public boolean isRightChild () {
		return isNotRoot() && this == parent.getRightChild();
	}

	public boolean hasLeftChild () {
		return left != null;
	}

	public boolean hasRightChild () {
		return right != null;
	}

	public boolean isLeaf () {
		return getDegree() == 0;
	}

	public int getDegree () {
		return (hasLeftChild() ? 1 : 0) + (hasRightChild() ? 1 : 0);
	}

	public static <T extends Comparable<T>> void swapValues (BSTNode<T> a, BSTNode<T> b) {
		T temp = a.value;
		a.value = b.value;
		b.value = temp;
	}

	public BSTNode<T> getInorderSuccessor(){
		if(this.hasRightChild()) {
			var n = getRightChild();
			while (n != null && n.hasLeftChild()) n = n.getLeftChild();
			return n;
		}
		else {
			var n = this;
			while(n != null && n.isRightChild()) n = n.getParent();
			return n == null ? null : n.getParent();
		}
	}
}