package io.github.rainvaporeon.fishutils.misc.exec;

import java.util.Collection;
import java.util.function.Function;

public interface ExecutionContext<T> {
    /**
     * Retrieves the head node for this tree
     * @return the head node to begin
     * @apiNote This method will be called exactly once
     * in a tree's lifespan.
     */
    ExecutionNode<T> poll();

    /**
     * Retrieves the container to store the results in
     * @return a singleton mutable container to store the results
     * @apiNote This method may be called once in a tree's lifespan
     */
    Collection<T> accumulator();

    /**
     * Finalizes the result and produces a value T
     * @return a function to map the current container to get a value
     */
    Function<Collection<T>, T> finalizer();
}
