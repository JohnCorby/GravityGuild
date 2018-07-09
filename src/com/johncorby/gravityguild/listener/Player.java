package com.johncorby.gravityguild.listener;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.util.MessageHandler;
import com.johncorby.gravityguild.arena.CoolDown;
import org.bukkit.Location;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Player implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Ignore if not in arena
        Arena aI = Arena.arenaIn(event.getPlayer());
        if (aI == null) return;

        // If left clicked block
        if (event.getAction() == Action.LEFT_CLICK_AIR ||
                event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Spawn wither skull in front of player
            org.bukkit.entity.Player p = event.getPlayer();
            Location l = p.getEyeLocation();
            Vector d = p.getLocation().getDirection();
            //WORLD.createExplosion(l, 4);
            p.launchProjectile(WitherSkull.class, d);
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (Arena.arenaIn(event.getEntity()) != null)
            event.setCancelled(true);
    }


    @EventHandler
    public void onExpChange(PlayerExpChangeEvent event) {
        if (Arena.arenaIn(event.getPlayer()) != null)
            event.setAmount(0);
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        // Ignore if not in arena
        Arena aI = Arena.arenaIn(event.getEntity());
        if (aI == null) return;

        org.bukkit.entity.Player p = event.getEntity();
        p.setHealth(p.getMaxHealth());

        String deathMsg = event.getDeathMessage();
        aI.broadcast(event.getDeathMessage(), p);

        deathMsg = deathMsg.replace(p.getName(), "You")
                .replace("You was", "You were");
        MessageHandler.info(p, deathMsg);


        p.setLevel(p.getLevel() - 1);

        if (p.getLevel() > 0) {
            MessageHandler.info(p, "You lost a life");
            MessageHandler.info(p, "You have " + p.getLevel() + " life/lives left");
            aI.broadcast(p.getName() + " has lost a life", p);
            aI.broadcast(p.getName() + " has " + p.getLevel() + " life/lives left", p);

            // Teleport to random loc
            aI.tpToRandom(p);

            // Run cooldown
            new CoolDown(p);
        } else {
            MessageHandler.info(p, "You have died");
            aI.broadcast(p.getName() + " has died", p);
            aI.remove(p);
        }
    }
}
