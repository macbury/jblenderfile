package it.tukano.blenderfile;

/**
 * A very cheap logging utility
 * @author pgi
 */
public class Log {
    public static volatile boolean enabled = true;
    public static volatile boolean infoEnabled = true;
    public static volatile boolean exEnabled = true;

    /**
     * Instance initializer
     */
    public Log() {
    }

    public static void info(Object... tokens) {
        if(!enabled || !infoEnabled) return;
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if(stackTrace.length > 2) {
            StringBuilder buffer = new StringBuilder().append(stackTrace[2]).append("\n");
            if(tokens != null) for (int i = 0; i < tokens.length; i++) {
                Object object = tokens[i];
                buffer.append(object);
            }
            System.out.println(buffer);
        }
    }

    public static void ex(Throwable t, Object... tokens) {
        if(!enabled || !exEnabled) return;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if(stackTrace.length > 2) {
            StringBuilder buffer = new StringBuilder().append(stackTrace[2]).append("\n");
            if(tokens != null) for (int i = 0; i < tokens.length; i++) {
                Object object = tokens[i];
                buffer.append(object);
            }
            System.err.println(buffer);
            t.printStackTrace(System.err);
        }
    }
}
