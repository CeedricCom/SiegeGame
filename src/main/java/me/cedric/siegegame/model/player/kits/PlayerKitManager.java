package me.cedric.siegegame.model.player.kits;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class PlayerKitManager {

    private final HashMap<String, Kit> kits = new HashMap<>();
    private final UUID uuid;

    public PlayerKitManager(UUID uuid) {
        this.uuid = uuid;
    }

    public void addKit(Kit kit) {
        if (kits.containsKey(kit.getMapIdentifier())) {
            Kit k = kits.get(kit.getMapIdentifier());
            k.setContents(kit.getContents());
            return;
        }

        kits.put(kit.getMapIdentifier(), kit);
    }

    public Kit getKit(String mapIdentifier) {
        // First look for this map's kit
        if (kits.containsKey(mapIdentifier))
            return kits.get(mapIdentifier);

        // if there is no kit for this map then look for a default one
        if (kits.containsKey("allmaps"))
            return kits.get("allmaps");

        return null;
    }

    public Kit getKitExact(String mapIdentifier) {
        return kits.get(mapIdentifier);
    }

    public void removeKit(String mapIdentifier) {
        kits.remove(mapIdentifier);
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public Collection<Kit> getKits() {
        return kits.values();
    }
}
