package io.github.rainvaporeon.fishutils.misc.functions;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Sequence<T> {

    T get(int index);

    default void forEach(Consumer<? super T> consumer) {
        try {
            // It will complete after throwing an exception
            for(int i = 0;;i++) consumer.accept(this.get(i));
        } catch (Exception e) {
            /* no-op */
        }
    }

    default <R> Sequence<R> map(Function<? super T, ? extends R> mapper) {
        return idx -> mapper.apply(this.getAt(idx));
    }

    default Sequence<T> filter(Predicate<? super T> predicate) {
        return index -> {
            int cursor = 0;
            T element;
            do {
                element = this.getAt(cursor++);
                if(predicate.test(element)) index--;
            } while (index >= 0);
            return element;
        };
    }

    private T getAt(int idx) {
        try { return get(idx); } catch (IndexOutOfBoundsException ex) { throw new NoSuchElementException(); }
    }
}
