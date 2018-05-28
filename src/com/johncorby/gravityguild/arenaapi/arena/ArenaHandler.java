package com.johncorby.gravityguild.arenaapi.arena;

import com.johncorby.gravityguild.GravityGuild;
import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.command.Lobby;
import com.johncorby.gravityguild.game.arena.JoinLeave;
import com.johncorby.gravityguild.game.arena.StateChange;
import com.johncorby.gravityguild.util.Common;
import com.johncorby.gravityguild.util.Runnable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.johncorby.gravityguild.GravityGuild.*;
import static com.johncorby.gravityguild.MessageHandler.*;
import static com.johncorby.gravityguild.util.Common.*;

public class ArenaHandler {
    public static ArrayList<Arena> arenas = new ArrayList<>();
    public static ConfigurationSection arenaSection = GravityGuild.CONFIG.getConfigurationSection("Arenas");

    public static Arena add(Arena arena) {
        if (arenas.contains(arena)) return null;
        arenas.add(arena);
        return arena;
    }

    public static Arena remove(Arena arena) {
        arenaSection.set(arena.getName(), null);
        gravityGuild.saveConfig();
        return arenas.remove(arena) ? arena : null;
    }

    // Get arena by name
    public static Arena get(String name) {
        name = name.toLowerCase();
        for (Arena arena : arenas)
            if (arena.getName().equals(name))
                return arena;
        return null;
    }

    // Get arena names
    public static String[] getNames() {
        if (arenas == null) return null;
        return toArray(map(arenas, Arena::getName), new String[0]);
    }

    // Get arena entity is in
    public static Arena arenaIn(Entity entity) {
        if (entity == null) return null;
        return arenaIn(entity.getLocation());
    }

    // Get arena block is in
    public static Arena arenaIn(Block block) {
        return arenaIn(block.getState());
    }

    public static Arena arenaIn(BlockState block) {
        if (block == null) return null;
        return arenaIn(block.getLocation());
    }

    // Get location is in
    public static Arena arenaIn(Location location) {
        if (location == null) return null;
        for (Arena a : arenas)
            if (a.contains(location)) return a;
        return null;
    }


    public static class Arena {
        private State state = State.STOPPED;
        //private ArrayList<BlockState> changedBlocks = new ArrayList<>();
        private Sign sign;
        private ArrayList<BlockState> blocks = new ArrayList<>();
        private Integer[] region;
        private String name;
        private ConfigurationSection configLoc;

        public Arena(String name) {
            this.name = name.toLowerCase();
            configLoc = arenaSection.createSection(this.name);
        }

        // Constructor and sets config
        public Arena(String name, Integer[] region, Integer[] signLoc) {
            this.name = name.toLowerCase();
            configLoc = arenaSection.getConfigurationSection(this.name);

            setRegion(region);

            // Try to get sign from config signLoc
            if (signLoc.length == 0) return;
            Location l = new Location(WORLD, signLoc[0], signLoc[1], signLoc[2]);
            setSign((Sign) l.getBlock().getState());
        }

        // Message of type to players in arena
        public void broadcast(Object message) {
            broadcast(message, null);
        }

        // Message of type to players in arena except
        public void broadcast(Object message, Player... except) {
            if (except == null) except = new Player[0];
            for (Entity entity : getEntities())
                if (entity instanceof Player && !Arrays.asList(except).contains(entity))
                    MessageHandler.msg(entity, MessageHandler.MessageType.GAME, message);
        }

        @Override
        public String toString() {
            return "Arena@" + name;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Arena && ((Arena) obj).name.equals(name);
        }

        public List<Entity> getEntities() {
            return WORLD.getEntities().stream().filter(this::contains).collect(Collectors.toList());
        }

        public List<Player> getPlayers() {
            List<Player> p = new ArrayList<>();
            for (Entity e : getEntities()) {
                if (e instanceof Player) p.add((Player) e);
            }
            return p;
        }

        public String getName() {
            return name;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
            switch (state) {
                case OPEN:
                    StateChange.on(this);
                    break;
                case RUNNING:
                    broadcast("Game start");

                    StateChange.on(this);
                    break;
                case STOPPED:
                    // Remove entities
                    for (Entity e : getEntities())
                        remove(e);


                    // This probably wont work lol
                    setBlocks();


                    /*
                    Common.debug("Setting blocks");
                    for (BlockState b : blocks)
                        b.update(true, false);
                    Common.debug("Done setting blocks");


                    // Set back all changed blocks
                    for (BlockState b : changedBlocks)
                        b.update(true, false);
                    changedBlocks.clear();
                    */

                    StateChange.on(this);
                    break;
            }

            // SetRegion sign
            if (sign != null) {
                sign.setLine(2, state.get());
                sign.update(true, false);
            }
        }

        public Integer[] getRegion() {
            return region;
        }

        public void setRegion(Integer[] region) {
            this.region = region;
            configLoc.set("Region", region);
            gravityGuild.saveConfig();


            // This probably wont work lol
            getBlocks();
            /*

            //blocks.clear();
            //int percentTotal = 256 * (region[2] - region[0] + 1) * (region[3] - region[1] + 1),
            //percentDone = 0;
            /*
            for (int z = region[1]; z <= region[3]; z++) {
                for (int x = region[0]; x <= region[2]; x++) {
                    for (int y = 0; y < 256; y++) {
                        blocks.add(WORLD.getBlockAt(x, y, z).getState());
                    }
                    //percentDone += 256;
                    //gravityGuild.getLogger().info(percentDone  + "/" + percentTotal);
                }
            }

            for (BlockState b : blocks)
                b.update(true, false);


            new MultiForLoop(0, 0, 1,
                    new MultiForLoop.Container(0, 10),
                    new MultiForLoop.Container(0, 10),
                    new MultiForLoop.Container(0, 10));


            //new Common.MultiForLoop(0, 10, 1, 0, 10, 1,
            //        Common::debug);

            Common.debug("Getting blocks");
            blocks.clear();
            for (int z = region[1]; z <= region[3]; z++)
                for (int x = region[0]; x <= region[2]; x++)
                    for (int y = 0; y < 256; y++)
                        blocks.add(WORLD.getBlockAt(x, y, z).getState());
            Common.debug("Done getting blocks");



            Common.debug("Testing runnable lag");
            for (int i = 0; i < 10000000; i++)
                new Runnable() {
                    @Override
                    public void run() {
                        blocks.add(WORLD.getBlockAt(0, 0, 0).getState());
                    }
                }.runTask();
            Common.debug("Done testing runnable lag");
            */
        }

        public Sign getSign() {
            return sign;
        }

        public void setSign(Sign sign) {
            this.sign = sign;

            // Try to set config signLoc from sign
            if (sign == null)
                configLoc.set("SignLoc", null);
            else {
                Location l = sign.getLocation();
                Integer[] signLoc = new Integer[]{l.getBlockX(), l.getBlockY(), l.getBlockZ()};
                configLoc.set("SignLoc", signLoc);

                // SetRegion sign
                sign.setLine(0, ChatColor.YELLOW + "[GravityGuild]");
                sign.setLine(1, name);
                sign.setLine(2, state.get());
                sign.setLine(3, Common.toStr(getPlayers().size()));
                sign.update(true, false);
            }
            gravityGuild.saveConfig();
        }

        //public ArrayList<BlockState> getChangedBlocks() {
        //    return changedBlocks;
        //}

        public boolean add(Entity entity) {
            Arena aI = arenaIn(entity);

            // If entity is player
            if (entity instanceof Player) {
                Player p = (Player) entity;

                // Error if arena has started
                if (state == State.RUNNING) return commandError(p, "Arena " + name + " has already started");

                // Don't join arena if in it
                if (aI == this && !overridePlayers.contains(p))
                    return commandError(p, "You're already in arena " + name);
                // Leave arena if in one
                if (aI != null) aI.remove(p);

                // Teleport to random x/y and highest y
                int x = randInt(region[0], region[2]);
                int z = randInt(region[1], region[3]);
                int highY = WORLD.getHighestBlockYAt(x, z);
                p.teleport(new Location(WORLD, x + 0.5, highY, z + 0.5, randInt(-180, 180), 0));

                // Start arena if it's stopped
                if (state == State.STOPPED) setState(State.OPEN);

                // SetRegion sign
                if (sign != null) {
                    sign.setLine(3, Common.toStr(getPlayers().size()));
                    sign.update(true, false);
                }

                // Send join messages
                msg(p, MessageHandler.MessageType.GAME, "Joined arena " + name);
                broadcast(p.getDisplayName() + " joined the arena", p);

                JoinLeave.onJoin(p, this);
            } else {
                // Don't join arena if in it
                if (aI == this) return false;
            }

            //return getEntities().add(entity);
            return contains(entity);
        }

        public boolean remove(Entity entity) {
            //boolean result = getEntities().remove(entity);

            // If entity is player
            if (entity instanceof Player) {
                Player p = (Player) entity;

                // Teleport to lobby
                Lobby.lobby(p);

                // Stop arena if no more players
                if (getPlayers().size() == 0) setState(State.STOPPED);
                // Stop arena if running and less than 2 players
                if (state == State.RUNNING && getPlayers().size() < 2) setState(State.STOPPED);

                // SetRegion sign
                if (sign != null) {
                    sign.setLine(3, Common.toStr(getPlayers().size()));
                    sign.update(true, false);
                }

                // Send leave messages
                broadcast(p.getDisplayName() + " left the arena", p);
                msg(p, MessageHandler.MessageType.GAME, "Left arena " + name);

                JoinLeave.onLeave(p, this);
            } else entity.remove();

            return contains(entity);
        }

        /*
        public void add(Block block) {
            add(block.getState());
        }

        public void add(BlockState blockState) {
            // Ignore if arena is stopped
            if (state == State.STOPPED) return;


            // Don't add to list if block is already there
            if (arenaIn(blockState) != null) {
                //changedBlocks.set(locI, blockState);
                //Common.debug("Updated changed block: " + blockState.getBlock());
                Common.debug("Didn't add: " + blockState.getBlock());
                return;
            }

            // Else, add BlockState to list
            changedBlocks.add(blockState);
            Common.debug("Added: " + blockState.getBlock());
        }
        */

        public boolean contains(Location location) {
            Integer[] l = {location.getBlockX(), location.getBlockZ()};
            return l[0] >= region[0] &&
                    l[0] <= region[2] &&
                    l[1] >= region[1] &&
                    l[1] <= region[3] &&
                    location.getWorld() == WORLD;
        }

        public boolean contains(Entity entity) {
            if (entity == null) return false;
            return contains(entity.getLocation());
        }

        public boolean contains(Block block) {
            return contains(block.getState());
        }

        public boolean contains(BlockState block) {
            if (block == null) return false;
            return contains(block.getLocation());
        }


        // Get blocks
        private void getBlocks() {
            blocks.clear();
            debug("Getting blocks");
            new Runnable() {
                int x = region[0],
                        y = 0,
                        z = region[1];

                //percentDone = 0,
                //percentTotal = 256 * (region[2] - region[0]) * (region[3] - region[1]);
                @Override
                public void run() {
                    for (int t = 0; t < 100000; t++) {
                        if (y >= 256) {
                            x++;
                            y = 0;
                        }
                        if (x >= region[2]) {
                            z++;
                            x = region[0];
                        }
                        if (z >= region[3]) {
                            debug("Done getting");
                            cancel();
                            return;
                        }
                        blocks.add(WORLD.getBlockAt(x, y, z).getState());
                        y++;
                        //percentDone++;
                    }
                    //debug(percentDone + "/" + percentTotal);
                }
            }.runTaskTimer(0, 0);
        }

        // Set blocks
        private void setBlocks() {
            debug("Setting blocks");
            new Runnable() {
                int i = 0;

                //percentDone = 0,
                //percentTotal = blocks.size();
                @Override
                public void run() {
                    for (int t = 0; t < 100000; t++) {
                        if (i >= blocks.size()) {
                            debug("Done setting");
                            cancel();
                            return;
                        }
                        blocks.get(i).update();
                        i++;
                        //percentDone++;
                    }
                    //debug(percentDone + "/" + percentTotal);
                }
            }.runTaskTimer(0, 0);
        }


        public enum State {
            OPEN(ChatColor.GREEN),
            RUNNING(ChatColor.RED),
            STOPPED(ChatColor.GREEN);

            private ChatColor color;

            State(ChatColor color) {
                this.color = color;
            }

            public String get() {
                return color + name();
            }
        }
    }
}



