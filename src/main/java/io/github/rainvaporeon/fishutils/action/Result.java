package io.github.rainvaporeon.fishutils.action;

/**
 * Utility enum to indicate the result of an action.
 * If more information is required, use {@link ActionResult} instead.
 *
 * @see ActionResult
 * @since 1.0
 */
public enum Result {
    /**
     * Indicating that this action is successful, a value is usually
     * associated with this return result
     */
    SUCCESS,
    /**
     * Indicating that this action is unsuccessful due to some sort of exception
     * during execution.
     * This sort of result usually provides further information to provide
     * insight on the cause.
     */
    ERROR,
    /**
     * Indicating that this action is unsuccessful due to some conditions not matching,
     * failed predicate or any unknown errors occurred.
     * This sort of result usually provides further information to provide
     * insight on the cause.
     */
    FAIL,
    /**
     * Indicating that this action has completed
     * @since 1.2
     */
    COMPLETED,
    /**
     * Indicating that this action is still incomplete, or is waiting for some
     * sort of re-assignment. This kind of result usually indicates that the caller
     * should re-check the state after a while as it may change at a later time.
     * @since 1.2
     */
    INCOMPLETE;

    Result() {}

    /**
     * Checks whether this is {@link Result#SUCCESS}
     * @since 1.2.3
     */
    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    /**
     * Checks whether this is {@link Result#ERROR}
     * @since 1.2.3
     */
    public boolean isError() {
        return this == ERROR;
    }

    /**
     * Checks whether this is either {@link Result#ERROR} or {@link Result#FAIL}
     * @since 1.2.3
     */
    public boolean isFailed() {
        return this == FAIL || isError();
    }

    /**
     * Checks whether this is {@link Result#COMPLETED}
     * @since 1.2.3
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * Checks whether this is {@link Result#INCOMPLETE}
     * @since 1.2.3
     */
    public boolean isIncomplete() {
        return this == INCOMPLETE;
    }

    /**
     * Checks whether this is {@link Result#COMPLETED} or {@link Result#INCOMPLETE}
     * @since 1.2.3
     */
    public boolean isCompletionStage() {
        return this == COMPLETED || this == INCOMPLETE;
    }
}
