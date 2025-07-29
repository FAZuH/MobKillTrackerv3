package io.github.rainvaporeon.fishutils.misc;

public class Arrays {
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;

    public static <T> T[] resize(T[] array, int nextElements) {
        int expandSize = Math.min((int) (array.length * (1 + DEFAULT_LOAD_FACTOR)), Integer.MAX_VALUE);
        if(array.length + nextElements < 0) throw new ArrayStoreException("resize result is larger than 2,147,483,647!");
        return java.util.Arrays.copyOf(array, expandSize);
    }
}
