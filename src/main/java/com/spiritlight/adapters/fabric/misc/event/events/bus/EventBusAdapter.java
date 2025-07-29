package com.spiritlight.adapters.fabric.misc.event.events.bus;

import com.spiritlight.adapters.fabric.misc.event.events.Event;
import com.spiritlight.adapters.fabric.misc.event.events.game.ClientCommandInitializationEvent;
import com.spiritlight.adapters.fabric.misc.event.events.game.ClientPlayJoinEvent;
import com.spiritlight.adapters.fabric.misc.event.events.game.ClientTickEndEvent;
import com.spiritlight.adapters.fabric.misc.event.events.game.RunnableExecutionEvent;
import com.spiritlight.adapters.fabric.misc.event.events.game.entity.EntityTrackingEvent;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventBusAdapter implements IEventBusSubscriber {

    @AdapterMethod
    public void onEntityTracking(EntityTrackingEvent event) {

    }

    @AdapterMethod
    public void onClientCommandInitialization(ClientCommandInitializationEvent event) {

    }

    @AdapterMethod
    public void onRunnableExecution(RunnableExecutionEvent event) {

    }

    @AdapterMethod
    public void onClientTickEnd(ClientTickEndEvent event) {

    }

    @AdapterMethod
    public void onClientPlayJoin(ClientPlayJoinEvent event) {

    }

    private static final String EVENT_KEY = "Event";
    @Override
    public final void onEvent(Event event) {
        String key = event.getClass().getSimpleName();
        try {
            String methodName = "on" + key.substring(0, key.lastIndexOf(EVENT_KEY));

            Method method = this.getClass().getMethod(methodName, event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);

        } catch (Exception e) {
            final String reason = getReason(e, key);

            LogManager.getLogger("FishHelper/EventBus").error("Failed to fire event {}: {}", event.getClass().getCanonicalName(), reason, e);
        }
    }

    @NotNull
    private static String getReason(Exception e, String key) {
        String reason;

        if(e instanceof StringIndexOutOfBoundsException) {
            reason = "invalid class name: for key " + key;
        } else if (e instanceof NoSuchMethodException) {
            reason = "method " + key + " does not exist";
        } else if (e instanceof NullPointerException) {
            reason = "null";
        } else if (e instanceof InvocationTargetException ite) {
            reason = "exception thrown from invocation: " + ite.getTargetException().getMessage();
        } else if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException) {
            reason = "invalid access queried for operation: " + e.getMessage();
        } else {
            reason = "unexpected exception: " + e.getClass().getCanonicalName();
        }
        return reason;
    }
}
