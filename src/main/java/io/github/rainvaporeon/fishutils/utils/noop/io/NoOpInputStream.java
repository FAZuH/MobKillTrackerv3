package io.github.rainvaporeon.fishutils.utils.noop.io;

import java.io.InputStream;

public final class NoOpInputStream extends InputStream {
    @Override
    public int read() {
        return -1;
    }
}
