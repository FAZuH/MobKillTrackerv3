package io.github.rainvaporeon.fishutils.misc.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A reactive effect.
 * @param <T>
 */
public class ReferenceEffect<T> implements Effect<T> {
    protected final List<Consumer<T>> listeners = new ArrayList<>();
    private final Object mutex = new Object();
    protected T value;

    ReferenceEffect(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
        this.onUpdate(value);
    }

    @Override
    public boolean addListener(Consumer<T> listener) {
        synchronized (mutex) {
            return listeners.add(listener);
        }
    }

    @Override
    public boolean removeListener(Consumer<T> consumer) {
        synchronized (mutex) {
            return listeners.remove(consumer);
        }
    }

    @Override
    public void onUpdate(T value) {
        synchronized (mutex) {
            this.listeners.forEach(ls -> ls.accept(value));
        }
    }
}
