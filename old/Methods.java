package old;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import old.Arena.States;

public class Methods {

    GravityGuild g;
    Methods m;
    Events e;
    Commands c;
    TabCompletion t;
    Arena a;
    Methods(GravityGuild g, Methods m, Events e, Commands c, TabCompletion t, Arena a) {
        this.g = g;
        this.m = m;
        this.e = e;
        this.c = c;
        this.t = t;
        this.a = a;
    }

    // Random range
    int randRange(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    // Get int
    Integer getInt(String i) {
        try {return Integer.parseInt(i);}
        catch (Exception e) {return null;}
    }

    int inArena(Player p) {
        for (int i = 0; i < g.arena.length; i++) {
            if (g.arena[i] == null)
                continue;
            if (g.arena[i].players.contains(p))
                return i;
        }
        return -1;
    }

    // Leave arena
    boolean leaveArena(Player p) {
        // Have them leave arena if they were in one
        int aI = inArena(p);
        if (aI > -1) {
            g.arena[aI].removePlayer(p);

            p.setGlowing(false);
            p.setInvulnerable(false);
            p.sendMessage(ChatColor.AQUA + "Left arena " + aI);

            p.getInventory().clear();

            // Clear night vision
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);

            tpToLobby(p);

            // Check if arena should end
            if (g.arena[aI].players.size() == 0)
                endGame(aI);
        } else {
            // Send player error message
            p.sendMessage(ChatColor.RED + "You are not in any game");
            return false;
        }
        return true;
    }

    // Death cooldown
    void deathCoolDown(Player p) {
        p.setHealth(20);
        p.setFoodLevel(20);
        p.setInvulnerable(true);
        p.setGlowing(true);
        p.sendMessage(ChatColor.YELLOW + "You are invincible for 5 seconds");
        new BukkitRunnable() {public void run() {
            if (inArena(p) < 0) {
                cancel();
                return;
            }
            p.setInvulnerable(false);
            p.setGlowing(false);
            p.sendMessage(ChatColor.YELLOW + "Invincibility off");
        }}.runTaskLater(g, 20 * 5);
    }

    // End game
    void endGame(int aI) {
        g.arena[aI].setState(States.STOPPING);

        // Fix blocks
        for (BrokenBlocks block : g.arena[aI].brokenBlocks) {
            //getLogger().info("" + block.loc + ", " + block.mat + ", " + block.data);
            Block blockAt = block.loc.getBlock();
            blockAt.setType(block.mat);
            blockAt.setData(block.data);
        }
        // Remove arrows/skulls
        for (Arrow arrow : g.arena[aI].arrows)
            arrow.remove();
        for (WitherSkull skull : g.arena[aI].skulls)
            skull.remove();

        try {
            for (Player p : g.arena[aI].players)
                leaveArena(p);
        } catch (Exception e) {}

        g.arena[aI].brokenBlocks.clear();
        g.arena[aI].arrows.clear();
        g.arena[aI].skulls.clear();
        g.arena[aI].players.clear();

        g.arena[aI].setState(States.STOPPED);
    }






    // broadcast message to players in arena
    // can be regular, death w/o killer, or death w/ killer
    void broadcast(int arenaIndex, String message) {
        for (Player player : g.world.getPlayers())
            if (g.arena[arenaIndex].players.contains(player)) {
                player.sendMessage(message);
            }
    }

    void broadcast(int arenaIndex, Player playerDamaged, String message, String damagedMessage) {
        for (Player player : g.world.getPlayers())
            if (g.arena[arenaIndex].players.contains(player)) {
                if (player != playerDamaged)
                    player.sendMessage(message);
                if (player == playerDamaged && damagedMessage != null)
                    player.sendMessage(damagedMessage);
            }
    }

    void broadcast(int arenaIndex, Player playerDamaged, Player playerKiller, String message, String damagedMessage, String killerMessage) {
        for (Player player : g.world.getPlayers())
            if (g.arena[arenaIndex].players.contains(player)) {
                if (player != playerDamaged && player != playerKiller)
                    player.sendMessage(message);
                if (player == playerDamaged && damagedMessage != null)
                    player.sendMessage(damagedMessage);
                if (player == playerKiller && killerMessage != null)
                    player.sendMessage(killerMessage);
            }
    }









    // Teleport player to lobby
    void tpToLobby(Player p) {
        String[] lC = g.getConfig().getString("Lobby").split(",");
        Location lobbyLoc = new Location(g.world, Float.parseFloat(lC[0]), Float.parseFloat(lC[1]), Float.parseFloat(lC[2]), Float.parseFloat(lC[3]), Float.parseFloat(lC[4]));
        p.teleport(lobbyLoc);
        p.sendMessage(ChatColor.AQUA + "Teleporting to lobby");
    }



    // Set sign from arena index
    void setSign(int index, int line, String msg) {
        // Get sign loc
        ConfigurationSection cs = g.getConfig().getConfigurationSection("Arenas." + index);
        if (!cs.contains("SignLoc"))
            return;
        String[] pos = cs.getString("SignLoc").split(",");
        Location loc = new Location(g.world, getInt(pos[0]), getInt(pos[1]), getInt(pos[2]));

        Sign sign = (Sign) g.world.getBlockAt(loc).getState();

        sign.setLine(line, msg);
        sign.update();
    }
}
