package me.cedric.siegegame.model.game.death;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private final DeathManager deathManager;

    public RespawnListener(DeathManager deathManager) {
        this.deathManager = deathManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRespawn(PlayerRespawnEvent event) {
        if (deathManager.getWorldGame() == null)
            return;

        Player player = event.getPlayer();
        event.setRespawnLocation(player.getLocation());
        deathManager.makeSpectator(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (deathManager.getWorldGame() == null)
            return;

        Player player = event.getPlayer();

        if (!deathManager.isPlayerDead(player.getUniqueId()))
            return;

        deathManager.makeSpectator(player);
        deathManager.setPausePlayer(player.getUniqueId(), false); // continue
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (deathManager.getWorldGame() == null)
            return;

        Player player = event.getPlayer();

        if (!deathManager.isPlayerDead(player.getUniqueId()))
            return;

        deathManager.setPausePlayer(player.getUniqueId(), true); // pause
    }

}
