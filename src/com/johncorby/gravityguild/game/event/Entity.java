package com.johncorby.gravityguild.game.event;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.game.arena.ProjVelSet;
import org.bukkit.entity.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import static com.johncorby.gravityguild.GravityGuild.WORLD;

public class Entity implements Listener {
    // Revert witherskull damage from players
    // And arrow damage to yourself
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        org.bukkit.entity.Entity e = event.getDamager();
        if (e instanceof Projectile) {
            Projectile p = (Projectile) e;
            org.bukkit.entity.Entity s = (org.bukkit.entity.Entity) p.getShooter();
            org.bukkit.entity.Entity h = event.getEntity();

            // Don't damage if shot self with arrow
            if (p instanceof Arrow && s == h) event.setDamage(0);

            // Don't damage if shot with witherskull from player
            if (p instanceof WitherSkull && s instanceof Player) {
                event.setDamage(0);
            }
        }
    }


    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile p = event.getEntity();
        org.bukkit.entity.Entity s = (org.bukkit.entity.Entity) p.getShooter();

        // Ignore if shooter not in arena
        Arena aI = Arena.arenaIn(s);
        if (aI == null) return;

        // Do cool things with snowballs
        if (p instanceof Snowball) {
            p.setGlowing(true);
            p.setFireTicks(9999);
        }

        // Get projectile and apply stuff
        p.setGravity(false);
        new ProjVelSet(p);
    }


    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile p = event.getEntity();
        org.bukkit.entity.Entity h = event.getHitEntity();

        // Ignore if not in arena
        Arena aI = Arena.arenaIn(p);
        if (aI == null) return;

        // Do cool things with snowballs
        if (p instanceof Snowball) {
            WORLD.strikeLightningEffect(p.getLocation());
            if (h instanceof Damageable)
                ((Damageable) h).damage(9999, p);
        }

        // Kill entity
        p.remove();
        ProjVelSet.dispose(p);
    }
}
