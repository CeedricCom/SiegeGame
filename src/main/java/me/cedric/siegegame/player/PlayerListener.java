package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    public SiegeGamePlugin plugin;

    public PlayerListener(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();
        if (match != null) {
            match.getWorldGame().addPlayer(player.getUniqueId());
            GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());

            match.getWorldGame().assignTeam(gamePlayer);

            if (gamePlayer.hasTeam()) {
                player.teleport(gamePlayer.getTeam().getSafeSpawn());
                gamePlayer.getDisplayer().updateScoreboard();
            }

            gamePlayer.grantNightVision();
        }

        player.getInventory().clear();
        player.setLevel(0);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();
        if (match != null)
            match.getWorldGame().removePlayer(player.getUniqueId());
    }

    @EventHandler
    public void onXP(PlayerExpChangeEvent event) {
        event.setAmount(0);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {

        Player killer = event.getPlayer().getKiller();

        if (killer == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer killerGamePlayer = match.getWorldGame().getPlayer(killer.getUniqueId());
        killer.playSound(killer.getLocation(), "entity.experience_orb.pickup", 1.0F, 0F);

        if (killerGamePlayer == null)
            return;

        if (!killerGamePlayer.hasTeam())
            return;

        Team team = killerGamePlayer.getTeam();

        for (GamePlayer player : team.getPlayers()) {
            Player bukkitPlayer = player.getBukkitPlayer();

            if (bukkitPlayer == null)
                continue;

            int levels = bukkitPlayer.getLevel();

            bukkitPlayer.setLevel(levels + plugin.getGameConfig().getLevelsPerKill());
        }

        team.addPoints(plugin.getGameConfig().getPointsPerKill());

        if (team.getPoints() >= plugin.getGameConfig().getPointsToEnd()) {
            plugin.getGameManager().startNextMap();
        }

        plugin.getGameManager().getCurrentMatch().getWorldGame().updateAllScoreboards();

        GamePlayer dead = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());
            if (gamePlayer == null)
                continue;
            gamePlayer.getDisplayer().displayKill(dead, killerGamePlayer);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        gamePlayer.getDisplayer().updateScoreboard();
        gamePlayer.grantNightVision();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer player = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (player == null || !player.hasTeam())
            return;

        if (!(event.getMessage().endsWith("t spawn") || event.getMessage().endsWith("town spawn")))
            return;

        player.getBukkitPlayer().teleport(player.getTeam().getSafeSpawn());
        event.setCancelled(true);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player))
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        Player damager = (Player) event.getDamager();
        Player player = (Player) event.getEntity();

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer damagerGamePlayer = match.getWorldGame().getPlayer(damager.getUniqueId());
        GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());

        if (damagerGamePlayer == null || gamePlayer == null)
            return;

        if (damagerGamePlayer.hasTeam() && gamePlayer.hasTeam() && damagerGamePlayer.getTeam().equals(gamePlayer.getTeam()))
            event.setCancelled(true);
    }

}
