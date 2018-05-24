package com.johncorby.gravityguild.game.event;

import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import com.johncorby.gravityguild.game.arena.CoolDownHandler;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import static com.johncorby.gravityguild.GravityGuild.WORLD;
import static com.johncorby.gravityguild.MessageHandler.MessageType.GAME;
import static com.johncorby.gravityguild.MessageHandler.msg;
import static com.johncorby.gravityguild.Utils.randInt;
import static com.johncorby.gravityguild.arenaapi.arena.ArenaHandler.broadcast;

public class Entity implements Listener {
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        // Ignore if not in arena
        ArenaHandler.Arena aI = ArenaHandler.arenaIn(event.getEntity());
        if (aI == null) return;

        // Ignore if not player
        if (!(event.getEntity() instanceof Player)) return;
        Player p = (Player) event.getEntity();

        // Prevent damage unless damage would kill
        if (p.getHealth() - event.getFinalDamage() > 0) {
            // Revert damage if not lava
            if (event.getCause() == EntityDamageEvent.DamageCause.LAVA) return;
        } else {
            // Player died
            p.setLevel(p.getLevel() - 1);

            if (p.getLevel() > 0) {
                msg(p, GAME, "You lost a life");
                msg(p, GAME, "You have " + p.getLevel() + " life/lives left");
                broadcast(aI, p.getDisplayName() + " has lost a life", p);
                broadcast(aI, p.getDisplayName() + " has " + p.getLevel() + " life/lives left", p);

                // Teleport to random x/y and highest y
                int x = randInt(aI.getRegion()[0], aI.getRegion()[2]);
                int z = randInt(aI.getRegion()[1], aI.getRegion()[3]);
                int highY = WORLD.getHighestBlockYAt(x, z);
                p.teleport(new Location(WORLD, x + 0.5, highY, z + 0.5, randInt(-180, 180), 0));

                // Run cooldown
                CoolDownHandler.run(p);
            } else {
                msg(p, GAME, "You have died");
                broadcast(aI, p.getDisplayName() + " has died", p);
                aI.removeEntity(p);
            }
        }

        event.setDamage(0);
    }



    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        // Ignore if not in arena
        ArenaHandler.Arena aI = ArenaHandler.arenaIn(event.getEntity());
        if (aI == null) return;

        // Remove entity from arena
        aI.removeEntity(event.getEntity());

        // Add exploded blocks to arena's changed blocks list
        //List<Block> bL = event.blockList();
        //for (Block b : bL) aI.addChangedBlock(b);
    }



    /*
    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        // Ignore if not in arena
        Arena aI = ArenaHandler.arenaIn(event.getEntity());
        if (aI == null) return;

        // Get arrow, apply stuff, add to arena
        Arrow a = (Arrow) event.getProjectile();
        a.setGravity(false);
        aI.addEntity(a);
    }
    */



    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile p = event.getEntity();
        org.bukkit.entity.Entity s = (org.bukkit.entity.Entity) p.getShooter();

        // Ignore if shooter not in arena
        ArenaHandler.Arena aI = ArenaHandler.arenaIn(s);
        if (aI == null) return;

        // Do cool things with snowballs
        if (p instanceof Snowball) {
            p.setGlowing(true);
            p.setFireTicks(9999);
        }

        // Get projectile, apply stuff, add to arena
        event.getEntity().setGravity(false);
        aI.addEntity(event.getEntity());
    }



    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile p = event.getEntity();
        org.bukkit.entity.Entity s = (org.bukkit.entity.Entity) p.getShooter();

        // Ignore if not in arena
        ArenaHandler.Arena aI = ArenaHandler.arenaIn(p);
        if (aI == null) return;

        // Do cool things with snowballs
        if (p instanceof Snowball) {
            WORLD.strikeLightningEffect(p.getLocation());
            org.bukkit.entity.Entity eh = event.getHitEntity();
            if (eh instanceof LivingEntity)
                ((LivingEntity) eh).damage(9999, p);
        }

        // Kill and remove entity
        event.getEntity().remove();
        aI.removeEntity(event.getEntity());
    }



    @Deprecated
    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        //debug("Entity spawn " + event.getEntity().getType());
        ArenaHandler.Arena aI = ArenaHandler.arenaInC(event.getEntity().getLocation());
        if (aI == null) return;
        aI.addEntity(event.getEntity());
    }




    @Deprecated
    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        //debug("Entity death " + event.getEntity().getType());
        ArenaHandler.Arena aI = ArenaHandler.arenaInC(event.getEntity().getLocation());
        //ArenaHandler.Arena aI = ArenaHandler.arenaIn(event.getEntity());
        if (aI == null) return;
        aI.removeEntity(event.getEntity());
    }
}
