package me.cedric.siegegame.player;

import com.palmergames.bukkit.towny.event.player.PlayerKilledPlayerEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.config.Settings;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

        if (plugin.getGameManager().isOngoingGame()) {
            plugin.getGameManager().assignTeam(plugin.getPlayerManager().getPlayer(player.getUniqueId()));
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
    public void onKill(PlayerKilledPlayerEvent event) {
        Player killer = event.getKiller();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());

        if (!gamePlayer.hasTeam())
            return;

        Team team = gamePlayer.getTeam();
        Town town = team.getTeamTown();

        for (Resident resident : town.getResidents()) {
            Player res = resident.getPlayer();

            if (res == null)
                continue;

            int levels = res.getLevel();

            res.setLevel(levels + Settings.LEVELS_PER_KILL.getValue());
        }

        team.addPoints(Settings.POINTS_PER_KILL.getValue());

        if (team.getPoints() >= Settings.POINTS_TO_END.getValue()) {
            plugin.getGameManager().startNextMap();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().addPotionEffect(
                new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false)
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommandProcess(PlayerCommandPreprocessEvent event) {
        GamePlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());

        if (!player.hasTeam())
            return;

        if (!(event.getMessage().endsWith("t spawn") || event.getMessage().endsWith("town spawn")))
            return;

        player.getBukkitPlayer().teleport(player.getTeam().getSafeSpawn());
        event.setCancelled(true);
    }

}
