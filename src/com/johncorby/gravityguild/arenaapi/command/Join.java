package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.Arena;
import org.bukkit.entity.Player;

public class Join extends BaseCommand {
    Join() {
        super("JoinLeave an arena", "<name>", "");
        TabCompleteHandler.register(getName(), 0, Arena::getNames);
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no arena name
        if (args.length == 0) return MessageHandler.commandError(sender, "You must supply an arena name");

        // Try to get arena
        Arena a = Arena.get(args[0]);
        if (a == null) return MessageHandler.commandError(sender, "Arena " + args[0] + " does not exist");

        return a.add(sender);
    }
}
