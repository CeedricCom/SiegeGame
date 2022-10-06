package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.config.Settings;
import me.cedric.siegegame.display.Displayer;
import me.cedric.siegegame.teams.Team;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    public SiegeGame plugin;

    public PlayerListener(SiegeGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerManager().addPlayer(player.getUniqueId());
        player.getInventory().clear();
        player.setLevel(0);

        if (plugin.getGameManager().isOngoingGame()) {
            GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            plugin.getGameManager().assignTeam(gamePlayer);
            if (gamePlayer.hasTeam()) {
                player.teleport(gamePlayer.getTeam().getSafeSpawn());
                Displayer.updateScoreboard(plugin, gamePlayer, gamePlayer.getTeam().getWorldGame());
            }
        }

        player.addPotionEffect(
                new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false)
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerManager().removePlayer(player.getUniqueId());
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

        GamePlayer killerGamePlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());

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

        plugin.getGameManager().updateAllScoreboards();

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
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());
        event.getPlayer().addPotionEffect(
                new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false)
        );
        Displayer.updateScoreboard(plugin, gamePlayer, gamePlayer.getTeam().getWorldGame());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        GamePlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());

        if (player == null || !player.hasTeam())
            return;

        if (!(event.getMessage().endsWith("t spawn") || event.getMessage().endsWith("town spawn")))
            return;

        player.getBukkitPlayer().teleport(player.getTeam().getSafeSpawn());
        event.setCancelled(true);
    }

}
