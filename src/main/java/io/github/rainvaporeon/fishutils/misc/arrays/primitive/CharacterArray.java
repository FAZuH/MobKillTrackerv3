package io.github.rainvaporeon.fishutils.misc.arrays.primitive;

import io.github.rainvaporeon.fishutils.misc.arrays.PrimitiveArrayLike;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * A class representing an optionally mutable char array.
 */
public class CharacterArray extends PrimitiveArrayLike<Character> {
    private final char[] array;
    private final boolean mutable;

    public CharacterArray() {
        super(char.class, Character.class);
        this.array = new char[0];
        this.mutable = false;
    }

    public CharacterArray(char[] array) {
        super(char.class, Character.class);
        this.array = array;
        this.mutable = false;
    }

    public CharacterArray(char[] array, boolean mutable) {
        super(char.class, Character.class);
        this.array = array;
        this.mutable = mutable;
    }

    @Override
    public Character get(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public char getAsCharacter(int index) {
        checkRange(index);
        return array[index];
    }

    @Override
    public void set(int index, Character value) {
        setCharacter(index, value);
    }

    @Override
    public void setCharacter(int index, char value) {
        if(mutable) {
            checkRange(index);
            array[index] = value;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public char[] toCharArray() {
        return this.array.clone();
    }

    protected void checkRange(int val) {
        if(val < 0 || val >= size()) throw new IndexOutOfBoundsException(val);
    }

    @Override
    public boolean isMutable() {
        return mutable;
    }

    public CharacterArray toMutable() {
        if(this.mutable) return this;
        return new CharacterArray(this.array.clone(), true);
    }

    public CharacterArray toImmutable() {
        if(!this.mutable) return this;
        return new CharacterArray(this.array.clone(), false);
    }

    @Override
    public int size() {
        return array.length;
    }

    /**
     * Converts the given char array to an immutable CharArray
     * @param array the array
     * @return the wrapped CharArray
     */
    public static CharacterArray fromArray(char... array) {
        return new CharacterArray(array);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to 0.
     * @param size the size
     * @return an immutable array filled with zeroes.
     */
    public static CharacterArray createEmpty(int size) {
        return new CharacterArray(new char[size]);
    }

    /**
     * Creates an immutable array with size {@code size} and all elements set to {@code value}.
     * @param size the size
     * @param value the value
     * @return an immutable array filled with {@code value}.
     */
    public static CharacterArray create(int size, char value) {
        char[] arr = new char[size];
        Arrays.fill(arr, value);
        return new CharacterArray(arr);
    }

    /**
     * Creates an immutable array with size {@code size} and mapped
     * individually to its position as described by {@code mapper}
     * @param size the size
     * @param mapper the mapper
     * @return a new CharArray
     */
    public static CharacterArray create(int size, IntFunction<Character> mapper) {
        char[] v = new char[size];
        for(int i = 0; i < size; i++) {
            v[i] = mapper.apply(i);
        }
        return new CharacterArray(v);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        CharacterArray arr = (CharacterArray) object;
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