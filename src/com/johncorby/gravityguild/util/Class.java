package com.johncorby.gravityguild.util;

import com.johncorby.gravityguild.MessageHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.johncorby.gravityguild.util.Common.runSilent;

/**
 * Store classes to manage them
 */
public abstract class Class {
    static final List<Class> classes = new ArrayList<>();
    private boolean disposed = false;

    public Class() {
        runSilent(() -> {
            if (classes.contains(this))
                throw new IllegalStateException("Class already exists");
            classes.add(this);
        });
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
        runSilent(() -> {
            if (disposed)
                throw new IllegalStateException("Already disposed");
            if (!classes.contains(this))
                throw new IllegalStateException("Class doesn't exist");
            classes.remove(this);
            //debug("Disposed");
            disposed = true;
        });
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

