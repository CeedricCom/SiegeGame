package me.cedric.siegegame.player.kits;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.GameManager;
import me.cedric.siegegame.player.GamePlayer;

import java.util.HashMap;
import java.util.UUID;

public class KitStorage {

    private final HashMap<UUID, PlayerKitManager> kits = new HashMap<>();
    private final SiegeGamePlugin plugin;

    public KitStorage(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    private void addKitManager(UUID uuid) {
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

    public void assignKitManager(GamePlayer gamePlayer) {
        if (!hasKitManager(gamePlayer.getUUID()))
            addKitManager(gamePlayer.getUUID());
        PlayerKitManager kitManager = getKitManager(gamePlayer.getUUID());
        gamePlayer.setPlayerKitManager(kitManager);
    }

    public void saveKits(GamePlayer gamePlayer) {
        if (!kits.containsKey(gamePlayer.getUUID()))
            kits.put(gamePlayer.getUUID(), gamePlayer.getPlayerKitManager());
        else
            kits.replace(gamePlayer.getUUID(), gamePlayer.getPlayerKitManager());
    }

}
