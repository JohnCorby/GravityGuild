package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.util.Identifiable;
import com.johncorby.gravityguild.util.Runnable;
import org.bukkit.entity.Player;

public class CoolDown extends Identifiable<Player> {
    private static final int d = 5;
    private final Task task = new Task();

    public CoolDown(Player reference) {
        super(reference);
        get().setInvulnerable(true);
        get().setGlowing(true);
        get().setHealth(reference.getMaxHealth());
        get().setFireTicks(0);
        MessageHandler.msg(get(), MessageHandler.MessageType.GAME, "You are invincible for " + d + " seconds");

        task.runTaskLater(20 * d);
    }

    public static CoolDown get(Player identity) {
        return (CoolDown) get(identity, CoolDown.class);
    }

    public static boolean contains(Player identity) {
        return contains(identity, CoolDown.class);
    }

    public static boolean dispose(Player identity) {
        return dispose(identity, CoolDown.class);
    }

    @Override
    public boolean dispose() {
        return task.dispose();
    }

    private class Task extends Runnable {
        @Override
        public void run() {
            MessageHandler.msg(get(), MessageHandler.MessageType.GAME, "You are no longer invincible");
            cancel();
        }

        @Override
        public synchronized void cancel() {
            dispose();
        }

        private boolean dispose() {
            get().setInvulnerable(false);
            get().setGlowing(false);
            get().setHealth(get().getMaxHealth());
            get().setFireTicks(0);

            super.cancel();
            return CoolDown.super.dispose();
        }
    }
}

/*
class _CoolDown {
    private static List<Task> coolDowns = new ArrayList<>();

    // Run task
    public static void run(Player p) {
        new Task(p);
    }

    // Cancel task
    public static void cancel(Player p) {
        Task t = get(p);
        if (t == null) return;
        t.cancel();
    }

    // Get task by player
    private static Task get(Player player) {
        return Common.find(coolDowns, c -> c.p, player);
    }

    private static class Task extends Runnable {
        private static final int d = 5;
        private Player p;

        Task(Player p) {
            if (get(p) != null) return;
            coolDowns.add(this);

            p.setInvulnerable(true);
            p.setGlowing(true);
            p.setHealth(p.getMaxHealth());
            p.setFireTicks(0);
            MessageHandler.msg(p, MessageHandler.MessageType.GAME, "You are invincible for " + d + " seconds");

            this.p = p;
            runTaskLater(20 * d);
        }

        @Override
        public void run() {
            MessageHandler.msg(p, MessageHandler.MessageType.GAME, "You are no longer invincible");
            _CoolDown.cancel(p);
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            p.setInvulnerable(false);
            p.setGlowing(false);
            p.setHealth(p.getMaxHealth());
            p.setFireTicks(0);

            coolDowns.remove(this);
            super.cancel();
        }
    }
}
*/
