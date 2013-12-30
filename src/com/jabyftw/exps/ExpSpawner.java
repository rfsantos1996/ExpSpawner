package com.jabyftw.exps;

import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Rafael
 */
public class ExpSpawner extends JavaPlugin implements Listener {

    private int exp;

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        config.addDefault("config.expQuantity", 8);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        exp = config.getInt("config.expQuantity");
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().log(Level.INFO, "Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Disabled");
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
            ExperienceOrb orb = e.getLocation().getWorld().spawn(e.getLocation(), ExperienceOrb.class);
            orb.setExperience(exp);
            e.setCancelled(true);
        }
    }
}
