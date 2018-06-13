package com.johncorby.gravityguild.arenaapi.event;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.arenaapi.arena.SetRegion;
import com.johncorby.gravityguild.util.Runnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.johncorby.gravityguild.MessageHandler.MessageType.ERROR;

public class Player implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // If left clicked block
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // If setting region
            SetRegion setRegion = SetRegion.get(event.getPlayer());
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

    // Notify about bugs being present
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        new Runnable() {
            @Override
            public void run() {
                MessageHandler.msg(event.getPlayer(), ERROR,
                        "Be aware that there are still some bugs in this plugin!",
                        "check https://github.com/JohnCorby/GravityGuild/issues and report any new bug you find!");
            }
        }.runTaskLater(10);
    }
}
