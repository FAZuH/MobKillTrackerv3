package io.github.rainvaporeon.fishutils.utils.secure;

import io.github.rainvaporeon.fishutils.utils.Numbers;
import io.github.rainvaporeon.fishutils.utils.collectors.StringCollectors;
import io.github.rainvaporeon.fishutils.utils.enums.Secure;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static io.github.rainvaporeon.fishutils.utils.enums.Secure.YES;

public class SeededGenerator {
    public static final String ALPHABETS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
    public static final String NUMERICAL = "0123456789";
    public static final String ALPHABET_NUMERICAL = ALPHABETS + NUMERICAL;
    private final String allowedCharacters;
    private final Supplier<? extends Random> randomInst;
    private long genSeed;
    private final boolean allowModifications;

    public static final SeededGenerator ALPHABET_ONLY = new SeededGenerator(ALPHABETS, YES, null, false);
    public static final SeededGenerator NUMERICS_ONLY = new SeededGenerator(NUMERICAL, YES, null, false);
    public static final SeededGenerator ALPHABET_NUMERICS = new SeededGenerator(ALPHABET_NUMERICAL, YES, null, false);

    public SeededGenerator(String allowedCharacters) {
        this(allowedCharacters, Secure.NO);
    }

    public SeededGenerator(String allowedCharacters, Secure secure) {
        this(allowedCharacters, secure, null);
    }

    public SeededGenerator(String allowedCharacters, Secure secure, Supplier<? extends Random> randomSupplier) {
        this(allowedCharacters, secure, randomSupplier, true);
    }

    public SeededGenerator(String allowedCharacters, Secure secure, Supplier<? extends Random> randomSupplier, boolean allowModifications) {
        this.allowedCharacters = allowedCharacters;
        this.genSeed = System.nanoTime();
        this.randomInst = randomSupplier == null ? (secure == YES ? () -> new SecureRandom(Numbers.getBytes(this.getSeed()))
                : () -> new Random(this.getSeed())) : randomSupplier;
        this.allowModifications = allowModifications;
    }

    public String generate(int size) {
        return this.generate()
                .limit(size)
                .boxed()
                .collect(StringCollectors.concat());
    }

    public boolean allowModifications() {
        return allowModifications;
    }

    public IntStream generate() {
        nextSeed();
        final Random random = randomInst.get();
        return IntStream.generate(() -> allowedCharacters.charAt(random.nextInt(allowedCharacters.length())));
    }

    /**
     * XOR-s the current seed
     */
    public void addNoise(long noise) {
        if(!allowModifications) throw new UnsupportedOperationException("Modifications not allowed");
        this.genSeed ^= noise;
    }

    public void addNoise(Object noise) {
        this.addNoise(genSeed(noise));
    }

    public void setSeed(long seed) {
        if(!allowModifications) throw new UnsupportedOperationException("Modifications not allowed");
        this.genSeed = seed;
    }

    public void setSeed(Object seed) {
        this.setSeed(genSeed(seed));
    }

    /**
     * Refreshes the seed of this generator, the process is independent on
     * current seed value, therefore, is unable to provide a consistent
     * output value
     */
    public void refreshSeed() {
        if(!allowModifications) throw new UnsupportedOperationException("Modifications not allowed");
        this.setSeed(System.nanoTime());
    }

    public long getSeed() {
        return this.genSeed;
    }

    private void nextSeed() {
        Random random = new Random(genSeed);
        this.genSeed = random.nextLong();
    }

    private static long genSeed(Object in) {
        if(in == null) return 0;
        String stringValue = String.valueOf(in);
        long value = 0;
        for(char c : stringValue.toCharArray()) {
            value += c | in.hashCode() >>> 1;
            value ^= c;
        }
        value *= String.valueOf(in).chars().sum();
        return value;
    }
}
