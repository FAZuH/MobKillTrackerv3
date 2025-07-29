package io.github.rainvaporeon.fishutils.tests;

import io.github.rainvaporeon.fishutils.misc.ThrowingRunnable;

public interface TestComponent<T> extends TestInstruction<T> {
    /**
     * Executes this test, this works same as calling the
     * underlying method with the supplied parameters.
     * @param args the args to pass
     * @return itself
     */
    TestComponent<T> run(Object... args);

    /**
     * Performs an action if the previous condition fails,
     * must be chained after an assertion statement
     * @param action the action to run if the previous condition fails
     * @return itself
     */
    TestComponent<T> orElse(ThrowingRunnable action);
}
