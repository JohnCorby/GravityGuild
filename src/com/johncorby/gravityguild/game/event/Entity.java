package com.johncorby.gravityguild.game.event;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.game.arena.ProjectileWrapper;
import org.bukkit.entity.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import static com.johncorby.gravityguild.GravityGuild.WORLD;

public class Entity implements Listener {
    // Revert
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof WitherSkull)) return;
        if (!(((WitherSkull) event.getDamager()).getShooter() instanceof Player)) return;
        event.setDamage(0);
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
        new ProjectileWrapper(p);
    }



    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile p = event.getEntity();
        org.bukkit.entity.Entity s = (org.bukkit.entity.Entity) p.getShooter();

        // Ignore if not in arena
        Arena aI = Arena.arenaIn(p);
        if (aI == null) return;

        // Do cool things with snowballs
        if (p instanceof Snowball) {
            WORLD.strikeLightningEffect(p.getLocation());
            org.bukkit.entity.Entity eh = event.getHitEntity();
            if (eh instanceof LivingEntity)
                ((LivingEntity) eh).damage(9999, p);
        }

        // Kill entity
        p.remove();
        ProjectileWrapper.get(p).dispose();
    }
}
