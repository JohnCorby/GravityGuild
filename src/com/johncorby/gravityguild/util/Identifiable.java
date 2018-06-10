package com.johncorby.gravityguild.util;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import static com.johncorby.gravityguild.util.Common.run;

/**
 * A class that is identified by another class
 * Can also be used as a wrapper to associate methods/fields/classes with the identity
 *
 * identity is a weak reference for wrappers so they can still be finalized
 * i don't like exceptions so i just made it send an error message and then return so code execution will continue lol
 */
public class Identifiable<I> extends Class {
    private final WeakReference<I> identity;

    /**
     * Constructor
     * @param identity our identity
     */
    public Identifiable(@NotNull I identity) {
        super();
        this.identity = new WeakReference<>(identity);
//        debug("Added", classes);
    }

    /**
     * Get Identifiable of our class by identity
     * TODO: override this in subclasses
     * @param identity identity to get Identifiable of
     * @return gotten Identifiable or null if one doesn't exist
     */
    public static Identifiable get(Object identity) {
        return get(identity, Identifiable.class);
    }

    /**
     * Check if Identifiable of our class by identity
     * TODO: override this in subclasses
     * @param identity identity to get Identifiable of
     * @return if Identifiable exists
     */
    public static boolean contains(Object identity) {
        return contains(identity, Identifiable.class);
    }

    /**
     * Dispose Identifiable of our class by identity
     * TODO: override this in subclasses
     * @param identity identity to get Identifiable of
     * @return if Identifiable was disposed
     */
    public static boolean dispose(Object identity) {
        return dispose(identity, Identifiable.class);
    }

    /**
     * Gets an Identifiable by identity and class
     * @param identity identity to get Identifiable of
     * @param clazz class of Identifiable to get
     * @return gotten Identifiable or null if one doesn't exist
     */
    protected static Identifiable get(@NotNull Object identity,
                                      @NotNull java.lang.Class<? extends Identifiable> clazz) {
        for (Class c : classes) {
//            MessageHandler.debug(clazz.toString(),
//                    c.getClass(),
//                    clazz,
//                    ((Identifiable) c).get(),
//                    identity);
            if (c.getClass().equals(clazz) &&
                    ((Identifiable) c).get().equals(identity)) return ((Identifiable) c);
        }
        return null;
    }

    /**
     * Check if Identifiable with identity and class exists
     * @param identity identity to get Identifiable of
     * @param clazz class of Identifiable to get
     * @return if Identifiable exists
     */
    protected static boolean contains(@NotNull Object identity,
                                      @NotNull java.lang.Class<? extends Identifiable> clazz) {
        return get(identity, clazz) != null;
    }

    /**
     * Dispose Identifiable by identity and class
     * @param identity identity to get Identifiable of
     * @param clazz class of Identifiable to get
     * @return if Identifiable was disposed
     */
    protected static boolean dispose(@NotNull Object identity,
                                     @NotNull java.lang.Class<? extends Identifiable> clazz) {
        Boolean r = run(() -> {
//            MessageHandler.debug(clazz.toString(), "Disposing Identifiable with identity " + identity, classes);
            if (!contains(identity, clazz))
                throw new IllegalStateException("Identifiable with identity " + identity + " doesn't exist");
            return get(identity, clazz).dispose();
        });
        return r == null ? false : r;
    }

    /**
     * Get our identity
     * Disposes us if identity is unavailable/nonexistent
     * @return our identity or null if it's unavailable/nonexistent
     */
    public I get() {
        return run(() -> {
            if (isDisposed())
                throw new IllegalStateException(this + " already disposed");
            if (identity == null) return null;
            I i = identity.get();
            if (i == null) {
                dispose();
                throw new IllegalStateException("Identity for " + this + " unavailable");
            }
            return i;
        });
    }

    @Override
    public String toString() {
        if (isDisposed())
            return getClass().getSimpleName() + "<disposed>";
        if (identity == null) {
            return getClass().getSimpleName() + "<null>";
        }
        return getClass().getSimpleName() + "<" + get().toString() + ">";
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) &&
                ((Identifiable) obj).get().equals(get());
    }

    @Override
    public int hashCode() {
        return get().hashCode();
    }
}
