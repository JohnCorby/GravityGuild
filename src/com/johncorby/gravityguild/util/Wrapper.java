package com.johncorby.gravityguild.util;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import static com.johncorby.gravityguild.util.Common.runSilent;

/**
 * Wrap this around other classes to associate methods/fields/classes with them
 *
 * i dont like exceptions so i just made it send an error message and then return so code execution will continue lol
 */
public class Wrapper<R> extends Class {
    private final WeakReference<R> reference;

    public Wrapper(@NotNull R reference) {
        super();
        this.reference = new WeakReference<>(reference);
        //debug("Added");
    }

    public static Wrapper get(@NotNull Object reference) {
        for (Class c : classes)
            if (c instanceof Wrapper && ((Wrapper) c).get().equals(reference)) return (Wrapper) c;
        return null;
    }

    public static boolean contains(@NotNull Object reference) {
        return get(reference) != null;
    }

    public static void dispose(@NotNull Object reference) {
        runSilent(() -> {
            if (!contains(reference))
                throw new IllegalStateException("Wrapper doesn't exist");
            get(reference).dispose();
        });
    }

    public R get() {
        return runSilent(() -> {
            if (isDisposed())
                throw new IllegalStateException("Already disposed");
            if (reference == null) return null;
            R r = reference.get();
            if (r == null) {
                dispose();
                throw new IllegalStateException("Reference unavailable");
            }
            return r;
        });
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public String toString() {
        if (isDisposed())
            return getClass().getSimpleName() + "<disposed>";
        if (reference == null) {
            return getClass().getSimpleName() + "<null>";
        }
        return getClass().getSimpleName() + "<" + get().toString() + ">";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Wrapper && ((Wrapper) obj).get().equals(get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }
}
