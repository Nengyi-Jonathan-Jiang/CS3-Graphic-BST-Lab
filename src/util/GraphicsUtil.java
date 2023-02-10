package util;

import app.treedrawer.TreeDrawer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;

public class GraphicsUtil {
	private static final Graphics dummyGraphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).getGraphics();

	public static Dimension getRenderedStringSize (String text, Font font) {
		Rectangle2D size = dummyGraphics.getFontMetrics(font).getStringBounds(text, dummyGraphics);
		return new Dimension((int) size.getWidth(), (int) size.getHeight());
	}

	public static AttributedString withFallbackFont (String text, Font mainFont, Font fallbackFont) {
		AttributedString result = new AttributedString(text);

		result.addAttribute(TextAttribute.FONT, mainFont, 0, text.length());

		boolean usingFallback = false;
		int fallbackBegin = 0;
		for (int i = 0; i < text.length(); i++) {
			char currChar = text.charAt(i);
			boolean canDisplay = !mainFont.canDisplay(currChar);
			if (canDisplay != usingFallback) {
				usingFallback = canDisplay;
				if (usingFallback)
					fallbackBegin = i;
				else
					result.addAttribute(TextAttribute.FONT, fallbackFont, fallbackBegin, i);
			}
		}
		if(usingFallback){
			result.addAttribute(TextAttribute.FONT, fallbackFont, fallbackBegin, text.length());
		}

		return result;
	}
}
