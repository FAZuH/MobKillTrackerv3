package io.github.rainvaporeon.fishutils.misc;

import java.util.function.Function;

public interface ThrowingRunnable {
    void run() throws Throwable;

    default void runUnchecked() {
        this.runUnchecked(null);
    }

    default <X extends RuntimeException> void runUnchecked(Function<Throwable, X> mapper) {
        try {
            this.run();
        } catch (Throwable t) {
            if(mapper != null) {
                throw mapper.apply(t);
            } else {
                throw new RuntimeException(t);
            }
        }
    }
}
