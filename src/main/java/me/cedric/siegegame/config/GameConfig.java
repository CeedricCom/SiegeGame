package me.cedric.siegegame.config;

import org.bukkit.entity.EntityType;

import java.util.List;

public interface GameConfig {

    int getPointsPerKill();

    int getLevelsPerKill();

    int getPointsToEnd();

    int getRespawnTimer();

    List<EntityType> getBlacklistedProjectiles();

    void reloadConfig();

    List<String> getMapIDs();

}
