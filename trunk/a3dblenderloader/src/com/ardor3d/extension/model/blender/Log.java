package com.ardor3d.extension.model.blender;

/**
 * Cheap logging
 * @author pgi
 */
public class Log {

    /**
     * Checked before to emit a message: if false, logs nothing
     */
    public static volatile boolean enabled = true;

    /**
     * Instance initializer
     */
    public Log() {
    }

    /**
     * Emit a message concatenating the given tokens
     * @param messageTokens the tokens that form the log message. Can be null or
     * empry.
     */
    public static void log(Object... messageTokens) {
        if(!enabled) return;
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[2];
        StringBuilder buffer = new StringBuilder();
        buffer.append(caller).append("\n");
        if(messageTokens != null) for (int i = 0; i < messageTokens.length; i++) {
            Object object = messageTokens[i];
            buffer.append(object);
        }
        System.out.println(buffer);
    }
}
