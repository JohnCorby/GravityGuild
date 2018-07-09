package com.johncorby.gravityguild.arena;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.util.Common;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class ArenaEvents extends com.johncorby.arenaapi.arena.ArenaEvents {
    @Override
    public void onOpen(Arena arena) {
        // Run countdown
        new CountDown(arena);
    }

    @Override
    public void onRunning(Arena arena) {
        // Run cooldown for players
        for (Player p : arena.getPlayers())
            new CoolDown(p);
    }

    @Override
    public void onStopped(Arena arena) {
        // Stop countdown
        Common.run(CountDown.get(arena)::dispose);

        // Stop ProjectileWrappers
        for (Entity e : arena.getEntities())
            if (e instanceof Projectile)
                ProjVelSet.get((Projectile) e).dispose();
    }

    @Override
    public void onJoin(Arena arena, Player player) {
        // Set experience to health
        player.setLevel(10);
        player.setExp(0);

        // Set game mode
        player.setGameMode(GameMode.SURVIVAL);

        // Give items
        ItemStack bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 9999);
//        bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
        bow.addUnsafeEnchantment(Enchantment.DURABILITY, 9999);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

        ItemStack elytra = new ItemStack(Material.ELYTRA);
        elytra.addUnsafeEnchantment(Enchantment.DURABILITY, 9999);
        elytra.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);

        ItemStack endRod = new ItemStack(Material.END_ROD);
        endRod.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);

        player.getInventory().clear();
        player.getInventory().addItem(bow);
        player.getInventory().addItem(new ItemStack(Material.ARROW));
        player.getInventory().setChestplate(elytra);
        player.getInventory().setHelmet(endRod);

        // Heal
        CoolDown.heal(player);
        player.setInvulnerable(true);
        player.setGlowing(true);

        // For override players
        if (arena.getState() == Arena.State.RUNNING)
            new CoolDown(player);
    }

    @Override
    public void onLeave(Arena arena, Player player) {
        CoolDown.heal(player);
        player.setInvulnerable(false);
        player.setGlowing(false);

        // Clear inventory
        player.getInventory().clear();

        // Cancel cooldown
        Common.run(CoolDown.get(player)::dispose);
    }
}