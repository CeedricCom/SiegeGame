package me.cedric.siegegame.controller.stats;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.view.display.StatsDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.UUID;

public class StatsController implements Listener {

    private final SiegeGamePlugin plugin;
    private final WorldGame worldGame;
    private final HashMap<UUID, Double> damageMap = new HashMap<>();
    private final HashMap<UUID, Integer> killsMap = new HashMap<>();

    public StatsController(SiegeGamePlugin plugin, WorldGame worldGame) {
        this.plugin = plugin;
        this.worldGame = worldGame;
    }

    public void initialize() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void shutdown() {
        HandlerList.unregisterAll(this);
        StatsDisplay.display(worldGame, damageMap, killsMap);
        damageMap.clear();
        killsMap.clear();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onKill(PlayerDeathEvent event) {
        Player killer = event.getPlayer().getKiller();

        if (plugin == null || worldGame == null || killer == null)
            return;

        addKill(killer.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity entityDamager = event.getDamager();
        Entity entityDamaged = event.getEntity();

        if (!(entityDamager instanceof Player))
            return;

        if (!(entityDamaged instanceof Player))
            return;

        if (plugin == null || worldGame == null)
            return;

        Player damager = (Player) entityDamager;

        double finalDamage = event.getFinalDamage();
        addDamage(damager.getUniqueId(), finalDamage);
    }

    private void addDamage(UUID uuid, double amount) {
        if (!damageMap.containsKey(uuid))
            damageMap.put(uuid, 0D);

        double current = damageMap.get(uuid);
        damageMap.replace(uuid, current, current + amount);
    }

    private void addKill(UUID uuid) {
        if (!killsMap.containsKey(uuid))
            killsMap.put(uuid, 0);

        int current = killsMap.get(uuid);
        killsMap.replace(uuid, current, current + 1);
    }
}











