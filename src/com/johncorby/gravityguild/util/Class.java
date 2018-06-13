package com.johncorby.gravityguild.util;

import com.johncorby.gravityguild.MessageHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Store classes to manage them
 * <p>
 * i don't like exceptions so i just made it send an error message and then return so code execution will continue lol
 */
public class Class {
    protected static final List<Class> classes = new ArrayList<>();
    private boolean exists = false;

    /**
     * Actual constructor
     *
     * @throws IllegalStateException if we're already in stored classes
     */
    public Class() throws IllegalStateException {
        create();
    }

    /**
     * Get stored classes
     *
     * @return stored classes
     */
    public static List<Class> getClasses() {
        return classes;
    }

    /**
     * Check if class is in stored classes
     *
     * @param clazz class to check
     * @return if class is in stored classes
     */
    public static boolean contains(@Nonnull Class clazz) {
        return classes.contains(clazz);
    }

    /**
     * Constructor
     * Adds us to stored classes
     *
     * @return if we've been created
     * @throws IllegalStateException if we're already in stored classes
     */
    protected boolean create() throws IllegalStateException {
        if (exists()) return false;
            //throw new IllegalStateException(this + " already exists");
        classes.add(this);
        debug("Created");
        return exists = true;
    }

    /**
     * Check if we exist
     *
     * @return if we exist
     */
    public final boolean exists() {
        return exists || classes.contains(this);
    }

    /**
     * Destructor
     * Dispose ourselves
     *
     * @return if we've been disposed
     * @throws IllegalStateException if we aren't in stored classes
     */
    public boolean dispose() throws IllegalStateException {
        if (!exists()) return false;
        classes.remove(this);
        debug("Disposed");
        return exists = false;
    }

    /**
     * Actual destructor
     *
     * @throws Throwable if something fails
     */
    @Override
    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    /**
     * Handy method to show debug messages with us as a prefix
     *
     * @param msgs the messages to show
     */
    protected final void debug(Object... msgs) {
        MessageHandler.debug(toString(), msgs);
    }

    /**
     * Handy method to show error messages with us as a prefix
     *
     * @param msgs the messages to show
     */
    protected final void error(Object... msgs) {
        MessageHandler.error(toString(), msgs);
    }

    /**
     * Gets debug to print out on /gg debug
     *
     * @return the result to print
     */
    public List<String> getDebug() {
        List<String> r = new ArrayList<>();
        r.add(toString());
        return r;
    }
}

