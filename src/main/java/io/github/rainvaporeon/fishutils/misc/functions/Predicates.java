package io.github.rainvaporeon.fishutils.misc.functions;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

public interface Predicates {
    Predicate<Boolean> IDENTITY = t -> t;

    Predicate<Number> POSITIVE = t -> t.doubleValue() > 0;

    Predicate<Number> NEGATIVE = t -> t.doubleValue() < 0;

    Predicate<Number> ZERO = t -> t.doubleValue() == 0;
}
