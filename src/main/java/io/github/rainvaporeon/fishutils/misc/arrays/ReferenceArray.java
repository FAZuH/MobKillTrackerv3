package io.github.rainvaporeon.fishutils.misc.arrays;

public class ReferenceArray<T> extends ArrayLike<T> {
    private final T[] array;
    private final boolean mutable;

    @SafeVarargs
    public ReferenceArray(T... array) {
        this(false, array);
    }

    @SafeVarargs @SuppressWarnings("unchecked")
    public ReferenceArray(boolean mutable, T... array) {
        super((Class<T>) array.getClass().componentType());
        this.array = array;
        this.mutable = mutable;
    }

    @Override
    public T get(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public void set(int index, T value) {
        if(!mutable) throw new UnsupportedOperationException();
        array[index] = value;
    }

    @Override
    public int size() {
        return array.length;
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    @Override
    protected synchronized T[] getInternalArray() {
        return array;
    }
}
