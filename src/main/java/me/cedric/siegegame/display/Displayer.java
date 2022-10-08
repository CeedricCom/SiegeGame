package me.cedric.siegegame.display;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.display.placeholderapi.Placeholder;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import me.deltaorion.bukkit.display.bukkit.BukkitApiPlayer;
import me.deltaorion.bukkit.display.scoreboard.EScoreboard;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Displayer {

    private final SiegeGame plugin;
    private final GamePlayer gamePlayer;
    private final BukkitApiPlayer apiPlayer;

    public Displayer(SiegeGame plugin, GamePlayer gamePlayer) {
        this.plugin = plugin;
        this.gamePlayer = gamePlayer;
        this.apiPlayer = plugin.getBukkitPlayerManager().getPlayer(gamePlayer.getUUID());
    }

    public void updateScoreboard() {
        if (apiPlayer == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        List<String> lines = new ArrayList<>();
        lines.add("");

        List<Team> teams = match.getWorldGame().getTeams().stream().sorted(Comparator.comparing(Team::getName)).collect(Collectors.toList());

        for (Team team : teams) {
            lines.add(Placeholder.getRelationalColor(gamePlayer.getTeam(), team) + team.getName() + ": " +
                    ChatColor.WHITE + team.getPoints() + " points");
        }

        lines.add("");
        lines.add("    " + ChatColor.DARK_AQUA + "Super Items");

        for (int i = 0; i < match.getWorldGame().getSuperItemManager().getSuperItems().size(); i++) {
            List<SuperItem> superItems = new ArrayList<>(match.getWorldGame().getSuperItemManager().getSuperItems());
            SuperItem superItem = superItems.get(i);
            String owner = superItem.getOwner() == null ? "" : superItem.getOwner().getBukkitPlayer().getName();

            lines.add(ChatColor.LIGHT_PURPLE + superItem.getDisplayName() + ": " + ChatColor.GRAY + owner);
        }

        lines.add("");
        lines.add(ChatColor.GOLD + "Map: " + ChatColor.GRAY + match.getGameMap().getDisplayName());
        lines.add("");
        lines.add(ChatColor.YELLOW + "ceedric.com");

        EScoreboard scoreboard = apiPlayer.getScoreboard() == null ? apiPlayer.setScoreboard(apiPlayer.getUniqueID().toString(), lines.size()) : apiPlayer.getScoreboard();
        scoreboard.setTitle(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Sieges");

        int i = 0;
        for (String line : lines) {
            scoreboard.setLine(line, i);
            i++;
        }
    }

    public void wipeScoreboard() {
        BukkitApiPlayer apiPlayer = plugin.getBukkitPlayerManager().getPlayer(gamePlayer.getUUID());

        if (apiPlayer == null)
            return;

        apiPlayer.removeScoreboard();
    }
}

