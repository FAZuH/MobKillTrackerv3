package com.spiritlight.mobkilltracker.v3.core;

import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.events.CompletionEvent;
import com.spiritlight.mobkilltracker.v3.events.EventHandler;
import com.spiritlight.mobkilltracker.v3.events.TerminationEvent;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataHandler {
    protected static boolean inProgress = false;
    protected static DataHandler lastHandler = null;

    private final EntityEventHandler handler;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Consumer<DropStatistics> completionAction = null;
    private Supplier<Boolean> terminationAction = null;
    private boolean isTerminated = false;
    private boolean isCompleted = false;

    public static final DataHandler EMPTY;

    static {
        EMPTY = new DataHandler();
        EMPTY.isTerminated = true;
    }

    private DataHandler() {
        handler = new EntityEventHandler();
        // Always track the last handler for active sessions
        lastHandler = this;
    }

    public static void invalidateLast() {
        lastHandler = null;
    }

    public void start() {
        start(300);
    }

    public void start(int duration) {
        Main.LOGGER.info("[MKT] DataHandler.start() called with duration={}s", duration);
        Main.LOGGER.info("[MKT] inProgress={}, lastHandler={}", inProgress, lastHandler != null ? "set" : "null");

        inProgress = true;
        scheduler.schedule(this::stop, duration, TimeUnit.SECONDS);

        Main.LOGGER.info(
                "[MKT] Tracking session started. inProgress={}, lastHandler={}",
                inProgress,
                lastHandler != null ? "set" : "null");
    }

    /**
     * Executes this action when completion is reached. This is executed after the termination
     * handler is called.
     *
     * @param handler The handler to handle the returned stats
     */
    public DataHandler whenComplete(Consumer<DropStatistics> handler) {
        this.completionAction = handler;
        return this;
    }

    /**
     * Executes this action if the action was terminated instead of completed normally. This will be
     * executed before any other completion action was executed.
     *
     * @param action The action, {@code true} to terminate any next code action, {@code false}
     *     otherwise.
     * @apiNote Despite that the completion stage may get reached earlier, the actual skipped
     *     actions are executing the completion action and firing a completion event. <br>
     *     If you need the completion event, you should fire it manually.
     */
    public DataHandler onTerminate(Supplier<Boolean> action) {
        this.terminationAction = action;
        return this;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public DropStatistics getStats() {
        return handler.getStats();
    }

    public DropStatistics stop() {
        completion();
        return handler.getStats();
    }

    public void terminate() {
        isTerminated = true;
        completion();
    }

    public static boolean isInProgress() {
        return inProgress;
    }

    protected void completion() {

        Message.debugv("DataHandler logging finished: " + this);

        this.scheduler.shutdownNow();
        inProgress = false;
        if (terminationAction != null && isTerminated) {
            isCompleted = true;
            if (terminationAction.get()) return;
        }
        if (completionAction != null) completionAction.accept(handler.getStats());
        EventHandler.onCompletion(new CompletionEvent(this));
        isCompleted = true;
    }

    public boolean isActive() {
        if (isCompleted || isTerminated) return false;
        return inProgress;
    }

    public EntityEventHandler getHandler() {
        return handler;
    }

    public static DataHandler newHandler() {
        return new DataHandler();
    }

    public static DataHandler newListenedHandler() {
        return new ListenerHandler();
    }

    /**
     * @return The last data handler (including the currently active one, if any)
     */
    public static DataHandler getLastHandler() {
        return lastHandler;
    }

    public static class ListenerHandler extends DataHandler {
        private ListenerHandler() {
            super();
        }

        public void onTermination(TerminationEvent event) {
            if (event.getType() == TerminationEvent.Type.TERMINATE) {
                terminate();
            } else {
                stop();
            }
        }

        @Override
        protected void completion() {
            super.completion();
        }
    }
}
