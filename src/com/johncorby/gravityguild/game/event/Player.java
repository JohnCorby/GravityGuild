package com.johncorby.gravityguild.game.event;

import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import static com.johncorby.gravityguild.GravityGuild.WORLD;

public class Player implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Ignore if not in arena
        ArenaHandler.Arena aI = ArenaHandler.arenaIn(event.getPlayer());
        if (aI == null) return;

        // If left clicked block
        if (event.getAction() == Action.LEFT_CLICK_AIR ||
                event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Spawn wither skull in front of player
            org.bukkit.entity.Player p = event.getPlayer();
            Location l = p.getEyeLocation();
            Vector d = p.getLocation().getDirection();
            l.add(d);
            //WORLD.createExplosion(l, 4);
            WitherSkull s = (WitherSkull) WORLD.spawnEntity(l, EntityType.WITHER_SKULL);
            s.setVelocity(s.getDirection());
            s.setShooter(p);
            aI.add(s);
            event.setCancelled(true);
        }
    }
}
