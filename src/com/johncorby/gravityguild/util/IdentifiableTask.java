package com.johncorby.gravityguild.util;

public abstract class IdentifiableTask<I> extends Identifiable<I> {
    protected Task task;

    public IdentifiableTask(I identity) {
        super(identity);
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

    // TODO: Override this in subclasses
    protected boolean create(I identity) {
        if (!super.create(identity)) return false;
        task = new Task();
        return true;
    }

    // TODO: Override this in subclasses
    protected void run() {
    }

    @Override
    public boolean dispose() {
        if (!task.isCancelled()) task.cancel();
        return exists();
    }

    protected final class Task extends Runnable {
        @Override
        public final void run() {
            IdentifiableTask.this.run();
        }

        @Override
        public final synchronized void cancel() {
            super.cancel();
            IdentifiableTask.super.dispose();
        }
    }
}
