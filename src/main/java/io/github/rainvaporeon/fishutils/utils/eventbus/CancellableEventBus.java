package io.github.rainvaporeon.fishutils.utils.eventbus;

import io.github.rainvaporeon.fishutils.action.ActionResult;
import io.github.rainvaporeon.fishutils.utils.eventbus.events.Event;
import io.github.rainvaporeon.fishutils.utils.eventbus.events.EventBusAccessor;
import io.github.rainvaporeon.fishutils.utils.eventbus.events.EventBusSubscriber;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * An event bus that is capable of firing events that can be canceled.
 * <p></p>
 * This class behaves nearly identical to the default implementation, but
 * with {@link CancellableEventBus#fireCancellable(Event)} to fire events
 * that listeners can return {@code true} to cancel it half-way.
 * @since 1.2.19
 */
public class CancellableEventBus extends EventBus {

    public boolean fireCancellable(Event event) {
        if(!this.processDuplicates) {
            EventBusAccessor accessor = Secret.getAccessor();
            if(accessor.signed(event, identifier)) return false;
            accessor.sign(event, identifier);
        }

        for (Object o : subscribers) {
            if (fireCancellable(o, event)) return true;
        }
        for (EventBus bus : inheritances) {
            if (bus instanceof CancellableEventBus ceb) {
                if (ceb.fireCancellable(event)) return true;
            } else {
                bus.fire(event);
            }
        }
        return false;
    }

    protected boolean fireCancellable(Object o, Event event) {
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
                    boolean boo = ActionResult.run(() -> method.invoke(invocationTarget, event))
                            .onFail(t -> {
                                if (t instanceof ReflectiveOperationException rf) throw new InternalError("Failed to invoke " + method + ": ", rf);
                                if (this.errorHandler != null) this.errorHandler.accept(t);
                            })
                            .map(obj -> obj instanceof Boolean && (boolean) obj).getReturnValue();
                    if (boo) return true;
                } else {
                    for(Class<?> clazz : a.only()) {
                        if(clazz == c) {
                            boolean boo = ActionResult.run(() -> method.invoke(invocationTarget, event))
                                    .onFail(t -> {
                                        if (t instanceof ReflectiveOperationException rf) throw new InternalError("Failed to invoke " + method + ": ", rf);
                                        if (this.errorHandler != null) this.errorHandler.accept(t);
                                    })
                                    .map(obj -> obj instanceof Boolean && (boolean) obj).getReturnValue();
                            if (boo) return true;
                            break condition;
                        }
                    }
                    for(Class<?> clazz : a.value()) {
                        if(clazz.isAssignableFrom(c)) {
                            boolean boo = ActionResult.run(() -> method.invoke(invocationTarget, event))
                                    .onFail(t -> {
                                        if (t instanceof ReflectiveOperationException rf) throw new InternalError("Failed to invoke " + method + ": ", rf);
                                        if (this.errorHandler != null) this.errorHandler.accept(t);
                                    })
                                    .map(obj -> obj instanceof Boolean && (boolean) obj).getReturnValue();
                            if (boo) return true;
                            break condition;
                        }
                    }
                }
            }
        }
        return false;
    }
}
