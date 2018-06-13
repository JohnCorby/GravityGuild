package com.johncorby.gravityguild.arenaapi.arena;

import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.util.Identifiable;
import org.bukkit.entity.Player;

public class SetRegion extends Identifiable<Player> {
    public int step;
    public String name;
    public Integer[] region = new Integer[4];
    private boolean add;


    public SetRegion(Player identity, String name, boolean add) {
        super(identity);
        create(identity, name, add);
    }

    public static SetRegion get(Player identity) {
        return (SetRegion) get(identity, SetRegion.class);
    }

    public static boolean contains(Player identity) {
        return contains(identity, SetRegion.class);
    }

    public static boolean dispose(Player identity) {
        return dispose(identity, SetRegion.class);
    }

    protected boolean create(Player identity, String name, boolean add) throws IllegalStateException {
        // Error if arena doesn't exist
        if (Arena.get(name) == null && !add)
            return MessageHandler.commandError(identity, "Arena " + name + " does not exist");

        // Error if player already setting region
        SetRegion sR = get(identity);
        if (sR != null)
            return MessageHandler.commandError(identity, "You are already setting region for arena " + sR.name);

        MessageHandler.msg(identity, MessageHandler.MessageType.GENERAL, "Left click block to set pos 1");

        this.name = name.toLowerCase();
        this.add = add;
        return super.create(identity);
    }

    @Override
    protected boolean create(Player identity) {
        return true;
    }

    // Go to next step in region setting
    public void next(int x, int z) {
        switch (step) {
            case 0: // Set first pos
                region[0] = x;
                region[1] = z;
                MessageHandler.msg(get(), MessageHandler.MessageType.GENERAL, "Left click block to set pos 2");
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
                    Arena arena = new Arena(name);
                    arena.setRegion(region);
                } else Arena.get(name).setRegion(region);

                MessageHandler.msg(get(), MessageHandler.MessageType.GENERAL, "Arena " + name + " region set");
                dispose();
                break;
        }
        step++;
    }
}



