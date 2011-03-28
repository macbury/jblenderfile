package com.ardor3d.extension.model.blender;

public class Log {

    public static volatile boolean enabled = true;

    /**
     * Instance initializer
     */
    public Log() {
    }

    public static void log(Object... messageTokens) {
        if(!enabled) return;
        
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTrace[2];
        StringBuilder buffer = new StringBuilder();
        buffer.append(caller).append("\n");
        for (int i = 0; i < messageTokens.length; i++) {
            Object object = messageTokens[i];
            buffer.append(object);
        }
        System.out.println(buffer);
    }
}
