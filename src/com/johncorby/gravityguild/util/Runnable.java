package com.johncorby.gravityguild.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;

public abstract class Runnable extends BukkitRunnable {
    /*
    public synchronized BukkitTask runTask() {
        return runTask(gravityGuild);
    }

    public synchronized BukkitTask runTaskAsynchronously() {
        return runTaskAsynchronously(gravityGuild);
    }

    public synchronized BukkitTask runTaskLater(long delay) {
        return runTaskLater(gravityGuild, delay);
    }

    public synchronized BukkitTask runTaskLaterAsynchronously(long delay) {
        return runTaskLaterAsynchronously(gravityGuild, delay);
    }

    public synchronized BukkitTask runTaskTimer(long delay, long period) {
        return runTaskTimer(gravityGuild, delay, period);
    }

    public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period) {
        return runTaskTimerAsynchronously(gravityGuild, delay, period);
    }
    */


    public synchronized BukkitTask runTask() {
        return runTask(gravityGuild);

    }

    public synchronized BukkitTask runTaskAsynchronously() {
        return runTaskAsynchronously(gravityGuild);
    }

    public synchronized BukkitTask runTaskLater(long delay) {
        return runTaskLater(gravityGuild, delay);
    }

    public synchronized BukkitTask runTaskLaterAsynchronously(long delay) {
        return runTaskLaterAsynchronously(gravityGuild, delay);
    }

    public synchronized BukkitTask runTaskTimer(long delay, long period) {
        return runTaskTimer(gravityGuild, delay, period);
    }

    public synchronized BukkitTask runTaskTimerAsynchronously(long delay, long period) {
        return runTaskTimerAsynchronously(gravityGuild, delay, period);
    }

    /*
    private void preRunTask(Long delay, Long period, boolean async) {
        try {
            for (BukkitTask t : Bukkit.getScheduler().getPendingTasks())
                MessageHandler.debug(t.getOwner(), t.getClass().getSimpleName(), t.getTaskId());

            if (!gravityGuild.isEnabled()) {
                MessageHandler.error("Plugin not enabled");
                return null;
            }
            BukkitTask task = null;
            if (!async) task = runTask(gravityGuild);
            if (async) task = runTaskAsynchronously(gravityGuild);
            if (delay != null && !async) task = runTaskLater(gravityGuild, delay);
            if (delay != null && async) task = runTaskLaterAsynchronously(gravityGuild, delay);
            if (delay != null && period != null && !async) task = runTaskTimer(gravityGuild, delay, period);
            if (delay != null && period != null && async) task = runTaskTimerAsynchronously(gravityGuild, delay, period);
            MessageHandler.debug("Scheduled task " + getTaskId());
            return task;
        } catch (IllegalStateException e) {
            // Cancel already scheduled task
            MessageHandler.error(e.getMessage());
            String[] sE = e.getMessage().split(" ");
            Bukkit.getScheduler().cancelTask(Common.toInt(sE[sE.length - 1]));
            return null;
        } catch (Exception e) {
            MessageHandler.error(getStackTrace(e));
            return null;
        }
    }
    */
}
