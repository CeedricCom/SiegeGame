package me.cedric.siegegame.view.display;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.enums.Messages;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.model.teams.territory.Territory;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Handles per-player UI (scoreboard, bossbars, messages)
 */
public class Displayer {

    private static final String OBJECTIVE_NAME = "sieges";
    private static final Component OBJECTIVE_TITLE =
            Component.text(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Sieges");

    private final SiegeGamePlugin plugin;
    private final GamePlayer gamePlayer;

    private Scoreboard scoreboard;
    private Objective objective;

    // Cache of last fully rendered ordered lines to avoid redundant rewrites
    private List<String> lastLines = Collections.emptyList();

    private BossBar territoryBossBar;

    public Displayer(SiegeGamePlugin plugin, GamePlayer gamePlayer) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.gamePlayer = Objects.requireNonNull(gamePlayer, "gamePlayer");
    }

    /* ========================= SCOREBOARD ========================= */

    public void updateScoreboard() {
        Player bukkitPlayer = getOnlinePlayer();
        if (bukkitPlayer == null)
            return;

        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();
        if (match == null)
            return;

        ensureScoreboard();

        List<String> newLines = buildLines(match);

        // If identical, skip expensive operations
        if (newLines.equals(lastLines))
            return;

        // Remove stale entries
        for (String existing : new ArrayList<>(scoreboard.getEntries())) {
            if (!newLines.contains(existing)) {
                scoreboard.resetScores(existing);
            }
        }

        // Render new lines (top line gets highest score)
        int score = newLines.size();
        for (String line : newLines) {
            // Score only set if changed or absent; safe to just set
            objective.getScore(line).setScore(score);
            score--;
        }

        lastLines = newLines;
        bukkitPlayer.setScoreboard(scoreboard);
    }

    public void wipeScoreboard() {
        Player player = getOnlinePlayer();
        if (player == null)
            return;
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        scoreboard = null;
        objective = null;
        lastLines = Collections.emptyList();
    }

    private void ensureScoreboard() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        if (objective == null) {
            // 1.21.4 still supports "dummy" criteria string
            objective = scoreboard.getObjective(OBJECTIVE_NAME);
            if (objective == null) {
                objective = scoreboard.registerNewObjective(
                        OBJECTIVE_NAME,
                        "dummy",
                        OBJECTIVE_TITLE,
                        RenderType.INTEGER
                );
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
        }
    }

    private List<String> buildLines(SiegeGameMatch match) {
        List<String> lines = new ArrayList<>();

        lines.add(blank(0));

        List<Team> teams = new ArrayList<>(match.getWorldGame().getTeams());
        teams.sort(Comparator.comparing(Team::getName)); // Could change comparator without touching rendering

        for (Team team : teams) {
            net.md_5.bungee.api.ChatColor color = ColorUtil.getRelationalColor(gamePlayer.getTeam(), team);
            lines.add(color + team.getName() + ": " + ChatColor.WHITE + team.getPoints() + " points");
        }

        lines.add(blank(1));
        lines.add(ChatColor.GOLD + "Map: " + ChatColor.GRAY + match.getGameMap().getDisplayName());
        lines.add(blank(2));
        lines.add(ChatColor.YELLOW + plugin.getGameConfig().getServerIP());

        return lines;
    }

    // Produces unique blank lines using distinct color codes
    private String blank(int index) {
        // Cycle through formatting codes to ensure uniqueness
        ChatColor[] values = ChatColor.values();
        ChatColor cc = values[index % values.length];
        return cc.toString();
    }

    /* ========================= MESSAGES ========================= */

    public void displayKill(GamePlayer dead, GamePlayer killerGamePlayer) {
        if (dead == null || killerGamePlayer == null)
            return;

        Team killerTeam = killerGamePlayer.getTeam();
        Player killer = killerGamePlayer.getBukkitPlayer();
        Player recipient = getOnlinePlayer();
        if (killer == null || recipient == null)
            return;

        TextComponent base = Component.text("")
                .append(Component.text(Messages.PREFIX.toString(), TextColor.color(88, 140, 252)));

        TextComponent message = base
                .append(Component.text(ColorUtil.getRelationalColor(gamePlayer.getTeam(), killerTeam) + killer.getName()))
                .append(Component.text(" eliminated ", TextColor.color(252, 252, 53)))
                .append(Component.text(ColorUtil.getRelationalColor(gamePlayer.getTeam(), dead.getTeam()) + dead.getBukkitPlayer().getName()))
                .append(Component.text(" | ", TextColor.color(180, 180, 180)))
                .append(Component.text(killerTeam.getName() + " +", TextColor.color(255, 194, 97)))
                .append(Component.text(plugin.getGameConfig().getPointsPerKill() + " points", TextColor.color(255, 73, 23)));

        recipient.sendMessage(message);

        if (killerTeam.equals(gamePlayer.getTeam())) {
            displayXPGain(gamePlayer);
        }
    }

    public void displayCombatLogKill(String dead) {
        Player recipient = getOnlinePlayer();
        if (recipient == null)
            return;

        TextComponent message = Component.text("")
                .append(Component.text(Messages.PREFIX.toString(), TextColor.color(88, 140, 252)))
                .append(Component.text(dead, TextColor.color(237, 77, 255)))
                .append(Component.text(" logged out in combat. ", TextColor.color(252, 252, 53)))
                .append(Component.text("Enemies gain +", TextColor.color(255, 194, 97)))
                .append(Component.text(plugin.getGameConfig().getPointsPerKill() + " points", TextColor.color(255, 73, 23)));

        recipient.sendMessage(message);
    }

    public void displayXPGain(GamePlayer gp) {
        Player p = gp == null ? null : gp.getBukkitPlayer();
        if (p == null)
            return;

        TextComponent xpLevels = Component.text("")
                .color(TextColor.color(0, 143, 26))
                .append(Component.text("+" + plugin.getGameConfig().getLevelsPerKill() + " XP Levels"));
        p.sendMessage(xpLevels);
    }

    public void displayVictory() {
        Player p = getOnlinePlayer();
        if (p != null) {
            p.sendTitle(
                    ChatColor.GOLD + "" + ChatColor.BOLD + "VICTORY",
                    ChatColor.YELLOW + "Your team prevailed!",
                    10, 70, 20
            );
        }
    }

    public void displayLoss() {
        Player p = getOnlinePlayer();
        if (p != null) {
            p.sendTitle(
                    ChatColor.RED + "" + ChatColor.BOLD + "DEFEAT",
                    ChatColor.GRAY + "Better luck next time.",
                    10, 70, 20
            );
        }
    }

    /* ========================= TERRITORY / CLAIMS ========================= */

    public void displayInsideClaims(WorldGame worldGame, Territory territory) {
        Player p = getOnlinePlayer();
        if (p == null || worldGame == null || territory == null)
            return;

        Team territoryTeam = worldGame.getTeam(territory.getTeam().getConfigKey());
        if (territoryTeam == null)
            return;

        String formatted = String.format(
                Messages.CLAIMS_ENTERED,
                ColorUtil.getRelationalColor(gamePlayer.getTeam(), territoryTeam) + territoryTeam.getName()
        );

        p.sendActionBar(Component.text(formatted));

        Component barTitle = Component.text(
                ChatColor.YELLOW + "Inside " + formatted + ChatColor.YELLOW + " territory"
        );

        if (territoryBossBar == null) {
            territoryBossBar = BossBar.bossBar(barTitle, 1f, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
            p.showBossBar(territoryBossBar);
        } else {
            territoryBossBar.name(barTitle);
        }
    }

    public void removeDisplayInsideClaims() {
        Player p = getOnlinePlayer();
        if (p != null && territoryBossBar != null) {
            p.hideBossBar(territoryBossBar);
        }
        territoryBossBar = null;
    }

    public void displayActionCancelled() {
        // Placeholder for future cancellation feedback
    }

    private Player getOnlinePlayer() {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(gamePlayer.getUUID());
        if (offline == null)
            return null;

        if (!offline.isOnline())
            return null;

        return offline.getPlayer();
    }
}
