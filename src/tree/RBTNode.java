package tree;

public class RBTNode<T extends Comparable<T>> extends BSTNode<T> {
	public enum Color {RED, BLACK}

	private Color color = Color.RED;

	public RBTNode (T value) {
		super(value);
	}

	public Color getColor () {
		return color;
	}

	public static Color getColor (RBTNode<?> node) {
		return node == null ? Color.BLACK : node.getColor();
	}

	public boolean isBlack(){
		return getColor() == Color.BLACK;
	}

	public boolean isRed(){
		return getColor() == Color.RED;
	}

	public static boolean isRed (RBTNode<?> node) {
		return getColor(node) == Color.RED;
	}

	public static boolean isBlack (RBTNode<?> node) {
		return getColor(node) == Color.BLACK;
	}

	public static void swapColor (RBTNode<?> node) {
		if (node != null) node.swapColor();
	}

	public void makeRed (){
		color = Color.RED;
	}

	public void makeBlack (){
		color = Color.BLACK;
	}

	public static void makeBlack (RBTNode<?> node) {
		if (node != null) node.color = Color.BLACK;
	}

	public static void setColor (RBTNode<?> node, Color color) {
		if (node != null) node.color = color;
	}

	@Override
	public BSTNode<T> makeRoot () {
		color = Color.BLACK;
		return super.makeRoot();
	}

	public void swapColor () {
		color = switch (color) {
			case RED -> Color.BLACK;
			case BLACK -> Color.RED;
		};
	}

	// <editor-fold>

	@Override
	public RBTNode<T> getLeftChild () {
		return (RBTNode<T>) super.getLeftChild();
	}

	@Override
	public RBTNode<T> getRightChild () {
		return (RBTNode<T>) super.getRightChild();
	}

	@Override
	public RBTNode<T> getParent () {
		return (RBTNode<T>) super.getParent();
	}

	@Override
	public RBTNode<T> insertLeft (T value) {
		RBTNode<T> res = new RBTNode<>(value);
		setLeftChild(res);
		return res;
	}

	@Override
	public RBTNode<T> insertRight (T value) {
		RBTNode<T> res = new RBTNode<>(value);
		setRightChild(res);
		return res;
	}

	@Override
	public RBTNode<T> getSibling () {
		return (RBTNode<T>) super.getSibling();
	}
}