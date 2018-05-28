package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.entity.Player;

public class Update extends BaseCommand {
    Update() {
        super("Update arena blocks", "<name>", "gg.admin");
        TabCompleteHandler.register(this, 0, ArenaHandler.getNames());
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) return MessageHandler.commandError(sender, "You must supply an arena name");

        // Error if arena doesn't exist
        ArenaHandler.Arena a = ArenaHandler.get(args[0]);
        if (a == null) return MessageHandler.commandError(sender, "Arena " + args[0] + " does not exist");

        // Try to update region
        a.setRegion(a.getRegion());
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "Updating arena blocks");
        return true;
    }
}
