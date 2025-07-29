package io.github.rainvaporeon.fishutils.utils.eventbus.events;

import io.github.rainvaporeon.fishutils.utils.eventbus.Secret;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The common class for all events
 */
public abstract class Event {
    /**
     * The event buses' signatures, to prevent the same event
     * from arriving to a bus multiple times
     */
    private final Set<UUID> signatures = new HashSet<>();

    private final Object mutex = new Object();

    static {
        Secret.setAccessor(new EventBusAccessor() {
            @Override
            public void sign(Event event, UUID identifier) {
                event.sign(identifier);
            }

            @Override
            public boolean signed(Event event, UUID identifier) {
                return event.isSigned(identifier);
            }
        });
    }

    /**
     * signs this event
     * @param identifier the bus identifier
     */
    private void sign(UUID identifier) {
        synchronized (mutex) {
            this.signatures.add(identifier);
        }
    }

    /**
     * checks for sign status
     * @param identifier the bus identifier
     * @return true if the identifier is present on the record
     */
    private boolean isSigned(UUID identifier) {
        return this.signatures.contains(identifier);
    }
}
