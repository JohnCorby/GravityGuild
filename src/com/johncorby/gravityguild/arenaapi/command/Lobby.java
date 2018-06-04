package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.arenaapi.arena.LobbyHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Lobby extends BaseCommand {
    Lobby() {
        super("Teleport to lobby", "", "");
    }

    public static boolean lobby(Player player) {
        // Error if no lobby loc
        Location lL = LobbyHandler.get();
        if (lL == null) return MessageHandler.commandError(player, "No lobby location set");

        player.teleport(lL);
        MessageHandler.msg(player, MessageHandler.MessageType.GENERAL, "Teleported to lobby");
        return true;
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        // Leave arena if in one
        Arena aI = Arena.arenaIn(sender);
        if (aI != null) return aI.remove(sender);
        else return lobby(sender);
    }
}
