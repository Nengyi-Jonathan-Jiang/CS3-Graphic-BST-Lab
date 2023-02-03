package values;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class NumberOrString implements Comparable<NumberOrString> {
	public final Object value;
	private final type t;

	private enum type {I, D, S}

	public NumberOrString (int i) {
		value = i;
		t = type.I;
	}

	public NumberOrString (double d) {
		value = d;
		t = type.D;
	}

	NumberOrString (String s) {
		value = s;
		t = type.S;
	}

	@Override
	public int compareTo (@NotNull NumberOrString o) {
		return !isString() && !o.isString()
		       ? Double.compare(getDoubleVal(), o.getDoubleVal())
		       : getStringVal().equals(o.getStringVal())
		         ? t.ordinal() - o.t.ordinal()
		         : (getStringVal()).compareTo(o.getStringVal());
	}

	@Override
	public String toString () {
		return t == type.S ? "\"" + value.toString() + "\"" : value.toString();
	}

	public double getDoubleVal () {
		return t == type.I ? Double.valueOf((int) value) : (Double) value;
	}

	public String getStringVal() { return value.toString(); }

	public boolean isNumber() {
		return t != type.S;
	}

	public boolean isString () {
		return t == type.S;
	}

	public static NumberOrString fromStringString (String str) {
		return str == null ? null : new NumberOrString(str.substring(1, str.length() - 1).replaceAll("\\\\(.)", "$1"));
	}
}