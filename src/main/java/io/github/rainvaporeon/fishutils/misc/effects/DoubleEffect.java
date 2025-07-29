package io.github.rainvaporeon.fishutils.misc.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class DoubleEffect implements Effect<Double> {
    protected final List<DoubleConsumer> listeners = new ArrayList<>();
    private final Object mutex = new Object();
    private double value;

    public DoubleEffect(double value) {
        this.value = value;
    }

    @Override
    public Double get() {
        return this.value;
    }

    public double getDouble() {
        return this.value;
    }

    @Override
    public void set(Double d) {
        this.set(d.doubleValue());
    }

    public void set(double value) {
        this.value = value;
        this.onUpdate(value);
    }

    @Override
    public boolean addListener(Consumer<Double> consumer) {
        DoubleConsumer csm = consumer::accept;
        synchronized (mutex) {
            return listeners.remove(csm);
        }
    }

    public boolean addListener(DoubleConsumer consumer) {
        synchronized (mutex) {
            return listeners.add(consumer);
        }
    }

    @Override
    public boolean removeListener(Consumer<Double> consumer) {
        DoubleConsumer csm = consumer::accept;
        synchronized (mutex) {
            return listeners.remove(csm);
        }
    }

    public boolean removeListener(DoubleConsumer consumer) {
        synchronized (mutex) {
            return listeners.remove(consumer);
        }
    }

    @Override
    public void onUpdate(Double value) {
        synchronized (mutex) {
            listeners.forEach(csm -> csm.accept(value));
        }
    }
}
