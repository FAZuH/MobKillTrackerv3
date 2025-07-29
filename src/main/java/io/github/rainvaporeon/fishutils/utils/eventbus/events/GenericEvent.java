package io.github.rainvaporeon.fishutils.utils.eventbus.events;

public abstract class GenericEvent<T> extends Event {
    private final Class<?> type;

    public GenericEvent(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
