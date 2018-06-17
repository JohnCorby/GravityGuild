package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.util.MessageHandler;
import org.bukkit.entity.Player;

public class Update extends BaseCommand {
    Update() {
        super("Update arena blocks", "<name>", "gg.admin");
        TabCompleteHandler.register(getName(), 0, Arena::getNames);
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) return MessageHandler.commandError(sender, "You must supply an arena name");

        // Error if arena doesn't exist
        Arena a = Arena.get(args[0]);
        if (a == null) return MessageHandler.commandError(sender, "Arena " + args[0] + " does not exist");

        // Try to update region
        a.setRegion(a.getRegion());
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "Updated arena blocks");
        return true;
    }
}
