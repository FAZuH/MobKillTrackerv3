package io.github.rainvaporeon.fishutils.utils;

import java.security.SecureRandom;
import java.util.Random;

public class Numbers {
    private static final Random RANDOM = new SecureRandom();

    public static byte[] getBytes(long value) {
        char[] chars = Long.toBinaryString(value).toCharArray();
        byte[] bytes = new byte[Long.SIZE];
        for(int i = bytes.length - 1, j = chars.length - 1; i > 0 && j > 0; i--, j--) {
            bytes[i] = (byte) (chars[j] - '0');
        }
        return bytes;
    }

    public static byte[] getBytes(int value) {
        char[] chars = Integer.toBinaryString(value).toCharArray();
        byte[] bytes = new byte[Integer.SIZE];
        for(int i = bytes.length - 1, j = chars.length - 1; i > 0 && j > 0; i--, j--) {
            bytes[i] = (byte) (chars[j] - '0');
        }
        return bytes;
    }

    public static void nextBytes(byte[] bytes) {
        RANDOM.nextBytes(bytes);
    }
}
