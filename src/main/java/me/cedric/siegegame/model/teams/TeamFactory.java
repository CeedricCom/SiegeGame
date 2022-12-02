package me.cedric.siegegame.model.teams;

import me.cedric.siegegame.player.border.Border;
import me.cedric.siegegame.territory.Territory;
import org.bukkit.Location;

import java.awt.Color;

public class TeamFactory {

    private Territory territory;
    private Location safeSpawn;
    private final Border safeArea;
    private final String configKey;
    private final String name;
    private final Color color;

    public TeamFactory(Border safeArea, Location safeSpawn, String name, String configKey, Color color) {
        this.safeArea = safeArea;
        this.safeSpawn = safeSpawn;
        this.name = name;
        this.configKey = configKey;
        this.color = color;
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public Border getSafeArea() {
        return safeArea;
    }

    public Location getSafeSpawn() {
        return safeSpawn.clone();
    }

    public void setSafeSpawn(Location safeSpawn) {
        this.safeSpawn = safeSpawn;
    }

    public String getName() {
        return name;
    }

    public String getConfigKey() {
        return configKey;
    }

    public Color getColor() {
        return color;
    }


}
