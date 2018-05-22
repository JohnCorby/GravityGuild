package old;

import org.bukkit.entity.Player;

// This class is used for setting up an arena
public class SetArena {
    String player;
    int pos = 0;
    int number = 0;
    String region = "";

    // Constructor
    SetArena(Player player) {
        this.player = player.getName();
        //System.out.println("SetRegion list: " + GravityGuild.getPlugin(GravityGuild.class).setArena);
    }
}
