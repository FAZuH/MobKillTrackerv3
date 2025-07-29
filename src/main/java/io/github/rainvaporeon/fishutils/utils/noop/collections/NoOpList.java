package io.github.rainvaporeon.fishutils.utils.noop.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A list implementation that does nothing, the iterator
 * is always empty, and all mutation calls will be voided.
 * @param <E>
 * @see NoOpIterator
 */
public class NoOpList<E> implements List<E> {
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

    @Override @SuppressWarnings("unchecked") /* Empty array */
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
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public E get(int index) {
        return null;
    }

    @Override
    public E set(int index, E element) {
        return null;
    }

    @Override
    public void add(int index, E element) {

    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new NoOpIterator<>();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new NoOpIterator<>();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return this;
    }
}
