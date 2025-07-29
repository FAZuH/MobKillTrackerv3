package io.github.rainvaporeon.fishutils.misc.annotations;

import java.lang.annotation.*;

/**
 * Indicates that this type or method delegates the
 * method call to a shadowed object. That is, if an
 * object {@code A} is shading another object {@code B},
 * then {@code A} may want to return {@code B}'s hashcode,
 * rather than making a new implementation.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DelegatesToShadow {

    /**
     * Annotates the shaded target
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @java.lang.annotation.Target(ElementType.FIELD)
    @interface Target {
    }
}
