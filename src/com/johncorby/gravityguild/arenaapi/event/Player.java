package com.johncorby.gravityguild.arenaapi.event;

import com.johncorby.gravityguild.GravityGuild;
import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.arenaapi.arena.SetRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Player implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        MessageHandler.msg(event.getPlayer(),
                MessageHandler.MessageType.GAME,
                GravityGuild.WORLD.getHighestBlockAt(event.getPlayer().getLocation()).getY());

        // If left clicked block
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // If setting region
            SetRegion setRegion = (SetRegion) SetRegion.get(event.getPlayer());
            if (setRegion != null) {
                // Call next for region
                Location loc = event.getClickedBlock().getLocation();
                setRegion.next(loc.getBlockX(), loc.getBlockZ());
                event.setCancelled(true);
            }
        }

        // If right clicked block
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Ignore if not sign
            if (!(event.getClickedBlock().getState() instanceof Sign)) return;
            Sign s = (Sign) event.getClickedBlock().getState();

            // Ignore normal signs
            if (!s.getLine(0).equalsIgnoreCase(ChatColor.YELLOW + "[GravityGuild]")) return;

            // Try to join game
            Arena.get(s.getLine(1)).add(event.getPlayer());
        }

    }



    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        org.bukkit.entity.Player player = event.getPlayer();
        // Try to get arena and make player leave it
        Arena aI = Arena.arenaIn(player);
        if (aI != null) aI.remove(player);
    }
}
