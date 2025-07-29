package com.spiritlight.adapters.fabric;

import org.jetbrains.annotations.NotNull;

/**
 * Denotes that the implementing interface is an effort to mimic
 * a specific type of existing class. This allows two types of mimicking,
 * first being holding the actual entity internally, and have a representation
 * based on the entity. Secondly is implementing the type itself (must extend from T in that case)
 * which is also valid.
 *
 * @param <T> the mimicking class type
 */
public interface AdaptableEntity<T> {

    /**
     * Fetches the entity this class is adapting to.
     * This method cannot return null, but may return itself
     * if the class is re-implemented to T rather than
     * retaining a reference to itself.
     * @return the mimicking element
     */
    @NotNull T getRepresentativeEntity();

    /**
     * Checks whether this entity is done by implementation, or
     * by holding a representation. The checking is same as running
     * {@link AdaptableEntity#getRepresentativeEntity()} {@code == this}
     * @return whether this object may be implemented to T
     */
    default boolean implemented() {
        return this.getRepresentativeEntity() == this;
    }
}
