package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.entity.Player;

public class StateChange {
    // On arena state change
    public static void on(ArenaHandler.Arena a) {
        switch (a.getState()) {
            case OPEN:
                // Run countdown
                CountDownHandler.run(a);
                break;
            case RUNNING:
                // Run cooldown for players
                for (Player p : a.getPlayers())
                    CoolDownHandler.run(p);
                break;
            case STOPPED:
                // Stop countdown/cooldown
                CountDownHandler.cancel(a);
                for (Player p : a.getPlayers())
                    CoolDownHandler.cancel(p);
                break;
        }
    }
}
