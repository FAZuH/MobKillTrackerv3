package io.github.rainvaporeon.fishutils.utils.noop.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * An empty Set implementation, all mutation
 * calls will be voided.
 * @param <E>
 * @see NoOpIterator
 */
public class NoOpSet<E> implements Set<E> {
    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new NoOpIterator<>();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override @SuppressWarnings("unchecked") /* again, empty array */
    public <T> T[] toArray(T[] a) {
        return (T[]) toArray();
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }
}
