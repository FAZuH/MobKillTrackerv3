package io.github.rainvaporeon.fishutils.misc.annotations;

import java.lang.annotation.*;

/**
 * Indicates that this method returns a new instance of something
 * rather than pulling from existing objects and/or modifying something
 * and returns it.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Documented
public @interface New {
}
