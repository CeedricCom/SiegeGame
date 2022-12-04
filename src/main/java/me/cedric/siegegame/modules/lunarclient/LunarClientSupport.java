package me.cedric.siegegame.modules.lunarclient;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointAdd;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointRemove;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LunarClientSupport {

    public static void updateTeammateView(Team team) {
        Map<UUID, Map<String, Double>> teamView = new HashMap<>();

        for (GamePlayer gamePlayer : team.getPlayers()) {
            Player bukkitPlayer = gamePlayer.getBukkitPlayer();
            Map<String, Double> currentPlayerPos = new HashMap<>();
            currentPlayerPos.put("x", bukkitPlayer.getLocation().getX());
            currentPlayerPos.put("y", bukkitPlayer.getLocation().getX());
            currentPlayerPos.put("z", bukkitPlayer.getLocation().getX());

            teamView.put(gamePlayer.getUUID(), currentPlayerPos);
        }

        LCPacketTeammates packetTeammates = new LCPacketTeammates(UUID.randomUUID(), System.currentTimeMillis(), teamView);

        for (GamePlayer gamePlayer : team.getPlayers()) {
            if (!gamePlayer.isLunarClient()) // Only send the packet to people on lunar
                continue;

            LunarClientAPI.getInstance().sendTeammates(gamePlayer.getBukkitPlayer(), packetTeammates);
        }
    }

    public static void sendTemporaryWaypoint(SiegeGamePlugin plugin, Team team, Location location, long delay) {
        LCPacketWaypointAdd waypointAdd = new LCPacketWaypointAdd("Rally", location.getWorld().getName(),
                new Color(255, 46, 53).getRGB(),
                location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                true, true);

        for (GamePlayer gamePlayer : team.getPlayers()) {
            if (!gamePlayer.isLunarClient())
                continue;

            LunarClientAPI.getInstance().sendPacket(gamePlayer.getBukkitPlayer(), waypointAdd);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (GamePlayer gamePlayer : team.getPlayers()) {
                if (!gamePlayer.isLunarClient())
                    continue;
                LCPacketWaypointRemove waypointRemove = new LCPacketWaypointRemove("Rally", location.getWorld().getName());
                LunarClientAPI.getInstance().sendPacket(gamePlayer.getBukkitPlayer(), waypointRemove);
            }
        }, delay);

    }

}
