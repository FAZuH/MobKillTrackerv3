package io.github.rainvaporeon.fishutils.misc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that this method may
 * fail catastrophically if used without proper care.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MayExplode {

    /**
     * Determines how bad it can get
     * @return the severity
     */
    Severity value() default Severity.IRRECOVERABLE;

    enum Severity {
        /**
         * The damage may be irrecoverable.
         */
        IRRECOVERABLE,
        /**
         * The damage may be recoverable.
         */
        RECOVERABLE
    }
}
