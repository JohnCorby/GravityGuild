package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.Arena;
import org.bukkit.entity.Player;

public class Leave extends BaseCommand {
    Leave() {
        super("Leave the arena you're in", "", "");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Try to get arena that player is in
        Arena aI = Arena.arenaIn(sender);
        if (aI == null) return MessageHandler.commandError(sender, "You're not in any arena");

        // Leave and tp to lobby
        return aI.remove(sender);
    }

}
