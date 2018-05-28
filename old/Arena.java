package old;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;

import java.util.ArrayList;

public class Arena {
    // For referencing other classes
    static GravityGuild g;
    static Methods m;
    static Events e;
    static Commands c;
    static TabCompletion t;
    static Arena a;
    States state;
    ArrayList<Player> players;
    ArrayList<BrokenBlocks> brokenBlocks;
    ArrayList<Arrow> arrows;
    ArrayList<WitherSkull> skulls;
    int ID;

    Arena(GravityGuild g, Methods m, Events e, Commands c, TabCompletion t, Arena a) {
        this.g = g;
        this.m = m;
        this.e = e;
        this.c = c;
        this.t = t;
        this.a = a;
    }

    Arena(int ID) {
        this.ID = ID;

        setState(States.STOPPED);
        players = new ArrayList<>();
        brokenBlocks = new ArrayList<>();
        arrows = new ArrayList<>();
        skulls = new ArrayList<>();

        m.setSign(ID, 2, "0 Players");
    }

    public void setState(States state) {
        this.state = state;
        m.setSign(ID, 3, "" + this.state);
        //g.getLogger().info("Arena: " + ID + " - State: " + this.state);
    }

    public void addPlayer(Player player) {
        players.add(player);
        m.setSign(ID, 2, "" + players.size() + (players.size() == 1 ? " Player" : " Players"));
        //g.getLogger().info("Arena: " + ID + " - Added Player: " + player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        m.setSign(ID, 2, "" + players.size() + (players.size() == 1 ? " Player" : " Players"));
        //g.getLogger().info("Arena: " + ID + " - Removed Player: " + player);
    }

    enum States {STOPPED, STARTING, STARTED, STOPPING}
}

class BrokenBlocks {
    Material mat;
    Byte data;
    Location loc;

    BrokenBlocks(Block block) {
        mat = block.getType();
        data = block.getData();
        loc = block.getLocation();
    }
}
