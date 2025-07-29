package io.github.rainvaporeon.fishutils.utils.eventbus;

import io.github.rainvaporeon.fishutils.utils.eventbus.events.Event;
import io.github.rainvaporeon.fishutils.utils.eventbus.events.EventBusAccessor;

public class Secret {
    private static EventBusAccessor accessor;

    static EventBusAccessor getAccessor() {
        return accessor;
    }

    public static void setAccessor(EventBusAccessor accessor) {
        if(StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass() != Event.class) {
            throw new SecurityException("Secret#setAccessor(EventBusAccessor) may only be called internally.");
        }
        Secret.accessor = accessor;
    }
}
