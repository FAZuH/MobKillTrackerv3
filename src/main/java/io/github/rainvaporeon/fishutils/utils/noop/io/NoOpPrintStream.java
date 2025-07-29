package io.github.rainvaporeon.fishutils.utils.noop.io;

import java.io.OutputStream;
import java.io.PrintStream;

public class NoOpPrintStream extends PrintStream {
    public NoOpPrintStream() {
        super(OutputStream.nullOutputStream());
    }

    @Override public void write(int b) {}
    @Override public void write(byte[] buf) {}
    @Override public void write(byte[] buf, int off, int len) {}
    @Override public void writeBytes(byte[] buf) {}
    @Override public void flush() {}
    @Override public void close() {}
}
