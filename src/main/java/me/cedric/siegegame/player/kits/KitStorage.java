package me.cedric.siegegame.player.kits;

import me.cedric.siegegame.SiegeGamePlugin;

import java.util.HashMap;
import java.util.UUID;

public class KitStorage {

    private final HashMap<UUID, PlayerKitManager> kits = new HashMap<>();
    private final SiegeGamePlugin plugin;

    public KitStorage(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    public void addKitManager(UUID uuid) {
        if (kits.containsKey(uuid))
            return;

        PlayerKitManager playerKitManager = new PlayerKitManager(uuid);
        kits.put(uuid, playerKitManager);
    }

    public boolean hasKitManager(UUID uuid) {
        return kits.containsKey(uuid);
    }

    public PlayerKitManager getKitManager(UUID uuid) {
        return kits.get(uuid);
    }

}
