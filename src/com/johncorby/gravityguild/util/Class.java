package com.johncorby.gravityguild.util;

import com.johncorby.gravityguild.MessageHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Store classes to manage them
 */
public abstract class Class {
    static final ArrayList<Class> classes = new ArrayList<>();
    private boolean disposed = false;

    public Class() {
        if (classes.contains(this)) {
            error("Class already exists");
            return;
        }
        classes.add(this);
        debug("Added");
    }

    public static List<Class> getClasses() {
        return classes;
    }

    public static boolean contains(@NotNull Class clazz) {
        return classes.contains(clazz);
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void dispose() {
        if (disposed) {
            error("Already disposed");
            return;
        }
        if (!classes.contains(this)) {
            error("Class doesn't exist");
            return;
        }
        classes.remove(this);
        disposed = true;
        debug("Disposed");
    }

    @Override
    protected void finalize() throws Throwable {
        if (!disposed) dispose();
        super.finalize();
    }

    void debug(Object... msgs) {
        MessageHandler.debug(toString(), msgs);
    }

    void error(Object... msgs) {
        MessageHandler.error(toString(), msgs);
    }
}

