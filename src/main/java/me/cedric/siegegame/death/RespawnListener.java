package me.cedric.siegegame.death;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final SiegeGame plugin;
    private final DeathManager deathManager;

    public RespawnListener(SiegeGame plugin, DeathManager deathManager) {
        this.plugin = plugin;
        this.deathManager = deathManager;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        event.setRespawnLocation(player.getLocation());
        deathManager.makeSpectator(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (!deathManager.isPlayerDead(gamePlayer))
            return;

        deathManager.makeSpectator(player);
        deathManager.setPausePlayer(gamePlayer, false); // continue
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (!deathManager.isPlayerDead(gamePlayer))
            return;

        deathManager.setPausePlayer(gamePlayer, true); // pause
    }

}
