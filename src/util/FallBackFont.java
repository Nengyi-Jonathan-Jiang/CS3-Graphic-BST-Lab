package util;


import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

public class FallBackFont {
	public static AttributedString createFallbackString(String text, Font mainFont, Font fallbackFont) {
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