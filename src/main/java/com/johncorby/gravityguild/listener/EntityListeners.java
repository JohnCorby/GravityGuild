package com.johncorby.gravityguild.listener;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.util.Runnable;
import com.johncorby.gravityguild.arena.ProjVelSet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.jetbrains.annotations.NotNull;

import static com.johncorby.gravityguild.GravityGuild.WORLD;

public class EntityListeners implements Listener {
    // Revert fireball damage from players
    // And arrow damage to yourself
    @EventHandler
    public void onDamageByEntity(@NotNull EntityDamageByEntityEvent event) {
        org.bukkit.entity.Entity e = event.getDamager();
        if (e instanceof Projectile) {
            Projectile p = (Projectile) e;
            if (p.getShooter() instanceof BlockProjectileSource) return;
            org.bukkit.entity.Entity s = (org.bukkit.entity.Entity) p.getShooter();
            org.bukkit.entity.Entity h = event.getEntity();

            // Don't damage if shot self with arrow
            if (p instanceof Arrow && s == h) event.setDamage(0);

            // Don't damage if shot with fireball from player
            if (p instanceof Fireball && s instanceof Player) {
                event.setDamage(0);
            }
        }
    }


    @EventHandler
    public void onProjectileLaunch(@NotNull ProjectileLaunchEvent event) {
        Projectile p = event.getEntity();
        if (p.getShooter() instanceof BlockProjectileSource) return;
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
    public void onProjectileHit(@NotNull ProjectileHitEvent event) {
        Projectile p = event.getEntity();
        if (p.getShooter() instanceof BlockProjectileSource) return;
        org.bukkit.entity.Entity h = event.getHitEntity();
        org.bukkit.entity.Entity s = (org.bukkit.entity.Entity) p.getShooter();

        // Ignore if not in arena
        Arena aI = Arena.arenaIn(p);
        if (aI == null) return;

        // Do cool things with snowballs
        if (p instanceof Snowball) {
            WORLD.strikeLightning(p.getLocation());
            if (h instanceof Damageable)
                ((Damageable) h).damage(9999, p);
        }

        // Do knockback with fireballs have close hit and shooter
        if (p instanceof Fireball) {
            if (s.getLocation().distance(p.getLocation()) < 4)
                new Runnable() {
                    @Override
                    public void run() {
                        s.setVelocity(s.getLocation()
                                .subtract(p.getLocation())
                                .multiply(4)
                                .toVector());
                    }
                }.runTaskLater(0);
        }

        // Kill entity
        p.remove();
        ProjVelSet.get(p).dispose();
    }
}
