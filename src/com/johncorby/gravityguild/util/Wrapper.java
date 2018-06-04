package com.johncorby.gravityguild.util;

import com.johncorby.gravityguild.MessageHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * Wrap this around other classes to associate methods/fields/classes with them
 */
public class Wrapper<R> extends Class {
    private final WeakReference<R> reference;

    public Wrapper(@NotNull R reference) {
        super();
        this.reference = new WeakReference<>(reference);
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
        if (!contains(reference)) {
            MessageHandler.error((Object) "Wrapper doesn't exist");
            return;
        }
        get(reference).dispose();
    }

    public R get() {
        if (isDisposed()) {
            error("Already disposed");
            return null;
        }
        if (reference == null) return null;
        R r = reference.get();
        if (r == null) {
            dispose();
            error("Reference unavailable");
            return null;
        }
        return r;
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
