package me.cedric.siegegame.config;

import org.bukkit.entity.EntityType;

import java.util.List;

public interface GameConfig {

    int getPointsPerKill();

    int getLevelsPerKill();

    int getPointsToEnd();

    int getRespawnTimer();

    int getSuperBreakerCooldown();

    int getSuperBreakerTimer();

    List<EntityType> getBlacklistedProjectiles();

    void reloadConfig();

    List<String> getMapIDs();

}
