package me.cedric.siegegame.model;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.territory.TerritoryListener;
import org.bukkit.World;

public class SiegeGameMatch {

    private final SiegeGame plugin;
    private final WorldGame worldGame;
    private final GameMap gameMap;
    private final String configKey;

    public SiegeGameMatch(SiegeGame plugin, WorldGame worldGame, GameMap gameMap, String configKey) {
        this.plugin = plugin;
        this.worldGame = worldGame;
        this.gameMap = gameMap;
        this.configKey = configKey;

        for (Team team : worldGame.getTeams()) {
            TerritoryListener blockers = new TerritoryListener(worldGame, team.getTerritory());
            plugin.getServer().getPluginManager().registerEvents(blockers, plugin);
        }
    }

    public WorldGame getWorldGame() {
        return worldGame;
    }

    public World getWorld() {
        return gameMap.getWorld();
    }

    public String getConfigKey() {
        return configKey;
    }

    public GameMap getGameMap() {
        return gameMap;
    }
}
