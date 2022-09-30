package me.cedric.siegegame.display;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import me.deltaorion.bukkit.display.bukkit.BukkitApiPlayer;
import me.deltaorion.bukkit.display.scoreboard.EScoreboard;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Displayer {

    public static void updateScoreboard(SiegeGame plugin, GamePlayer gamePlayer, WorldGame worldGame) {
        BukkitApiPlayer apiPlayer = plugin.getBukkitPlayerManager().getPlayer(gamePlayer.getUUID());

        if (apiPlayer == null)
            return;

        List<String> lines = new ArrayList<>();
        lines.add("");

        List<Team> teams = worldGame.getTeams().stream().sorted(Comparator.comparing(Team::getName)).collect(Collectors.toList());

        if (teams.size() == 2) {

            Team firstTeam = teams.get(0);
            Team secondTeam = teams.get(1);

            String firstMessage = firstTeam.getName() + ": " + ChatColor.WHITE.asBungee() + firstTeam.getPoints();
            String secondMessage = secondTeam.getName() + ": " + ChatColor.WHITE.asBungee() + secondTeam.getPoints();

            if (gamePlayer.hasTeam()) {
                if (firstTeam.equals(gamePlayer.getTeam())) {
                    firstMessage = ChatColor.BLUE + firstMessage;
                    firstMessage = firstMessage + " " + ChatColor.GRAY + "YOU";
                    secondMessage = ChatColor.RED + secondMessage;
                } else {
                    secondMessage = ChatColor.BLUE + secondMessage;
                    secondMessage = secondMessage + " " + ChatColor.GRAY + "YOU";
                    firstMessage = ChatColor.RED + firstMessage;
                }
            }

            lines.add(firstMessage);
            lines.add(secondMessage);
        } else {
            for (int i = 0; i < worldGame.getTeams().size() - 1; i++) {
                Team team = teams.get(i);
                String message = net.md_5.bungee.api.ChatColor.of(team.getColor()) + team.getName() + ": " + ChatColor.WHITE + team.getPoints() + " points ";

                if (gamePlayer.hasTeam() && gamePlayer.getTeam().equals(team)) {
                    message = message + ChatColor.GRAY + "YOU";
                }

                lines.add(message);
            }
        }

        lines.add("");
        lines.add("    " + ChatColor.DARK_AQUA + "Super Items");

        for (int i = 0; i < worldGame.getSuperItems().size(); i++) {
            List<SuperItem> superItems = worldGame.getSuperItems();
            SuperItem superItem = superItems.get(i);
            String owner = superItem.getOwner() == null ? "" : superItem.getOwner().getBukkitPlayer().getName();

            lines.add(ChatColor.LIGHT_PURPLE + superItem.getDisplayName() + ": " + ChatColor.GRAY + owner);
        }

        lines.add("");
        lines.add(ChatColor.GOLD + "Map: " + ChatColor.GRAY + worldGame.getGameMap().getDisplayName());
        lines.add("");
        lines.add(ChatColor.YELLOW + "ceedric.com");

        EScoreboard scoreboard = apiPlayer.setScoreboard(apiPlayer.getUniqueID().toString(), lines.size());
        scoreboard.setTitle(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Sieges");

        int i = 0;
        for (String line : lines) {
            scoreboard.setLine(line, i);
            i++;
        }
    }

    public static void wipeScoreboard(SiegeGame plugin, GamePlayer gamePlayer) {
        BukkitApiPlayer apiPlayer = plugin.getBukkitPlayerManager().getPlayer(gamePlayer.getUUID());

        if (apiPlayer == null)
            return;

        apiPlayer.removeScoreboard();
    }

}
