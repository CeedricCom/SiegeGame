package me.cedric.siegegame.model;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.util.RandomString;
import org.bukkit.Location;
import org.bukkit.World;

public class SiegeGameMatch {

    private final SiegeGamePlugin plugin;
    private final WorldGame worldGame;
    private final GameMap gameMap;
    private final String matchID;

    public SiegeGameMatch(SiegeGamePlugin plugin, WorldGame worldGame, GameMap gameMap) {
        this.plugin = plugin;
        this.worldGame = worldGame;
        this.gameMap = gameMap;
        this.matchID = RandomString.make(5);
    }

    public WorldGame getWorldGame() {
        return worldGame;
    }

    public World getWorld() {
        return gameMap.getWorld();
    }

    public String getMatchID() {
        return matchID;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void startGame() {
        if (!gameMap.isWorldLoaded())
            gameMap.load();

        for (Team team : worldGame.getTeams()) {
            Location safeSpawn = team.getSafeSpawn();
            safeSpawn.setWorld(gameMap.getWorld());
            team.setSafeSpawn(safeSpawn);
        }

        worldGame.startGame();
    }

    public void endGame(boolean unload) {
        worldGame.endGame();
        if (unload)
            gameMap.unload();
    }
}
