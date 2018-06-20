package com.johncorby.gravityguild;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.arenaapi.command.CommandHandler;
import com.johncorby.gravityguild.arenaapi.command.TabCompleteHandler;
import com.johncorby.gravityguild.arenaapi.event.EventHandler;
import com.johncorby.gravityguild.util.Common;
import com.johncorby.gravityguild.util.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * TODO BUGS: https://github.com/JohnCorby/GravityGuild/issues?q=is%3Aopen+is%3Aissue+label%3Abug
 * TODO FEATURES: https://github.com/JohnCorby/GravityGuild/issues?q=is%3Aopen+is%3Aissue+label%3Afeature
 */
public class GravityGuild extends JavaPlugin {
    public static GravityGuild gravityGuild;
    public static CommandHandler commandCommandHandler;
    public static TabCompleteHandler tabCompleteHandler;
    public static EventHandler eventEventHandler;

    public static FileConfiguration CONFIG;
    public static World WORLD;

    public static String[] overridePlayers = {"johncorby", "funkymunky111"};

    public static ArrayList<Player> getOverridePlayers() {
        return Common.map(Arrays.asList(overridePlayers), p -> Bukkit.getServer().getPlayer(p));
    }

    // When plugin enabled
    @Override
    public void onEnable() {
        // Init classes
        gravityGuild = this;
        CONFIG = getConfig();

        commandCommandHandler = new CommandHandler();
        tabCompleteHandler = new TabCompleteHandler();
        eventEventHandler = new EventHandler();

        // Set up config
        getConfig().options().copyDefaults(true);
        saveConfig();

        // Load stuff from config
        WORLD = getServer().getWorld(getConfig().getString("World"));

        for (String name : Arena.arenaSection.getKeys(false)) {
            ConfigurationSection arena = Arena.arenaSection.getConfigurationSection(name);
            Integer[] region = arena.getIntegerList("Region").toArray(new Integer[0]);
            Integer[] signLoc = arena.getIntegerList("SignLoc").toArray(new Integer[0]);
            new Arena(name, region, signLoc);
        }

        MessageHandler.log(MessageHandler.MessageType.GENERAL, "GravityGuild Enabled");
    }

    // When plugin disabled
    @Override
    public void onDisable() {
        // Stop all arenas
        for (Arena a : Arena.getArenas()) {
            for (Player p : a.getPlayers())
                MessageHandler.msg(p, MessageHandler.MessageType.ERROR, "You have been forced out of the arena because the plugin was reloaded (probably for debugging)");
            a.setState(Arena.State.STOPPED);
        }

        // Stop all GravityGuild tasks
        Bukkit.getScheduler().cancelTasks(this);

        MessageHandler.log(MessageHandler.MessageType.GENERAL, "GravityGuild Disabled");
    }
}
