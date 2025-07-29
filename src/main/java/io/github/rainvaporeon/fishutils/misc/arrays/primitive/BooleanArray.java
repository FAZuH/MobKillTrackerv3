package io.github.rainvaporeon.fishutils.misc.arrays.primitive;

import io.github.rainvaporeon.fishutils.misc.arrays.PrimitiveArrayLike;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * A class representing an optionally mutable boolean array.
 */
public class BooleanArray extends PrimitiveArrayLike<Boolean> {
    private final boolean[] array;
    private final boolean mutable;

    public BooleanArray() {
        super(boolean.class, Boolean.class);
        this.array = new boolean[0];
        this.mutable = false;
    }

    public BooleanArray(boolean[] array) {
        super(boolean.class, Boolean.class);
        this.array = array;
        this.mutable = false;
    }

    public BooleanArray(boolean[] array, boolean mutable) {
        super(boolean.class, Boolean.class);
        this.array = array;
        this.mutable = mutable;
    }

    @Override
    public Boolean get(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public boolean getAsBoolean(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public void set(int index, Boolean value) {
        setBoolean(index, value);
    }

    @Override
    public void setBoolean(int index, boolean value) {
        if(mutable) {
            checkRange(index);
            array[index] = value;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public boolean[] toBooleanArray() {
        return this.array.clone();
    }

    protected void checkRange(int val) {
        if(val < 0 || val >= size()) throw new IndexOutOfBoundsException(val);
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    public BooleanArray toMutable() {
        if(this.mutable) return this;
        return new BooleanArray(this.array.clone(), true);
    }

    public BooleanArray toImmutable() {
        if(!this.mutable) return this;
        return new BooleanArray(this.array.clone(), false);
    }

    @Override
    public int size() {
        return array.length;
    }

    /*
        Static collections
     */

    /**
     * Converts the given int array to an immutable BooleanArray
     * @param array the array
     * @return the wrapped BooleanArray
     */
    public static BooleanArray fromArray(boolean... array) {
        return new BooleanArray(array);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to 0.
     * @param size the size
     * @return an immutable array filled with zeroes.
     */
    public static BooleanArray createEmpty(int size) {
        return new BooleanArray(new boolean[size], false);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to {@code value}.
     * @param size the size
     * @param value the value
     * @return an immutable array filled with {@code value}.
     */
    public static BooleanArray create(int size, boolean value) {
        boolean[] arr = new boolean[size];
        Arrays.fill(arr, value);
        return new BooleanArray(arr);
    }

    /**
     * Creates an immutable array with size {@code size} and mapped
     * individually to its position as described by {@code mapper}
     * @param size the size
     * @param mapper the mapper
     * @return a new BooleanArray
     */
    public static BooleanArray create(int size, IntFunction<Boolean> mapper) {
        boolean[] v = new boolean[size];
        for(int i = 0; i < size; i++) {
            v[i] = mapper.apply(i);
        }
        return new BooleanArray(v);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        BooleanArray arr = (BooleanArray) object;
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
}