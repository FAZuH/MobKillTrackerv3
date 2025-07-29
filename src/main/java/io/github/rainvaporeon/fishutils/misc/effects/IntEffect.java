package io.github.rainvaporeon.fishutils.misc.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class IntEffect implements Effect<Integer> {
    protected final List<IntConsumer> listeners = new ArrayList<>();
    private final Object mutex = new Object();
    private int value;

    public IntEffect(int value) {
        this.value = value;
    }

    @Override
    public Integer get() {
        return this.value;
    }

    public int getInt() {
        return this.value;
    }

    @Override
    public void set(Integer integer) {
        this.set(integer.intValue());
    }

    public void set(int value) {
        this.value = value;
        this.onUpdate(value);
    }

    @Override
    public boolean addListener(Consumer<Integer> consumer) {
        IntConsumer csm = consumer::accept;
        synchronized (mutex) {
            return listeners.remove(csm);
        }
    }

    public boolean addListener(IntConsumer consumer) {
        synchronized (mutex) {
            return listeners.add(consumer);
        }
    }

    @Override
    public boolean removeListener(Consumer<Integer> consumer) {
        IntConsumer csm = consumer::accept;
        synchronized (mutex) {
            return listeners.remove(csm);
        }
    }

    public boolean removeListener(IntConsumer consumer) {
        synchronized (mutex) {
            return listeners.remove(consumer);
        }
    }

    @Override
    public void onUpdate(Integer value) {
        synchronized (mutex) {
            listeners.forEach(csm -> csm.accept(value));
        }
    }
}
