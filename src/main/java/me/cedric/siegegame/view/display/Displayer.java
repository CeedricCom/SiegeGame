package me.cedric.siegegame.view.display;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.model.teams.territory.Territory;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Displayer {

    private final SiegeGamePlugin plugin;
    private final GamePlayer gamePlayer;
    private Scoreboard scoreboard = null;
    private BossBar bossBar = null;

    public Displayer(SiegeGamePlugin plugin, GamePlayer gamePlayer) {
        this.plugin = plugin;
        this.gamePlayer = gamePlayer;
    }

    public void updateScoreboard() {
        if (gamePlayer == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        List<String> lines = new ArrayList<>();

        lines.add(ChatColor.YELLOW + plugin.getGameConfig().getServerIP());
        lines.add(ChatColor.RED + "");

        List<Team> teams = match.getWorldGame().getTeams().stream().sorted(Comparator.comparing(Team::getName)).collect(Collectors.toList());

        for (Team team : teams) {
            lines.add(ColorUtil.getRelationalColor(gamePlayer.getTeam(), team) + team.getName() + ": " +
                    ChatColor.WHITE + team.getPoints() + " points");
        }

        lines.add(ChatColor.BLUE + "");
        lines.add(ChatColor.GOLD + "Map: " + ChatColor.GRAY + match.getGameMap().getDisplayName());
        lines.add(ChatColor.DARK_PURPLE + "");

        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("sieges", "dummy",
                    Component.text(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Sieges"), RenderType.INTEGER);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        Objective objective = scoreboard.getObjective("sieges");

        int i = 0;
        for (String line : lines) {
            Score score = objective.getScore(line);
            score.setScore(i);
            i++;
        }

        gamePlayer.getBukkitPlayer().setScoreboard(scoreboard);
    }

    public void wipeScoreboard() {
        Player player = Bukkit.getPlayer(gamePlayer.getUUID());

        if (player == null)
            return;

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void displayKill(GamePlayer dead, GamePlayer killerGamePlayer) {

        Team killerTeam = killerGamePlayer.getTeam();
        Player killer = killerGamePlayer.getBukkitPlayer();

        TextComponent textComponent = Component.text("")
                .color(TextColor.color(88, 140, 252))
                .append(Component.text(Messages.PREFIX.toString())
                .append(Component.text(ColorUtil.getRelationalColor(gamePlayer.getTeam(), killerTeam) + killer.getName())
                .append(Component.text(" has killed ", TextColor.color(252, 252, 53)))
                .append(Component.text(ColorUtil.getRelationalColor(gamePlayer.getTeam(), dead.getTeam()) + dead.getBukkitPlayer().getName() + " "))
                .append(Component.text(killerTeam.getName() + ": ", TextColor.color(255, 194, 97)))
                .append(Component.text("+" + plugin.getGameConfig().getPointsPerKill() + " points ", TextColor.color(255, 73, 23)))));

        gamePlayer.getBukkitPlayer().sendMessage(textComponent);

        TextComponent xpLevels = Component.text("")
                .color(TextColor.color(0, 143, 26))
                .append(Component.text("+" + plugin.getGameConfig().getLevelsPerKill() + " XP Levels"));

        if (killerTeam.equals(gamePlayer.getTeam()))
            gamePlayer.getBukkitPlayer().sendMessage(xpLevels);
    }

    public void displayCombatLogKill(String dead) {
        TextComponent textComponent = Component.text("")
                .color(TextColor.color(88, 140, 252))
                .append(Component.text(Messages.PREFIX.toString())
                        .append(Component.text(dead, TextColor.color(237, 77, 255))
                                .append(Component.text(" has logged out in combat. ", TextColor.color(252, 252, 53)))
                                .append(Component.text("Enemies have received ", TextColor.color(255, 194, 97)))
                                .append(Component.text("+" + plugin.getGameConfig().getPointsPerKill() + " points ", TextColor.color(255, 73, 23)))));

        gamePlayer.getBukkitPlayer().sendMessage(textComponent);
    }

    public void displayInsideClaims(WorldGame worldGame, Territory territory) {
        if (bossBar != null)
            removeDisplayInsideClaims();

        String message = Messages.CLAIMS_ENTERED;
        Team team = worldGame.getTeam(territory.getTeam().getConfigKey());

        String s = String.format(message, ColorUtil.getRelationalColor(gamePlayer.getTeam(), team) + team.getName());

        gamePlayer.getBukkitPlayer().sendActionBar(Component.text(s));

        bossBar = BossBar.bossBar(Component.text(ChatColor.YELLOW + "You are currently in " + s + ChatColor.YELLOW + " claims"),
                1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        gamePlayer.getBukkitPlayer().showBossBar(bossBar);
    }

    public void removeDisplayInsideClaims() {
        gamePlayer.getBukkitPlayer().hideBossBar(bossBar);
        bossBar = null;
    }

    public void displayActionCancelled() {
        //gamePlayer.getBukkitPlayer().sendMessage(Messages.CLAIMS_ACTION_CANCELLED.toString());
    }

    public void displayVictory() {
        gamePlayer.getBukkitPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "VICTORY", ChatColor.YELLOW + "gg ez yall are dog z tier rands");
    }

    public void displayLoss() {
        gamePlayer.getBukkitPlayer().sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "DEFEAT", ChatColor.RED + "u folded gg ez dog");
    }

    public void displayXPGain(GamePlayer gamePlayer) {
        TextComponent xpLevels = Component.text("")
                .color(TextColor.color(0, 143, 26))
                .append(Component.text("+" + plugin.getGameConfig().getLevelsPerKill() + " XP Levels"));
        gamePlayer.getBukkitPlayer().sendMessage(xpLevels);
    }
}

