package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;

public class CoolDownHandler {
    private static ArrayList<Task> coolDowns = new ArrayList<>();

    // Run task
    public static void run(Player p) {
        new Task(p);
    }

    // Cancel task
    public static void cancel(Player p) {
        p.setInvulnerable(false);
        p.setGlowing(false);
        p.setHealth(p.getMaxHealth());
        p.setFireTicks(0);

        Task t = get(p);
        if (t == null) return;
        t.cancel();
        coolDowns.remove(t);
    }

    // Get task by player
    private static Task get(Player player) {
        return Utils.find(coolDowns, c -> c.p, player);
    }

    private static class Task extends BukkitRunnable {
        private Player p;
        private static final int d = 5;

        Task(Player p) {
            if (get(p) != null) return;
            coolDowns.add(this);

            p.setInvulnerable(true);
            p.setGlowing(true);
            p.setHealth(p.getMaxHealth());
            p.setFireTicks(0);
            MessageHandler.msg(p, MessageHandler.MessageType.GAME, "You are invincible for " + d + " seconds");

            this.p = p;
            runTaskLater(gravityGuild, 20 * d);
        }

        @Override
        public void run() {
            MessageHandler.msg(p, MessageHandler.MessageType.GAME, "You are no longer invincible");
            CoolDownHandler.cancel(p);
        }
    }
}
