package io.github.rainvaporeon.fishutils.internal;

import io.github.rainvaporeon.fishutils.internal.accessor.StableFieldAccess;
import io.github.rainvaporeon.fishutils.misc.StableField;
import io.github.rainvaporeon.fishutils.misc.annotations.MayExplode;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class UtilityAccess {
    private static final UtilityAccess instance = new UtilityAccess();

    private static final Map<String, Class<?>> classMap = new HashMap<>() {{
        StableField.ensureInitialized();
        put("stableFieldAccess", StableField.class);
    }};

    @MayExplode
    public static UtilityAccess getInstance() {
        return instance;
    }

    @MayExplode
    public StableFieldAccess getStableFieldAccess() {
        return stableFieldAccess;
    }

    public static void setAccess(String parameter, Object value) {
        Class<?> caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        Class<?> expected = UtilityAccess.classMap.get(parameter);
        if(caller != expected) throw new IllegalArgumentException("illegal caller class");

        try {
            Field field = UtilityAccess.class.getDeclaredField(parameter);
            field.setAccessible(true);
            field.set(UtilityAccess.getInstance(), value);
        } catch (ReflectiveOperationException ex) {
            throw new AssertionError("failed to initialize access field " + parameter + "@" + value + ": ", ex);
        }
    }

    private StableFieldAccess stableFieldAccess;


}
