package me.cedric.siegegame.model.map;

import me.cedric.siegegame.model.player.border.Border;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.model.teams.TeamFactory;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;

public class GameMap {

    private final FileMapLoader fileMapLoader;
    private final Set<TeamFactory> teamFactories;
    private final String displayName;
    private Border mapBorder;
    private final Location defaultSpawn;

    public GameMap(FileMapLoader fileMapLoader, String displayName, Set<TeamFactory> teamFactory, Border border, Location defaultSpawn) {
        this.fileMapLoader = fileMapLoader;
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
        fileMapLoader.unload();
    }

    public World getWorld() {
        return fileMapLoader.getWorld();
    }

    public boolean isWorldLoaded() {
        return fileMapLoader.isLoaded();
    }

    public boolean load() {
        if (!fileMapLoader.load())
            return false;

        defaultSpawn.setWorld(fileMapLoader.getWorld());
        fileMapLoader.getWorld().setSpawnLocation(defaultSpawn);
        BoundingBox box = mapBorder.getBoundingBox();
        mapBorder = new Border(new BoundingBox(fileMapLoader.getWorld(),
                (int) box.getMinX(), (int) box.getMinY(), (int) box.getMinZ(),
                (int) box.getMaxX(), (int) box.getMaxY(), (int) box.getMaxZ()));
        mapBorder.setAllowBlockChanges(false);
        for (TeamFactory team : teamFactories) {
            Location location = team.getSafeSpawn();
            location.setWorld(fileMapLoader.getWorld());
            team.getSafeArea().getBoundingBox().setWorld(fileMapLoader.getWorld());
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
