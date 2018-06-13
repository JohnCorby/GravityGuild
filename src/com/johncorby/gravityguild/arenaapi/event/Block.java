package com.johncorby.gravityguild.arenaapi.event;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.util.Common;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import static com.johncorby.gravityguild.MessageHandler.commandError;

public class Block implements Listener {
    // For setting gravityguild signs
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        // Ignore if not sign
        if (!(event.getBlock().getState() instanceof Sign)) return;
        Sign s = (Sign) event.getBlock().getState();

        // Ignore if not gravityguild sign
        if (!s.getLine(0).equalsIgnoreCase(ChatColor.YELLOW + "[GravityGuild]")) return;

        // Try to get arena
        Arena a = Arena.get(s.getLine(1));
        if (a == null) return;

        // Ignore if sign loc doesn't match arena loc
        if (!a.getSign().getLocation().equals(event.getBlock().getLocation())) return;

        // Remove sign from arena
        a.setSign(null);
        MessageHandler.msg(event.getPlayer(), MessageHandler.MessageType.GENERAL, "Removed sign for " + a.get());
    }


    // For setting gravityguild signs
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        // Ignore normal signs
        if (!event.getLine(0).equalsIgnoreCase("[gravityguild]")) return;

        // Try to get arena
        String aN = event.getLine(1);
        Arena a = Arena.get(aN);
        if (a == null) {
            commandError(event.getPlayer(), "Arena " + aN + " doesn't exist");
            return;
        }

        // Ignore if arena already has sign
        if (a.getSign() != null) {
            commandError(event.getPlayer(), "Arena " + aN + " already has sign");
            return;
        }

        // Set sign for arena
        event.setLine(0, ChatColor.YELLOW + "[GravityGuild]");
        event.setLine(1, a.get());
        event.setLine(2, a.getState().get());
        event.setLine(3, Common.toStr(a.getPlayers().size()));
        a.setSign((Sign) event.getBlock().getState());
        MessageHandler.msg(event.getPlayer(), MessageHandler.MessageType.GENERAL, "Set sign for " + aN);
    }
}
