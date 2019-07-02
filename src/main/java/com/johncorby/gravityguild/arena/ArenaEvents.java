package com.johncorby.gravityguild.arena;

import com.johncorby.arenaapi.arena.Arena;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ArenaEvents implements com.johncorby.arenaapi.arena.ArenaEvents {
    @Override
    public void onOpen(Arena arena) {
        // Run countdown
        new CountDown(arena);
    }

    @Override
    public void onRunning(@NotNull Arena arena) {
        // Run cooldown for players
        for (Player p : arena.getPlayers())
            new CoolDown(p);
    }

    @Override
    public void onStopped(@NotNull Arena arena) {
        // Stop countdown
        try {
            Objects.requireNonNull(CountDown.get(arena)).dispose();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onJoin(@NotNull Arena arena, @NotNull Player player) {
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
    public void onLeave(Arena arena, @NotNull Player player) {
        CoolDown.heal(player);
        player.setInvulnerable(false);
        player.setGlowing(false);

        // Clear inventory
        player.getInventory().clear();

        // Cancel cooldown
        try {
            Objects.requireNonNull(CoolDown.get(player)).dispose();
        } catch (Exception ignored) {
        }
    }
}
