package com.johncorby.gravityguild.util;

import java.util.Objects;

/**
 * A class that is identified by another class
 * Can also be used as a wrapper to associate methods/fields/classes with the identity
 */
public class Identifiable<I> extends Class {
    private I identity;

    public Identifiable(I identity) {
        super();
        create(identity);
    }

    public static Identifiable get(Object identity) {
        return get(identity, Identifiable.class);
    }

    public static boolean dispose(Object identity) {
        return dispose(identity, Identifiable.class);
    }

    protected static Identifiable get(Object identity,
                                      java.lang.Class<? extends Identifiable> clazz) {
        for (Class c : classes) {
//            MessageHandler.debug(toString(identity, clazz), "Search " + c,
//                    c.getClass().equals(clazz),
//                    ((Identifiable) c).get().equals(identity));
            if (c.getClass().equals(clazz) &&
                    ((Identifiable) c).get().equals(identity)) {
//                MessageHandler.debug(toString(identity, clazz), "FOUND", "");
                return ((Identifiable) c);
            }
        }
        return null;
    }

    protected static boolean dispose(Object identity,
                                     java.lang.Class<? extends Identifiable> clazz) {
        Identifiable i = get(identity, clazz);
        return i != null && i.dispose();
    }

    public static String toString(Object identity,
                                  java.lang.Class<? extends Identifiable> clazz) {
        return clazz.getSimpleName() + "<" + identity + ">";
    }

    protected boolean create(I identity) {
        this.identity = identity;
        return super.create();
    }

    @Override
    protected boolean create() {
        return true;
    }

    public final I get() throws IllegalStateException {
        if (!exists())
            throw new IllegalStateException(this + " doesn't exist");
        if (identity == null) {
            super.dispose();
            throw new IllegalStateException("Identity for " + this + " unavailable");
        }
        return identity;
    }

    @Override
    public String toString() {
        //if (exists())
        //    return getClass().getSimpleName() + "<disposed>";
        return getClass().getSimpleName() + "<" + identity + ">";
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) &&
                Objects.equals(identity, ((Identifiable) obj).identity);
    }

    @Override
    public int hashCode() {
        return identity.hashCode();
    }
}
