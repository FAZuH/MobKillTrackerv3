package io.github.rainvaporeon.fishutils.collections;

import io.github.rainvaporeon.fishutils.collections.iterators.ArrayIterator;

import java.util.*;

public class ArrayUtils {

    public static <T> List<T> unpackArrays(Object object) {
        Holder holder = new Holder();
        itArray(object, holder);
        return (List<T>) holder.list;
    }

    public static <T> Iterator<T> arrayIterator(Object object) {
        return ArrayIterator.create(object);
    }

    public static <T> void fill(T[] array, T value) {
        Arrays.fill(array, value);
    }

    // TODO: Primitive arrays
    private static void itArray(Object object, Holder data) {
        if(object != null && object.getClass().isArray()) {
            Object[] arr = (Object[]) object;
            for(Object o : arr) {
                itArray(o, data);
            }
        } else {
            data.list.add(object);
        }
    }

    private static class Holder {
        final List<Object> list = new ArrayList<>();
    }

}