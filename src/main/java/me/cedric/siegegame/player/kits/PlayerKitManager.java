package me.cedric.siegegame.player.kits;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerKitManager {

    private final Set<Kit> kits = new HashSet<>();
    private final UUID uuid;

    public PlayerKitManager(UUID uuid) {
        this.uuid = uuid;
    }

    public void addKit(Kit kit) {
        kits.add(kit);
    }

    public Kit getKit(String mapIdentifier) {
        // First look for this map's kit
        Kit kit = kits.stream().filter(kit1 -> kit1.getMapIdentifier().equalsIgnoreCase(mapIdentifier)).findAny().orElse(null);

        // if there is no kit for this map then look for a default one
        if (kit == null)
            return kits.stream().filter(kit2 -> kit2.getMapIdentifier().equalsIgnoreCase("allmaps")).findAny().orElse(null);

        return kit;
    }

    public Kit getKitExact(String mapIdentifier) {
        return kits.stream().filter(kit -> kit.getMapIdentifier().equalsIgnoreCase(mapIdentifier)).findAny().orElse(null);
    }

    public UUID getPlayerUUID() {
        return uuid;
    }

    public Set<Kit> getKits() {
        return new HashSet<>(kits);
    }
}
