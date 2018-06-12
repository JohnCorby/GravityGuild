package com.johncorby.gravityguild.arenaapi.arena;

import com.boydti.fawe.object.schematic.Schematic;
import com.johncorby.gravityguild.GravityGuild;
import com.johncorby.gravityguild.MessageHandler;
import com.johncorby.gravityguild.arenaapi.command.Lobby;
import com.johncorby.gravityguild.game.arena.JoinLeave;
import com.johncorby.gravityguild.game.arena.StateChange;
import com.johncorby.gravityguild.util.Class;
import com.johncorby.gravityguild.util.Common;
import com.johncorby.gravityguild.util.Identifiable;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BlockType;
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

public class Arena extends Identifiable<String> {
    public final static ConfigurationSection arenaSection = GravityGuild.CONFIG.getConfigurationSection("Arenas");
    private State state = State.STOPPED;
    private Sign sign;
    private Integer[] region;
    private ConfigurationSection configLoc;

    public Arena(String name) {
        super(name.toLowerCase());
        configLoc = arenaSection.createSection(get());
    }

    // Constructor and sets config
    public Arena(String name, Integer[] region, Integer[] signLoc) {
        super(name.toLowerCase());
        configLoc = arenaSection.getConfigurationSection(get());

        setRegion(region);

        // Try to get sign from config signLoc
        if (signLoc.length == 0) return;
        Location l = new Location(WORLD, signLoc[0], signLoc[1], signLoc[2]);
        setSign((Sign) l.getBlock().getState());
    }

    public static Arena get(String identity) {
        return (Arena) get(identity, Arena.class);
    }

    public static boolean contains(String identity) {
        return contains(identity, Arena.class);
    }

    public static boolean dispose(String identity) {
        return dispose(identity, Arena.class);
    }

    public static List<Arena> getArenas() {
        List<Arena> r = new ArrayList<>();
        for (Class c : classes)
            if (c instanceof Arena) r.add((Arena) c);
        return r;
    }

    // Get arena names
    public static List<String> getNames() {
        //if (arenas == null) return null;
        return map(getArenas(), a -> a.get());
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
        if (location == null || location.getWorld() != WORLD) return null;
        for (Arena a : getArenas())
            if (a.contains(location)) return a;
        return null;
    }

    @Override
    public boolean dispose() {
        arenaSection.set(get(), null);
        gravityGuild.saveConfig();

        return super.dispose();
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
                return commandError(p, "Arena " + get() + " isn't joinable");

            // Don't join arena if in it
            if (aI == this && !getOverridePlayers().contains(p))
                return commandError(p, "You're already in arena " + get());
            // Leave arena if in one
            if (aI != null) aI.remove(p);

            // Teleport to random loc
            tpToRandom(p);

            // Start arena if it's stopped
            if (state == State.STOPPED) setState(State.OPEN);

            updateSign();

            // Send join messages
            msg(p, MessageHandler.MessageType.GAME, "Joined arena " + get());
            broadcast(p.getName() + " joined the arena", p);

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
            broadcast(p.getName() + " left the arena", p);
            msg(p, MessageHandler.MessageType.GAME, "Left arena " + get());

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
        sign.setLine(1, get());
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
                File file = new File(gravityGuild.getDataFolder() + "/" + get() + ".schematic");
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
                File file = new File(gravityGuild.getDataFolder() + "/" + get() + ".schematic");
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

    // Edited from FAWE
    public void tpToRandom(Player player) {
        Location l = new Location(WORLD,
                randInt(region[0], region[2]),
                randInt(0, 255),
                randInt(region[1], region[3]),
                randInt(-180, 180),
                0);
        player.teleport(l);

        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();

        // Tp to unstuck
        int origY = y;

        byte free = 0;

        while (y <= WORLD.getMaxHeight() + 2) {
            Block b = WORLD.getBlockAt(x, y, z);
            if (BlockType.canPassThrough(b.getTypeId(), b.getData())) {
                ++free;
            } else {
                free = 0;
            }

            if (free == 2) {
                if (y - 1 != origY) {
                    b = WORLD.getBlockAt(x, y - 2, z);
                    l.setX(x + .5);
                    l.setY(y - 2 + BlockType.centralTopLimit(b.getTypeId(), b.getData()));
                    l.setZ(z + .5);
                    player.teleport(l);
                }

                break;
            }

            ++y;
        }

        // Tp to ground
        while (y >= 0) {
            Block b = WORLD.getBlockAt(x, y, z);
            final int id = b.getTypeId();
            final int data = b.getData();
            if (!BlockType.canPassThrough(b.getTypeId(), b.getData())) {
                l.setX(x + .5);
                l.setY(y + BlockType.centralTopLimit(id, data));
                l.setZ(z + .5);
                player.teleport(l);
                break;
            }

            --y;
        }
    }

    @Override
    public List<String> getDebug() {
        List<String> r = new ArrayList<>();
        r.add(toString());

        r.add("State: " + state.get());

        Location s = sign == null ? null : sign.getLocation();
        r.add("Region: " +
                (region == null ? "N/A" : String.format("(%s, %s), (%s, %s)", region[0], region[1], region[2], region[3])));
        r.add("SignLoc: " +
                (sign == null ? "N/A" : String.format("(%s, %s, %s)", s.getBlockX(), s.getBlockY(), s.getBlockZ())));

        List<Entity> el = getEntities();
        r.add("Entities: " + (el.isEmpty() ? "None" : ""));
        for (Entity e : el) {
            Location l = e.getLocation();
            r.add(String.format("%s {(%s, %s, %s), (%s, %s)}",
                    e.getType(),
                    l.getBlockX(),
                    l.getBlockY(),
                    l.getBlockZ(),
                    (int) l.getPitch(),
                    (int) l.getYaw()));
        }
        return r;
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
