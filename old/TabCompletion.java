package old;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabCompletion implements TabCompleter {
    GravityGuild g;
    Methods m;
    Events e;
    Commands c;
    TabCompletion t;
    Arena a;

    TabCompletion(GravityGuild g, Methods m, Events e, Commands c, TabCompletion t, Arena a) {
        this.g = g;
        this.m = m;
        this.e = e;
        this.c = c;
        this.t = t;
        this.a = a;
    }

    // Tab completion
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        //String cmdName = cmd.getName();
        //if (!cmdName.equalsIgnoreCase("ggdel") && !cmdName.equalsIgnoreCase("ggupdate") && !cmdName.equalsIgnoreCase("ggjoin"))
        //	return null;

        if (args[0] != null) {
            ArrayList<String> arenas = new ArrayList<>();
            arenas.addAll(g.getConfig().getConfigurationSection("Arenas").getKeys(false));
            ;
            return arenas;
        }

        if (args[1] != null) {
            ArrayList<String> complete = new ArrayList<>();
            complete.add("state");
            complete.add("players");
            return complete;
        }

        return null;
    }
}
