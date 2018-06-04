package com.johncorby.gravityguild.arenaapi.event;

import com.johncorby.gravityguild.game.event.Entity;
import org.bukkit.event.Listener;

import static com.johncorby.gravityguild.GravityGuild.gravityGuild;

public class EventHandler {

    public EventHandler() {
        // Register events
        register(new Block());
        register(new Player());

        register(new com.johncorby.gravityguild.game.event.Player());
        register(new Entity());

        //new Any();
    }

    private static void register(Listener event) {
        gravityGuild.getServer().getPluginManager().registerEvents(event, gravityGuild);
    }
}
