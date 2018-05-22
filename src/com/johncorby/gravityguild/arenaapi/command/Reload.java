package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import org.bukkit.entity.Player;

import static org.bukkit.Bukkit.getConsoleSender;
import static org.bukkit.Bukkit.getServer;

public class Reload extends BaseCommand {
    Reload() {
        super("Reload the plugin", "", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "Reloading GravityGuild");
        // Reload using console and plugman
        return getServer().dispatchCommand(getConsoleSender(), "plugman reload GravityGuild");
    }
}
