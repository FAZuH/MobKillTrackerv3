package io.github.rainvaporeon.fishutils.utils.noop.collections;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * An iterator that does nothing, any modifications to this iterator will be discarded,
 * and calling next() or previous() always return null.
 * @param <E>
 */
public final class NoOpIterator<E> implements Iterator<E>, ListIterator<E> {
    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public E previous() {
        return null;
    }

    @Override
    public int nextIndex() {
        return 0;
    }

    @Override
    public int previousIndex() {
        return 0;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public void set(E e) {

    }

    @Override
    public void add(E e) {

    }
}
