package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.entity.Player;

public class Leave extends BaseCommand {
    Leave() {
        super("Leave the arena you're in", "", "");
        TabCompleteHandler.register(this, 0, ArenaHandler.getNames());
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Try to get arena that player is in
        ArenaHandler.Arena aI = ArenaHandler.arenaIn(sender);
        if (aI == null) return error(sender, "You're not in any arena");

        // Leave and tp to lobby
        return aI.removeEntity(sender);
    }

}
