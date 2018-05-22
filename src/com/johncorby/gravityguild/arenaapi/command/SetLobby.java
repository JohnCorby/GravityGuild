package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.arena.LobbyHandler;
import org.bukkit.entity.Player;

public class SetLobby extends BaseCommand {
    SetLobby() {
        super("Set lobby location", "", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        LobbyHandler.set(sender.getLocation());
        MessageHandler.msg(sender, MessageHandler.MessageType.GENERAL, "Lobby location set");
        return true;
    }
}
