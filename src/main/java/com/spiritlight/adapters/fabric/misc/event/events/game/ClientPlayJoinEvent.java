package com.spiritlight.adapters.fabric.misc.event.events.game;

import com.spiritlight.adapters.fabric.game.FabricClient;
import com.spiritlight.adapters.fabric.misc.event.events.Event;

public class ClientPlayJoinEvent extends Event {
    private final FabricClient client;

    public ClientPlayJoinEvent(FabricClient client) {
        this.client = client;
    }

    public FabricClient getClient() {
        return client;
    }
}
