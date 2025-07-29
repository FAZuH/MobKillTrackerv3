package io.github.rainvaporeon.fishutils.misc;

import io.github.rainvaporeon.fishutils.internal.UtilityAccess;
import io.github.rainvaporeon.fishutils.internal.accessor.StableFieldAccess;
import io.github.rainvaporeon.fishutils.misc.annotations.DelegatesToShadow;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A stable field that updates at most once during the program runtime.
 * @param <T> the type
 * @since 1.2.6
 */
public class StableField<T> implements Serializable, Cloneable {
    @DelegatesToShadow.Target
    private T t;
    private boolean modified;

    /**
     * Creates a stable field with the default value as {@code null}
     */
    public StableField() {
        this(null);
    }

    /**
     * Creates a stable field with the default value as
     * the value provided.
     * @param t the value
     */
    public StableField(T t) {
        this.t = t;
        this.modified = false;
    }

    /**
     * Sets this stable field to a new value
     * @param t the value
     * @throws IllegalStateException if it was previously modified
     */
    public void set(T t) {
        if(modified) throw new IllegalStateException("stable field modified more than once");
        this.modified = true;
        this.t = t;
    }

    /**
     * Gets the value
     * @return the value
     */
    public T get() {
        return t;
    }

    /**
     * Checks whether this field was modified before. If yes,
     * subsequent calls to {@link StableField#set(Object)} will throw
     * an exception.
     * @return whether this field was modified before
     */
    public boolean isModified() {
        return modified;
    }

    /**
     * Checks whether the two objects are equal.
     * This may return true if the underlying object's
     * {@code equals} method returns true.
     * @param object the object to compare to
     * @return true if this object's underlying object is identical
     * to the other, or if both stable fields hold the same object
     */
    @Override @DelegatesToShadow
    public boolean equals(Object object) {
        if(Objects.equals(t, object)) return true;
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        StableField<?> that = (StableField<?>) object;
        return Objects.equals(t, that.t);
    }

    @Override @DelegatesToShadow
    public int hashCode() {
        return Objects.hashCode(t);
    }

    @Override @DelegatesToShadow
    public String toString() {
        return String.valueOf(t);
    }

    @Override
    public StableField<T> clone() {
        if(t instanceof Cloneable) {
            try /* evil dark magic */ {
                Method method = t.getClass().getMethod("clone");
                method.setAccessible(true);
                StableField<T> ret = new StableField<>((T) method.invoke(t));
                if(this.modified) ret.modified = true;
                return ret; // default to false
            } catch (Exception ex) {
                throw new AssertionError("clone failed on cloneable type " + t.getClass().getName() + ": ", ex);
            }
        } else {
            throw new RuntimeException("calling clone to non-cloneable type " + t.getClass().getName());
        }
    }

    public static void ensureInitialized() { /* static loading */}

    static {
        UtilityAccess.setAccess("stableFieldAccess", new StableFieldAccess() {
            @Override
            public <T> void updateField(StableField<T> field, T value) {
                field.t = value;
            }
        });
    }
}
