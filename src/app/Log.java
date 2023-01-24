package app;

import java.awt.Color;
import java.util.*;

public class Log {
	private static final Deque<LogItem> log = new ArrayDeque<>();
	private static final long fadeTime = 16000;
	private static final int maxSize = 30;

	private static void _addMessage (String message, LogLevel level) {
		log.addFirst(new LogItem(message, level));
		System.out.println(level.tColor + message + ANSICode.CLEAR);
	}

	public static void err (String message) {
		_addMessage(message, LogLevel.Error);
	}

	public static void warn (String input) {
		_addMessage(input, LogLevel.Echo);
	}

	public static void log (String message) {
		_addMessage(message, LogLevel.Normal);
	}

	public static void logVerbose (String message) {
		_addMessage(message, LogLevel.Verbose);
	}

	public static void echoInput (String input, boolean logToTerminal) {
		log.addFirst(new LogItem(input, LogLevel.Echo));
		if (logToTerminal)
			System.out.println(LogLevel.Echo.tColor + input + ANSICode.CLEAR);
	}

	public static void output (String output) {
		_addMessage(output, LogLevel.Output);
	}

	public static void forEach (LogItemAction action) {
		long currentTime = System.currentTimeMillis();

		while (log.size() > maxSize || !log.isEmpty() && currentTime >= log.peekLast().startTime + fadeTime) {
			log.removeLast();
		}

		int i = 0;
		for (var item : log) {
			float t = (currentTime - item.startTime) / (float) fadeTime;
			action.execute(i++, item.message, t, item.level);
		}
	}

	public interface LogItemAction {
		void execute (int i, String message, float t, LogLevel level);
	}

	public enum LogLevel {

		Echo(ANSICode.CLEAR, Color.BLACK),
		Output(ANSICode.BLUE, Color.BLUE),
		Error(ANSICode.RED, Color.RED),
		Warning(ANSICode.YELLOW, Color.YELLOW.darker()),
		Normal(ANSICode.BLUE, Color.BLUE),
		Verbose(ANSICode.BLUE, Color.BLUE);

		public final ANSICode tColor;
		public final Color gColor;

		LogLevel (ANSICode tColor, Color gColor) {
			this.tColor = tColor;
			this.gColor = gColor;
		}
	}

	private static class LogItem {
		public final long startTime;
		public final String message;
		public final LogLevel level;

		LogItem (String text, LogLevel level) {
			this.startTime = System.currentTimeMillis();
			this.message = text;
			this.level = level;
		}

		@Override
		public String toString () {
			return "LogItem{" + message + "}";
		}
	}
}

