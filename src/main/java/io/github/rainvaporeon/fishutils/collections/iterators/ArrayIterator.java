package io.github.rainvaporeon.fishutils.collections.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterable<T>, Iterator<T> {
    private static final Object[] EMPTY = new Object[0];
    private final T[] array;
    private int cursor;

    /**
     * Singleton constructor
     */
    private ArrayIterator() {
        this.array = (T[]) EMPTY;
        this.cursor = 0;
    }

    ArrayIterator(T[] array) {
        this.array = array;
        this.cursor = 0;
    }

    @SuppressWarnings("unchecked") /* Should be fine */
    public static <T> Iterator<T> create(Object array) {
        if(array == null || !array.getClass().isArray()) {
            return new SingletonIterator<>((T) array);
        } else {
            return new ArrayIterator<>((T[]) array);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return cursor < array.length;
    }

    @Override
    public T next() {
        return array[cursor++];
    }

    private static class SingletonIterator<E> implements Iterator<E> {
        private final E element;
        private boolean iterated;

        private SingletonIterator(E element) {
            this.element = element;
        }

        @Override
        public boolean hasNext() {
            return !iterated;
        }

        @Override
        public E next() {
            if(!hasNext()) throw new NoSuchElementException();
            iterated = true;
            return this.element;
        }
    }
}
