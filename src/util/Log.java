package util;

import app.Style;

import java.awt.Color;
import java.util.*;

public class Log {
	private static final Deque<LogItem> log = new ArrayDeque<>();
	private static final long fadeTime = 16000;
	private static final int maxSize = 30;

	public static final int NO_TERMINAL = 1, NO_DISPLAY = 2;

	private static void _addMessage (String message, LogLevel level, int flags) {
		if((flags & NO_DISPLAY) == 0) {
			synchronized (log) {
				log.addFirst(new LogItem(message, level));
			}
		}
		if((flags & NO_TERMINAL) == 0)
			System.out.println(level.tColor + message + ANSICode.CLEAR);
	}

	public static void err(String message) { err(message, 0); }
	public static void err (String message, int flags) {
		_addMessage(message, LogLevel.Error, flags);
	}

	public static void log(String message) { log(message, 0); }
	public static void log (String message, int flags) {
		_addMessage(message, LogLevel.Normal, flags);
	}

	public static void echoInput(String input){
		echoInput(input, 0);
	}
	public static void echoInput (String input, int flags) {
		_addMessage(input, LogLevel.Echo, flags);
	}

	public static void output(String output){
		output(output, 0);
	}
	public static void output (String output, int flags) {
		_addMessage(output, LogLevel.Output, flags);
	}

	public static void forEachLogItem(LogItemAction action) {
		long currentTime = System.currentTimeMillis();

		while (log.size() > maxSize || !log.isEmpty() && currentTime >= log.peekLast().startTime + fadeTime) {
			log.removeLast();
		}

		int i = 0;
		synchronized (log) {
			for (var item : log) {
				float t = (currentTime - item.startTime) / (float) fadeTime;
				action.execute(i++, item.message, t, item.level);
			}
		}
	}

	public interface LogItemAction {
		void execute (int i, String message, float t, LogLevel level);
	}

	public enum LogLevel {

		Echo(ANSICode.CLEAR, Color.WHITE),
		Output(ANSICode.GREEN, Style.Colors.GREEN),
		Error(ANSICode.RED, Style.Colors.RED),
		Normal(ANSICode.GREEN, Style.Colors.GREEN);

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

