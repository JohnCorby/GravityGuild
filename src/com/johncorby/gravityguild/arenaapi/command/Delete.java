package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.Arena;
import org.bukkit.entity.Player;

public class Delete extends BaseCommand {
    Delete() {
        super("Delete an arena", "<name>", "gg.admin");
        TabCompleteHandler.register(getName(), 0, Arena::getNames);
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) return MessageHandler.commandError(sender, "You must supply an arena name");

        // Error if arena doesn't exist
        if (Arena.get(args[0]) == null)
            return MessageHandler.commandError(sender, "Arena " + args[0] + " does not exist");

        // Delete arena
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "Arena " + args[0] + " deleted");
        return Arena.get(args[0]).dispose();
    }
}
