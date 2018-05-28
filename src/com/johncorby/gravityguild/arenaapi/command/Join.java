package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.entity.Player;

public class Join extends BaseCommand {
    Join() {
        super("JoinLeave an arena", "<name>", "");
        TabCompleteHandler.register(this, 0, ArenaHandler.getNames());
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no arena name
        if (args.length == 0) return MessageHandler.commandError(sender, "You must supply an arena name");

        // Try to get arena
        ArenaHandler.Arena a = ArenaHandler.get(args[0]);
        if (a == null) return MessageHandler.commandError(sender, "Arena " + args[0] + " does not exist");

        return a.add(sender);
    }
}
