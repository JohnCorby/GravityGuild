package com.johncorby.gravityguild.arenaapi.arena;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.command.BaseCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class SetRegionHandler {
    private static ArrayList<SetRegion> setRegions = new ArrayList<>();

    public static boolean add(Player player, String name, boolean add) {
        // Error if arena doesn't exist
        if (ArenaHandler.get(name) == null && !add) return BaseCommand.error(player, "Arena " + name + " does not exist");

        // Error if player already setting region
        SetRegion setRegion = regionSetting(player);
        if (setRegion != null)
            return BaseCommand.error(player, "You are already setting region for arena " + setRegion.name);

        setRegion = new SetRegion(player, name, add);
        setRegions.add(setRegion);
        MessageHandler.msg(player, MessageHandler.MessageType.GENERAL, "Left click block to set pos 1");
        return true;
    }

    public static boolean remove(SetRegion setRegion) {
        return setRegions.remove(setRegion);
    }

    // Try to get region player is setting
    public static SetRegion regionSetting(Player player) {
        for (SetRegion setRegion : setRegions) {
            if (setRegion.player == player) return setRegion;
        }
        return null;
    }



    public static class SetRegion {
        public Player player;
        public int step;
        public String name;
        public Integer[] region = new Integer[4];
        private boolean add;

        public SetRegion(Player player, String name, boolean add) {
            this.player = player;
            this.name = name.toLowerCase();
            this.add = add;
        }

        @Override
        public String toString() {
            return "SetRegion@" + name;
        }

        // Go to next step in region setting
        public void next(int x, int z) {
            switch (step) {
                case 0: // Set first pos
                    region[0] = x;
                    region[1] = z;
                    MessageHandler.msg(player, MessageHandler.MessageType.GENERAL, "Left click block to set pos 2");
                    break;
                case 1: // Set second pos
                    region[2] = x;
                    region[3] = z;
                    // Swap so 1st pos is min and 2nd is max
                    if (region[0] > region[2]) {
                        Integer temp = region[0];
                        region[0] = region[2];
                        region[2] = temp;
                    }
                    if (region[1] > region[3]) {
                        Integer temp = region[1];
                        region[1] = region[3];
                        region[3] = temp;
                    }

                    // Add of update arena region
                    if (add) {
                        ArenaHandler.Arena arena = ArenaHandler.add(new ArenaHandler.Arena(name));
                        arena.setRegion(region);
                    } else ArenaHandler.get(name).setRegion(region);

                    MessageHandler.msg(player, MessageHandler.MessageType.GENERAL, "Arena " + name + " region set");
                    remove(this);
                    break;
            }
            step++;
        }
    }
}



