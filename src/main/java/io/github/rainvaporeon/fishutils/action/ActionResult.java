package io.github.rainvaporeon.fishutils.action;

import io.github.rainvaporeon.fishutils.collections.Pair;
import io.github.rainvaporeon.fishutils.misc.ThrowingRunnable;
import io.github.rainvaporeon.fishutils.misc.ThrowingSupplier;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/*
* INTERNAL CHANGELOG:
* 1.1.1: All references to returnValue are now retrieved with the getter.
* 1.2.3: All internal fields are exposed to inheritors for extension purposes.
*/
/**
 * An object representing the execution status of an action. This is bundled
 * with a {@link Result} denoting the execution result, and can hold a return value
 * and a throwable if an exception had occurred.
 * <p>
 * The implementation also supports multiple chaining statements to provide ease of execution,
 * such as {@link ActionResult#expect(Class, Consumer)} to handle specific exceptions,
 * throwing exceptions with {@link ActionResult#throwIfPresent()} and such.
 * @param <T> The return type
 * @apiNote This object is not meant to be serialized, and only recommended to be used as
 * return values for simplifying handling the result of executing something.
 * @since 1.0
 * @author Rain
 */
public final class ActionResult<T> implements Action<T> {

    // Shared default value
    private static final ActionResult<?> DEFAULT_SUCCESS = new ActionResult<>(Result.SUCCESS, null);

    // 1.2: Weakened visibility (priv -> prot)
    // 1.2.3: Removed final modifier in favor of completion stages
    // 1.2.4: Returned to be final and deprecated completion stages, made class final
    // Note: CompletableFuture provides enough functionality so wrapping it is redundant.
    private final Result result; // The result (non-null)
    private final T returnValue; // Holding value (nullable)
    private final Throwable throwable; // Exception (null if successful)
    private boolean exceptionHandled; // Exception handled (@throws throwable if false and throwable != null)

    /**
     * Creates a new ActionResult instance with the provided parameters
     * @param result A non-null value representing the result state
     * @param returnValue The return value of this object
     * @param throwable The throwable if any is present
     */
    public ActionResult(Result result, T returnValue, Throwable throwable) {
        this.result = Objects.requireNonNull(result);;
        this.returnValue = returnValue;
        this.throwable = throwable;
        this.exceptionHandled = throwable == null;
    }

    /**
     * Creates a new ActionResult holding the result and a return value; the throwable is null in this case
     * @param result The result
     * @param returnValue The return value
     */
    public ActionResult(Result result, T returnValue) {
        this(result, returnValue, null);
    }

    /**
     * Creates a new ActionResult holding the result and a throwable; the return value is null in this case
     * @param result The result
     * @param throwable The return value
     */
    public ActionResult(Result result, Throwable throwable) {
        this(result, null, throwable);
    }

    // 1.1: Returns itself instead
    // 1.2.1: rename: consume -> thenAccept
    /**
     * Accepts this result's returned value
     * @param consumer the consumer to handle the value
     * @return the object itself, or fail if any exceptions occurred
     * @apiNote if execution fails, the returned ActionResult still will hold this object's
     * return value.
     */
    public ActionResult<T> thenAccept(Consumer<T> consumer) {
        try {
            consumer.accept(this.getReturnValue());
            return this;
        } catch (Exception e) {
            return new ActionResult<>(Result.ERROR, this.getReturnValue(), e);
        }
    }

    /**
     * Accepts this result and maps it to another value
     * @param function The mapping function
     * @return The action result for the returned value
     * @param <R> The return type
     */
    public <R> ActionResult<R> map(Function<T, R> function) {
        try {
            return new ActionResult<>(Result.SUCCESS, function.apply(this.getReturnValue()));
        } catch (Exception t) {
            return new ActionResult<>(Result.ERROR, t);
        }
    }

    public ActionResult<T> flatMap(Function<T, T> function) {
        try {
            return new ActionResult<>(Result.SUCCESS, function.apply(this.getReturnValue()));
        } catch (Exception t) {
            return new ActionResult<>(Result.ERROR, t);
        }
    }

    /**
     * Transforms the return value to another if the last operation is successful.
     * @param transformer the mapper
     * @return transformed value if it is previously successful, otherwise itself
     * @since 1.2.1
     */
    public ActionResult<T> applyIfSuccessful(Function<T, T> transformer) {
        if(result == Result.SUCCESS) return this.map(transformer);
        return this;
    }

    /**
     * Transforms the return value to another if the last operation is failed.
     * @param transformer the mapper
     * @return transformed value if it is previously successful, otherwise itself
     * @since 1.2.1
     */
    public ActionResult<T> applyIfFailed(Function<T, T> transformer) {
        if(result != Result.SUCCESS) return this.map(transformer);
        return this;
    }

    /**
     * Transforms the return value to another depending on the result.
     * @param transformer the mapper
     * @return the transformed value
     * @since 1.2.2
     */
    public ActionResult<T> apply(BiFunction<Result, T, T> transformer) {
        try {
            return ActionResult.success(transformer.apply(this.result, this.getReturnValue()));
        } catch (Exception t) {
            return ActionResult.fail(t);
        }
    }

    /**
     * Handles the exception if any is present.
     * @param type The type of exception to expect
     * @param action the handler for the exception
     * @return the same ActionResult for chaining purposes
     * @param <X> the type of exception
     * @apiNote if one of the exceptions were handled successfully,
     * then this is considered a "handled result", and so calling
     * {@link ActionResult#getReturnValue()} will not throw the exception.
     */
    public <X extends Throwable> ActionResult<T> expect(Class<X> type, Consumer<X> action) {
        if(this.throwable == null) return this;
        if(type.isAssignableFrom(this.throwable.getClass())) {
            //noinspection unchecked
            action.accept((X) this.throwable);
            this.exceptionHandled = true;
        }
        return this;
    }

    /**
     * Handles the exception and produces a new value
     * @param type The type of exception to expect
     * @param handler The handler to execute if this type is expected
     * @return the same ActionResult for chaining purposes, or a new one specified by the handler
     * @param <X> The exception type
     */
    public <X extends Throwable> ActionResult<T> expect(Class<X> type, Function<X, T> handler) {
        if(this.throwable == null) return this;
        if(type.isAssignableFrom(this.throwable.getClass())) {
            try {
                T t = handler.apply((X) this.throwable);
                return ActionResult.success(t);
            } catch (Exception t) {
                return ActionResult.fail(t);
            }
        }
        return this;
    }

    /**
     * Retrieves the underlying return value. May raise exceptions
     * if an unhandled one is present.
     * @return the return value
     * @apiNote this method will throw an exception if
     * one is present and is not handled.
     * @see ActionResult#ignoreFail()
     * @see ActionResult#expect(Class, Consumer)
     */
    public T getReturnValue() {
        if(!this.exceptionHandled && this.throwable != null) {
            this.throwUnchecked0();
        }
        return returnValue;
    }

    /**
     * Gets the throwable if any is present
     * @return the throwable
     * @apiNote if this method is called, it's assumed that
     * the exception is handled, and therefore any future calls of
     * {@link ActionResult#getReturnValue()} will not throw an exception unless
     * any processing methods recreated one.
     * It's not like you'd usually retrieve a return value and an exception,
     * but this can come in handy if you are handling an exception safely and
     * need a default return value anyway.
     */
    public Throwable getThrowable() {
        this.exceptionHandled = true;
        return throwable;
    }

    /**
     * Handles the return value if this action was successful
     *
     * @param consumer the handler if the result is {@link Result#SUCCESS}
     * @return itself for chaining purposes
     */
    public ActionResult<T> onSuccess(Consumer<T> consumer) {
        if(result == Result.SUCCESS) {
            consumer.accept(this.getReturnValue());
        }
        return this;
    }

    /**
     * Utility method to handle any throwable if the result is not {@link Result#SUCCESS}.
     * @param consumer a consumer that takes a Result and a Throwable for handling
     * @return itself for chaining purposes
     * @see ActionResult#expect(Class, Consumer)
     */
    public ActionResult<T> onFail(BiConsumer<Result, Throwable> consumer) {
        if(result != Result.SUCCESS && this.throwable != null) {
            consumer.accept(result, this.throwable);
            this.exceptionHandled = true;
        }
        return this;
    }

    /**
     * Utility method to handle any throwable if the result is not {@link Result#SUCCESS}.
     * Useful when the result is irrelevant
     * @param consumer a consumer that takes a Throwable for handling
     * @return itself for chaining purposes
     * @see ActionResult#expect(Class, Consumer)
     */
    public ActionResult<T> onFail(Consumer<Throwable> consumer) {
        if(result != Result.SUCCESS && this.throwable != null) {
            consumer.accept(this.throwable);
            this.exceptionHandled = true;
        }
        return this;
    }

    /**
     * Ignores this exception and moves on
     * @return the object itself
     */
    public ActionResult<T> ignoreFail() {
        this.exceptionHandled = true;
        return this;
    }

    /**
     * Throws the exception if the provided type is caught
     * @param type The type of exception
     * @return itself for chaining purposes, if no such exception exists
     * @param <X> The exception type
     * @throws X The exception
     */
    public <X extends Throwable> ActionResult<T> throwIf(Class<X> type) throws X {
        if(this.throwable == null) return this;
        if(Objects.requireNonNull(type).isAssignableFrom(this.throwable.getClass())) throw (X) this.throwable;
        return this;
    }

    /**
     * Throws the exception as unchecked if any of the provided types are caught
     * @param types the types
     * @return itself if no such exception is present
     */
    @SafeVarargs
    public final ActionResult<T> throwUncheckedIf(Class<? extends Throwable>... types) {
        if(this.throwable == null) return this;
        for(Class<?> clazz : types) {
            if(clazz.isAssignableFrom(this.throwable.getClass())) throwUnchecked0();
        }
        return this;
    }

    /**
     * Throws the exception if any is present
     * @return itself for chaining purposes, if no such exception exists
     * @throws Throwable if one is present, regardless whether it has been handled.
     * @see ActionResult#throwIf(Class)
     */
    public ActionResult<T> throwIfPresent() throws Throwable {
        if(this.throwable != null) throw throwable;
        return this;
    }

    /**
     * Checks whether the result is {@link Result#SUCCESS}
     * @return
     */
    public boolean isSuccessful() {
        return this.result == Result.SUCCESS;
    }

    /**
     * Checks whether the result is not {@link Result#SUCCESS}
     * @return
     */
    public boolean failed() {
        return !isSuccessful();
    }

    // as of version 1.1, this returns a shared object instead
    /**
     * Convenience method to create an ActionResult that holds {@code null}
     * as the return type.
     */
    public static <T> ActionResult<T> success() {
        return (ActionResult<T>) DEFAULT_SUCCESS;
    }

    /**
     * Convenience method to create an ActionResult that the provided element
     * as the return type.
     */
    public static <T> ActionResult<T> success(T element) {
        return new ActionResult<>(Result.SUCCESS, element, null);
    }

    /**
     * Convenience method to create an ActionResult with the current
     * stacktrace as the exception.
     */
    public static <T> ActionResult<T> fail() {
        return new ActionResult<>(Result.FAIL, getDefaultException());
    }

    /**
     * Convenience method to create an action result
     * @param element the element
     * @param throwable the throwable, nullable
     * @return the action result
     * @param <T> action type
     * @since 1.2.1
     */
    public static <T> ActionResult<T> of(T element, Throwable throwable) {
        if(element == null && throwable == null) return success();
        if(element == null) return fail(throwable);
        return success(element);
    }

    // note: If throwable is not null, it used to return FAIL here, if something breaks,
    // this should be the first thing to check out.

    // right as of version 1.1, null-supplied fail no longer fetches
    // the exception, if needed, use ActionResult#fail().
    /**
     * Convenience method to create an ActionResult with the provided
     * throwable as the exception.
     * @param throwable The throwable, nullable.
     * @return An ActionResult holding {@link Result#FAIL}, or {@link Result#ERROR}
     * if throwable is not null
     */
    public static <T> ActionResult<T> fail(Throwable throwable) {
        if(throwable == null) return new ActionResult<>(Result.FAIL, null, null);
        return new ActionResult<>(Result.ERROR, throwable);
    }

    /**
     * Tries to run this action
     * @param action the action to run
     * @return a success action with the value if no exceptions occurred,
     * otherwise return a failed action with the exception
     */
    public static <T> ActionResult<T> run(ThrowingSupplier<T> action) {
        try {
            return new ActionResult<>(Result.SUCCESS, action.get());
        } catch (Throwable e) {
            return new ActionResult<>(Result.ERROR, e);
        }
    }

    /**
     * Tries to run this action
     * @param runnable the runnable for execution, providing a result and return value
     * @return an action holding the result and value if no exceptions occurred,
     * otherwise return a failed action with the exception
     */
    public static <T> ActionResult<T> run(ResultRunnable<T> runnable) {
        try {
            Pair<Result, T> pair = runnable.run();
            return new ActionResult<>(pair.getKey(), pair.getValue());
        } catch (Throwable e) {
            return ActionResult.fail(e);
        }
    }

    /**
     * Tries to run this action
     * @param runnable the runnable for execution
     * @return a success action if no exceptions occurred,
     * otherwise return a failed action with the exception
     */
    public static ActionResult<Void> run(ThrowingRunnable runnable) {
        try {
            runnable.run();
            return ActionResult.success();
        } catch (Throwable e) {
            return ActionResult.fail(e);
        }
    }

    /**
     * Gets the result value
     * @return the result
     */
    public Result getResult() {
        return result;
    }

    private static Throwable getDefaultException() {
        return new RuntimeException("No information provided").fillInStackTrace();
    }

    /**
     * Throws the exception this object currently holds if it is present,
     * otherwise return itself
     * @return itself
     */
    public ActionResult<T> throwUnchecked() {
        throwUnchecked0();
        return this;
    }

    /**
     * internal method to throw existing exception as an unchecked one, otherwise ignore
     */
    private void throwUnchecked0() {
        if(this.throwable == null) return;
        if(this.throwable instanceof Error e) throw e;
        if(this.throwable instanceof RuntimeException rte) throw rte;
        throw new RuntimeException(this.throwable);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionResult<?> that = (ActionResult<?>) o;
        return exceptionHandled == that.exceptionHandled && result == that.result && Objects.equals(returnValue, that.returnValue) && Objects.equals(throwable, that.throwable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, returnValue, throwable, exceptionHandled);
    }
}
