package io.github.rainvaporeon.fishutils.misc.arrays;

import io.github.rainvaporeon.fishutils.misc.annotations.New;
import io.github.rainvaporeon.fishutils.misc.arrays.primitive.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntFunction;

/**
 * An optionally mutable class representing an array.
 * @param <T> the array type
 */
public abstract class ArrayLike<T> implements Cloneable, Iterable<T>, Serializable {

    /**
     * The representing array type.
     * @since 1.2.7
     */
    protected final Class<?> type;

    public ArrayLike(Class<T> type) {
        this.type = type;
    }

    /**
     * Retrieves the element at this index
     * @param index the index
     * @return the element
     */
    public abstract T get(int index);

    /**
     * Sets the index to this element
     * @param index the index
     * @param value the value
     * @apiNote This method throws an exception if {@link ArrayLike#isMutable()}
     * returns {@code false}
     */
    public abstract void set(int index, T value);

    /**
     * Retrieves the array size
     * @return the array size
     */
    public abstract int size();

    /**
     * Checks whether the array is mutable. If not, {@link ArrayLike#set(int, Object)}
     * will throw an exception.
     * @return whether this array is mutable
     */
    public abstract boolean isMutable();

    /**
     * Checks the range and throws an {@link IndexOutOfBoundsException} if
     * {@code value} is less than 0 or larger or equal to {@link ArrayLike#size()}
     * @param value the value
     */
    protected void checkRange(int value) {
        if(value < 0 || value >= size()) throw new IndexOutOfBoundsException(value);
    }

    /**
     * Internal method used to retrieve the array directly.
     * Implementations of this class may benefit from overriding
     * this method and return the direct reference to the backing
     * array, which is usually going to increase speed.
     * @return the backing array
     * @implNote By default, this method returns null and therefore
     * is not used.
     * One has to return the actual backing array
     * if they do not wish to implement all other misc methods but
     * also wishes for some extent of speed benefits.
     * In other words, <b>the default methods may use the array returned
     * here to do operations!</b> Therefore, it is important that the latest
     * reference of the backing array should be used.
     */
    protected synchronized T[] getInternalArray() {
        return null;
    }

    /**
     * Fills this array in a given range to the specified value
     * @param from the offset to start, inclusive
     * @param to the offset to end, exclusive
     * @param value the value
     */
    public void fill(int from, int to, T value) {
        T[] intern = this.getInternalArray();
        if(intern != null) {
            if(!this.isMutable()) throw new UnsupportedOperationException();
            Arrays.fill(intern, from, to, value);
            return;
        }
        for(int i = from; i < to; i++) {
            set(i, value);
        }
    }

    /**
     * Fills this array in a given range to the specified value
     * @param from the offset to start, inclusive
     * @param to the offset to end, exclusive
     * @param mapper the value mapper, the actual position is
     *               supplied into this mapper
     */
    public void fill(int from, int to, IntFunction<T> mapper) {
        for(int i = from; i < to; i++) {
            set(i, mapper.apply(i));
        }
    }

    /**
     * Converts the representing array into an object array
     * @return the array, may or may not represent itself and
     * makes no guarantee that the returned element cannot be mutated
     * or be reflected onto the array.
     * @apiNote since this method makes no guarantee that the returned
     * array is a copy of itself or a reference to its backing array,
     * nor does it guarantee this is deep-copied, it is highly advised
     * against modification of the returned array.
     */
    public @New T[] toArray() {
        T[] intern = this.getInternalArray();
        if(intern != null) {
            return intern.clone();
        }
        Object[] ret = new Object[this.size()];
        for (int i = 0; i < size(); i++) {
            ret[i] = get(i);
        }
        return (T[]) ret;
    }

    @Override
    public @New Iterator<T> iterator() {
        return new ArrayIterator<>(this);
    }


    /**
     * Clones this array type.
     * @return the clone of this array
     * @apiNote As this class wraps a mutable array, it is
     * almost always recommended to override this method
     * so the backing array is also cloned.
     * By default, the mutation to the array itself will still reflect
     * onto the origin object!
     * @implNote if {@link ArrayLike#getInternalArray()} is implemented,
     * this method may return a new instance of {@link ArrayLike} with
     * a clone of the array provided.
     */
    @Override @SuppressWarnings("unchecked")
    public ArrayLike<T> clone() {
        try {
            T[] intern = this.getInternalArray();
            if(intern != null) {
                return ArrayLike.of(intern.clone());
            }
            return (ArrayLike<T>) super.clone();
        } catch (CloneNotSupportedException error) {
            // Java's fault
            throw new InternalError(error);
        } catch (ClassCastException ex) {
            // Again, Java's fault
            throw new InternalError(ex);
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(this.toArray());
    }

    /**
     * Checks for object equality between one and another.
     * @param obj the object
     * @return whether both backing arrays are equal
     * @implNote it is almost always recommended that the implementation
     * should implement their own equals method, as this method will
     * fetch both side's array via the {@link ArrayLike#toArray()} method,
     * which can be slow at times, thus nearly any implementation will be
     * beneficial.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof ArrayLike<?> al) {
            if(this.type != al.type) return false;
            return Arrays.deepEquals(this.toArray(), al.toArray());
        }
        return false;
    }

    @SafeVarargs
    public static <T> ArrayLike<T> of(T... elements) {
        return new ReferenceArray<>(elements);
    }

    public static IntArray of(int... elements) {
        return new IntArray(elements);
    }

    public static DoubleArray of(double... elements) {
        return new DoubleArray(elements);
    }

    public static ShortArray of(short... elements) {
        return new ShortArray(elements);
    }

    public static LongArray of(long... elements) {
        return new LongArray(elements);
    }

    public static ByteArray of(byte... el) {
        return new ByteArray(el);
    }

    public static FloatArray of(float... f) {
        return new FloatArray(f);
    }

    public static CharacterArray of(char... c) {
        return new CharacterArray(c);
    }

    public static BooleanArray of(boolean... boo) {
        return new BooleanArray(boo);
    }

    // 1.2.18 mutable factory
    @SafeVarargs
    public static <T> ArrayLike<T> ofMutable(T... elements) {
        return new ReferenceArray<>(true, elements);
    }

    public static IntArray ofMutable(int... elements) {
        return new IntArray(elements, true);
    }

    public static DoubleArray ofMutable(double... elements) {
        return new DoubleArray(elements, true);
    }

    public static ShortArray ofMutable(short... elements) {
        return new ShortArray(elements, true);
    }

    public static LongArray ofMutable(long... elements) {
        return new LongArray(elements, true);
    }

    public static ByteArray ofMutable(byte... el) {
        return new ByteArray(el, true);
    }

    public static FloatArray ofMutable(float... f) {
        return new FloatArray(f, true);
    }

    public static CharacterArray ofMutable(char... c) {
        return new CharacterArray(c, true);
    }

    public static BooleanArray ofMutable(boolean... boo) {
        return new BooleanArray(boo, true);
    }
}
