package io.github.rainvaporeon.fishutils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

public class CollectionUtils {

    /**
     * Generates a collection of size and function
     * @param size The size
     * @param function The function for each individual index
     * @return The generated mutable collection
     */
    public static <T> List<T> generateCollection(int size, IntFunction<T> function) {
        List<T> list = new ArrayList<>(size);

        for(int i = 0; i < size; i++) {
            list.add(function.apply(i));
        }

        return list;
    }

    public static <T> Collector<Collection<T>, List<T>, List<T>> extractToList() {
        return new Collector<>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, Collection<T>> accumulator() {
                return List::addAll;
            }

            @Override
            public BinaryOperator<List<T>> combiner() {
                return (left, right) -> {
                    left.addAll(right);
                    return left;
                };
            }

            @Override
            public Function<List<T>, List<T>> finisher() {
                return Function.identity();
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.IDENTITY_FINISH);
            }
        };
    }
}
