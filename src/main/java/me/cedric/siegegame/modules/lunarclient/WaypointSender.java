package me.cedric.siegegame.modules.lunarclient;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.model.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;

public class WaypointSender {

    private final WorldGame worldGame;

    public WaypointSender(WorldGame worldGame) {
        this.worldGame = worldGame;
    }

    public void send() {
        for (Team team : worldGame.getTeams()) {
            for (GamePlayer gamePlayer : team.getPlayers()) {
                Player player = gamePlayer.getBukkitPlayer();
                ApolloPlayer apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId()).orElse(null);

                if (apolloPlayer == null)
                    continue;

                Waypoint base = createWaypoint("Base", team.getSafeSpawn());
                Apollo.getModuleManager().getModule(WaypointModule.class).displayWaypoint(apolloPlayer, base);
            }

        }
    }

    public static void sendTemporaryWaypoint(SiegeGamePlugin plugin, Team team, Location location, String name, int ticks) {
        for (GamePlayer gamePlayer : team.getPlayers()) {
            sendTemporaryWaypoint(plugin, gamePlayer.getBukkitPlayer(), location, name, ticks);
        }
    }

    public static void sendTemporaryWaypoint(SiegeGamePlugin plugin, Player player, Location location, String name, int ticks) {
        ApolloPlayer apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId()).orElse(null);

        if (apolloPlayer == null)
            return;

        Waypoint waypoint = createWaypoint(name, location);
        Apollo.getModuleManager().getModule(WaypointModule.class).displayWaypoint(apolloPlayer, waypoint);

        Bukkit.getScheduler().runTaskLater(plugin,
                () -> Apollo.getModuleManager().getModule(WaypointModule.class).removeWaypoint(apolloPlayer, waypoint),
                ticks);
    }

    private static Waypoint createWaypoint(String name, Location loc) {
        return Waypoint.builder()
                .color(Color.CYAN)
                .name(name)
                .location(ApolloBlockLocation.builder().x(loc.getBlockX()).y(loc.getBlockY()).z(loc.getBlockZ()).world(loc.getWorld().getName()).build())
                .build();

    }

}
