package io.github.rainvaporeon.fishutils;

import io.github.rainvaporeon.fishutils.misc.arrays.ArrayLike;

import java.util.Arrays;
import java.util.Map;

public class PrimitiveHelper {

    private static final ArrayLike<String> PRIMITIVES = ArrayLike.of(
            "byte", "short", "char", "int", "float", "double", "long", "boolean"
    );

    private static final Map<String, Class<?>> PRIMITIVE_CLASS = Map.of(
            "int", Integer.class,
            "double", Double.class,
            "char", Character.class,
            "float", Float.class,
            "boolean", Boolean.class,
            "long", Long.class,
            "byte", Byte.class,
            "short", Short.class
    );

    /**
     * Replaces each occurrence of {} into the primitive type.
     * If uppercase on first character is preferred, use {A} instead.
     * If the canonical class name is preferred, use {C} instead
     * @param form the string form
     * @param exclusions the exclusions, case-sensitive.
     * @return a string with {} replaced with primitive types
     */
    public static String generate(String form, String... exclusions) {
        StringBuilder builder = new StringBuilder();
        for(String type : PRIMITIVES) {
            if(Arrays.asList(exclusions).contains(type)) continue;
            builder.append(form
                    .replace("{}", type)
                    .replace("{A}", Character.toUpperCase(type.charAt(0)) + type.substring(1))
                    .replace("{C}", PRIMITIVE_CLASS.get(type).getSimpleName())).append("\n");

        }
        return builder.toString();
    }
}
