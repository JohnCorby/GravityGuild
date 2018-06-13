package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.Arena;
import org.bukkit.entity.Player;

public class SetRegion extends BaseCommand {
    SetRegion() {
        super("Set an arena's region", "<name>", "gg.admin");
        TabCompleteHandler.register(getName(), 0, Arena::getNames);
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) return MessageHandler.commandError(sender, "You must supply an arena name");

        // Try to update region
        return new com.johncorby.gravityguild.arenaapi.arena.SetRegion(sender, args[0], false).exists();
    }
}
