package com.johncorby.gravityguild.arena;

import com.johncorby.arenaapi.arena.Arena;
import com.johncorby.coreapi.util.storedclass.IdentTask;
import com.johncorby.gravityguild.GravityGuild;
import org.jetbrains.annotations.Nullable;

import static com.johncorby.coreapi.util.Collections.containsAny;

public class CountDown extends IdentTask<Arena> {
    private static final int d = 10;
    private int t = d;

    public CountDown(Arena identity) {
        super(identity);
        create();
    }

    @Nullable
    public static CountDown get(Arena identity) {
        return get(CountDown.class, identity);
    }

    @Override
    public boolean create() {
        if (!super.create()) return false;
        task.runTaskTimer(0, 20);
        return true;
    }

    @Override
    protected void run() {
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
                        containsAny(get().getPlayers(), GravityGuild.getOverridePlayers())) {
                    get().setState(Arena.State.RUNNING);
                    dispose();
                } else {
                    get().broadcast("Arena needs 2 or more players to join");
                    t = d;
                }
                break;
        }
        t--;
    }

}
