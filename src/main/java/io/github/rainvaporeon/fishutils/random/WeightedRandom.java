package io.github.rainvaporeon.fishutils.random;

import io.github.rainvaporeon.fishutils.utils.Numbers;
import io.github.rainvaporeon.fishutils.utils.enums.Secure;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Stream;

/**
 * A weighted random, this type of random is synchronized, meaning
 * it may be used across threads safely without throwing an exception.
 * <p>
 *     The underlying random instance always uses a SecureRandom unless
 *     the {@link io.github.rainvaporeon.fishutils.utils.enums.Secure} parameter is NO.
 * @param <T>
 */
public class WeightedRandom<T> {
    private final Map<T, Integer> weightMap = new HashMap<>();
    private final Random RANDOM;

    public WeightedRandom() {
        this(Secure.YES);
    }

    public WeightedRandom(Secure secure) {
        if(secure == Secure.NO) {
            RANDOM = new Random();
        } else {
            byte[] random = new byte[256];
            Numbers.nextBytes(random);
            RANDOM = new SecureRandom(random);
        }
    }

    private int totalWeight = 0;

    private final Object mutex = new Object();

    /**
     * Generates the next element
     * @return the element
     */
    public T getNext() {
        synchronized (mutex) {
            int value = RANDOM.nextInt(totalWeight) + 1;
            for(Map.Entry<T, Integer> entry : weightMap.entrySet()) {
                value -= entry.getValue();
                if(value <= 0) {
                    return entry.getKey();
                }
            }
        }
        throw new InternalError("totalWeight is larger than all items.");
    }

    /**
     * Retrieves all possible outcomes
     * @return
     */
    public Set<T> getOutcomes() {
        return Set.copyOf(weightMap.keySet());
    }

    /**
     * Adds a new object into the generator
     * @param object The object, it should not be in the list already.
     * @param weight The weight of the object, should be higher than 0
     */
    public void addObject(T object, int weight) {
        if(weight < 0) throw new IllegalArgumentException("weight cannot be less than 0");

        synchronized (mutex) {
            weightMap.put(object, weight);
            updateWeight();
        }
    }

    /**
     * Updates the object with a new weight
     * @param object the object
     * @param weight the weight of the object, should be higher than 0
     * @apiNote This call is equal to calling {@link WeightedRandom#removeObject(Object)}
     * and then call {@link WeightedRandom#addObject(Object, int)}
     */
    public void updateObject(T object, int weight) {
        removeObject(object);
        addObject(object, weight);
    }

    /**
     * Removes this object from the random
     * @param object the object
     */
    public void removeObject(T object) {
        synchronized (mutex) {
            weightMap.remove(object);
            updateWeight();
        }
    }

    /**
     * Retrieves a stream source of this generator, the
     * underlying stream is infinitely-supplying, and
     * structure modifications will have an impact
     * on the generated object.
     * @return an infinitely supplying stream which calls {@link WeightedRandom#getNext()}
     */
    public Stream<T> infinitelySupplyingStream() {
        return Stream.generate(this::getNext);
    }

    /**
     * Retrieves the probability (in double) of getting this value in a roll
     * @param object the object
     * @return 0 if it is absent or is configured to have no odds of generating,
     * otherwise a number between 0.0 to 1.0 denoting the odds of rolling this element.
     */
    public double getProbabilityOf(T object) {
        if(!weightMap.containsKey(object)) return 0;
        return (double) weightMap.get(object) / totalWeight;
    }

    private void updateWeight() {
        totalWeight = weightMap.values().stream().mapToInt(Integer::intValue).sum();
    }
}
