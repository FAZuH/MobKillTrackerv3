package com.spiritlight.mobkilltracker.v3.events;

import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;

public class CompletionEvent {
    private final DataHandler handler;

    public CompletionEvent(DataHandler handler) {
        Message.debugv("Constructing CompletionEvent for DataHandler " + handler);
        this.handler = handler;
    }

    public DataHandler getHandler() {
        return handler;
    }
}
