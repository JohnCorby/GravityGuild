package com.johncorby.gravityguild.arenaapi.arena;

import com.boydti.fawe.object.schematic.Schematic;
import com.johncorby.gravityguild.GravityGuild;
import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.command.Lobby;
import com.johncorby.gravityguild.game.arena.JoinLeave;
import com.johncorby.gravityguild.game.arena.StateChange;
import com.johncorby.gravityguild.util.Common;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.johncorby.gravityguild.GravityGuild.*;
import static com.johncorby.gravityguild.MessageHandler.commandError;
import static com.johncorby.gravityguild.MessageHandler.msg;
import static com.johncorby.gravityguild.util.Common.map;
import static com.johncorby.gravityguild.util.Common.randInt;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;

//public class Arena extends Identifiable<String> {
//    /**
//     * Factory/handler
//     */
//    public static List<Arena> arenas = new ArrayList<>();
//    public static ConfigurationSection arenaSection = GravityGuild.CONFIG.getConfigurationSection("Arenas");
//    /**
//     * Actual class
//     */
//    private State state = State.STOPPED;
//    private Sign sign;
//    private Integer[] region;
//    private String name;
//    private ConfigurationSection configLoc;
//
//    public Arena(String name) {
//        super(name);
//        this.name = name.toLowerCase();
//        configLoc = arenaSection.createSection(this.name);
//    }
//
//    // Constructor and sets config
//    public Arena(String name, Integer[] region, Integer[] signLoc) {
//        super(name);
//        this.name = name.toLowerCase();
//        configLoc = arenaSection.getConfigurationSection(this.name);
//
//        setRegion(region);
//
//        // Try to get sign from config signLoc
//        if (signLoc.length == 0) return;
//        Location l = new Location(WORLD, signLoc[0], signLoc[1], signLoc[2]);
//        setSign((Sign) l.getBlock().getState());
//    }
//
//    public static Arena add(Arena arena) {
//        if (arenas.contains(arena)) return null;
//        arenas.add(arena);
//        return arena;
//    }
//
//    public static Arena remove(Arena arena) {
//        arenaSection.set(arena.getName(), null);
//        gravityGuild.saveConfig();
//        return arenas.remove(arena) ? arena : null;
//    }
//
//    // Get arena by name
//    public static Arena get(String name) {
//        name = name.toLowerCase();
//        for (Arena arena : arenas)
//            if (arena.getName().equals(name))
//                return arena;
//        return null;
//    }
//
//    // Get arena names
//    public static List<String> getNames() {
//        //if (arenas == null) return null;
//        return map(arenas, Arena::getName);
//    }
//
//    // Get arena entity is in
//    public static Arena arenaIn(Entity entity) {
//        if (entity == null) return null;
//        return arenaIn(entity.getLocation());
//    }
//
//    // Get arena block is in
//    public static Arena arenaIn(Block block) {
//        return arenaIn(block.getState());
//    }
//
//    public static Arena arenaIn(BlockState block) {
//        if (block == null) return null;
//        return arenaIn(block.getLocation());
//    }
//
//    // Get arena location is in
//    public static Arena arenaIn(Location location) {
//        if (location == null) return null;
//        for (Arena a : arenas)
//            if (a.contains(location)) return a;
//        return null;
//    }
//
//    // Message of type to players in arena
//    public void broadcast(Object message) {
//        broadcast(message, null);
//    }
//
//    // Message of type to players in arena except
//    public void broadcast(Object message, Player... except) {
//        if (except == null) except = new Player[0];
//        for (Entity entity : getEntities())
//            if (entity instanceof Player && !Arrays.asList(except).contains(entity))
//                MessageHandler.msg(entity, MessageHandler.MessageType.GAME, message);
//    }
//
//    @Override
//    public String toString() {
//        return "Arena<" + name + ">";
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        return obj instanceof Arena && ((Arena) obj).name.equals(name);
//    }
//
//    public List<Entity> getEntities() {
//        return WORLD.getEntities().stream().filter(this::contains).collect(Collectors.toList());
//    }
//
//    public List<Player> getPlayers() {
//        List<Player> p = new ArrayList<>();
//        for (Entity e : getEntities()) {
//            if (e instanceof Player) p.add((Player) e);
//        }
//        return p;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public State getState() {
//        return state;
//    }
//
//    public void setState(State state) {
//        if (this.state == state) return;
//        switch (state) {
//            case OPEN:
//                this.state = state;
//                StateChange.on(this);
//                break;
//            case RUNNING:
//                broadcast("Game start");
//
//                this.state = state;
//                StateChange.on(this);
//                break;
//            case STOPPED:
//                // Remove entities
//                for (Entity e : getEntities())
//                    remove(e);
//
//                // This probably wont work lol
//                setBlocks();
//
//                this.state = state;
//                StateChange.on(this);
//                break;
//        }
//        this.state = state;
//
//        updateSign();
//    }
//
//    public Integer[] getRegion() {
//        return region;
//    }
//
//    public void setRegion(Integer[] region) {
//        this.region = region;
//        configLoc.set("Region", region);
//        gravityGuild.saveConfig();
//
//
//        // This probably wont work lol
//        getBlocks();
//    }
//
//    public Sign getSign() {
//        return sign;
//    }
//
//    public void setSign(Sign sign) {
//        this.sign = sign;
//
//        // Try to set config signLoc from sign
//        if (sign == null)
//            configLoc.set("SignLoc", null);
//        else {
//            Location l = sign.getLocation();
//            Integer[] signLoc = new Integer[]{l.getBlockX(), l.getBlockY(), l.getBlockZ()};
//            configLoc.set("SignLoc", signLoc);
//
//            updateSign();
//        }
//        gravityGuild.saveConfig();
//    }
//
//    public boolean add(Entity entity) {
//        Arena aI = arenaIn(entity);
//
//        // If entity is player
//        if (entity instanceof Player) {
//            Player p = (Player) entity;
//
//            // Error if arena has started
//            if (state == State.RUNNING && !getOverridePlayers().contains(p))
//                return commandError(p, "Arena " + name + " isn't joinable");
//
//            // Don't join arena if in it
//            if (aI == this && !getOverridePlayers().contains(p))
//                return commandError(p, "You're already in arena " + name);
//            // Leave arena if in one
//            if (aI != null) aI.remove(p);
//
//            // Teleport to random x/y and highest y
//            int x = randInt(region[0], region[2]);
//            int z = randInt(region[1], region[3]);
//            int highY = WORLD.getHighestBlockYAt(x, z);
//            debug(x, highY, z);
//            p.teleport(new Location(WORLD, x + 0.5, highY, z + 0.5, randInt(-180, 180), 0));
//
//            // Start arena if it's stopped
//            if (state == State.STOPPED) setState(State.OPEN);
//
//            updateSign();
//
//            // Send join messages
//            msg(p, MessageHandler.MessageType.GAME, "Joined arena " + name);
//            broadcast(p.getDisplayName() + " joined the arena", p);
//
//            JoinLeave.onJoin(p, this);
//        } else {
//            // Don't join arena if in it
//            if (aI == this) return false;
//        }
//
//        //return getEntities().add(entity);
//        return contains(entity);
//    }
//
//    public boolean remove(Entity entity) {
//        //boolean result = getEntities().dispose(entity);
//
//        // If entity is player
//        if (entity instanceof Player) {
//            Player p = (Player) entity;
//
//            // Teleport to lobby
//            Lobby.lobby(p);
//
//            // Stop arena if no more players
//            if (getPlayers().size() == 0) setState(State.STOPPED);
//            // Stop arena if running and less than 2 players
//            if (state == State.RUNNING && getPlayers().size() < 2) setState(State.STOPPED);
//
//            updateSign();
//
//            // Send leave messages
//            broadcast(p.getDisplayName() + " left the arena", p);
//            msg(p, MessageHandler.MessageType.GAME, "Left arena " + name);
//
//            JoinLeave.onLeave(p, this);
//        } else entity.remove();
//
//        return contains(entity);
//    }
//
//    public boolean contains(Location location) {
//        Integer[] l = {location.getBlockX(), location.getBlockZ()};
//        return state != State.STOPPED &&
//                location.getWorld() == WORLD &&
//                l[0] >= region[0] &&
//                l[0] <= region[2] &&
//                l[1] >= region[1] &&
//                l[1] <= region[3];
//    }
//
//    public boolean contains(Entity entity) {
//        if (entity == null) return false;
//        return contains(entity.getLocation());
//    }
//
//    public boolean contains(Block block) {
//        return contains(block.getState());
//    }
//
//    public boolean contains(BlockState block) {
//        if (block == null) return false;
//        return contains(block.getLocation());
//    }
//
//    // Update sign
//    private void updateSign() {
//        if (sign == null) return;
//        sign.setLine(0, ChatColor.YELLOW + "[GravityGuild]");
//        sign.setLine(1, name);
//        sign.setLine(2, state.get());
//        sign.setLine(3, Common.toStr(getPlayers().size()));
//        sign.update(true, false);
//    }
//
//    // Get blocks
//    private void getBlocks() {
//        //state = State.UPDATING;
//        //updateSign();
//
//        long time = Common.time(() -> {
//            try {
//                File file = new File(gravityGuild.getDataFolder() + "/" + name + ".schematic");
//                World world = new BukkitWorld(WORLD);
//                Vector pos1 = new Vector(region[0], 0, region[1]);
//                Vector pos2 = new Vector(region[2], 255, region[3]);
//                CuboidRegion region = new CuboidRegion(world, pos1, pos2);
//                new Schematic(region).save(file, ClipboardFormat.SCHEMATIC);
//            } catch (Exception e) {
//                error(getStackTrace(e));
//            }
//        });
//
//        //state = State.STOPPED;
//        //updateSign();
//        debug("Got blocks in " + time + " ms");
//    }
//
//    // Set blocks
//    private void setBlocks() {
//        //state = State.UPDATING;
//        //updateSign();
//
//        long time = Common.time(() -> {
//            try {
//                File file = new File(gravityGuild.getDataFolder() + "/" + name + ".schematic");
//                World world = new BukkitWorld(WORLD);
//                Vector pos1 = new Vector(region[0], 0, region[1]);
//                ClipboardFormat.SCHEMATIC.load(file).paste(world, pos1, false, true, null);
//            } catch (Exception e) {
//                error(getStackTrace(e));
//            }
//        });
//
//        //state = State.STOPPED;
//        //updateSign();
//        debug("Set blocks in " + time + " ms");
//    }
//
//    private void debug(Object... msgs) {
//        MessageHandler.debug(toString(), msgs);
//    }
//
//    private void error(Object... msgs) {
//        MessageHandler.error(toString(), msgs);
//    }
//
//    public enum State {
//        OPEN(true),
//        RUNNING(false),
//        STOPPED(true);
//
//        private boolean joinable;
//
//        State(boolean joinable) {
//            this.joinable = joinable;
//        }
//
//        public String get() {
//            return (joinable ? ChatColor.GREEN : ChatColor.RED) + name();
//        }
//    }
//}

public class Arena {
    /**
     * Factory/handler
     */
    public static List<Arena> arenas = new ArrayList<>();
    public static ConfigurationSection arenaSection = GravityGuild.CONFIG.getConfigurationSection("Arenas");
    /**
     * Actual class
     */
    private State state = State.STOPPED;
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

    public static Arena add(Arena arena) {
        if (arenas.contains(arena)) return null;
        arenas.add(arena);
        return arena;
    }

    public static Arena remove(Arena arena) {
        arenaSection.set(arena.getName(), null);
        gravityGuild.saveConfig();
        new File(gravityGuild.getDataFolder() + "/" + arena.getName() + ".schematic").delete();
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
    public static List<String> getNames() {
        //if (arenas == null) return null;
        return map(arenas, Arena::getName);
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

    // Get arena location is in
    public static Arena arenaIn(Location location) {
        if (location == null) return null;
        for (Arena a : arenas)
            if (a.contains(location)) return a;
        return null;
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
        return "Arena<" + name + ">";
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
        if (this.state == state) return;
        switch (state) {
            case OPEN:
                this.state = state;
                StateChange.on(this);
                break;
            case RUNNING:
                broadcast("Game start");

                this.state = state;
                StateChange.on(this);
                break;
            case STOPPED:
                // Remove entities
                for (Entity e : getEntities())
                    remove(e);

                // This probably wont work lol
                setBlocks();

                this.state = state;
                StateChange.on(this);
                break;
        }
        this.state = state;

        updateSign();
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

            updateSign();
        }
        gravityGuild.saveConfig();
    }

    public boolean add(Entity entity) {
        Arena aI = arenaIn(entity);

        // If entity is player
        if (entity instanceof Player) {
            Player p = (Player) entity;

            // Error if arena has started
            if (state == State.RUNNING && !getOverridePlayers().contains(p))
                return commandError(p, "Arena " + name + " isn't joinable");

            // Don't join arena if in it
            if (aI == this && !getOverridePlayers().contains(p))
                return commandError(p, "You're already in arena " + name);
            // Leave arena if in one
            if (aI != null) aI.remove(p);

            // Teleport to random x/y and highest y
            int x = randInt(region[0], region[2]);
            int z = randInt(region[1], region[3]);
            int highY = WORLD.getHighestBlockYAt(x, z);
            debug(x, highY, z);
            p.teleport(new Location(WORLD, x + 0.5, highY, z + 0.5, randInt(-180, 180), 0));

            // Start arena if it's stopped
            if (state == State.STOPPED) setState(State.OPEN);

            updateSign();

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
        //boolean result = getEntities().dispose(entity);

        // If entity is player
        if (entity instanceof Player) {
            Player p = (Player) entity;

            // Teleport to lobby
            Lobby.lobby(p);

            // Stop arena if no more players
            if (getPlayers().size() == 0) setState(State.STOPPED);
            // Stop arena if running and less than 2 players
            if (state == State.RUNNING && getPlayers().size() < 2) setState(State.STOPPED);

            updateSign();

            // Send leave messages
            broadcast(p.getDisplayName() + " left the arena", p);
            msg(p, MessageHandler.MessageType.GAME, "Left arena " + name);

            JoinLeave.onLeave(p, this);
        } else entity.remove();

        return contains(entity);
    }

    public boolean contains(Location location) {
        Integer[] l = {location.getBlockX(), location.getBlockZ()};
        return state != State.STOPPED &&
                location.getWorld() == WORLD &&
                l[0] >= region[0] &&
                l[0] <= region[2] &&
                l[1] >= region[1] &&
                l[1] <= region[3];
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

    // Update sign
    private void updateSign() {
        if (sign == null) return;
        sign.setLine(0, ChatColor.YELLOW + "[GravityGuild]");
        sign.setLine(1, name);
        sign.setLine(2, state.get());
        sign.setLine(3, Common.toStr(getPlayers().size()));
        sign.update(true, false);
    }

    // Get blocks
    private void getBlocks() {
        //state = State.UPDATING;
        //updateSign();

        long time = Common.time(() -> {
            try {
                File file = new File(gravityGuild.getDataFolder() + "/" + name + ".schematic");
                World world = new BukkitWorld(WORLD);
                Vector pos1 = new Vector(region[0], 0, region[1]);
                Vector pos2 = new Vector(region[2], 255, region[3]);
                CuboidRegion region = new CuboidRegion(world, pos1, pos2);
                new Schematic(region).save(file, ClipboardFormat.SCHEMATIC);
            } catch (Exception e) {
                error(getStackTrace(e));
            }
        });

        //state = State.STOPPED;
        //updateSign();
        debug("Got blocks in " + time + " ms");
    }

    // Set blocks
    private void setBlocks() {
        //state = State.UPDATING;
        //updateSign();

        long time = Common.time(() -> {
            try {
                File file = new File(gravityGuild.getDataFolder() + "/" + name + ".schematic");
                World world = new BukkitWorld(WORLD);
                Vector pos1 = new Vector(region[0], 0, region[1]);
                ClipboardFormat.SCHEMATIC.load(file).paste(world, pos1, false, true, null);
            } catch (Exception e) {
                error(getStackTrace(e));
            }
        });

        //state = State.STOPPED;
        //updateSign();
        debug("Set blocks in " + time + " ms");
    }

    private void debug(Object... msgs) {
        MessageHandler.debug(toString(), msgs);
    }

    private void error(Object... msgs) {
        MessageHandler.error(toString(), msgs);
    }

    public enum State {
        OPEN(true),
        RUNNING(false),
        STOPPED(true);

        private boolean joinable;

        State(boolean joinable) {
            this.joinable = joinable;
        }

        public String get() {
            return (joinable ? ChatColor.GREEN : ChatColor.RED) + name();
        }
    }
}


