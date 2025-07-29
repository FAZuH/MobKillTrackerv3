package io.github.rainvaporeon.fishutils.logging;

import java.util.Arrays;

public interface ILogger {

    void newline();

    default void success(String message) {
        success(message, null);
    }

    default void success(Object message) {
        success(String.valueOf(message));
    }

    default void success(Object... messages) {
        success(Arrays.toString(messages));
    }

    void success(String message, Throwable t);

    default void info(String message) {
        info(message, null);
    }

    default void info(Object message) {
        info(String.valueOf(message));
    }

    default void info(Object... messages) {
        info(Arrays.toString(messages));
    }

    void info(String message, Throwable t);

    default void warn(String message) {
        warn(message, null);
    }

    default void warn(Object message) {
        warn(String.valueOf(message));
    }

    default void warn(Object... messages) {
        warn(Arrays.toString(messages));
    }

    void warn(String message, Throwable t);

    default void error(String message) {
        error(message, null);
    }

    default void error(Object message) {
        error(String.valueOf(message));
    }

    default void error(Object... messages) {
        error(Arrays.toString(messages));
    }

    void error(String message, Throwable t);

    default void fatal(String message) {
        fatal(message, null);
    }

    default void fatal(Object message) {
        fatal(String.valueOf(message));
    }

    default void fatal(Object... messages) {
        fatal(Arrays.toString(messages));
    }

    void fatal(String message, Throwable t);

    void debug(String message);

    default void debug(Object message) {
        debug(String.valueOf(message));
    }

    default void debug(Object... messages) {
        debug(Arrays.toString(messages));
    }

    // @since 1.2.8, for some reason this was not added
    default void debug(String message, Throwable t) {
        debug(message);
    }

    static ILogger of(String name) {
        return new Logger(name);
    }
}
