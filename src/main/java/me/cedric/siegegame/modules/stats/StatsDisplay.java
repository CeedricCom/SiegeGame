package me.cedric.siegegame.modules.stats;

import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.ChatColor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatsDisplay {

    private static final int displayUntilTop = 10;
    private static String MESSAGE_TITLE_HEADER = "";
    private static String MESSAGE_TITLE = "&e&lMATCH LEADERBOARD";
    private static String POSITION_PLACEMENT_FORMAT = "&f%position%. &e%player% &7Damage: &f%hearts% &c❤ &8| &7Kills: &f%kills%";

    static {
        MESSAGE_TITLE = ChatColor.translateAlternateColorCodes('&', MESSAGE_TITLE);
        MESSAGE_TITLE_HEADER = ChatColor.translateAlternateColorCodes('&', MESSAGE_TITLE_HEADER);
        POSITION_PLACEMENT_FORMAT = ChatColor.translateAlternateColorCodes('&', POSITION_PLACEMENT_FORMAT);
    }

    public static void display(WorldGame worldGame, HashMap<UUID, Double> damageMap, HashMap<UUID, Integer> killMap) {
        List<Map.Entry<UUID, Double>> damageSorted = damageMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList());

        Collections.reverse(damageSorted);

        broadcastMessage(worldGame, MESSAGE_TITLE_HEADER);
        broadcastMessage(worldGame, MESSAGE_TITLE);
        broadcastMessage(worldGame, "");

        int i = 1;
        for (Map.Entry<UUID, Double> damageEntry : damageSorted) {
            GamePlayer gamePlayer = worldGame.getPlayer(damageEntry.getKey());
            if (gamePlayer == null)
                continue;

            if (i > displayUntilTop)
                break;

            double damage = roundToHalf(damageMap.getOrDefault(gamePlayer.getUUID(), 0D));
            double hearts = damageToHearts(damage);

            String toDisplay = POSITION_PLACEMENT_FORMAT
                    .replace("%position%", i + "")
                    .replace("%damage%", damage + "")
                    .replace("%kills%", (killMap.getOrDefault(gamePlayer.getUUID(), 0)) + "")
                    .replace("%hearts%", hearts + "")
                    .replace("%player%", gamePlayer.getBukkitPlayer().getName());
            broadcastMessage(worldGame, toDisplay);
            i++;
        }

        broadcastMessage(worldGame, MESSAGE_TITLE_HEADER);

    }

    private static void broadcastMessage(WorldGame worldGame, String message) {
        for (GamePlayer gamePlayer : worldGame.getPlayers()) {
            gamePlayer.getBukkitPlayer().sendMessage(message);
        }
    }

    private static double damageToHearts(double damage) {
        double hearts = damage / 2;
        return roundToHalf(hearts);
    }

    private static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }



}













