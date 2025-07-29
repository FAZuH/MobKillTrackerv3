package io.github.rainvaporeon.fishutils.misc.arrays.primitive;

import io.github.rainvaporeon.fishutils.misc.arrays.PrimitiveArrayLike;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * A class representing an optionally mutable byte array.
 */
public class ByteArray extends PrimitiveArrayLike<Byte> {
    private final byte[] array;
    private final boolean mutable;

    public ByteArray() {
        super(byte.class, Byte.class);
        this.array = new byte[0];
        this.mutable = false;
    }

    public ByteArray(byte[] array) {
        super(byte.class, Byte.class);
        this.array = array;
        this.mutable = false;
    }

    public ByteArray(byte[] array, boolean mutable) {
        super(byte.class, Byte.class);
        this.array = array;
        this.mutable = mutable;
    }

    @Override
    public Byte get(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public byte getAsByte(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public void set(int index, Byte value) {
        setByte(index, value);
    }

    @Override
    public void setByte(int index, byte value) {
        if(mutable) {
            checkRange(index);
            array[index] = value;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public byte[] toByteArray() {
        return this.array.clone();
    }

    protected void checkRange(int val) {
        if(val < 0 || val >= size()) throw new IndexOutOfBoundsException(val);
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    public ByteArray toMutable() {
        if(this.mutable) return this;
        return new ByteArray(this.array.clone(), true);
    }

    public ByteArray toImmutable() {
        if(!this.mutable) return this;
        return new ByteArray(this.array.clone(), false);
    }

    @Override
    public int size() {
        return array.length;
    }

    /**
     * Converts the given byte array to an immutable ByteArray
     * @param array the array
     * @return the wrapped ByteArray
     */
    public static ByteArray fromArray(byte... array) {
        return new ByteArray(array);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to 0.
     * @param size the size
     * @return an immutable array filled with zeroes.
     */
    public static ByteArray createEmpty(int size) {
        return new ByteArray(new byte[size]);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to {@code value}.
     * @param size the size
     * @param value the value
     * @return an immutable array filled with {@code value}.
     */
    public static ByteArray create(int size, byte value) {
        byte[] arr = new byte[size];
        Arrays.fill(arr, value);
        return new ByteArray(arr);
    }

    /**
     * Creates an immutable array with size {@code size} and mapped
     * individually to its position as described by {@code mapper}
     * @param size the size
     * @param mapper the mapper
     * @return a new ByteArray
     */
    public static ByteArray create(int size, IntFunction<Byte> mapper) {
        byte[] v = new byte[size];
        for(int i = 0; i < size; i++) {
            v[i] = mapper.apply(i);
        }
        return new ByteArray(v);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ByteArray arr = (ByteArray) object;
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