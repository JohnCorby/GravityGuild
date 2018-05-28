package old;

import old.Arena.States;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static java.lang.Math.floor;

public class Commands implements CommandExecutor {
    GravityGuild g;
    Methods m;
    Events e;
    Commands c;
    TabCompletion t;
    Arena a;

    ArrayList<String> bypassPlayers = new ArrayList<>();

    Commands(GravityGuild g, Methods m, Events e, Commands c, TabCompletion t, Arena a) {
        this.g = g;
        this.m = m;
        this.e = e;
        this.c = c;
        this.t = t;
        this.a = a;

        bypassPlayers.add("johncorby");
        bypassPlayers.add("funkymunky111");
        bypassPlayers.add("cheaplaptop");
        bypassPlayers.add("spookydollar");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // If sender not player
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;

        // Create arena
        if (cmd.getName().equalsIgnoreCase("ggadd")) {
            // If no perms
            if (!(player.hasPermission("gg.admin"))) {
                player.sendMessage(ChatColor.DARK_RED + "No permission");
                return false;
            }

            SetArena setArena = g.setArena.get(player);

            // If already setting region
            if (setArena != null) {

                player.sendMessage(ChatColor.RED + "You're already setting region for arena " + setArena.number);
                return false;
            }

            // Create setArena object
            g.setArena.put(player, new SetArena(player));
            setArena = g.setArena.get(player);

            //g.getLogger().info("" + setArena);

            // Create arena section in "Arenas" section
            ConfigurationSection cs = g.getConfig().getConfigurationSection("Arenas");

            while (g.getConfig().getConfigurationSection("Arenas." + setArena.number) != null)
                setArena.number++;

            cs.createSection("" + setArena.number);

            player.sendMessage(ChatColor.AQUA + "Arena " + setArena.number + " will be created");
            player.sendMessage(ChatColor.AQUA + "Left click to set position for arena region");

            return true;
        }

        // Delete arena
        if (cmd.getName().equalsIgnoreCase("ggdel")) {
            // If no perms
            if (!(player.hasPermission("gg.admin"))) {
                player.sendMessage(ChatColor.DARK_RED + "No permission");
                return false;
            }

            // If wrong amount of arguments
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
                player.sendMessage(ChatColor.RED + "Usage: /ggdel <arena number>");
                return false;
            }

            // If arg not int
            if (m.getInt(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "Argument 1 must be integer");
                player.sendMessage(ChatColor.RED + "Usage: /ggdel <arena number>");
                return false;
            }

            // If arena exists
            if (g.getConfig().getConfigurationSection("Arenas." + args[0]) != null) {
                // Delete section in config
                g.getConfig().set("Arenas." + args[0], null);
                g.saveConfig();

                g.arena[m.getInt(args[0])] = null;
                player.sendMessage(ChatColor.AQUA + "Arena " + args[0] + " deleted");
            } else {
                // Send player error message
                player.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist");
                return false;
            }
            return true;
        }

        // Cancel set region
        if (cmd.getName().equalsIgnoreCase("ggcancel")) {
            // If no perms
            if (!(player.hasPermission("gg.admin"))) {
                player.sendMessage(ChatColor.DARK_RED + "No permission");
                return false;
            }

            SetArena setArena = g.setArena.get(player);

            // If not setting region
            if (setArena == null) {
                player.sendMessage(ChatColor.RED + "You're not setting a region");
                return false;
            }

            player.sendMessage(ChatColor.AQUA + "Cancelled arena " + setArena.number + " region setting");

            g.getConfig().set("Arenas." + setArena.number, null);
            g.setArena.remove(player);
            return true;
        }

        // Update arena
        if (cmd.getName().equalsIgnoreCase("ggupdate")) {
            // If no perms
            if (!(player.hasPermission("gg.admin"))) {
                player.sendMessage(ChatColor.DARK_RED + "No permission");
                return false;
            }

            // If wrong amount of arguments
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
                player.sendMessage(ChatColor.RED + "Usage: /ggupdate <arena number>");
                return false;
            }

            // If arg not int
            if (m.getInt(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "Argument 1 must be integer");
                player.sendMessage(ChatColor.RED + "Usage: /ggupdate <arena number>");
                return false;
            }

            SetArena setArena = g.setArena.get(player);

            // If already setting region
            if (setArena != null) {
                player.sendMessage(ChatColor.RED + "You're already setting region for arena " + setArena.number);
                return false;
            }

            // If arena exists
            if (g.getConfig().getConfigurationSection("Arenas." + args[0]) != null) {
                // Update map for arena
                player.sendMessage(ChatColor.AQUA + "Arena " + args[0] + " region will be updated");
                player.sendMessage(ChatColor.AQUA + "Left click to set position for arena region");
                g.setArena.put(player, new SetArena(player));
                setArena = g.setArena.get(player);
                setArena.number = m.getInt(args[0]);
            } else {
                // Send player error message
                player.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist");
                return false;
            }
            return true;
        }


        // Join arena
        if (cmd.getName().equalsIgnoreCase("ggjoin")) {
            // If wrong amount of arguments
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
                player.sendMessage(ChatColor.RED + "Usage: /ggjoin <arena number>");
                return false;
            }

            // If arg not int
            if (m.getInt(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "Argument 1 must be integer");
                player.sendMessage(ChatColor.RED + "Usage: /ggjoin <arena number>");
                return false;
            }

            try {
                if (g.arena[m.getInt(args[0])].state == States.STOPPED) {
                }
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist");
                return false;
            }

            // Dont join unless part of bypass players
            if (g.arena[m.getInt(args[0])].state != States.STOPPED && g.arena[m.getInt(args[0])].state != States.STARTING && !bypassPlayers.contains(player.getName().toLowerCase())) {
                player.sendMessage(ChatColor.RED + "Arena " + args[0] + " has already started");
                return false;
            }

            int aI = m.inArena(player);
            if (aI > -1) {
                player.sendMessage(ChatColor.RED + "Already in arena " + aI);
                return false;
            }

            // If arena exists
            if (g.getConfig().getConfigurationSection("Arenas." + args[0]) != null) {
                player.setLevel(10);
                Arena arena = g.arena[m.getInt(args[0])];
                arena.addPlayer(player);

                // Tp to random place in arena at highest y level
                ConfigurationSection cs = g.getConfig().getConfigurationSection("Arenas." + args[0]);
                String[] coords = cs.getString("Region").split(",");
                int x = m.randRange(m.getInt(coords[0]), m.getInt(coords[2]));
                int z = m.randRange(m.getInt(coords[1]), m.getInt(coords[3]));
                int highY = g.world.getHighestBlockYAt(x, z);
                Location loc = new Location(g.world, x + 0.5, highY, z + 0.5, m.randRange(-180, 180), 0);
                player.teleport(loc);

                player.sendMessage(ChatColor.AQUA + "Joined arena " + args[0]);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setGlowing(true);
                player.setInvulnerable(true);
                player.setGameMode(GameMode.SURVIVAL);

                // Add potion effects
                for (PotionEffect effect : player.getActivePotionEffects())
                    player.removePotionEffect(effect.getType()); // Clear existing effects
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1, true, false));

                // Give player items
                ItemStack bow = new ItemStack(Material.BOW);
                bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1000);
                bow.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
                bow.addUnsafeEnchantment(Enchantment.DURABILITY, 1000);
                bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
                ItemStack arrow = new ItemStack(Material.ARROW);
                ItemStack elytra = new ItemStack(Material.ELYTRA);
                elytra.addUnsafeEnchantment(Enchantment.DURABILITY, 1000);

                player.getInventory().clear();
                player.getInventory().addItem(bow);
                player.getInventory().addItem(arrow);
                player.getInventory().setChestplate(elytra);

                // For the bypass players
                if (arena.state == States.STARTED)
                    m.deathCoolDown(player);

                // Timer for starting arena
                if (arena.state != States.STARTING && arena.state != States.STARTED) {
                    arena.setState(States.STARTING);

                    new BukkitRunnable() {
                        int timer = 10;

                        public void run() {
                            if (arena.state != States.STARTING || arena.players.size() <= 0) {
                                cancel();
                                return;
                            }
                            if (timer <= 0)
                                // Dont start with less than 2 players unless player is bypass player
                                if (arena.players.size() >= 2 || bypassPlayers.contains(player.getName().toLowerCase())) {
                                    m.broadcast(m.getInt(args[0]), ChatColor.YELLOW + "[Arena]: MATCH START!");
                                    arena.setState(States.STARTED);
                                    for (Player p : arena.players)
                                        m.deathCoolDown(p);

                                    cancel();
                                } else {
                                    m.broadcast(m.getInt(args[0]), ChatColor.YELLOW + "[Arena]: Arena needs minimum of 2 players to start");
                                    timer = 10;
                                }
                            if ((timer <= 5 || timer == 10 || timer == 30) && timer != 0)
                                m.broadcast(m.getInt(args[0]), ChatColor.YELLOW + "[Arena]: " + timer + " seconds until the match starts!");
                            timer--;
                        }
                    }.runTaskTimer(g, 0, 20);
                }
            } else {
                // Send player error message
                player.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist");
                return false;
            }
            return true;
        }

        // Leave arena
        if (cmd.getName().equalsIgnoreCase("ggleave")) {
            m.leaveArena(player);
            return true;
        }


        // Teleport to lobby
        if (cmd.getName().equalsIgnoreCase("gglobby")) {
            if (m.inArena(player) > -1)
                m.leaveArena(player);
            else
                m.tpToLobby(player);
        }

        // Set lobby
        if (cmd.getName().equalsIgnoreCase("ggsetlobby")) {
            // If no perms
            if (!(player.hasPermission("gg.admin"))) {
                player.sendMessage(ChatColor.DARK_RED + "No permission");
                return false;
            }

            Location lL = player.getLocation();
            String lC = "";
            lC += floor(lL.getX()) + .5 + ",";
            lC += floor(lL.getY()) + ",";
            lC += floor(lL.getZ()) + .5 + ",";
            lC += floor(lL.getYaw()) + ",";
            lC += floor(lL.getPitch());

            g.getConfig().set("Lobby", lC);
            g.saveConfig();

            player.sendMessage(ChatColor.AQUA + "Lobby set");
        }


        if (cmd.getName().equalsIgnoreCase("ggstat")) {
            // If no perms
            if (!(player.hasPermission("gg.admin"))) {
                player.sendMessage(ChatColor.DARK_RED + "No permission");
                return false;
            }

            // If wrong amount of arguments
            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "Wrong amount of arguments");
                player.sendMessage(ChatColor.RED + "Usage: /ggstat <arena number> <state|players>");
                return false;
            }

            if (m.getInt(args[0]) == null) {
                player.sendMessage(ChatColor.RED + "Argument 1 must be integer");
                player.sendMessage(ChatColor.RED + "Usage: /ggstat <arena number> <state|players>");
                return false;
            }

            Arena arena = g.arena[m.getInt(args[0])];
            int ID = m.getInt(args[0]);
            String var = args[1];

            if (arena == null) {
                player.sendMessage(ChatColor.RED + "Arena " + args[0] + " does not exist");
                return false;
            }

            g.getLogger().info(var);

            if (var.equalsIgnoreCase("state")) {
                player.sendMessage(ChatColor.AQUA + "Arena " + ID + " state: " + arena.state);
            } else if (var.equalsIgnoreCase("players")) {
                String players = arena.players.size() == 0 ? "none" : "";
                for (Player p : arena.players)
                    players += p.getDisplayName() + ", ";

                players = players.substring(0, players.length() - 2);

                player.sendMessage(ChatColor.AQUA + "Arena " + ID + " players: " + players);
            } else {
                player.sendMessage(ChatColor.RED + "Argument 2 must be 'state' or 'players'");
                player.sendMessage(ChatColor.RED + "Usage: /ggstat <arena number> <state|players>");
                return false;
            }

        }


        return false;
    }
}
