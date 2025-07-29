package io.github.rainvaporeon.fishutils.misc.arrays;

import java.util.Iterator;

/**
 * An array iterator, providing only the fundamental requirements
 * for an iterator.
 * @param <T> the iterator type
 */
public class ArrayIterator<T> implements Iterator<T>, Iterable<T> {
    /**
     * The backing array, highly advised to be immutable
     */
    private final T[] shadow;
    private int cursor;

    public ArrayIterator(ArrayLike<T> shadow) {
        this(shadow.toArray());
    }

    public ArrayIterator(T[] shadow) {
        this.shadow = shadow;
    }

    @Override
    public boolean hasNext() {
        return cursor < shadow.length;
    }

    @Override
    public T next() {
        return shadow[cursor++];
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
