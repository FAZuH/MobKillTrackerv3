package io.github.rainvaporeon.fishutils.misc.arrays.primitive;

import io.github.rainvaporeon.fishutils.misc.arrays.PrimitiveArrayLike;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * A class representing an optionally mutable short array.
 */
public class ShortArray extends PrimitiveArrayLike<Short> {
    private final short[] array;
    private final boolean mutable;

    public ShortArray() {
        super(short.class, Short.class);
        this.array = new short[0];
        this.mutable = false;
    }

    public ShortArray(short[] array) {
        super(short.class, Short.class);
        this.array = array;
        this.mutable = false;
    }

    public ShortArray(short[] array, boolean mutable) {
        super(short.class, Short.class);
        this.array = array;
        this.mutable = mutable;
    }

    @Override
    public Short get(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public short getAsShort(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public void set(int index, Short value) {
        setShort(index, value);
    }

    @Override
    public void setShort(int index, short value) {
        if(mutable) {
            checkRange(index);
            array[index] = value;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public short[] toShortArray() {
        return this.array.clone();
    }

    protected void checkRange(int val) {
        if(val < 0 || val >= size()) throw new IndexOutOfBoundsException(val);
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    public ShortArray toMutable() {
        if(this.mutable) return this;
        return new ShortArray(this.array.clone(), true);
    }

    public ShortArray toImmutable() {
        if(!this.mutable) return this;
        return new ShortArray(this.array.clone(), false);
    }

    @Override
    public int size() {
        return array.length;
    }

    /**
     * Converts the given short array to an immutable ShortArray
     * @param array the array
     * @return the wrapped ShortArray
     */
    public static ShortArray fromArray(short... array) {
        return new ShortArray(array);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to 0.
     * @param size the size
     * @return an immutable array filled with zeroes.
     */
    public static ShortArray createEmpty(int size) {
        return new ShortArray(new short[size]);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to {@code value}.
     * @param size the size
     * @param value the value
     * @return an immutable array filled with {@code value}.
     */
    public static ShortArray create(int size, short value) {
        short[] arr = new short[size];
        Arrays.fill(arr, value);
        return new ShortArray(arr);
    }

    /**
     * Creates an immutable array with size {@code size} and mapped
     * individually to its position as described by {@code mapper}
     * @param size the size
     * @param mapper the mapper
     * @return a new ShortArray
     */
    public static ShortArray create(int size, IntFunction<Short> mapper) {
        short[] v = new short[size];
        for(int i = 0; i < size; i++) {
            v[i] = mapper.apply(i);
        }
        return new ShortArray(v);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ShortArray arr = (ShortArray) object;
        return Arrays.equals(array, arr.array);
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    private static class DefaultShortArray extends ShortArray {
        private final int size;
        private final short value;

        private DefaultShortArray(int size, short value) {
            super(null, false);
            if(size < 0) throw new IllegalArgumentException("size cannot be negative");
            this.size = size;
            this.value = value;
        }

        @Override
        public Short get(int index) {
            checkRange(index);
            return value;
        }

        @Override
        public short getAsShort(int index) {
            checkRange(index);
            return value;
        }

        @Override
        protected void checkRange(int val) {
            if(val < 0 || val >= size) throw new IndexOutOfBoundsException(val);
        }
    }
}