package io.github.rainvaporeon.fishutils.misc;

public interface ThrowingSupplier<T> {
    T get() throws Throwable;
    
    default T getUnchecked() {
        try {
            return get();
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
