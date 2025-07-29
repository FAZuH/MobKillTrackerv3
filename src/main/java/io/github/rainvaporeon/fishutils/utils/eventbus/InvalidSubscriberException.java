package io.github.rainvaporeon.fishutils.utils.eventbus;

import io.github.rainvaporeon.fishutils.utils.eventbus.events.Event;

/**
 * Thrown when the subscribing object to {@link EventBus}
 * has mismatching types (that is, the parameter is not an instance
 * of {@link Event}),
 * or if the object does not contain any subscriber methods.
 */
public class InvalidSubscriberException extends RuntimeException {
    public InvalidSubscriberException() {

    }

    public InvalidSubscriberException(String s) {
        super(s);
    }
}
