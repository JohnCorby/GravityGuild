/* TODO:
 * scoreboard containing alive players etc etc
 *
 * more metadata (i think its tile entities?) so that chests, banners, signs, etc will store stuff, etc.
 * 
 * choose random map like murder except without votes
 * every so often, blocks should regenerate OR regenerate a layer of floor every so often
 *
 * TEAM MODE
 */

package old;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class GravityGuild extends JavaPlugin {
	Methods m;
	Events e;
	Commands c;
	TabCompletion t;
	Arena a;

	FileConfiguration config = getConfig();
	
	Arena[] arena = new Arena[100];

	World world = Bukkit.getWorld("World");

    // List of setArena instances
	HashMap<Player, SetArena> setArena = new HashMap<>();

	// When plugin enabled
	@Override
	public void onEnable() {
		m = new Methods(this, m, e, c, t, a);
		e = new Events(this, m, e, c, t, a);
		c = new Commands(this, m, e, c, t, a);
		t = new TabCompletion(this, m, e, c, t, a);
        a = new Arena(this, m, e, c, t, a);
		
		// Set up config
		getConfig().options().copyDefaults(true);
		saveConfig();

		// Creates arena object for each array index
		//for (int i = 0; i < arena.length; i++)
		//	arena[i] = new Arena();

		// Set arena already in config
		for (int i = 0; i < arena.length; i++)
			if (getConfig().getConfigurationSection("Arenas." + i) != null)
				arena[i] = new Arena(i);
		
		// Set up com.johncorby.gravityguild.command executors + tab completers + register events
		getCommand("ggadd").setExecutor(c);
		
		getCommand("ggdel").setExecutor(c);
		getCommand("ggdel").setTabCompleter(t);
		
		getCommand("ggupdate").setExecutor(c);
		getCommand("ggupdate").setTabCompleter(t);

        getCommand("ggcancel").setExecutor(c);
		
		getCommand("ggjoin").setExecutor(c);
		getCommand("ggjoin").setTabCompleter(t);
		
		getCommand("ggleave").setExecutor(c);

        getCommand("gglobby").setExecutor(c);

        getCommand("ggsetlobby").setExecutor(c);

        getCommand("ggstat").setExecutor(c);
        getCommand("ggstat").setTabCompleter(t);

        getServer().getPluginManager().registerEvents(e, this);

		// Stuff for MagicPortalTest
		getServer().getPluginManager().registerEvents(new MagicPortalTest(), this);
		MagicPortalTest.enable();

		getLogger().info("Gravityguild Enabled");
    }

	// When plugin disabled
	@Override
	public void onDisable() {
		// End all setRegions
		for (int i = 0; i < arena.length; i++)
			if (arena[i] != null) {
                m.broadcast(i, ChatColor.RED + "You have been forced out of the arena because the plugin was reloaded (probably for debugging)");
                m.endGame(i);
            }
	}
}
