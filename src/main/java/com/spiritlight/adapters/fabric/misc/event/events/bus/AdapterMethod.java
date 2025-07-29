package com.spiritlight.adapters.fabric.misc.event.events.bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that this method is an adapter method for {@link com.spiritlight.adapters.fabric.misc.event.events.bus.EventBusAdapter}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AdapterMethod {
}
