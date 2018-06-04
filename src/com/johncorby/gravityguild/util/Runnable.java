package com.johncorby.gravityguild.util;

import com.johncorby.gravityguild.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;

/**
 * Convenient version of BukkitRunnable
 * Unlike BukkitRunnable, you can run it again after it's cancelled
 */
public abstract class Runnable implements java.lang.Runnable {
    private BukkitTask task;

    public synchronized boolean isCancelled() throws IllegalStateException {
        //checkScheduled();
        return task.isCancelled();
    }

    public synchronized void cancel() throws IllegalStateException {
        Bukkit.getScheduler().cancelTask(getTaskId());
        task = null;
    }

    public synchronized BukkitTask runTask() throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(Bukkit.getScheduler().runTask(gravityGuild, this));
    }

    public synchronized BukkitTask runTaskAsynchronously() throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(Bukkit.getScheduler().runTaskAsynchronously(gravityGuild, this));
    }

    public synchronized BukkitTask runTaskLater(final long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(Bukkit.getScheduler().runTaskLater(gravityGuild, this, delay));
    }

    public synchronized BukkitTask runTaskLaterAsynchronously(final long delay) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(gravityGuild, this, delay));
    }

    public synchronized BukkitTask runTaskTimer(final long delay, final long period) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(Bukkit.getScheduler().runTaskTimer(gravityGuild, this, delay, period));
    }

    public synchronized BukkitTask runTaskTimerAsynchronously(final long delay, final long period) throws IllegalArgumentException, IllegalStateException {
        checkNotScheduled();
        return setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(gravityGuild, this, delay, period));
    }

    public synchronized int getTaskId() throws IllegalStateException {
        checkScheduled();
        return task.getTaskId();
    }

    private void checkScheduled() {
        if (task == null)
            throw new IllegalStateException("Not scheduled");
    }

    private void checkNotScheduled() {
        if (task != null)
            if (!task.isCancelled())
                throw new IllegalStateException("Still running as task " + getTaskId());
            else {
                MessageHandler.debug("Cancelled task " + getTaskId());
                cancel();
            }
    }

    private BukkitTask setupTask(final BukkitTask task) {
        return this.task = task;
    }


}
