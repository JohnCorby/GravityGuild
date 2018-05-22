package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.arenaapi.arena.SetRegionHandler;
import org.bukkit.entity.Player;

public class Add extends BaseCommand {
    Add() {
        super("Add an arena", "<name>", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if no name given
        if (args.length == 0) return error(sender, "You must give a name for the arena");

        // Add arena
        return SetRegionHandler.add(sender, args[0], true);
    }
}