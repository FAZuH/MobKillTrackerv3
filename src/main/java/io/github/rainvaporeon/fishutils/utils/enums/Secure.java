package io.github.rainvaporeon.fishutils.utils.enums;

/**
 * Enum to indicate whether this implementation is secure,
 * by either providing secure processes, or has done secure measures.
 * If a class' Secure is YES, one may assume that the object
 * is safe, and if a method receives this as a parameter, it should
 * adjust accordingly (usually for optimization purposes)
 */
public enum Secure {
    YES,
    /**
     * Up to implementation, or up to caller. Often this is treated equally
     * to providing NO, but implementations can be more flexible if this is
     * provided, whatever more convenient goes.
     */
    MAYBE,
    NO
}
