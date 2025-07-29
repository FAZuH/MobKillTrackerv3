package io.github.rainvaporeon.fishutils.utils.eventbus;

import io.github.rainvaporeon.fishutils.action.ActionResult;
import io.github.rainvaporeon.fishutils.utils.eventbus.events.Event;
import io.github.rainvaporeon.fishutils.utils.eventbus.events.EventBusAccessor;
import io.github.rainvaporeon.fishutils.utils.eventbus.events.EventBusSubscriber;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

/**
 * An event bus which fires events to listeners.
 * <p></p>
 * The bus provides several utility methods to
 * listen to certain events and to fire them,
 * only the bus subscribers may find them.
 * <p></p>
 * All methods that are to receive events from the event bus
 * should be annotated with {@link io.github.rainvaporeon.fishutils.utils.eventbus.events.EventBusSubscriber}
 * in order for the bus to acknowledge the class and fire
 * appropriate event.
 */
public class EventBus {

    /**
     * All registered instances in the event bus
     */
    private static final Set<UUID> registeredInstances = new CopyOnWriteArraySet<>();
    protected final UUID identifier;

    /**
     * Classes that are interested in events fired by this event bus
     */
    protected final List<Object> subscribers = new CopyOnWriteArrayList<>();

    /**
     * Buses that this bus is listening to
     */
    protected final Set<EventBus> inheritances = new CopyOnWriteArraySet<>();

    /**
     * The error handler to call when a method invocation fails
     */
    protected Consumer<Throwable> errorHandler;

    /**
     * Whether the bus should be able to receive the same event
     * multiple times. If this is {@code false}, this bus will
     * not write a signature to the event.
     */
    protected final boolean processDuplicates;

    /**
     * The shared event bus instance
     */
    public static final EventBus INSTANCE = new EventBus();

    /**
     * Creates an event bus
     * @param processDuplicates whether this bus should be able to
     *                     receive the same event multiple times
     * @apiNote "Same event" in this context does not indicate that
     * for event a and b, if {@code a.equals(b)} returns true, the
     * event is discarded, but rather if {@code a == b} is true,
     * the event is discarded.
     * In other words, the event is discarded if and only if {@code processDuplicates}
     * is set to false (default) and if the two events have identical identity.
     */
    public EventBus(boolean processDuplicates) {
        UUID identifier;
        // Keeps fetching until there are no duplicating identifiers.

        //noinspection StatementWithEmptyBody
        while(registeredInstances.contains((identifier = UUID.randomUUID())));
        registeredInstances.add(identifier);
        this.identifier = identifier;
        this.processDuplicates = processDuplicates;
        this.errorHandler = t -> {
            throw (t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t));
        };
    }

    public EventBus() {
        this(false);
    }

    /**
     * Sets the error handler for this event bus
     * @param errorHandler the handler, or null to drop errors.
     */
    public void setErrorHandler(Consumer<Throwable> errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void subscribe(Object object) throws InvalidSubscriberException {
        check(object);
        subscribers.add(object);
    }

    public void unsubscribe(Object object) {
        subscribers.remove(object);
    }

    /**
     * Hooks this event bus to a certain parent, listening
     * to all events from the said parent.
     * <p></p>
     * This method does a deep recursion check to see whether
     * the parent inherits from this bus, if so, an {@link IllegalStateException}
     * will be thrown.
     *
     * @param parent the parent
     * @apiNote This will add the current bus instance to the
     * parent's inheritance set, in which is called every time
     * the parent fires an event. In other words, this bus will
     * receive every event the parent receives. In some structures,
     * the event may be received multiple times. If this is intended,
     * the event bus should be created with {@link EventBus#EventBus(boolean)}}
     * and with "processMulti" set to true.
     */
    public void hook(EventBus parent) {
        if(parent == this) throw new IllegalStateException("cannot inherit from self");
        recursionCheck(Objects.requireNonNull(parent) /* Implicit null check */);
        parent.inheritances.add(this);
    }

    public void uninherit(EventBus parent) {
        parent.inheritances.remove(this);
    }

    public void fire(Event event) {
        if(!this.processDuplicates) {
            EventBusAccessor accessor = Secret.getAccessor();
            if(accessor.signed(event, identifier)) return;
            accessor.sign(event, identifier);
        }
        subscribers.forEach(s -> fire(s, event));
        inheritances.forEach(bus -> bus.fire(event));
    }

    protected void recursionCheck(EventBus parent) {
        if(this.inheritances.contains(parent)) throw new IllegalStateException("recursive inheritance");
        this.inheritances.forEach(this::recursionCheck);
    }

    protected void fire(Object o, Event event) {
        EventBusSubscriber a; Class<?> c;
        Method[] methods;
        Object invocationTarget;
        if(o instanceof Class<?> cls) {
            invocationTarget = null;
            methods = cls.getMethods();
        } else {
            invocationTarget = o;
            methods = o.getClass().getDeclaredMethods();
        }

        // 1.2.15: Added EventBus priority
        Arrays.sort(methods, (left, right) -> {
            EventBusSubscriber l = left.getAnnotation(EventBusSubscriber.class);
            EventBusSubscriber r = right.getAnnotation(EventBusSubscriber.class);
            if(l == null && r == null) return 0;
            if(l == null) return -1;
            if(r == null) return 1;
            // the lower the priority, the higher the ordinal.
            return r.priority().ordinal() - l.priority().ordinal();
        });

        for(Method method : methods) {
            condition:
            if((a = method.getAnnotation(EventBusSubscriber.class)) != null &&
               method.getParameterCount() == 1 &&
               method.getParameterTypes()[0].isAssignableFrom((c = event.getClass()))) {
                method.setAccessible(true);
                if(a.value().length == 0 && a.only().length == 0) {
                    ActionResult.run(() -> method.invoke(invocationTarget, event));
                } else {
                    for(Class<?> clazz : a.only()) {
                        if(clazz == c) {
                            ActionResult.run(() -> method.invoke(invocationTarget, event))
                                    .onFail(t -> {
                                        if(t instanceof ReflectiveOperationException rf) throw new InternalError("Failed to invoke " + method + ": ", rf);
                                        if(this.errorHandler != null) this.errorHandler.accept(t);
                                    });
                            break condition;
                        }
                    }
                    for(Class<?> clazz : a.value()) {
                        if(clazz.isAssignableFrom(c)) {
                            ActionResult.run(() -> method.invoke(invocationTarget, event))
                                    .onFail(t -> {
                                        if(t instanceof ReflectiveOperationException rf) throw new InternalError("Failed to invoke " + method + ": ", rf);
                                        if(this.errorHandler != null) this.errorHandler.accept(t);
                                    });
                            break condition;
                        }
                    }
                }
            }
        }
    }

    protected static void check(Object o) {
        EventBusSubscriber a; Class<?> c; int cnt;
        Method[] methods = o instanceof Class<?> ? ((Class<?>) o).getMethods() : o.getClass().getDeclaredMethods();
        for(Method method : methods) {
            if((a = method.getAnnotation(EventBusSubscriber.class)) != null) {
                // only one parameter for a listening method
                if((cnt = method.getParameterCount()) != 1) {
                    throw new InvalidSubscriberException("annotated method " + method + " has " + cnt + " parameters, expected 1");
                }

                if(Event.class.isAssignableFrom((c = method.getParameterTypes()[0]))) {
                    for(Class<?> clazz : a.value()) {
                        // parameter is not event
                        if(!Event.class.isAssignableFrom(clazz)) {
                            throw new InvalidSubscriberException("class " + clazz + " in annotated member " + method + " is not an event type");
                        }
                        // listening to other classes other than parent is a no no
                        if(!c.isAssignableFrom(clazz)) {
                            throw new InvalidSubscriberException("class " + clazz + " is incompatible for listening event type " + c);
                        }
                    }
                    for(Class<?> clazz : a.only()) {
                        // parameter is not event
                        if(!Event.class.isAssignableFrom(clazz)) {
                            throw new InvalidSubscriberException("class " + clazz + " in annotated member " + method + " is not an event type");
                        }
                        // listening to other classes other than parent is a no no
                        if(!c.isAssignableFrom(clazz)) {
                            throw new InvalidSubscriberException("class " + clazz + " is incompatible for listening event type " + c);
                        }
                    }
                    return; // yay!!! we found one, this qualifies as a subscriber then
                } else {
                    throw new InvalidSubscriberException("class " + c + " is not an instance of Event");
                }
            }
        }
        // Deprecated since 1.2
        // throw new InvalidSubscriberException("class " + o.getClass() + " does not have any subscriber methods");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EventBus eventBus = (EventBus) object;
        return Objects.equals(identifier, eventBus.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
