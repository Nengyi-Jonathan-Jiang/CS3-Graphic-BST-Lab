public class NumberOrString implements Comparable<NumberOrString> {
    public final Object value;
    private final type t;
    private enum type {I, D, S}

    NumberOrString(int i){
        value = i; t = type.I;
    }
    NumberOrString(double d){
        value = d; t = type.D;
    }
    NumberOrString(String s){
        value = s; t = type.S;
    }

    @Override
    public int compareTo(NumberOrString o) {
        return !isString() && !o.isString()
                ? Double.compare(getDoubleVal(), o.getDoubleVal())
                : toString().equals(o.toString())
                    ? t.ordinal() - o.t.ordinal()
                    : (toString()).compareTo(o.toString());
    }

    @Override
    public String toString(){
        return value.toString();
    }

    public int getIntVal(){
        return (Integer) value;
    }
    public double getDoubleVal(){
        return t == type.I ? Double.valueOf((int) value) : (Double) value;
    }
    public String getStringVal(){
        return (String) value;
    }
    public Object getVal(){
        return value;
    }
    public boolean isDouble(){return t == type.D;}
    public boolean isInt(){return t == type.I;}
    public boolean isString(){return t == type.S;}

    public static NumberOrString fromStringString(String str){
        return str == null ? null : new NumberOrString(str.substring(1, str.length() - 1).replaceAll("\\\\(.)", "$1"));
    }
}