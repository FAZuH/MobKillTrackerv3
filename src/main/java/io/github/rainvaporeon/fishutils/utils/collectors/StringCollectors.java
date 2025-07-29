package io.github.rainvaporeon.fishutils.utils.collectors;

import java.util.stream.Collector;

public class StringCollectors {
    /**
     * Returns a collector that combines all the characters
     * into a String, this collector does not permit nulls,
     * and has a suggested integer range of a char.
     * @return a new collector which combines all elements into a string
     */
    public static Collector<Integer, ?, String> concat() {
        return Collector.of(
                StringBuilder::new,
                (sb, i) -> sb.append((char) i.intValue()),
                StringBuilder::append,
                StringBuilder::toString
        );
    }
}
