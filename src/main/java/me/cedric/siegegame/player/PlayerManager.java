package me.cedric.siegegame.player;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGame;

import java.util.*;

public final class PlayerManager {

    private final Map<UUID, PlayerData> players = new HashMap<>();
    private final SiegeGame plugin;

    public PlayerManager(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(UUID uuid) {
        players.put(uuid, new PlayerData(uuid));
    }

    public void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public PlayerData getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public Set<UUID> getPlayers() {
        return new HashSet<>(players.keySet());
    }

    public void clear() {
        players.clear();
    }

}
