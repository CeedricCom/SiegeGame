package me.cedric.siegegame.player;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class LastSafe<T extends Entity> {

    private Location location;
    private final T t;

    public LastSafe(Location location, T t) {
        this.location = location;
        this.t = t;
    }

    public T getEntity() {
        return t;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
