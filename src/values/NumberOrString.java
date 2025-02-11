package values;

import org.jetbrains.annotations.NotNull;

import java.util.Scanner;

@SuppressWarnings("unused")
public class NumberOrString implements Comparable<NumberOrString> {
    public final Object value;
    private final type t;

    private enum type {I, D, S}

    public NumberOrString(int i) {
        value = i;
        t = type.I;
    }

    public NumberOrString(double d) {
        value = d;
        t = type.D;
    }

    public NumberOrString(String s) {
        value = s;
        t = type.S;
    }

    @Override
    public int compareTo(@NotNull NumberOrString other) {
        // If both NumberOrString are numeric, compare the numeric values
        if (!isString() && !other.isString()) {
            return Double.compare(getDoubleVal(), other.getDoubleVal());
        }

        int stringComparison = (getStringVal()).compareTo(other.getStringVal());
        if (stringComparison != 0) {
            return stringComparison;
        }

        // If the string value are equal, set strings to be "greater than" numbers
        return t.ordinal() - other.t.ordinal();
    }

    @Override
    public String toString() {
        return t == type.S ? "\"%s\"".formatted(value.toString()) : value.toString();
    }

    public double getDoubleVal() {
        return t == type.I ? Double.valueOf((int) value) : (Double) value;
    }

    public String getStringVal() {return value.toString();}

    public boolean isNumber() {
        return t != type.S;
    }

    public boolean isString() {
        return t == type.S;
    }

    public static NumberOrString fromStringString(String str) {
        return str == null ? null : new NumberOrString(str.substring(1, str.length() - 1).replaceAll("\\\\(.)", "$1"));
    }

    public static NumberOrString getFromScanner(Scanner scan) {
        String STRING_MATCHING_REGEX = "\"([^\"\\\\]|\\\\.){1,10}\"|'([^'\\\\]|\\\\.){1,10}'";

        NumberOrString v;
        String vs;

        if (scan.hasNextInt()) {
            return new NumberOrString(scan.nextInt());
        } else if (scan.hasNextDouble()) {
            return new NumberOrString(scan.nextDouble());
        } else if ((v = NumberOrString.fromStringString(scan.findInLine(STRING_MATCHING_REGEX))) != null) {
            return v;
        }
        return null;
    }
}