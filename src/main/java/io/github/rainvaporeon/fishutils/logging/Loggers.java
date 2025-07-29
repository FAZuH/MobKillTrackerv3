package io.github.rainvaporeon.fishutils.logging;

import io.github.rainvaporeon.fishutils.misc.StableField;

import java.io.File;

public final class Loggers {

    public static final ILogger NO_OP = new ILogger() {
        @Override public void newline() {}
        @Override public void success(String message, Throwable t) {}
        @Override public void info(String message, Throwable t) {}
        @Override public void warn(String message, Throwable t) {}
        @Override public void error(String message, Throwable t) {}
        @Override public void fatal(String message, Throwable t) {}
        @Override public void debug(String message) {}
    };

    private static final StableField<File> DEFAULT = new StableField<>(null);

    private Loggers() {

    }

    /**
     * Sets the default file to write to, this will apply to all newly created loggers
     * without a set output file.
     * This action can be done only once.
     * @param out the output file
     */
    public static void setDefault(File out) {
        DEFAULT.set(out);
    }

    /**
     * Returns a pre-configured logger, with the print stream
     * as the ones specified in System (System.out and System.err)
     * @return a logger
     */
    public static Logger getThreadLogger() {
        return getThreadLogger(DEFAULT.get());
    }

    /**
     * Returns a pre-configured logger, with the print stream
     * as the ones specified in System (System.out and System.err)
     * @param out the output file to write to
     * @return a logger
     */
    public static Logger getThreadLogger(File out) {
        final Logger logger = new Logger("Thread #" + Thread.currentThread().getId() + "/" + Thread.currentThread().getName(), out);
        logger.configured();
        return logger;
    }

    /**
     * Returns an unconfigured logger, with no print streams.
     * @param out the output file to write to
     * @return a logger
     */
    public static Logger unconfiguredThreadLogger(File out) {
        return new Logger("Thread #" + Thread.currentThread().getId() + "/" + Thread.currentThread().getName(), out);
    }

    /**
     * Returns a pre-configured logger, with the print stream
     * as the ones specified in System (System.out and System.err)
     * @return a logger
     */
    public static Logger getLogger(String name) {
        Logger logger = new Logger(name, DEFAULT.get());
        logger.configured();
        return logger;
    }

    /**
     * Returns an unconfigured logger, with no print streams.
     * @return a logger
     */
    public static Logger unconfiguredLogger(String name) {
        return new Logger(name, DEFAULT.get());
    }

    /**
     * Returns a pre-configured logger, with the print stream
     * as the ones specified in System (System.out and System.err)
     * @param out the output file to write to
     * @return a logger
     */
    public static Logger getLogger(String name, File out) {
        Logger logger = new Logger(name, out);
        logger.configured();
        return logger;
    }

    /**
     * Returns an unconfigured logger, with no print streams.
     * @param out the output file to write to
     * @return a logger
     */
    public static Logger unconfiguredLogger(String name, File out) {
        return new Logger(name, out);
    }

    public static ILogger noop() {
        return NO_OP;
    }
}
