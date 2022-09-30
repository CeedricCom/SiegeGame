package me.cedric.siegegame.display.placeholderapi;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
        Team team = getTeam(siegeGame, gamePlayer, s);
        if (team != null && gamePlayer.hasTeam() &&
                team.getConfigKey().equalsIgnoreCase(gamePlayer.getTeam().getConfigKey()))
            return "YOU";
        return "";
    }),

    MAP_NAME("map_name", false, (siegeGame, gamePlayer, s, extra) -> {
        World world = gamePlayer.getBukkitPlayer().getWorld();
        WorldGame worldGame = siegeGame.getGameManager().getWorldGame(world);
        return worldGame == null ? "" : worldGame.getGameMap().getDisplayName();
    }),

    SUPER_ITEM("super_item_or_nothing", false, (siegeGame, player, s, extra) -> {
        SuperItem item = siegeGame.getSuperItemManager().getSuperItems().stream()
                .filter(superItem -> superItem.getOwner().equals(player))
                .findAny()
                .orElse(null);
        if (item == null)
            return "";

        return item.getDisplayName();
    }),

    RELATIONAL_COLOR("rel_player_color", true, (siegeGame, gamePlayer, s, extra) -> {
        if (!(extra[0] instanceof Player))
            return "";

        Player two = (Player) extra[0];
        GamePlayer gamePlayer2 = siegeGame.getPlayerManager().getPlayer(two.getUniqueId());

        if (gamePlayer.getUUID().equals(two.getUniqueId()))
            return ChatColor.DARK_AQUA.toString();

        if (!gamePlayer.hasTeam()) {
            if (!gamePlayer.hasTeam())
                return ChatColor.WHITE.toString();
            return net.md_5.bungee.api.ChatColor.of(gamePlayer2.getTeam().getColor()).toString();
        }

        WorldGame worldGame = siegeGame.getGameManager().getWorldGame(gamePlayer.getBukkitPlayer().getWorld());

        if (worldGame.getTeams().size() == 2) {
            if (gamePlayer2.hasTeam() && gamePlayer.hasTeam()) {
                if (gamePlayer2.getTeam().equals(gamePlayer.getTeam()))
                    return ChatColor.DARK_AQUA.toString();
                return ChatColor.RED.toString();
            }
        }

        if (gamePlayer2.hasTeam())
            return net.md_5.bungee.api.ChatColor.of(gamePlayer.getTeam().getColor()).toString();

        return ChatColor.WHITE.toString();
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

        WorldGame worldGame = plugin.getGameManager().getWorldGame(gamePlayer.getBukkitPlayer().getWorld());

        if (worldGame == null)
            return null;

        return worldGame.getTeam(configKey);
    }
}
