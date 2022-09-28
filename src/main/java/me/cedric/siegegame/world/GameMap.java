package me.cedric.siegegame.world;

import org.bukkit.World;

public interface GameMap {

    String getDisplayName();

    boolean load();

    void unload();

    boolean restoreFromSource();

    boolean isLoaded();

    World getWorld();

}
