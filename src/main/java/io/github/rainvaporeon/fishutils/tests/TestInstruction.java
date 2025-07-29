package io.github.rainvaporeon.fishutils.tests;

public interface TestInstruction<T> {

    /**
     * Tests whether the value is equal to the other
     * @param other the object
     * @param message the message if it is not equal
     * @return
     */
    TestComponent<T> equals(Object other, String... message);

    /**
     * Tests whether the value is larger than the other
     * @param other the object
     * @param message the message if it is not greater
     * @return
     */
    TestComponent<T> larger(Comparable<T> other, String... message);

    /**
     * Tests whether the value is lesser than the other
     * @param other the object
     * @param message the message if it is not lesser
     * @return
     */
    TestComponent<T> lesser(Comparable<T> other, String... message);

    /**
     * Tests whether this instruction throws the exception
     * @param clazz the exception class
     * @param message the message if it is not
     * @return
     */
    TestComponent<T> shouldThrow(Class<? extends Throwable> clazz, String... message);
}
