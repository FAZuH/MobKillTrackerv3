package io.github.rainvaporeon.fishutils.action;

/**
 * An interface denoting an action
 * @param <T> the return type
 */
public interface Action<T> {
    /**
     * Retrieves the return value of this action
     * @return the value
     * @apiNote this may throw an exception if {@link Action#getThrowable()} is not null
     * and if {@link Action#isSuccessful()} returns false
     */
    T getReturnValue();

    /**
     * Retrieves the throwable of this action
     * @return the throwable
     */
    Throwable getThrowable();

    boolean isSuccessful();

    default boolean failed() {
        return !isSuccessful();
    }
}
