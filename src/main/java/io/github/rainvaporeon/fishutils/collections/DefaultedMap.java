package io.github.rainvaporeon.fishutils.collections;

import java.util.HashMap;

/**
 * A map which defaults to a value if it is absent. It may be null.
 * @param <K> the key type
 * @param <V> the value type
 * @apiNote This map may return the default value on its get and put operation
 * if no value was previously present.
 */
public class DefaultedMap<K, V> extends HashMap<K, V> {
    private final V DEFAULT_VALUE;

    public DefaultedMap(V defaultValue) {
        this.DEFAULT_VALUE = defaultValue;
    }

    public V getDefault() {
        return DEFAULT_VALUE;
    }

    @Override
    public V get(Object key) {
        V v = super.get(key);
        return v == null ? DEFAULT_VALUE : v;
    }

    @Override
    public V put(K key, V value) {
        V ret = super.put(key, value);
        if(ret == null) return DEFAULT_VALUE;
        return ret;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V ret = super.putIfAbsent(key, value);
        if(ret == null) return DEFAULT_VALUE;
        return ret;
    }
}
