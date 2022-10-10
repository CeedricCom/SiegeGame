package me.cedric.siegegame.model;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.territory.TerritoryListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class SiegeGameMatch implements Listener {

    private final SiegeGamePlugin plugin;
    private final WorldGame worldGame;
    private final GameMap gameMap;
    private final String identifier;
    private boolean waitForLoad = false;

    public SiegeGameMatch(SiegeGamePlugin plugin, WorldGame worldGame, GameMap gameMap, String identifier) {
        this.plugin = plugin;
        this.worldGame = worldGame;
        this.gameMap = gameMap;
        this.identifier = identifier;

        for (Team team : worldGame.getTeams()) {
            TerritoryListener blockers = new TerritoryListener(worldGame, team.getTerritory());
            plugin.getServer().getPluginManager().registerEvents(blockers, plugin);
        }
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public WorldGame getWorldGame() {
        return worldGame;
    }

    public World getWorld() {
        return gameMap.getWorld();
    }

    public String getIdentifier() {
        return identifier;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void shutdown() {
        for (GamePlayer gamePlayer : worldGame.getDeathManager().getDeadPlayers())
            worldGame.getDeathManager().revivePlayer(gamePlayer);

        for (SuperItem superItem : worldGame.getSuperItemManager().getSuperItems())
            superItem.remove();

        if (gameMap.isWorldLoaded())
            gameMap.unload();
    }

    public void startGame() {
        System.out.println("here");
        if (!gameMap.isWorldLoaded())
            gameMap.load();

        for (Team team : worldGame.getTeams()) {
            Location safeSpawn = team.getSafeSpawn();
            System.out.println("before set world");
            safeSpawn.setWorld(gameMap.getWorld());
            System.out.println("set world done");
            System.out.println("-------");
        }

        worldGame.startGame();
    }

    public void endGame() {
        worldGame.endGame();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        System.out.println("On event: wait for load: " + waitForLoad);
        if (!waitForLoad)
            return;
        System.out.println("On event: wait for load: " + waitForLoad);
        if (!gameMap.getWorld().equals(world))
            return;

        System.out.println("game map get world equals world");

        startGame();

        if (plugin.getGameManager().getLastMatch() != null)
            plugin.getGameManager().getLastMatch().getGameMap().resetMap();

        waitForLoad = false;
        System.out.println("wait for load false");
    }
}
