package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JoinLeave {
    // When player join arena
    public static void onJoin(Player player, Arena arena) {
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

        CoolDown.heal(player);
        player.setInvulnerable(true);
        player.setGlowing(true);

        // For override players
        if (arena.getState() == Arena.State.RUNNING)
            new CoolDown(player);
    }

    // When player leave arena
    public static void onLeave(Player player, Arena arena) {
        CoolDown.heal(player);
        player.setInvulnerable(false);
        player.setGlowing(false);

        // Clear invertory
        player.getInventory().clear();

        // Cancel cooldown
        CoolDown.dispose(player);
    }
}
