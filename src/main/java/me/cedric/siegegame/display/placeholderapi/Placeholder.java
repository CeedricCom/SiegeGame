package me.cedric.siegegame.display.placeholderapi;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

@SuppressWarnings("deprecation")
public enum Placeholder {

    TEAM_NAME("team_name", false, (siegeGame, gamePlayer, s, extra) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : team.getName();
    }),

    TEAM_COLOR("team_color", false, (siegeGame, gamePlayer, s, extra) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : team.getColor().getRGB() + "";
    }),

    TEAM_POINTS("team_points", false, (siegeGame, gamePlayer, s, extra) -> {
        Team team = getTeam(siegeGame, gamePlayer, s);
        return team == null ? "" : team.getPoints() + "";
    }),

    TEAM_YOU_OR_EMPTY("team_you_or_empty", false, (siegeGame, gamePlayer, s, extra) -> {
        return getTeamYouOrEmpty(siegeGame, gamePlayer, s);
    }),

    RELATIONAL_COLOR("rel_player_color", true, (siegeGame, gamePlayer, s, extra) -> {
        if (!(extra[0] instanceof Player))
            return "";
        GamePlayer two = siegeGame.getGameManager().getCurrentMatch().getWorldGame().getPlayer(((Player) extra[0]).getUniqueId());
        return getRelationalColor(gamePlayer.getTeam(), two.getTeam()).toString();
    });

    private final String param;
    private final PlaceholderAction<SiegeGame, GamePlayer, String, String, Object[]> action;
    private final boolean relational;

    Placeholder(String param, boolean relational, PlaceholderAction<SiegeGame, GamePlayer, String, String, Object[]> action) {
        this.param = param;
        this.action = action;
        this.relational = relational;
    }

    public boolean isRelational() {
        return relational;
    }

    public String getParameter() {
        return param;
    }

    public PlaceholderAction<SiegeGame, GamePlayer, String, String, Object[]> getAction() {
        return action;
    }

    private static Team getTeam(SiegeGame plugin, GamePlayer gamePlayer, String configKey) {
        if (configKey == null || configKey.isEmpty())
            return gamePlayer.hasTeam() ? gamePlayer.getTeam() : null;

        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return null;

        return gameMatch.getWorldGame().getTeam(configKey);
    }

    public static String getTeamYouOrEmpty(SiegeGame plugin, GamePlayer gamePlayer, String s) {
        Team team = getTeam(plugin, gamePlayer, s);
        if (team != null && gamePlayer.hasTeam() &&
                team.getConfigKey().equalsIgnoreCase(gamePlayer.getTeam().getConfigKey()))
            return "YOU";
        return "";
    }

    public static ChatColor getRelationalColor(Team one, Team two) {
        if (one == null || two == null)
            return ChatColor.WHITE;

        if (!one.getWorldGame().equals(two.getWorldGame()))
            return ChatColor.WHITE;

        if (one.equals(two))
            return ChatColor.DARK_AQUA;

        WorldGame worldGame = one.getWorldGame();

        if (worldGame.getTeams().size() == 2) {
            return ChatColor.RED;
        }

        return ChatColor.of(two.getColor());
    }
}
