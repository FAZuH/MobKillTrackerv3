package io.github.rainvaporeon.fishutils.misc.arrays;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Base class for any primitive array-alike.
 * This class provides support for all primitive getter methods
 * as well as setter methods.
 * @param <T> the type
 * @apiNote When extending this class, please make sure to override all
 * respective getter and setter methods to fit your implementation.
 * The default implementation for them is usually unoptimized and would
 * benefit from almost any sort of implementation.
 */
public abstract class PrimitiveArrayLike<T> extends ArrayLike<T> {
    protected final Class<?> primitiveType;

    public PrimitiveArrayLike(Class<?> primitiveType, Class<T> type) {
        super(type);
        this.primitiveType = primitiveType;
    }

    public boolean getAsBoolean(int index) {
        return (boolean) this.getAt(index);
    }

    public void setBoolean(int index, boolean value) {
        setAs(index, value);
    }

    public byte getAsByte(int index) {
        return (byte) this.getAt(index);
    }

    public void setByte(int index, byte value) {
        setAs(index, value);
    }

    public short getAsShort(int index) {
        return (short) this.getAt(index);
    }

    public void setShort(int index, short value) {
        setAs(index, value);
    }

    public char getAsCharacter(int index) {
        return (char) this.getAt(index);
    }

    public void setCharacter(int index, char value) {
        setAs(index, value);
    }

    public int getAsInt(int index) {
        return (int) this.getAt(index);
    }

    public void setInt(int index, int value) {
        setAs(index, value);
    }

    public float getAsFloat(int index) {
        return (float) this.getAt(index);
    }

    public void setFloat(int index, float value) {
        setAs(index, value);
    }

    public double getAsDouble(int index) {
        return (double) this.getAt(index);
    }

    public void setDouble(int index, double value) {
        setAs(index, value);
    }

    public long getAsLong(int index) {
        return (long) this.getAt(index);
    }

    public void setLong(int index, long value) {
        setAs(index, value);
    }

    public void fill(int from, int to, byte value) {
        for(int i = from; i < to; i++) {
            this.setAs(i, value);
        }
    }

    public void fill(int from, int to, short value) {
        for(int i = from; i < to; i++) {
            this.setAs(i, value);
        }
    }

    public void fill(int from, int to, char value) {
        for(int i = from; i < to; i++) {
            this.setAs(i, value);
        }
    }

    public void fill(int from, int to, int value) {
        for(int i = from; i < to; i++) {
            this.setAs(i, value);
        }
    }

    public void fill(int from, int to, float value) {
        for(int i = from; i < to; i++) {
            this.setAs(i, value);
        }
    }

    public void fill(int from, int to, double value) {
        for(int i = from; i < to; i++) {
            this.setAs(i, value);
        }
    }

    public void fill(int from, int to, long value) {
        for(int i = from; i < to; i++) {
            this.setAs(i, value);
        }
    }

    public void fill(int from, int to, boolean value) {
        for(int i = from; i < to; i++) {
            this.setAs(i, value);
        }
    }

    private Object getAt(int index) {
        Iterator<?> it = this.iterator();
        for(int i = 0; it.hasNext(); i++) {
            Object o = it.next();
            if(i == index) return o;
        }
        throw new IndexOutOfBoundsException(index);
    }

    private void setAs(int index, Object value) {
        set(index, (T) value);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof PrimitiveArrayLike<?> al) {
            if(this.type != al.type && this.primitiveType != al.primitiveType) return false;
            return Arrays.deepEquals(this.toArray(), al.toArray());
        }
        return false;
    }
}
