package io.github.rainvaporeon.fishutils.misc;

import java.util.Objects;
import java.util.function.Consumer;

public class FieldLookup {
    private final Class<?> type;
    private final Object target;

    private FieldLookup(Object object) {
        this.type = object instanceof Class ? (Class<?>) object : object.getClass();
        this.target = type == object ? null : Objects.requireNonNull(object);
    }

    public Field get(String field) {
        try {
            java.lang.reflect.Field f = type.getDeclaredField(field);
            f.setAccessible(true);
            return new Field(f.get(target));
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static FieldLookup getInstance(Object element) {
        return new FieldLookup(element);
    }

    public static class Field {
        private final Object value;

        private Field(Object value) {
            this.value = value;
        }

        public <T> T as(Class<T> clazz) {
            return (T) value;
        }

        public void thenAccept(Consumer<Object> consumer) {
            consumer.accept(value);
        }

        public <T> void thenAcceptT(Consumer<T> consumer, Class<T> clazz) {
            consumer.accept((T) value);
        }

        public Object get() {
            return value;
        }
    }
}
