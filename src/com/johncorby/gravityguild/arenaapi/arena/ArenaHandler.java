package com.johncorby.gravityguild.arenaapi.arena;

import com.johncorby.gravityguild.GravityGuild;
import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.Utils;
import com.johncorby.gravityguild.arenaapi.command.Lobby;
import com.johncorby.gravityguild.game.arena.JoinLeave;
import com.johncorby.gravityguild.game.arena.StateChange;
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
import java.util.concurrent.CopyOnWriteArrayList;

import static com.johncorby.gravityguild.GravityGuild.*;
import static com.johncorby.gravityguild.MessageHandler.msg;
import static com.johncorby.gravityguild.Utils.*;
import static com.johncorby.gravityguild.arenaapi.command.BaseCommand.error;

public class ArenaHandler {
    public static ArrayList<Arena> arenas = new ArrayList<>();
    public static ConfigurationSection arenaSection = GravityGuild.CONFIG.getConfigurationSection("Arenas");

    public static Arena add(Arena arena) {
        return arenas.add(arena) ? arena : null;
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
        for (Arena arena : arenas)
            if (arena.getEntities().contains(entity)) return arena;
        return null;
    }

    // Get arena entity is in via coords
    public static Arena arenaInC(Location location) {
        for (Arena a : arenas) {
            Integer[] r = a.region;
            Integer[] l = {location.getBlockX(), location.getBlockZ()};
            if (l[0] >= r[0] &&
                    l[0] <= r[2] &&
                    l[1] >= r[1] &&
                    l[1] <= r[3])
                return a;
        }
        return null;
    }

    // Message of type to players in arena
    public static void broadcast(Arena arena, Object message) {
        broadcast(arena, message, null);
    }

    // Message of type to players in arena except
    public static void broadcast(Arena arena, Object message, Player... except) {
        if (except == null) except = new Player[0];
        for (Entity entity : arena.getEntities())
            if (entity instanceof Player && !Arrays.asList(except).contains(entity))
                MessageHandler.msg(entity, MessageHandler.MessageType.GAME, message);
    }



    public static class Arena {
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
        private State state = State.STOPPED;
        private CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<>();
        private CopyOnWriteArrayList<BlockState> changedBlocks = new CopyOnWriteArrayList<>();
        //private CopyOnWriteArrayList<BlockState> blocks = new CopyOnWriteArrayList<>();

        private Sign sign;
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

        @Override
        public String toString() {
            return "Arena@" + name;
        }

        public List<Player> getPlayers() {
            List<Player> p = new ArrayList<>();
            for (Entity e : entities) {
                if (e instanceof Player) p.add((Player) e);
            }
            return p;
        }

        public void setState(State state) {
            this.state = state;
            switch (this.state) {
                case OPEN:
                    StateChange.on(this);
                    break;
                case RUNNING:
                    broadcast(this, "Game start");

                    StateChange.on(this);
                    break;
                case STOPPED:
                    // Remove entities
                    for (Entity e : entities)
                        removeEntity(e);

                    /*
                    // This probably wont work lol
                    // Set blocks
                    debug("Resetting blocks");
                    new BukkitRunnable() {
                        int i = 0,
                        percentDone = 0,
                        percentTotal = blocks.size();
                        @Override
                        public void run() {
                            for (int t = 0; t < 1000; t++) {
                                if (i >= blocks.size() || percentDone >= percentTotal) {
                                    debug("Done resetting");
                                    cancel();
                                    return;
                                }
                                blocks.get(i).update();
                                i++;
                                percentDone++;
                            }
                            debug(percentDone + "/" + percentTotal);
                        }
                    }.runTaskTimer(gravityGuild, 0, 0);
                    //for (BlockState b : blocks)
                    //    b.update(true, false);
                    */

                    // Set back all changed blocks
                    for (BlockState b : changedBlocks)
                        b.update(true, false);
                    changedBlocks.clear();

                    StateChange.on(this);
                    break;
            }

            // Update sign
            if (sign != null) {
                sign.setLine(2, state.get());
                sign.update(true, false);
            }
        }

        public String getName() {
            return name;
        }

        public State getState() {
            return state;
        }

        public Integer[] getRegion() {
            return region;
        }

        public Sign getSign() {
            return sign;
        }

        public CopyOnWriteArrayList<Entity> getEntities() {
            return entities;
        }

        public CopyOnWriteArrayList<BlockState> getChangedBlocks() {
            return changedBlocks;
        }

        public void setRegion(Integer[] region) {
            this.region = region;
            configLoc.set("Region", region);
            gravityGuild.saveConfig();

            /*
            // This probably wont work lol
            // Get blocks
            blocks.clear();
            debug("Getting blocks");
            new BukkitRunnable() {
                int x = region[0],
                y = 0,
                z = region[1],
                percentDone = 0,
                percentTotal = 256 * (region[2] - region[0]) * (region[3] - region[1]);
                @Override
                public void run() {
                    for (int t = 0; t < 1000; t++) {
                        if (y >= 256) {
                            x++;
                            y = 0;
                        }
                        if (x >= region[2]) {
                            z++;
                            x = region[0];
                        }
                        if (z >= region[3] || percentDone >= percentTotal) {
                            debug("Done getting");
                            cancel();
                            return;
                        }
                        blocks.add(WORLD.getBlockAt(x, y, z).getState());
                        y++;
                        percentDone++;
                    }
                    debug(percentDone + "/" + percentTotal);
                }
            }.runTaskTimer(gravityGuild, 0, 0);
            */
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

                // Update sign
                sign.setLine(0, ChatColor.YELLOW + "[GravityGuild]");
                sign.setLine(1, name);
                sign.setLine(2, state.get());
                sign.setLine(3, Utils.toStr(getPlayers().size()));
                sign.update(true, false);
            }
            gravityGuild.saveConfig();
        }

        public boolean addEntity(Entity entity) {
            Arena aI = arenaIn(entity);

            // If entity is player
            if (entity instanceof Player) {
                Player p = (Player) entity;

                // Error if arena has started
                if (state == State.RUNNING) return error(p, "Arena " + name + " has already started");

                // Don't join arena if in it
                if (aI == this && !overridePlayers.contains(p)) return error(p, "You're already in arena " + name);
                // Leave arena if in one
                if (aI != null) aI.removeEntity(p);

                // Start arena if it's stopped
                if (state == State.STOPPED) setState(State.OPEN);

                // Update sign
                if (sign != null) {
                    sign.setLine(3, Utils.toStr(getPlayers().size() + 1));
                    sign.update(true, false);
                }

                // Teleport to random x/y and highest y
                int x = randInt(region[0], region[2]);
                int z = randInt(region[1], region[3]);
                int highY = WORLD.getHighestBlockYAt(x, z);
                p.teleport(new Location(WORLD, x + 0.5, highY, z + 0.5, randInt(-180, 180), 0));

                // Send join messages
                msg(p, MessageHandler.MessageType.GAME, "Joined arena " + this.getName());
                broadcast(this, p.getDisplayName() + " joined the arena", p);

                JoinLeave.onJoin(p, this);
            } else {
                // Don't join arena if in it
                if (aI == this) return false;
            }

            return entities.add(entity);
        }

        public boolean removeEntity(Entity entity) {
            boolean result = this.entities.remove(entity);

            // If entity is player
            if (entity instanceof Player) {
                Player p = (Player) entity;

                // Stop arena if no more players
                if (getPlayers().size() == 0) setState(State.STOPPED);
                // Stop arena if running and less than 2 players
                if (state == State.RUNNING && getPlayers().size() < 2) setState(State.STOPPED);

                // Update sign
                if (sign != null) {
                    sign.setLine(3, Utils.toStr(getPlayers().size()));
                    sign.update(true, false);
                }

                // Send leave messages
                broadcast(this, p.getDisplayName() + " left the arena", p);
                msg(p, MessageHandler.MessageType.GAME, "Left arena " + name);

                // Teleport to lobby
                Lobby.lobby(p);

                JoinLeave.onLeave(p, this);
            } else entity.remove();

            return result;
        }

        public void addChangedBlock(Block block) {
            addChangedBlock(block.getState());
        }

        public void addChangedBlock(BlockState blockState) {
            // Ignore if arena is stopped
            if (state == State.STOPPED) return;

            // Don't add to list if block is already there
            int locI = Utils.map(changedBlocks, BlockState::getLocation).indexOf(blockState.getLocation());
            if (locI != -1) {
                //changedBlocks.set(locI, blockState);
                //Utils.debug("Updated changed block: " + blockState.getBlock());
                Utils.debug("Didn't add: " + blockState.getBlock());
                return;
            }

            // Else, add BlockState to list
            changedBlocks.add(blockState);
            Utils.debug("Added: " + blockState.getBlock());
        }
    }
}



