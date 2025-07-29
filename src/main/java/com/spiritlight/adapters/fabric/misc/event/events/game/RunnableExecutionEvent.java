package com.spiritlight.adapters.fabric.misc.event.events.game;

import com.spiritlight.adapters.fabric.misc.event.events.Event;

public class RunnableExecutionEvent extends Event {
    private volatile boolean taken;

    private static final Object lock = new Object();

    private final long key;

    private final Runnable runnable;

    public RunnableExecutionEvent(long key, Runnable runnable) {
        this.key = key;
        this.runnable = runnable;
        this.taken = false;
    }

    public RunnableExecutionEvent(Runnable runnable) {
        this(0L, runnable);
    }

    public Runnable getRunnable(long key) {
        if(this.key != key && this.key != 0L) throw new IllegalArgumentException("cannot accept key " + key);
        if(taken) throw new IllegalStateException("Execution task already taken");
        synchronized (lock) {
            this.taken = true;
        }
        return runnable;
    }

    public boolean isTaken() {
        return taken;
    }

    public boolean checkKey(long key) {
        return this.key == key;
    }

    @Override
    public String toString() {
        return "{" + key + ", taken=" + taken + "}" + super.toString();
    }
}
