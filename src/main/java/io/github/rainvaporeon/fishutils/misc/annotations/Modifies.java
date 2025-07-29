package io.github.rainvaporeon.fishutils.misc.annotations;

import java.lang.annotation.*;

/**
 * Annotation denoting that this method may be modifying the given
 * parameters. Alternatively, {@link Modifies#NONE} may be used
 * to indicate that no parameters are modified when passing into
 * the annotated method.
 *
 * @since 1.2.5
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Documented
public @interface Modifies {
    String NONE = "";

    // 1.2.6: Removed default tab as anything that uses this is expected
    // to indicate that some fields are modified.
    String[] value();
}
