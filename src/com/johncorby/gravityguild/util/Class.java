package com.johncorby.gravityguild.util;

import com.johncorby.gravityguild.MessageHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.johncorby.gravityguild.util.Common.run;

/**
 * Store classes to manage them
 *
 * i don't like exceptions so i just made it send an error message and then return so code execution will continue lol
 */
public class Class {
    protected static final List<Class> classes = new ArrayList<>();
    private boolean disposed = false;

    /**
     * Constructor
     * Adds us to stored classes
     */
    public Class() {
        run(() -> {
            if (classes.contains(this))
                throw new IllegalStateException(this + " already exists");
            classes.add(this);
        });
    }

    /**
     * Get stored classes
     * @return stored classes
     */
    public static List<Class> getClasses() {
        return classes;
    }

    /**
     * Check if class is in stored classes
     * @param clazz class to check
     * @return if class is in stored classes
     */
    public static boolean contains(@NotNull Class clazz) {
        return classes.contains(clazz);
    }

    /**
     * Check if we've been disposed
     * @return if we've been disposed
     */
    public boolean isDisposed() {
        return disposed;
    }

    /**
     * Dispose ourselves
     * @return if we've been disposed
     */
    public boolean dispose() {
        run(() -> {
            if (disposed)
                throw new IllegalStateException(this + " already disposed");
            if (!classes.contains(this))
                throw new IllegalStateException(this + " doesn't exist");
            classes.remove(this);
//            debug("Disposed", classes);
            disposed = true;
        });
        return disposed;
    }

    /**
     * Destructor
     * Also disposes us
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        if (!disposed) dispose();
        super.finalize();
    }

    /**
     * Handy method to show debug messages with us as a prefix
     * @param msgs the messages to show
     */
    void debug(Object... msgs) {
        MessageHandler.debug(toString(), msgs);
    }

    /**
     * Handy method to show error messages with us as a prefix
     * @param msgs the messages to show
     */
    void error(Object... msgs) {
        MessageHandler.error(toString(), msgs);
    }
}

