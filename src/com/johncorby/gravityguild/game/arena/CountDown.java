package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.util.Common;
import com.johncorby.gravityguild.util.IdentifiableTask;

import static com.johncorby.gravityguild.GravityGuild.getOverridePlayers;

public class CountDown extends IdentifiableTask<Arena> {
    private static final int d = 10;
    private int t = d;

    public CountDown(Arena identity) {
        super(identity);
    }

    public static CountDown get(Arena identity) {
        return (CountDown) get(identity, CountDown.class);
    }

    public static boolean contains(Arena identity) {
        return contains(identity, CountDown.class);
    }

    public static boolean dispose(Arena identity) {
        return dispose(identity, CountDown.class);
    }

    @Override
    protected boolean create(Arena identity) {
        if (!super.create(identity)) return false;
        task.runTaskTimer(0, 20);
        return true;
    }

    @Override
    protected void run() {
        super.run();
        switch (t) {
            case 10:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                get().broadcast(t + " second/seconds until game begins");
                break;
            case 0:
                // Start arena if 2 or more players
                if (get().getPlayers().size() > 1 ||
                        Common.containsAny(get().getPlayers(), getOverridePlayers())) {
                    get().setState(Arena.State.RUNNING);
                    dispose();
                } else {
                    get().broadcast("Arena needs 2 or more players to create");
                    t = d;
                }
                break;
        }
        t--;
    }

}
