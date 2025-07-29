package io.github.rainvaporeon.fishutils.misc.effects;

import java.util.function.Consumer;

public interface Effect<T> {
    T get();

    void set(T t);

    boolean addListener(Consumer<T> consumer);

    boolean removeListener(Consumer<T> consumer);

    void onUpdate(T value);

    static <T> Effect<T> of(T o) {
        return new ReferenceEffect<>(o);
    }

    static <T> Effect<T> ofConcurrent(T o) {
        return new ConcurrentEffect<>(o);
    }

    static <T> Effect<T> synchronizedEffect(Effect<T> t) {
        return new ConcurrentEffect<>(t);
    }
}
