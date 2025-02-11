package util;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class GraphicsUtil {
    private static final Graphics dummyGraphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();

    public static Dimension getRenderedStringSize(String text, Font font) {
        Rectangle2D size = dummyGraphics.getFontMetrics(font).getStringBounds(text, dummyGraphics);
        return new Dimension((int) size.getWidth(), (int) size.getHeight());
    }
}
