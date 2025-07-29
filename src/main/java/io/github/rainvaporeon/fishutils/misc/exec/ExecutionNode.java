package io.github.rainvaporeon.fishutils.misc.exec;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Representing an execution node
 * @param <T> the action type
 */
public abstract class ExecutionNode<T> {
    /**
     * Retrieves the result of this execution status
     * @return a {@link Future} pointing at the
     */
    public abstract Future<T> get();

    /**
     * Retrieves the result, may interrupt if the value
     * is not ready, may throw an {@link IllegalStateException}
     * if {@link ExecutionNode#run()} is not previously called.
     * @return the result
     * @throws ExecutionException if there's an exception during computation
     * @throws InterruptedException if the thread was interrupted whilst computing
     */
    public T waitAndGet() throws ExecutionException, InterruptedException {
        return get().get();
    }

    /**
     * Seeks the parent node of this node
     * @return the parent node, or {@code null} if this is the parent node
     */
    protected abstract ExecutionNode<T> parent();

    /**
     * Executes the action on this node.
     * After call to this method, {@link ExecutionNode#get()}
     * may return a {@link Future} denoting the outcome.
     */
    public abstract void run();
}
