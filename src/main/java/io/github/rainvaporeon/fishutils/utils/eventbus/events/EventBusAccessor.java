package io.github.rainvaporeon.fishutils.utils.eventbus.events;

import java.util.UUID;

public interface EventBusAccessor {
    void sign(Event event, UUID identifier);

    boolean signed(Event event, UUID identifier);
}
