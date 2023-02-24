package net.dirtcraft.clearlagfilter.utils;

import java.io.PrintStream;

public class Logger {

    public static void info(final String message, final Object... args) {
        log(System.out, LogLevel.INFO, message, args);
    }

    public static void warn(final String message, final Object... args) {
        log(System.out, LogLevel.WARN, message, args);
    }

    public static void error(final String message, final Object... args) {
        log(System.err, LogLevel.ERROR, message, args);
    }

    private static void log(final PrintStream printStream, final LogLevel logLevel,
            final String message,
            final Object... args) {
        printStream.println(String.format("[ClearlagFilter] %s | %s", logLevel.name(),
                String.format(message, args)));
    }

    private enum LogLevel {
        INFO, WARN, ERROR
    }
}
