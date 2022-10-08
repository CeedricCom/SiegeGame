package me.cedric.siegegame.model.map;

import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.border.BoundingBox;
import me.cedric.siegegame.model.teams.TeamFactory;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;

public class GameMap {

    private final LocalGameMap localGameMap;
    private final Set<TeamFactory> teamFactories;
    private final String displayName;
    private Border mapBorder;
    private final Location defaultSpawn;

    public GameMap(LocalGameMap localGameMap, String displayName, Set<TeamFactory> teamFactory, Border border, Location defaultSpawn) {
        this.localGameMap = localGameMap;
        this.teamFactories = teamFactory;
        this.displayName = displayName;
        this.mapBorder = border;
        this.defaultSpawn = defaultSpawn;
    }

    public TeamFactory getTeam(String configKey) {
        return teamFactories.stream().filter(factory -> factory.getConfigKey().equalsIgnoreCase(configKey)).findAny().orElse(null);
    }

    public Border getMapBorder() {
        return mapBorder;
    }

    public Location getDefaultSpawn() {
        return new Location(getWorld(), defaultSpawn.getBlockX(), defaultSpawn.getY(), defaultSpawn.getBlockZ(), defaultSpawn.getYaw(), defaultSpawn.getPitch());
    }

    public void unload() {
        localGameMap.unload();
    }

    public World getWorld() {
        if (!localGameMap.isLoaded())
            localGameMap.load();
        return localGameMap.getWorld();
    }

    public boolean isWorldLoaded() {
        return localGameMap.isLoaded();
    }

    public void resetMap() {
        localGameMap.restoreFromSource();
    }

    public boolean load() {
        if (!localGameMap.load())
            return false;

        defaultSpawn.setWorld(localGameMap.getWorld());
        localGameMap.getWorld().setSpawnLocation(defaultSpawn);
        BoundingBox box = mapBorder.getBoundingBox();
        mapBorder = new Border(new BoundingBox(localGameMap.getWorld(),
                (int) box.getMinX(), (int) box.getMinY(), (int) box.getMinZ(),
                (int) box.getMaxX(), (int) box.getMaxY(), (int) box.getMaxZ()));

        for (TeamFactory team : teamFactories) {
            Location location = team.getSafeSpawn();
            location.setWorld(localGameMap.getWorld());
            team.getSafeArea().getBoundingBox().setWorld(localGameMap.getWorld());
            team.setSafeSpawn(location);
        }

        return true;
    }

    public void addTeam(TeamFactory team) {
        teamFactories.add(team);
    }

    public String getDisplayName() {
        return displayName;
    }
}
