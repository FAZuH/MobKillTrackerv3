package io.github.rainvaporeon.fishutils.action;

import io.github.rainvaporeon.fishutils.collections.Pair;

public interface ResultRunnable<V> {
    Pair<Result, V> run() throws Throwable;
}
