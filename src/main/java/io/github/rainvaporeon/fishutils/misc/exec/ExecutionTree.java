package io.github.rainvaporeon.fishutils.misc.exec;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Represents an execution tree.
 */
public class ExecutionTree<T> {
    /**
     * The head node of this tree
     */
    private final ExecutionNode<T> head;

    /**
     * The execution context for this tree
     */
    private final ExecutionContext<T> context;

    /**
     * The result container for this tree
     */
    private final Collection<T> container;

    public ExecutionTree(ExecutionContext<T> context) {
        this.context = context;
        this.head = context.poll();
        this.container = context.accumulator();
    }

    /**
     * Starts the execution of this tree.
     * @throws ExecutionException if the head node throws an exception
     */
    public void start() throws ExecutionException {
        try {
            head.run();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }

    /**
     * Gets the result from the tree.
     * This method can be called multiple times, and does not
     * guarantee a consistent outcome unless the execution
     * has fully finished.
     * @return the produced value
     */
    public T get() {
        return this.context.finalizer().apply(container);
    }
}
