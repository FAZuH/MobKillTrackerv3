package io.github.rainvaporeon.fishutils.misc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this implementation is <b>unstable</b>.<br />
 * This may include that this method may be a work-in-progress, unstable,
 * or extremely prone to future changes.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Beta {
    boolean unstable() default false;

    String message() default "";
}
