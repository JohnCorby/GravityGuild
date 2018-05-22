package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.entity.Player;

public class Delete extends BaseCommand {
    Delete() {
        super("Delete an arena", "<name>", "gg.admin");
        TabCompleteHandler.register(this, 0, ArenaHandler.getNames());
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) return error(sender, "You must supply an arena name");

        // Error if arena doesn't exist
        if (ArenaHandler.get(args[0]) == null) return error(sender, "Arena " + args[0] + " does not exist");

        // Delete arena
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "Arena " + args[0] + " deleted");
        return ArenaHandler.remove(ArenaHandler.get(args[0])) != null;
    }
}
