package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class StateChange {
    // On arena state change
    public static void on(Arena a, Arena.State s) {
        switch (s) {
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

                // Stop ProjectileWrappers
                for (Entity e : a.getEntities())
                    if (e instanceof Projectile) ProjVelSet.dispose((Projectile) e);
                break;
        }
    }
}
