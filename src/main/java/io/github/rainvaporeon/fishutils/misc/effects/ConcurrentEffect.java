package io.github.rainvaporeon.fishutils.misc.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConcurrentEffect<T> implements Effect<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();
    private T t;
    private final Object mutex = new Object();
    private final Object listenerMutex = new Object();

    ConcurrentEffect(T t) {
        this.t = t;
    }

    public ConcurrentEffect(Effect<T> effect) {
        this(effect.get());
    }

    @Override
    public T get() {
        synchronized (mutex) {
            return t;
        }
    }

    @Override
    public void set(T t) {
        synchronized (mutex) {
            this.t = t;

            this.onUpdate(t);
        }
    }

    @Override
    public boolean addListener(Consumer<T> consumer) {
        synchronized (listenerMutex) {
            return this.listeners.add(consumer);
        }
    }

    @Override
    public boolean removeListener(Consumer<T> consumer) {
        synchronized (listenerMutex) {
            return this.listeners.remove(consumer);
        }
    }

    @Override
    public void onUpdate(T value) {
        synchronized (listenerMutex) {
            for(Consumer<T> consumer : this.listeners) {
                consumer.accept(value);
            }
        }
    }
}
