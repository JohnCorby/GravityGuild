package com.johncorby.gravityguild.util;

import com.johncorby.gravityguild.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;

/**
 * Convenient version of BukkitRunnable
 * Unlike BukkitRunnable, you can run it again after it's cancelled
 * It also won't throw exceptions :)))
 */
public abstract class Runnable implements java.lang.Runnable {
    private BukkitTask task;

    public final synchronized boolean isCancelled() {
        return task == null || task.isCancelled();
    }

    public synchronized void cancel() {
        if (checkScheduled()) return;
        Bukkit.getScheduler().cancelTask(getTaskId());
        task = null;
    }

    @Nullable
    public final synchronized BukkitTask runTask() {
        if (checkNotScheduled()) return null;
        return setupTask(Bukkit.getScheduler().runTask(gravityGuild, this));
    }

    @Nullable
    public final synchronized BukkitTask runTaskAsynchronously() {
        if (checkNotScheduled()) return null;
        return setupTask(Bukkit.getScheduler().runTaskAsynchronously(gravityGuild, this));
    }

    @Nullable
    public final synchronized BukkitTask runTaskLater(final long delay) {
        if (checkNotScheduled()) return null;
        return setupTask(Bukkit.getScheduler().runTaskLater(gravityGuild, this, delay));
    }

    @Nullable
    public final synchronized BukkitTask runTaskLaterAsynchronously(final long delay) {
        if (checkNotScheduled()) return null;
        return setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(gravityGuild, this, delay));
    }

    @Nullable
    public final synchronized BukkitTask runTaskTimer(final long delay, final long period) {
        if (checkNotScheduled()) return null;
        return setupTask(Bukkit.getScheduler().runTaskTimer(gravityGuild, this, delay, period));
    }

    @Nullable
    public final synchronized BukkitTask runTaskTimerAsynchronously(final long delay, final long period) {
        if (checkNotScheduled()) return null;
        return setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(gravityGuild, this, delay, period));
    }

    public final synchronized int getTaskId() {
        if (checkScheduled()) return -1;
        return task.getTaskId();
    }

    private boolean checkScheduled() {
        try {
            if (task == null)
                throw new IllegalStateException("Not scheduled");
        } catch (Exception e) {
            MessageHandler.error(e);
            return true;
        }
        return false;
    }

    private boolean checkNotScheduled() {
        try {
            if (task != null)
                if (!task.isCancelled())
                    throw new IllegalStateException("Still running as task " + getTaskId());
                else {
                    MessageHandler.debug("Cancelled task " + getTaskId());
                    cancel();
                }
        } catch (Exception e) {
            MessageHandler.error(e);
            return true;
        }
        return false;
    }

    private BukkitTask setupTask(final BukkitTask task) {
        return this.task = task;
    }
}
