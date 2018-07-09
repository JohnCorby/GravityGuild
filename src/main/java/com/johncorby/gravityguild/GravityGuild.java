package com.johncorby.gravityguild;

import com.johncorby.arenaapi.ArenaApiPlugin;
import com.johncorby.arenaapi.arena.ArenaEvents;
import com.johncorby.gravityguild.listener.Entity;
import com.johncorby.gravityguild.listener.Player;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

/**
 * TODO BUGS: https://github.com/JohnCorby/GravityGuild/issues?q=is%3Aopen+is%3Aissue+label%3Abug
 * TODO FEATURES: https://github.com/JohnCorby/GravityGuild/issues?q=is%3Aopen+is%3Aissue+label%3Afeature
 */
public class GravityGuild extends ArenaApiPlugin {
    @Override
    public String getMessagePrefix() {
        return ChatColor.GRAY + "[" + ChatColor.GOLD + "GravityGuild" + ChatColor.GRAY + "]";
    }

    @Override
    public Listener[] getListeners() {
        return (Listener[]) ArrayUtils.addAll(super.getListeners(), new Listener[]{
                new Entity(),
                new Player()
        });
    }

    @Override
    public ArenaEvents getArenaEvents() {
        return new com.johncorby.gravityguild.arena.ArenaEvents();
    }
}
