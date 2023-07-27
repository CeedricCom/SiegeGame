package me.cedric.siegegame.player;

import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

public class PlayerListener implements Listener {

    public SiegeGamePlugin plugin;

    public PlayerListener(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
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
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setLevel(0);
        player.sendMessage(ChatColor.DARK_AQUA + "Welcome to ceedric.com Use " + ChatColor.GOLD + "/resources" + ChatColor.DARK_AQUA + " for gear.");

        String currentMap = match == null ? null : match.getWorldGame().getMapIdentifier();
        plugin.getGameManager().getKitStorage().load(player, currentMap);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match != null)
            match.getWorldGame().removePlayer(player.getUniqueId());

        plugin.getGameManager().getKitStorage().unload(player.getUniqueId());

        for (PotionEffect potionEffect : player.getActivePotionEffects())
            player.removePotionEffect(potionEffect.getType());
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
        match.getWorldGame().updateAllScoreboards();

        GamePlayer dead = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (dead == null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());
                if (gamePlayer == null)
                    continue;
                gamePlayer.getDisplayer().displayCombatLogKill(event.getPlayer().getName());
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());
            if (gamePlayer == null)
                continue;
            gamePlayer.getDisplayer().displayKill(dead, killerGamePlayer);
        }

        if (team.getPoints() >= plugin.getGameConfig().getPointsToEnd())
            plugin.getGameManager().startNextGame();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager))
            return;

        if (!(event.getEntity() instanceof Player player))
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null) {
            event.setCancelled(true);
            return;
        }

        GamePlayer damagerGamePlayer = match.getWorldGame().getPlayer(damager.getUniqueId());
        GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());

        if (damagerGamePlayer == null || gamePlayer == null)
            return;

        if (damagerGamePlayer.hasTeam() && gamePlayer.hasTeam() && damagerGamePlayer.getTeam().equals(gamePlayer.getTeam()))
            event.setCancelled(true);

        if (!gamePlayer.hasTeam() || !damagerGamePlayer.hasTeam())
            return;

        Team damagerTeam = damagerGamePlayer.getTeam();
        Team team = gamePlayer.getTeam();

        BoundingBox damagerSafeArea = damagerTeam.getSafeArea().getBoundingBox();
        BoundingBox teamSafeArea = team.getSafeArea().getBoundingBox();

        if (damagerSafeArea.isColliding(damager.getLocation()) || damagerSafeArea.isColliding(player.getLocation())
                || teamSafeArea.isColliding(damager.getLocation()) || teamSafeArea.isColliding(player.getLocation())) {
            // They are either both in the safe area or one is in there - dont care which
            // If both are tag simply allow pvp
            ICombatManager combatManager = plugin.getCombatLogX().getCombatManager();
            if (!(combatManager.isInCombat(damager) && combatManager.isInCombat(player)))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (event.getCause().equals(EntityPotionEffectEvent.Cause.BEACON))
            event.setCancelled(true);
    }

}
