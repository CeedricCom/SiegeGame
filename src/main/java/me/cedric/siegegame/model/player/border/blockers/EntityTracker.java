package me.cedric.siegegame.model.player.border.blockers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityTracker {

    private final Map<UUID, Location> lastSafes = new HashMap<>();

    public void trackEntity(Entity entity) {
        lastSafes.put(entity.getUniqueId(), entity.getLocation());
    }

    public void stopTracking(UUID uuid) {
        lastSafes.remove(uuid);
    }

    public void setLastPosition(UUID uuid, Location location) {
        Location withInts = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (!lastSafes.containsKey(uuid)) {
            lastSafes.put(uuid, withInts);
            return;
        }
        lastSafes.replace(uuid, withInts);
    }

    public boolean isTracking(UUID uuid) {
        return lastSafes.containsKey(uuid);
    }

    public Location getLastPosition(UUID uuid) {
        return lastSafes.get(uuid);
    }

}
