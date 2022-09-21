package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGame;

import java.util.*;

public final class PlayerManager {

    private final Map<UUID, GamePlayer> players = new HashMap<>();
    private final SiegeGame plugin;

    public PlayerManager(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(UUID uuid) {
        players.put(uuid, new GamePlayer(uuid, plugin));
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public GamePlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public Set<UUID> getPlayers() {
        return new HashSet<>(players.keySet());
    }

    public void clear() {
        players.clear();
    }

}
