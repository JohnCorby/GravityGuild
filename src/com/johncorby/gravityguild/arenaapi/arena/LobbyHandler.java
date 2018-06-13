package com.johncorby.gravityguild.arenaapi.arena;

import com.johncorby.gravityguild.GravityGuild;
import org.bukkit.Location;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;

public class LobbyHandler {
    // Set lobby loc
    public static void set(Location lobbyLoc) {
        Integer[] iL = new Integer[]{lobbyLoc.getBlockX(), lobbyLoc.getBlockY(), lobbyLoc.getBlockZ(), (int) lobbyLoc.getYaw(), (int) lobbyLoc.getPitch()};
        GravityGuild.CONFIG.set("Lobby", iL);
        gravityGuild.saveConfig();
    }

    // Get lobby loc
    public static Location get() {
        // Try to get loc from config
        Integer[] iL = GravityGuild.CONFIG.getIntegerList("Lobby").toArray(new Integer[0]);
        if (iL.length == 0) return null;
        return new Location(GravityGuild.WORLD, iL[0] + .5, iL[1], iL[2] + .5, iL[3], iL[4]);
    }
}
