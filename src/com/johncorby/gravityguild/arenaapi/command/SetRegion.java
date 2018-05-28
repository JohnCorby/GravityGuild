package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import com.johncorby.gravityguild.arenaapi.arena.SetRegionHandler;
import org.bukkit.entity.Player;

public class SetRegion extends BaseCommand {
    SetRegion() {
        super("Set an arena's region", "<name>", "gg.admin");
        TabCompleteHandler.register(this, 0, ArenaHandler.getNames());
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) return MessageHandler.commandError(sender, "You must supply an arena name");

        // Try to update region
        return SetRegionHandler.add(sender, args[0], false);
    }
}
