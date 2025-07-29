package com.spiritlight.adapters.fabric.misc;

import java.util.ArrayList;
import java.util.List;

public class Iterables {

    public static <T> T[] toArray(Iterable<T> it) {
        List<T> ls = new ArrayList<>();
        it.forEach(ls::add);
        return (T[]) ls.toArray();
    }
}
