package com.johncorby.gravityguild.arenaapi.command;

import com.johncorby.gravityguild.arenaapi.arena.ArenaHandler;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.johncorby.gravityguild.MessageHandler.MessageType.GENERAL;
import static com.johncorby.gravityguild.MessageHandler.msg;
import static com.johncorby.gravityguild.arenaapi.arena.ArenaHandler.arenas;

public class ArenaStats extends BaseCommand {
    ArenaStats() {
        super("Get stats for arenas", "", "gg.admin");
    }

    @Override
    public boolean onCommand(Player sender, String[] args) {
        msg(sender, GENERAL, "----- Arena stats -----");

        // Error if no arenas
        if (arenas.isEmpty()) return error(sender, "There are no arenas");

        // Get stats of each arena
        for (ArenaHandler.Arena a : arenas) {
            List<String> sl = new ArrayList<>();
            sl.add("Arena " + a.getName() + ":");
            sl.add("    State: " + a.getState().get());

            /*
            Integer[] r = a.getRegion();
            Location s = a.getSign().getLocation();
            String rs = r == null ? "N/A" : String.format("(%s, %s), (%s, %s)", r[0], r[1], r[2], r[3]),
                    ss = s == null ? "N/A" : String.format("(%s, %s, %s)", s.getBlockX(), s.getBlockY(), s.getBlockZ());
            sl.add("Region: " + rs);
            sl.add("Sign location: " + ss);
            */

            List<Entity> el = a.getEntities();
            sl.add("    Entities: " + (el.isEmpty() ? "None" : ""));
            //sl.add("    Entities: " + el);
            for (Entity e : a.getEntities()) {
                Location l = e.getLocation();
                sl.add(String.format("        %s {(%s, %s, %s), (%s, %s)}",
                        e.getType(),
                        l.getBlockX(),
                        l.getBlockY(),
                        l.getBlockZ(),
                        (int) l.getPitch(),
                        (int) l.getYaw()));
            }

            List<BlockState> bl = a.getChangedBlocks();
            sl.add("    Changed blocks: " + (bl.isEmpty() ? "None" : ""));
            //sl.add("    Changed blocks: " + bl);
            for (BlockState b : a.getChangedBlocks()) {
                Location l = b.getLocation();
                sl.add(String.format("        %s {(%s, %s, %s)}",
                        b.getType(),
                        l.getBlockX(),
                        l.getBlockY(),
                        l.getBlockZ()));
            }

            msg(sender, GENERAL, sl.toArray());

        }
        return true;
    }
}
