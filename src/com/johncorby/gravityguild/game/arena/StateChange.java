package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import org.bukkit.entity.Player;

public class StateChange {
    // On arena state change
    public static void on(Arena a) {
        switch (a.getState()) {
            case OPEN:
                // Run countdown
                new CountDown(a);
                break;
            case RUNNING:
                // Run cooldown for players
                for (Player p : a.getPlayers())
                    new CoolDown(p);
                break;
            case STOPPED:
                // Stop countdown
                CountDown.dispose(a);
                break;
        }
    }
}
