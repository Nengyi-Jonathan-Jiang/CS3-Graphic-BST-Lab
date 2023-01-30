package util;

@SuppressWarnings("unused")
public enum ANSICode {
    CLEAR,
    BLACK(30), RED(31), GREEN(32), YELLOW(33), BLUE(36), PURPLE(35), ORANGE(34), WHITE(37),
    BOLD(1, 0), UNDERLINE(4, 0);

    private final int c, d;

    ANSICode() {
        this(0, 0);
    }

    ANSICode(int c) {
        this(0, c);
    }

    ANSICode(int d, int c) {
        this.d = d;
        this.c = c;
    }

    private static boolean enabled = false;
    public static void enable(){
        enabled = true;
    }
    public static void disable(){
        enabled = false;
    }

    @Override
    public String toString() {
        return enabled ? "\u001B[" + (c == 0 ? d : d + ";" + c) + "m" : "";
    }
}