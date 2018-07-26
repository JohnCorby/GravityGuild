package old;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Events implements Listener {
    GravityGuild g;
    Methods m;
    Events e;
    Commands c;
    TabCompletion t;
    Arena a;

    Events(GravityGuild g, Methods m, Events e, Commands c, TabCompletion t, Arena a) {
        this.g = g;
        this.m = m;
        this.e = e;
        this.c = c;
        this.t = t;
        this.a = a;
    }

    // When player left clicks block
    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getWorld() != g.world)
            return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK && m.inArena(event.getPlayer()) < 0) {
            if (!(event.getClickedBlock().getState() instanceof Sign))
                return;

            Sign sign = (Sign) event.getClickedBlock().getState();

            // So nothing will happen if sign isnt gravityguild sign and so on
            if (!sign.getLine(0).equalsIgnoreCase("[GravityGuild]"))
                return;
            int index;
            try {
                index = m.getInt(sign.getLine(1));
            } catch (Exception e) {
                return;
            }
            if (g.arena[index] == null)
                return;

            event.getPlayer().performCommand("ggjoin " + index);

            event.setCancelled(true);
            return;
        }

        // Throwing wither skull
        //if (com.johncorby.gravityguild.event.getAction() == Action.LEFT_CLICK_AIR || com.johncorby.gravityguild.event.getAction() == Action.LEFT_CLICK_BLOCK) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && m.inArena(event.getPlayer()) > -1) {
            // Find if player is in arena
            int aI = m.inArena(event.getPlayer());
            if (aI > -1) {
                // Spawn skull and set vel to player rot
                Location playerEyeLoc = event.getPlayer().getEyeLocation();
                Projectile skull = (Projectile) g.world.spawnEntity(playerEyeLoc.add(playerEyeLoc.getDirection()), EntityType.WITHER_SKULL);
                skull.setVelocity(playerEyeLoc.getDirection());
                skull.setShooter(event.getPlayer());
                g.arena[aI].skulls.add((WitherSkull) skull);
                event.setCancelled(true);
                return;
            }
        }

        // Setting arena
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && g.setArena.containsKey(event.getPlayer())) {
            SetArena setArena = g.setArena.get(event.getPlayer());

            ConfigurationSection cs = g.getConfig().getConfigurationSection("Arenas." + setArena.number);

            if (setArena.pos == 0) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.AQUA + "First position set to " + event.getClickedBlock().getX() + "," + event.getClickedBlock().getZ());

                // Coords
                setArena.region += event.getClickedBlock().getX() + "," + event.getClickedBlock().getZ();

                setArena.pos++;

                return;
            }

            if (setArena.pos == 1) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.AQUA + "Second position set to " + event.getClickedBlock().getX() + "," + event.getClickedBlock().getZ());


                // Set coords
                setArena.region += "," + event.getClickedBlock().getX() + "," + event.getClickedBlock().getZ();

                String[] coords = setArena.region.split(",");

                // Swap coords so first is smaller
                if (m.getInt(coords[0]) > m.getInt(coords[2])) {
                    String t = coords[2];
                    coords[2] = coords[0];
                    coords[0] = t;
                }
                if (m.getInt(coords[1]) > m.getInt(coords[3])) {
                    String t = coords[3];
                    coords[3] = coords[1];
                    coords[1] = t;
                }

                setArena.region = coords[0] + "," + coords[1] + "," + coords[2] + "," + coords[3];
                // Apply to config
                cs.set("Region", setArena.region);

                // Save config
                g.saveConfig();

                g.arena[setArena.number] = new Arena(setArena.number);

                event.getPlayer().sendMessage(ChatColor.AQUA + "Arena " + setArena.number + " region set");

                // Reset for next use
                g.setArena.remove(event.getPlayer());
            }
        }
    }

    // When player disconnects
    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getWorld() != g.world)
            return;
        m.leaveArena(event.getPlayer());
    }

    // When entity shoots bow
    @EventHandler
    void OnEntityShootBowEvent(EntityShootBowEvent event) {
        if (event.getEntity().getWorld() != g.world)
            return;
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        // Find if player is in arena
        int aI = m.inArena(player);
        if (aI > -1) {
            event.getProjectile().setGravity(false);

            g.arena[aI].arrows.add((Arrow) event.getProjectile());
            Vector vel = event.getProjectile().getVelocity();

            new BukkitRunnable() {
                public void run() {
                    //Vector aVel = new Vector(Math.abs(vel.getX()),Math.abs(vel.getY()),Math.abs(vel.getZ()));
                    if (event.getProjectile().isDead()) {
                        event.getProjectile().remove();
                        cancel();
                    }
                    //com.johncorby.gravityguild.event.getProjectile().setVelocity(vel.normalize());
                    event.getProjectile().setVelocity(vel);
                }
            }.runTaskTimer(g, 0, 0);
        }
    }

    // When entity damaged by entity
    @EventHandler
    void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getWorld() != g.world)
            return;
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        int aI = m.inArena(player);
        // If player not in arena
        if (aI < 0)
            return;
        if (event.getCause() == DamageCause.ENTITY_EXPLOSION) {
            event.setDamage(0);
            return;
        }
        if (event.getCause() != DamageCause.PROJECTILE) {
            event.setCancelled(true);
            return;
        }

        Player killer = (Player) ((Projectile) event.getDamager()).getShooter();

        //getLogger().info("" + killer + ", " + player);
        if (killer == player) {
            event.setCancelled(true);
            return;
        }

        int pI = g.arena[aI].players.indexOf(player);
        // If going to die
        if (player.getHealth() - event.getFinalDamage() < 1) {
            event.setDamage(0);

            // Teleport killer to player
            killer.teleport(player.getLocation());

            // Tp to random place in g.arena at highest y level
            ConfigurationSection cs = g.getConfig().getConfigurationSection("Arenas." + aI);
            String[] coords = cs.getString("Region").split(",");
            int x = m.randRange(m.getInt(coords[0]), m.getInt(coords[2]));
            int z = m.randRange(m.getInt(coords[1]), m.getInt(coords[3]));
            int highY = g.world.getHighestBlockYAt(x, z);
            Location loc = new Location(g.world, x + 0.5, highY, z + 0.5);
            player.teleport(loc);

            // Decrease lives
            g.arena[aI].players.get(pI).setLevel(player.getLevel() - 1);
            // Run out of lives
            if (g.arena[aI].players.get(pI).getLevel() <= 0) {
                // Tp player to lobby
                player.sendMessage(ChatColor.RED + "You have died!");
                m.leaveArena(player);
                return;
            }

            // Kill message
            m.broadcast(aI, player, killer, ChatColor.YELLOW + player.getDisplayName() + ChatColor.YELLOW + " was killed by " + killer.getDisplayName(), ChatColor.RED + "You were killed by " + killer.getDisplayName(), ChatColor.YELLOW + "You killed " + player.getDisplayName());

            if (g.arena[aI].players.get(pI).getLevel() != 1)
                player.sendMessage(ChatColor.RED + "You have " + g.arena[aI].players.get(pI).getLevel() + " lives left");
            else
                player.sendMessage(ChatColor.RED + "You have 1 life left");

            m.deathCoolDown(player);
        }
        event.setDamage(0);
    }

    // When entity damaged
    @EventHandler
    void onDamage(EntityDamageEvent event) {
        //g.getLogger().info("" + com.johncorby.gravityguild.event.getCause());
        if (event.getEntity().getWorld() != g.world)
            return;
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        // If player not in g.arena
        int aI = m.inArena(player);
        if (aI < 0)
            return;
        if (event.getCause() == DamageCause.ENTITY_EXPLOSION) {
            event.setDamage(0);
            return;
        }
        if (event.getCause() != DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        int pI = g.arena[aI].players.indexOf(player);
        // If going to die
        if (player.getHealth() - event.getFinalDamage() < 1) {
            event.setDamage(0);
            // Tp to random place in g.arena at highest y level
            ConfigurationSection cs = g.getConfig().getConfigurationSection("Arenas." + aI);
            String[] coords = cs.getString("Region").split(",");
            int x = m.randRange(m.getInt(coords[0]), m.getInt(coords[2]));
            int z = m.randRange(m.getInt(coords[1]), m.getInt(coords[3]));
            int highY = g.world.getHighestBlockYAt(x, z);
            Location loc = new Location(g.world, x + 0.5, highY, z + 0.5);
            player.teleport(loc);

            // Decrease lives
            g.arena[aI].players.get(pI).setLevel(player.getLevel() - 1);
            // Run out of lives
            if (g.arena[aI].players.get(pI).getLevel() <= 0) {
                // Tp player to lobby
                player.sendMessage(ChatColor.RED + "You have died!");
                m.leaveArena(player);
                return;
            }

            // Kill message
            m.broadcast(aI, player, ChatColor.YELLOW + player.getDisplayName() + ChatColor.YELLOW + " fell to their death", ChatColor.RED + "You fell to your death");

            if (g.arena[aI].players.get(pI).getLevel() != 1)
                player.sendMessage(ChatColor.RED + "You have " + g.arena[aI].players.get(pI).getLevel() + " lives left");
            else
                player.sendMessage(ChatColor.RED + "You have 1 life left");

            m.deathCoolDown(player);
        }
        event.setDamage(0);
    }

    // When projectile hits block or entity
    @EventHandler
    void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getWorld() != g.world)
            return;
        if (!(event.getEntity() instanceof Arrow))
            return;
        if (!(event.getEntity().getShooter() instanceof Player))
            return;
        //getLogger().info("" + com.johncorby.gravityguild.event.getEntity().getShooter() + ", " + com.johncorby.gravityguild.event.getHitEntity());
        if (event.getEntity().getShooter() == event.getHitEntity())
            return;
        if (m.inArena((Player) event.getEntity().getShooter()) > -1)
            event.getEntity().remove();
    }

    // When entity explodes
    @EventHandler
    void onExplode(EntityExplodeEvent event) {
        if (event.getEntity().getWorld() != g.world)
            return;
        if (event.blockList().size() == 0)
            return;

        if (!(event.getEntity() instanceof WitherSkull))
            return;
        Projectile proj = (Projectile) event.getEntity();

        if (!(proj.getShooter() instanceof Player))
            return;
        Player shooter = (Player) proj.getShooter();
        int aI = m.inArena(shooter);
        if (aI > -1)
            for (Block block : event.blockList())
                g.arena[aI].brokenBlocks.add(new BrokenBlocks(block));
    }

	/*
	@EventHandler
	void onIgnite(BlockIgniteEvent com.johncorby.gravityguild.event) {
		//if (com.johncorby.gravityguild.event.getBlock().getWorld() != g.world)
		//	return;
		g.getLogger().info("worrk");
	}
	*/

    // When food level changes
    @EventHandler
    void onFoodChange(FoodLevelChangeEvent event) {
        if (m.inArena((Player) event.getEntity()) > -1)
            event.setCancelled(true);
    }

    // When entity set fire
    @EventHandler
    void onSetFire(EntityCombustEvent event) {
        if (event.getEntity() instanceof Player && m.inArena((Player) event.getEntity()) > -1)
            event.setCancelled(true);
    }


    @EventHandler
    void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[GravityGuild]")) {
            event.setLine(0, "[GravityGuild]");

            int index;
            try {
                index = m.getInt(event.getLine(1));
            } catch (Exception e) {
                event.getPlayer().sendMessage(ChatColor.RED + "Line 1 must be integer");
                return;
            }
            if (g.arena[index] == null) {
                event.getPlayer().sendMessage(ChatColor.RED + "Arena " + index + " doesn't exist");
                return;
            }

            // Set sign loc for arena in config
            ConfigurationSection arenaConfig = g.getConfig().getConfigurationSection("Arenas." + index);

            Location signLoc = event.getBlock().getLocation();
            arenaConfig.set("SignLoc", signLoc.getBlockX() + "," + signLoc.getBlockY() + "," + signLoc.getBlockZ());
            g.saveConfig();

            event.setLine(2, "" + g.arena[index].players.size() + (g.arena[index].players.size() == 1 ? " Player" : " Players"));
            event.setLine(3, "" + g.arena[index].state);

            event.getPlayer().sendMessage(ChatColor.AQUA + "Sign linked to arena " + index);
        }
    }

    // When block is broken
    @EventHandler
    void onBlockBreak(BlockBreakEvent event) {
        //g.getLogger().info("" + com.johncorby.gravityguild.event.getBlock());

        // For unlinking sign from arena
        if (event.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) event.getBlock().getState();

            // So nothing will happen if sign isnt gravityguild sign and so on
            if (!sign.getLine(0).equalsIgnoreCase("[GravityGuild]"))
                return;

            int index;
            try {
                index = m.getInt(sign.getLine(1));
            } catch (Exception e) {
                return;
            }

            if (g.arena[index] == null)
                return;

            // Remove param in config
            ConfigurationSection arenaConfig = g.getConfig().getConfigurationSection("Arenas." + index);
            arenaConfig.set("SignLoc", null);
            g.saveConfig();

            event.getPlayer().sendMessage(ChatColor.AQUA + "Sign unlinked from arena " + index);
        }
    }
}
