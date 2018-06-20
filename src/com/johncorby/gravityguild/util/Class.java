package com.johncorby.gravityguild.util;

import java.util.ArrayList;

/**
 * Store classes to manage them
 */
public class Class {
    protected static final ArrayList<Class> classes = new ArrayList<>();
    private boolean exists = false;

    public Class() throws IllegalStateException {
        create();
    }

    public static ArrayList<Class> getClasses() {
        return classes;
    }

    public static boolean contains(Class clazz) {
        return classes.contains(clazz);
    }

    protected boolean create() throws IllegalStateException {
        if (exists()) return false;
        //throw new IllegalStateException(this + " already exists");
        classes.add(this);
        debug("Created");
        return exists = true;
    }

    public final boolean exists() {
        return exists || classes.contains(this);
    }

    public boolean dispose() throws IllegalStateException {
        if (!exists()) return false;
        classes.remove(this);
        debug("Disposed");
        return exists = false;
    }

    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    protected final void debug(Object... msgs) {
        MessageHandler.debug(toString(), msgs);
    }

    protected final void error(Object... msgs) {
        MessageHandler.error(toString(), msgs);
    }

    public ArrayList<String> getDebug() {
        ArrayList<String> r = new ArrayList<>();
        r.add(toString());
        return r;
    }
}

