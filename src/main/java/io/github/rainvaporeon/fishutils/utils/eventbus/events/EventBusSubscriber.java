package io.github.rainvaporeon.fishutils.utils.eventbus.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for the event bus to know which event to fire to.
 * <p></p>
 * The annotated method must have only one parameter, and it
 * should extend from the {@link Event} class.
 * @apiNote If the annotated method has the {@code static} modifier,
 * then that method has to be {@code public}, otherwise the bus
 * will not find the method.
 * @see EventBusSubscriber#value()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventBusSubscriber {
    /**
     * The event class this method is interested in
     * @return the classes and subclasses this method may
     * be interested in
     * @apiNote if this field is set, the bus will only fire
     * events of the provided classes to the method
     */
    Class<? extends Event>[] value() default {};

    /**
     * The event class this method is exclusively interested in
     * @return the classes this method may be interested in
     * @apiNote this field denotes a list of classes this listening method
     * can receive, unlike value, this one listens to only these classes and
     * not subclasses of them.
     */
    Class<? extends Event>[] only() default {};

    /**
     * The priority of this subscriber
     * @return the priority, normal by default
     */
    Priority priority() default Priority.NORMAL;

    /**
     * Priority for the event, higher event priority
     * will always receive the event before lower priority,
     * while there are no guarantees for two subscribers
     * at the same priority to have any order of receiving.
     */
    enum Priority {
        HIGHEST,
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }
}
