/* TODO:
 *
 * change arena resetting to replace chunks instead of using events/changed blocks
 * automatically add any created entity to arena list if its coordinately in one
 * automatically remove any removed entity from arena list if its in it
 *
 *
 * add detailed death stuff
 *
 *
 * scoreboard containing alive players etc etc
 *
 * more metadata (i think its tile entities?) so that chests, banners, signs, etc will store stuff, etc.
 * 
 * choose random map like murder except without votes
 * every so often, blocks should regenerate OR regenerate a layer of floor every so often
 *
 * TEAM MODE
 */

package com.johncorby.gravityguild;

import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import com.johncorby.gravityguild.arenaapi.command.CommandHandler;
import com.johncorby.gravityguild.arenaapi.event.EventHandler;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class GravityGuild extends JavaPlugin {
	public static JavaPlugin gravityGuild;
    public static CommandHandler commandHandler;
    public static EventHandler eventHandler;

    public static FileConfiguration CONFIG;
    public static World WORLD;

    public static ArrayList<Player> overridePlayers = new ArrayList<>();

	// When plugin enabled
	@Override
	public void onEnable() {
	    // Init classes
	    gravityGuild = getPlugin(GravityGuild.class);
	    CONFIG = getConfig();

	    commandHandler = new CommandHandler();
        eventHandler = new EventHandler();

        // Set up config
		getConfig().options().copyDefaults(true);
		saveConfig();

        // Load stuff from config
        WORLD = getServer().getWorld(getConfig().getString("World"));

        for (String name : ArenaHandler.arenaSection.getKeys(false)) {
            ConfigurationSection arena = ArenaHandler.arenaSection.getConfigurationSection(name);
            Integer[] region = arena.getIntegerList("Region").toArray(new Integer[0]);
            Integer[] signLoc = arena.getIntegerList("SignLoc").toArray(new Integer[0]);
            ArenaHandler.add(new ArenaHandler.Arena(name, region, signLoc));
        }

        // Add override players
        overridePlayers.add(gravityGuild.getServer().getPlayer("johncorby"));
        overridePlayers.add(gravityGuild.getServer().getPlayer("funkymunky111"));

        MessageHandler.log(MessageHandler.MessageType.GENERAL,"GravityGuild Enabled");
    }

	// When plugin disabled
	@Override
	public void onDisable() {
	    // Stop all arenas
	    for (ArenaHandler.Arena a : ArenaHandler.arenas) {
	        for (Player p : a.getPlayers())
                MessageHandler.msg(p, MessageHandler.MessageType.ERROR, "You have been forced out of the arena because the plugin was reloaded (probably for debugging)");
            a.setState(ArenaHandler.Arena.State.STOPPED);

        }

	    /*
		// End all setRegions
		for (int i = 0; i < arena.length; i++)
			if (arena[i] != null) {
                m.broadcast(i, ChatColor.RED + "You have been forced out of the arena because the plugin was reloaded (probably for debugging)");
                m.endGame(i);
            }
        */

        MessageHandler.log(MessageHandler.MessageType.GENERAL,"GravityGuild Disabled");
	}
}
