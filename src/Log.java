import java.util.ArrayDeque;
import java.util.Deque;

public class Log {
    private final Deque<LogItem> log = new ArrayDeque<>();
    private final long fadeTime;
    private final int maxSize;

    public Log(long fadeTime, int maxSize){
        this.fadeTime = fadeTime;
        this.maxSize = maxSize;
    }

    public void log(String message){
        log(message, 0);
    }
    public void log(String message, int status){
        log.addFirst(new LogItem(message, status));

        // Log to STDOUT
        switch (status) {
            case -1 -> System.out.println("\u001B[31m" + message + "\u001B[0m");
            case 0 -> {}
            default -> System.out.println("\u001B[35m" + message + "\u001B[0m");
        }
    }

    public void forEach(LogItemAction action) {
        long currentTime = System.currentTimeMillis();

        while(log.size() > maxSize || !log.isEmpty() && currentTime >= log.peekLast().startTime + fadeTime){
            log.removeLast();
        }


        int i = 0;
        for(var item : log){
            float t = (currentTime - item.startTime) / (float)fadeTime;
            action.execute(i++, item.message, t, item.status);
        }
    }

    public interface LogItemAction {
        void execute(int i, String message, float t, int status);
    }

    private static class LogItem {
        public final long startTime;
        public final String message;
        public final int status;

        LogItem(String text, int status) {
            this.startTime = System.currentTimeMillis();
            this.message = text;
            this.status = status;
        }

        @Override
        public String toString() {
            return "LogItem{" + message + "}";
        }
    }
}

