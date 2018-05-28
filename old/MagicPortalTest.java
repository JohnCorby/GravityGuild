// this literally has nothing to do with gravityguild
// i just included it with this because im too lazy to make another project

package old;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class MagicPortalTest implements Listener {
    static GravityGuild mainPlugin = GravityGuild.getPlugin(GravityGuild.class);
    static World world = mainPlugin.world;

    static ArrayList<Player> playerList = new ArrayList<Player>();

    static Vector degToVecY(double deg) {
        return new Vector(Math.sin(Math.toRadians(deg)), 0, Math.cos(Math.toRadians(deg)));
    }

    static void enable() {
        new BukkitRunnable() {
            public void run() {
                for (Player player : playerList)
                    coneView(player);
            }
        }.runTaskTimer(mainPlugin, 0, 2);
    }

    static void coneView(Player player) {
        boolean hitObs;
        boolean hitObsOld = false;
        Integer hitObsToggle = null;
        Integer pBlockZ = null;
        for (int i = -90; i <= 90; i++) {
            hitObs = false;
            for (int j = 3; j < 20 + 3; j++) {
                Vector pVec = degToVecY(i).multiply(j);
                Block pBlock = new Location(world, pVec.getX(), pVec.getY(), pVec.getZ())
                        .add(player.getEyeLocation().add(0, -1, 0)).getBlock();
                pBlockZ = pBlock.getZ();
                if (pBlock.getType() == Material.OBSIDIAN) {
                    hitObs = true;
                    break;
                }

                if (pBlock.getType() != Material.AIR && pBlock.getType() != Material.WOOD)
                    continue;

                if (hitObsToggle != null && player.getEyeLocation().getZ() >= hitObsToggle - 2)
                    continue;
                if (hitObsToggle == null || pBlockZ <= hitObsToggle)
                    continue;
                pBlock.setType(Material.WOOD);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        pBlock.setType(Material.AIR);
                    }
                }.runTaskLater(mainPlugin, 1);

            }
            if (hitObs && !hitObsOld)
                if (hitObsToggle == null)
                    hitObsToggle = pBlockZ;
                else
                    hitObsToggle = null;

            hitObsOld = hitObs;
        }
    }

    void log(String msg) {
        System.out.println("[MagicPortalTest] " + msg);
    }

    void msg(Player p, String msg) {
        p.sendMessage(ChatColor.AQUA + "[MagicPortalTest] " + msg);
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getWorld() != world || event.getPlayer().getInventory().getItemInMainHand().getType() != Material.OBSIDIAN)
            return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_AIR)
            return;

        if (playerList.contains(event.getPlayer())) {
            playerList.remove(event.getPlayer());
            msg(event.getPlayer(), "effect off");
        } else {
            playerList.add(event.getPlayer());
            msg(event.getPlayer(), "effect on");
        }
        event.setCancelled(true);
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        if (playerList.contains(event.getPlayer()))
            playerList.remove(event.getPlayer());
    }
}
