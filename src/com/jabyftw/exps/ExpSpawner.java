package com.jabyftw.exps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Rafael
 */
public class ExpSpawner extends JavaPlugin implements Listener, CommandExecutor {

    private FileConfiguration config;
    private int exp, radius, lastInt;
    private List<Location> locs = new ArrayList();

    @Override
    public void onEnable() {
        config = getConfig();
        lastInt = -1;
        config.addDefault("config.expQuantity", 8);
        config.addDefault("config.radiusThreshold", 16);
        config.addDefault("locations.1", "world;5;64;24");
        config.addDefault("locations.2", "world;3;32;21");
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        exp = config.getInt("config.expQuantity");
        radius = config.getInt("config.radiusThreshold");
        for (String key : config.getConfigurationSection("locations").getKeys(false)) {
            try {
                int i = Integer.parseInt(key);
                if (i > lastInt) {
                    lastInt = i;
                }
            } catch (NumberFormatException e) {
            }
            String[] s1 = config.getString("locations." + key).split(";");
            try {
                Location l = new Location(getServer().getWorld(s1[0]), Integer.parseInt(s1[1]), Integer.parseInt(s1[2]), Integer.parseInt(s1[3]));
                if (l.getBlock().getType().equals(Material.MOB_SPAWNER)) {
                    locs.add(l);
                }
            } catch (NullPointerException e) {
            } catch (NumberFormatException e) {
            }
        }
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginCommand("expspawner").setExecutor(this);
        getLogger().log(Level.INFO, "Enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Disabled");
    }

    private void addLocation(Location loc) {
        lastInt += 1;
        config.addDefault("locations." + lastInt, loc.getWorld().toString() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ());
        config.options().copyDefaults(true);
        saveConfig();
        locs.add(loc);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lavel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Block b = p.getTargetBlock(null, 200);
            if (b.getType().equals(Material.MOB_SPAWNER)) {
                addLocation(b.getLocation());
                sender.sendMessage("§eMobSpawner added/readded.");
                return true;
            } else {
                sender.sendMessage("§cThis is not a mob spawner.");
                return true;
            }
        } else {
            sender.sendMessage("Only ingame.");
            return true;
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        for (Location loc : locs) {
            if (e.getLocation().distanceSquared(loc) < (radius * radius)) {
                if (e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
                    ExperienceOrb orb = e.getLocation().getWorld().spawn(e.getLocation(), ExperienceOrb.class);
                    orb.setExperience(exp);
                    e.setCancelled(true);
                }
            }
        }
    }
}
