package io.github.rainvaporeon.fishutils.objects;

import io.github.rainvaporeon.fishutils.action.ActionResult;
import io.github.rainvaporeon.fishutils.action.Result;
import io.github.rainvaporeon.fishutils.collections.Pair;

import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectUtils {

    public static <T, R> R evaluateOrDefault(T object, R defaultValue, Function<T, R> mapper) {
        if(object == null) return defaultValue;
        return mapper.apply(object);
    }

    public static boolean assertOrElse(Supplier<Boolean> test, Runnable otherwise) {
        if(test.get()) {
            return true;
        } else {
            otherwise.run();
            return false;
        }
    }

    public static <T> boolean execute(Supplier<T> sup) {
        try {
            sup.get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Evaluates the time taken to run this function
     * @param function The supplier to run
     * @return A Pair with the key being the returned value, and value being the
     * time elapsed executing this function
     * @param <R> return type
     */
    public static <R> Pair<R, Long> evaluate(Supplier<R> function) {
        long curr = System.currentTimeMillis();
        R r = function.get();
        long now = System.currentTimeMillis();
        return Pair.of(r, now - curr);
    }

    /**
     * Evaluates the time taken to run this function
     * @param function The supplier to run
     * @return A Pair with the key being the returned value, and value being the
     * time elapsed executing this function
     * @param <R> return type
     */
    public static <R> Pair<R, Long> evaluateNanos(Supplier<R> function) {
        long curr = System.nanoTime();
        R r = function.get();
        long now = System.nanoTime();
        return Pair.of(r, now - curr);
    }
}
