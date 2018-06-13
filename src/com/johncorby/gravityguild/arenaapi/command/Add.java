package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.SetRegion;
import org.bukkit.entity.Player;

public class Add extends BaseCommand {
    Add() {
        super("Add an arena", "<name>", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) return MessageHandler.commandError(sender, "You must give a name for the arena");

        // Add arena
        return new SetRegion(sender, args[0], true).exists();
    }
}
