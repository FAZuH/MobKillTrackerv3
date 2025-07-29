package com.spiritlight.adapters.fabric.misc.event.events.bus;

import com.spiritlight.adapters.fabric.misc.event.events.Event;
public interface IEventBusSubscriber {

    void onEvent(Event event);

}
