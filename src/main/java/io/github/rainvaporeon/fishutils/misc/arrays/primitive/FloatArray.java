package io.github.rainvaporeon.fishutils.misc.arrays.primitive;

import io.github.rainvaporeon.fishutils.misc.arrays.PrimitiveArrayLike;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * A class representing an optionally mutable float array.
 */
public class FloatArray extends PrimitiveArrayLike<Float> {
    private final float[] array;
    private final boolean mutable;

    public FloatArray() {
        super(float.class, Float.class);
        this.array = new float[0];
        this.mutable = false;
    }

    public FloatArray(float[] array) {
        super(float.class, Float.class);
        this.array = array;
        this.mutable = false;
    }

    public FloatArray(float[] array, boolean mutable) {
        super(float.class, Float.class);
        this.array = array;
        this.mutable = mutable;
    }

    @Override
    public Float get(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public float getAsFloat(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public void set(int index, Float value) {
        setFloat(index, value);
    }

    @Override
    public void setFloat(int index, float value) {
        if(mutable) {
            checkRange(index);
            array[index] = value;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public float[] toFloatArray() {
        return this.array.clone();
    }

    protected void checkRange(int val) {
        if(val < 0 || val >= size()) throw new IndexOutOfBoundsException(val);
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    public FloatArray toMutable() {
        if(this.mutable) return this;
        return new FloatArray(this.array.clone(), true);
    }

    public FloatArray toImmutable() {
        if(!this.mutable) return this;
        return new FloatArray(this.array.clone(), false);
    }

    @Override
    public int size() {
        return array.length;
    }

    /**
     * Converts the given float array to an immutable FloatArray
     * @param array the array
     * @return the wrapped FloatArray
     */
    public static FloatArray fromArray(float... array) {
        return new FloatArray(array);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to 0.
     * @param size the size
     * @return an immutable array filled with zeroes.
     */
    public static FloatArray createEmpty(int size) {
        return new FloatArray(new float[size]);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to {@code value}.
     * @param size the size
     * @param value the value
     * @return an immutable array filled with {@code value}.
     */
    public static FloatArray create(int size, float value) {
        float[] arr = new float[size];
        Arrays.fill(arr, value);
        return new FloatArray(arr);
    }

    /**
     * Creates an immutable array with size {@code size} and mapped
     * individually to its position as described by {@code mapper}
     * @param size the size
     * @param mapper the mapper
     * @return a new FloatArray
     */
    public static FloatArray create(int size, IntFunction<Float> mapper) {
        float[] v = new float[size];
        for(int i = 0; i < size; i++) {
            v[i] = mapper.apply(i);
        }
        return new FloatArray(v);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FloatArray arr = (FloatArray) object;
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