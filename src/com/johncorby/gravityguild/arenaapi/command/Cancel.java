package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.SetRegionHandler;
import org.bukkit.entity.Player;

public class Cancel extends BaseCommand {
    Cancel() {
        super("Cancel arena region setting", "", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Error if setting region
        SetRegionHandler.SetRegion sR = SetRegionHandler.regionSetting(sender);
        if (sR == null) return MessageHandler.commandError(sender, "You're not setting an arena region");

        // Remove region setter
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "Cancelled region setting for arena " + sR.name);
        return SetRegionHandler.remove(sR);
    }
}
