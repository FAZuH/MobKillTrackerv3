package io.github.rainvaporeon.fishutils.utils;

import io.github.rainvaporeon.fishutils.collections.DefaultedMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A general stopwatch providing basic timer functionalities.
 * @since 1.2.7
 */
public class Stopwatch {
    private long startTime;
    // Holds the time since the fence started
    private final Map<String, Long> timeMap = new DefaultedMap<>(0L);
    // Holds the records
    private final Map<String, Long> records = new LinkedHashMap<>();
    // Holds the name translations
    private final Map<String, String> nameMap = new HashMap<>();

    public Stopwatch() {}

    /**
     * Starts the stopwatch. Clearing all the fenced time in the process.
     */
    public void start() {
        clear();
        startTime = System.nanoTime();
    }

    /**
     * Adds a fence to current stopwatch. This value can be retrieved later on.
     * @param name the fence name
     * @return the previous time elapsed for the fence
     */
    public long fence(String name) {
        Long l = timeMap.put(Objects.requireNonNull(name), System.nanoTime());
        return l == null ? 0 : System.nanoTime() - l;
    }

    /**
     * Retrieves the time elapsed since this fence
     * @param name the fence name
     * @return the time, -1 if it is not present.
     */
    public long get(String name) {
        if(!timeMap.containsKey(name)) return -1L;
        return System.nanoTime() - timeMap.get(name);
    }

    /**
     * Records the elapsed time since this key was recorded
     * @param name the name
     * @throws IllegalStateException if this key was not fenced.
     */
    public void record(String name) {
        if(!timeMap.containsKey(name)) throw new IllegalStateException("unknown time name: " + name);
        records.put(name, System.nanoTime() - timeMap.get(name));
    }

    /**
     * The name to use in place of the recorded key,
     * this key is only used on retrieval of the string
     * representation on this object
     * @param name the name
     * @param alias the alias
     */
    public void name(String name, String alias) {
        nameMap.put(name, alias);
    }

    /**
     * Retrieves all time maps, the String being the key, and the value being
     * time elapsed. Null is used as a key here to denote the time since this
     * stopwatch started.
     * @return the time map, and the null key being time elapsed since this
     * watch has started.
     */
    public Map<String, Long> getAll() {
        Map<String, Long> map = new HashMap<>();
        timeMap.forEach((str, time) -> {
            if(time == null) return;
            map.put(str, System.nanoTime() - time);
        });
        map.put(null, System.nanoTime() - startTime);
        return map;
    }

    /**
     * Stops this stopwatch and returns the time elapsed since this watch started
     * @return the time since this watch started
     */
    public long stop() {
        return System.nanoTime() - startTime;
    }

    /**
     * Clears all fence mappings.
     */
    public void clear() {
        timeMap.clear();
    }

    /**
     * Returns a string representation of all times
     * that was recorded with {@link Stopwatch#record(String)}
     * @return the record string
     */
    public String getRecordString() {
        StringBuilder builder = new StringBuilder();
        records.forEach((name, time) -> {
            builder.append(nameMap.getOrDefault(name, name)).append(":").append(" ").append(time).append("ns").append(" (~").append(String.format("%.2f", time / 1000000d)).append("ms").append(")").append("\n");
        });
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        timeMap.forEach((name, time) -> {
            builder.append(nameMap.getOrDefault(name, name)).append(":").append(" Since ").append(time).append("\n");
        });
        if (!records.isEmpty()) {
            records.forEach((name, time) -> {
                builder.append(nameMap.getOrDefault(name, name)).append(":").append(" ").append(time).append("ns").append(" (~").append(String.format("%.2f", time / 1000000d)).append("ms").append(")").append("\n");
            });
        }
        return builder.toString();
    }
}
