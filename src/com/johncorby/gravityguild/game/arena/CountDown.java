package com.johncorby.gravityguild.game.arena;

import com.johncorby.gravityguild.arenaapi.arena.Arena;
import com.johncorby.gravityguild.util.Common;
import com.johncorby.gravityguild.util.Identifiable;
import com.johncorby.gravityguild.util.Runnable;

import static com.johncorby.gravityguild.GravityGuild.getOverridePlayers;

public class CountDown extends Identifiable<Arena> {
    private static final int d = 10;
    private final Task task = new Task();
    private int t = d;

    public CountDown(Arena reference) {
        super(reference);
        task.runTaskTimer(0, 20);
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
    public boolean dispose() {
        return task.dispose();
    }

    private class Task extends Runnable {
        @Override
        public void run() {
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
                        cancel();
                    } else {
                        get().broadcast("Arena needs 2 or more players to start");
                        t = d;
                    }
                    break;
            }
            t--;
        }

        private boolean dispose() {
            super.cancel();
            return CountDown.super.dispose();
        }

        @Override
        public synchronized void cancel() {
            dispose();
        }
    }

}

/*
class _CountDown {
    private static List<Task> countDowns = new ArrayList<>();

    // Run task
    public static void run(Arena a) {
        new Task(a);
    }

    // Cancel task
    public static void cancel(Arena a) {
        Task t = get(a);
        if (t == null) return;
        t.cancel();
    }

    // Get task by arena
    private static Task get(Arena a) {
        return Common.find(countDowns, c -> c.a, a);
    }

    private static class Task extends Runnable {
        private static final int d = 10;
        private Arena a;
        private int t = d;

        Task(Arena a) {
            if (get(a) != null) return;
            countDowns.add(this);

            this.a = a;
            runTaskTimer(0, 20);
        }

        @Override
        public void run() {
            switch (t) {
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                case 1:
                    a.broadcast(t + " second/seconds until game begins");
                    break;
                case 0:
                    // Start arena if 2 or more players
                    if (a.getPlayers().size() > 1 ||
                            Common.containsAny(a.getPlayers(), getOverridePlayers())) {
                        a.setState(Arena.State.RUNNING);
                        CountDown.cancel(a);
                    } else {
                        a.broadcast("Arena needs 2 or more players to start");
                        t = d;
                    }
                    break;
            }
            t--;
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            countDowns.remove(this);
            super.cancel();
        }
    }
}
*/
