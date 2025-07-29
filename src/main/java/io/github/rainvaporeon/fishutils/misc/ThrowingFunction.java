package io.github.rainvaporeon.fishutils.misc;

public interface ThrowingFunction<T, R> {
    R apply(T t) throws Throwable;
}
