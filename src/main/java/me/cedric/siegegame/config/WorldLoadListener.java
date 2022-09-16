package me.cedric.siegegame.config;

import me.cedric.siegegame.SiegeGame;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadListener implements Listener {

    private final SiegeGame plugin;

    public WorldLoadListener(SiegeGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onLoad(WorldLoadEvent event) {
        ConfigLoader configLoader = plugin.getConfigLoader();
        World world = event.getWorld();
        String worldName = world.getName();

        if (!configLoader.getLateLoadWorldNames().contains(worldName))
            return;

        plugin.getLogger().info("New config world " + worldName + " has been detected. Loading...");
        try {
            configLoader.loadWorld(world, configLoader.getLateWorldMapKey(worldName));
        } catch (Exception x) {
            plugin.getLogger().info("Could not load world " + worldName + "");
            x.printStackTrace();
            return;
        }

        plugin.getLogger().info("Loaded!");
    }

}
























