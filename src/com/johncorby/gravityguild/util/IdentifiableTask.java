package com.johncorby.gravityguild.util;

public abstract class IdentifiableTask<I> extends Identifiable<I> {
    protected final Task task = new Task();

    public IdentifiableTask(I identity) {
        super(identity);
        setup();
    }

    public static IdentifiableTask get(Object identity) {
        return (IdentifiableTask) get(identity, IdentifiableTask.class);
    }

    public static boolean contains(Object identity) {
        return contains(identity, IdentifiableTask.class);
    }

    public static boolean dispose(Object identity) {
        return dispose(identity, IdentifiableTask.class);
    }

    protected void setup() {
        task.runTask();
    }

    // TODO: Override this in subclasses
    protected void run() {
    }

    // TODO: Override this in subclasses
    protected void cancel() {
    }

    @Override
    public final boolean dispose() {
        task.cancel();
        return isDisposed();
    }

    protected final class Task extends Runnable {

        @Override
        public final void run() {
            IdentifiableTask.this.run();
        }

        @Override
        public final synchronized void cancel() {
            IdentifiableTask.this.cancel();
            super.cancel();
            IdentifiableTask.super.dispose();
        }
    }
}
