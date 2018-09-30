package com.johncorby.gravityguild;

import com.johncorby.arenaapi.ArenaApiPlugin;
import com.johncorby.arenaapi.arena.ArenaEvents;
import com.johncorby.gravityguild.listener.EntityListeners;
import com.johncorby.gravityguild.listener.PlayerListeners;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * TODO BUGS: https://github.com/JohnCorby/GravityGuild/issues?q=is%3Aopen+is%3Aissue+label%3Abug
 * TODO FEATURES: https://github.com/JohnCorby/GravityGuild/issues?q=is%3Aopen+is%3Aissue+label%3Afeature
 */
public class GravityGuild extends ArenaApiPlugin {
    @NotNull
    @Override
    public String getMessagePrefix() {
        return ChatColor.GRAY + "[" + ChatColor.GOLD + "GravityGuild" + ChatColor.GRAY + "]";
    }

    @NotNull
    @Override
    public Listener[] getListeners() {
        return (Listener[]) ArrayUtils.addAll(super.getListeners(), new Listener[]{
                new EntityListeners(),
                new PlayerListeners()
        });
    }

    @NotNull
    @Override
    public ArenaEvents getArenaEvents() {
        return new com.johncorby.gravityguild.arena.ArenaEvents();
    }
}
