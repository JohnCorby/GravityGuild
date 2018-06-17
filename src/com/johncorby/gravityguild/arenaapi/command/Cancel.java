package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.arenaapi.arena.SetRegion;
import com.johncorby.gravityguild.util.MessageHandler;
import org.bukkit.entity.Player;

public class Cancel extends BaseCommand {
    Cancel() {
        super("Cancel arena region setting", "", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if setting region
        com.johncorby.gravityguild.arenaapi.arena.SetRegion sR = SetRegion.get(sender);
        if (sR == null) return MessageHandler.commandError(sender, "You're not setting an arena region");

        // Remove region setter
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "Cancelled region setting for arena " + sR.name);
        return SetRegion.dispose(sender);
    }
}
