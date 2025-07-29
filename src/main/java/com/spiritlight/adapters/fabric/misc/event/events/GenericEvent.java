package com.spiritlight.adapters.fabric.misc.event.events;

public class GenericEvent<T> extends Event {

    private final Class<?> type;

    public GenericEvent(Class<T> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
