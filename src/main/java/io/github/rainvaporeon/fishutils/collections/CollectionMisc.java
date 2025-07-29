package io.github.rainvaporeon.fishutils.collections;

import io.github.rainvaporeon.fishutils.misc.annotations.Beta;

import java.util.*;
import java.util.stream.Collectors;

public class CollectionMisc {
    private CollectionMisc() {}

    /**
     * Calculates the level of distribution of this collection
     * @param collection The collection
     * @return The level of distribution, normalized
     * @apiNote This method is implemented in a way so that if
     * a collection is completely sorted, it returns 0,
     * otherwise, returns the tendency of this collection
     */
    @Beta
    public static <T extends Comparable<T>> int noiseOf(Collection<T> collection) {
        Set<T> set = collection.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));

        int value = 0;

        for(T t : collection) {
            int curr = indexOf(collection, t);
            int sorted = indexOf(set, t);

            value += curr - sorted;
        }

        return value;
    }

    public static <T> int indexOf(Collection<T> coll, T element) {
        int ctr = 0;
        for(T t : coll) {
            if(Objects.equals(t, element)) return ctr;
            ctr++;
        }
        return -1;
    }
}
