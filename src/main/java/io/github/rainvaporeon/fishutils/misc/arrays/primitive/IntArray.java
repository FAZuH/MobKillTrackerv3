package io.github.rainvaporeon.fishutils.misc.arrays.primitive;

import io.github.rainvaporeon.fishutils.misc.arrays.ArrayLike;
import io.github.rainvaporeon.fishutils.misc.arrays.PrimitiveArrayLike;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntUnaryOperator;
import java.util.stream.StreamSupport;

/**
 * A class representing an optionally mutable integer array.
 */
public class IntArray extends PrimitiveArrayLike<Integer> {
    private final int[] array;
    private final boolean mutable;

    public IntArray() {
        super(int.class, Integer.class);
        this.array = new int[0];
        this.mutable = false;
    }

    public IntArray(int[] array) {
        super(int.class, Integer.class);
        this.array = array;
        this.mutable = false;
    }

    public IntArray(int[] array, boolean mutable) {
        super(int.class, Integer.class);
        this.array = array;
        this.mutable = mutable;
    }

    @Override
    public Integer get(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public int getAsInt(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public void set(int index, Integer value) {
        setInt(index, value);
    }

    public void setInt(int index, int value) {
        if(mutable) {
            checkRange(index);
            array[index] = value;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public int[] toIntArray() {
        return this.array.clone();
    }

    protected void checkRange(int val) {
        if(val < 0 || val >= size()) throw new IndexOutOfBoundsException(val);
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    public IntArray toMutable() {
        if(this.mutable) return this;
        return new IntArray(this.array.clone(), true);
    }

    public IntArray toImmutable() {
        if(!this.mutable) return this;
        return new IntArray(this.array.clone(), false);
    }

    public IntArray copy() {
        return new IntArray(this.array.clone());
    }

    @Override
    public int size() {
        return array.length;
    }

    /*
        Static collections
     */

    public static final IntUnaryOperator NON_NEGATIVE = i -> Math.max(0, i);

    public static final IntUnaryOperator REVERSE = i -> -i;

    public static IntArray createMutable(int size) {
        return new IntArray(new int[size], true);
    }

    public static IntArray createMutable(int size, int defaultElements) {
        int[] a = new int[size];
        Arrays.fill(a, defaultElements);
        return new IntArray(a, true);
    }

    /**
     * Converts the given int array to an immutable IntArray
     * @param array the array
     * @return the wrapped IntArray
     */
    public static IntArray fromArray(int... array) {
        return new IntArray(array);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to 0.
     * @param size the size
     * @return an immutable array filled with zeroes.
     */
    public static IntArray createEmpty(int size) {
        return new IntArray(new int[size]);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to {@code value}.
     * @param size the size
     * @param value the value
     * @return an immutable array filled with {@code  value}.
     */
    public static IntArray create(int size, int value) {
        int[] i = new int[size];
        Arrays.fill(i, value);
        return new IntArray(i);
    }

    /**
     * Creates an immutable array with size {@code size} and mapped
     * individually to its position as described by {@code mapper}
     * @param size the size
     * @param mapper the mapper
     * @return a new IntArray
     */
    public static IntArray create(int size, IntUnaryOperator mapper) {
        int[] v = new int[size];
        for(int i = 0; i < size; i++) {
            v[i] = mapper.applyAsInt(i);
        }
        return new IntArray(v);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        IntArray integers = (IntArray) object;
        return Arrays.equals(array, integers.array);
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }
}
