package io.github.rainvaporeon.fishutils.tests;

import io.github.rainvaporeon.fishutils.misc.ThrowingRunnable;
import io.github.rainvaporeon.fishutils.misc.ThrowingSupplier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Represents a test, this class provides multiple useful
 * methods to test for various assertions, as well as running
 * test cases on a certain method.
 */
public class Test<T> implements TestComponent<T> {
    private Object value;
    private String lastMessage;
    private boolean lastSuccess;
    private final ThrowingSupplier<T> action;
    private Throwable lastThrowable;

    public Test(ThrowingSupplier<T> action) {
        this.action = action;
        this.run();
    }

    @Override
    public TestComponent<T> run(Object... args) {
        try {
            this.value = action.get();
        } catch (Throwable t) {
            this.lastMessage = t.getMessage();
            this.lastThrowable = t;
        }
        return this;
    }

    @Override
    public TestComponent<T> orElse(ThrowingRunnable action) {
        if(!lastSuccess) action.runUnchecked(t -> new RuntimeException(lastMessage, t));
        return this;
    }

    @Override
    public TestComponent<T> equals(Object other, String... message) {
        if(Objects.equals(this.value, other)) {
            lastSuccess = true;
            return this;
        }
        throw new RuntimeException(message == null ? "not equal" : toString(message));
    }

    @Override
    public TestComponent<T> larger(Comparable<T> other, String... message) {
        if(this.value instanceof Comparable cmp) {
            int val = cmp.compareTo(other);
            if(val > 0) {
                lastSuccess = true;
                return this;
            }
        }
        throw new RuntimeException(message == null ? "not larger" : toString(message));
    }

    @Override @SuppressWarnings({"rawtypes", "unchecked"})
    public TestComponent<T> lesser(Comparable<T> other, String... message) {
        if(this.value instanceof Comparable cmp) {
            int val = cmp.compareTo(other);
            if(val < 0) {
                lastSuccess = true;
                return this;
            }
        }
        throw new RuntimeException(message == null ? "not lesser" : toString(message));
    }

    @Override
    public TestComponent<T> shouldThrow(Class<? extends Throwable> clazz, String... message) {
        if(lastThrowable != null) {
            if(clazz.isAssignableFrom(lastThrowable.getClass())) {
                return this;
            } else {
                throw new RuntimeException("Expected " + clazz.getSimpleName() + ", got " + lastThrowable.getClass().getSimpleName());
            }
        }
        throw new RuntimeException("Expected " + clazz.getSimpleName() + ", got nothing");
    }

    public long getExecutionTime() {
        long l = System.currentTimeMillis();
        action.getUnchecked();
        long l2 = System.currentTimeMillis();
        return l2 - l;
    }

    private void throwIfPresent() {
        if(lastThrowable != null) throw new RuntimeException("Unhandled exception: ", lastThrowable);
    }

    private String toString(String... content) {
        return String.join(" ", content);
    }
}
