import app.App;
import graphics.Window;
import util.ANSICode;

import java.util.Arrays;

public class Main {
    public static void main(String[] _args) {
        Window.enableHardwareAcceleration();

        var args = Arrays.asList(_args);

        if (args.contains("--color"))
            ANSICode.enable();

        new App();
    }
}