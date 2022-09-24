package me.cedric.siegegame.player;

import com.palmergames.bukkit.towny.event.player.PlayerKilledPlayerEvent;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.teams.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    public SiegeGame plugin;

    public PlayerListener(SiegeGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerManager().addPlayer(player.getUniqueId());
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

            res.setLevel(levels + SiegeGame.LEVELS_PER_KILL);
        }
    }

}
