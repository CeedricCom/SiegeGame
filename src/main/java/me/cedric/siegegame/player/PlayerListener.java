package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.config.Settings;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    public SiegeGame plugin;

    public PlayerListener(SiegeGame plugin) {
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

            bukkitPlayer.setLevel(levels + (int) Settings.LEVELS_PER_KILL.getValue());
        }

        team.addPoints((int) Settings.POINTS_PER_KILL.getValue());

        if (team.getPoints() >= (int) Settings.POINTS_TO_END.getValue()) {
            plugin.getGameManager().startNextMap();
        }

        plugin.getGameManager().getCurrentMatch().getWorldGame().updateAllScoreboards();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), "entity.experience_orb.pickup", 1.0F, 0F);
            TextComponent textComponent = Component.text("")
                    .color(TextColor.color(88, 140, 252))
                    .append(Component.text("[ceedric.com] ", TextColor.color(247, 80, 30)))
                    .append(Component.text(killer.getName(), NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(" has killed ", TextColor.color(252, 252, 53)))
                    .append(Component.text(event.getPlayer().getName() + " ", NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text(team.getName() + ": ", TextColor.color(255, 194, 97)))
                    .append(Component.text("+" + Settings.POINTS_PER_KILL.getValue() + " points ", TextColor.color(255, 73, 23)));
            TextComponent xpLevels = Component.text("")
                            .color(TextColor.color(0, 143, 26))
                            .append(Component.text("+" + Settings.LEVELS_PER_KILL.getValue() + " XP Levels"));
            player.sendMessage(textComponent);
            player.sendMessage(xpLevels);
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

}
