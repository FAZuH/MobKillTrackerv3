package io.github.rainvaporeon.fishutils.internal.accessor;

import io.github.rainvaporeon.fishutils.misc.StableField;

public interface StableFieldAccess {

    <T> void updateField(StableField<T> field, T value);

}
