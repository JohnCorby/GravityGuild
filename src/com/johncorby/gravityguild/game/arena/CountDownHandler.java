package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.Utils;
import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler.Arena;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;
import static com.johncorby.gravityguild.GravityGuild.overridePlayers;

public class CountDownHandler {
    private static ArrayList<Task> countDowns = new ArrayList<>();

    // Run task
    public static void run(Arena a) {
        new Task(a);
    }

    // Cancel task
    public static void cancel(Arena a) {
        Task t = get(a);
        if (t == null) return;
        t.cancel();
        countDowns.remove(t);
    }

    // Get task by arena
    private static Task get(Arena a) {
        return Utils.find(countDowns, c -> c.a, a);
    }

    private static class Task extends BukkitRunnable {
        private Arena a;
        private int t = d;
        private static final int d = 10;

        Task(Arena a) {
            if (get(a) != null) return;
            countDowns.add(this);

            this.a = a;
            runTaskTimer(gravityGuild, 0, 20);
        }

        @Override
        public void run() {
            switch (t) {
                case 10:
                case 5: case 4: case 3: case 2: case 1:
                    ArenaHandler.broadcast(a, t + " second/seconds until game begins");
                    break;
                case 0:
                    // Start arena if 2 or more players
                    if (a.getPlayers().size() > 1 ||
                            Utils.containsAny(a.getPlayers(), overridePlayers)) {
                        a.setState(Arena.State.RUNNING);
                        CountDownHandler.cancel(a);
                    } else {
                        ArenaHandler.broadcast(a, "Arena needs 2 or more players to start");
                        t = d;
                    }
                    break;
            }
            t--;
        }
    }
}
