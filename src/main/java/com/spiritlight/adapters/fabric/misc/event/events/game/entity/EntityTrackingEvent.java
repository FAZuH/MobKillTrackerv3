package com.spiritlight.adapters.fabric.misc.event.events.game.entity;

import com.spiritlight.adapters.fabric.entity.FabricEntity;
import com.spiritlight.adapters.fabric.misc.event.events.Event;

public class EntityTrackingEvent extends Event {

    private final FabricEntity entity;

    public EntityTrackingEvent(FabricEntity entity) {
        this.entity = entity;
    }

    public FabricEntity getEntity() {
        return entity;
    }

}
