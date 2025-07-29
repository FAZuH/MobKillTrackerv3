package io.github.rainvaporeon.fishutils.collections.iterators;

import io.github.rainvaporeon.fishutils.misc.functions.IntToBooleanFunction;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/**
 * A default iterator, provides an easy way to retrieve an iterator.
 * @param <T>
 */
public class DefaultIterator<T> implements Iterator<T>, Iterable<T> {
    private final IntToBooleanFunction hasNextFunction;
    private final IntFunction<T> nextFunction;
    private final IntConsumer removeFunction;
    private boolean removed = false;
    private int cursor = 0;

    public DefaultIterator(IntToBooleanFunction hasNextFunction, IntFunction<T> nextFunction, IntConsumer removeFunction) {
        this.hasNextFunction = Objects.requireNonNull(hasNextFunction);
        this.nextFunction = Objects.requireNonNull(nextFunction);
        this.removeFunction = removeFunction;
    }

    public DefaultIterator(IntToBooleanFunction hasNextFunction, IntFunction<T> nextFunction) {
        this(hasNextFunction, nextFunction, null);
    }

    @Override
    public boolean hasNext() {
        return this.hasNextFunction.apply(cursor + 1);
    }

    @Override
    public T next() {
        this.removed = false;
        return this.nextFunction.apply(cursor++);
    }

    @Override
    public void remove() {
        if(this.removeFunction == null) throw new UnsupportedOperationException();
        if(this.removed) throw new IllegalStateException();
        this.removed = true;
        this.removeFunction.accept(cursor);
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        Iterator.super.forEachRemaining(action);
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
