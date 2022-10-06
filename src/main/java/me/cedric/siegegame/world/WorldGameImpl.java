package me.cedric.siegegame.world;

import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.teams.Team;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldGameImpl implements WorldGame {

    private final Set<Team> teams = new HashSet<>();
    private final String configKey;
    private final GameMap gameMap;
    private Border border;
    private Location defaultSpawnLocation;
    List<SuperItem> superItems = new ArrayList<>();

    public WorldGameImpl(String configKey, GameMap gameMap, Border border, Location defaultSpawnLocation) {
        this.defaultSpawnLocation = defaultSpawnLocation;
        this.configKey = configKey;
        this.gameMap = gameMap;
        this.border = border;
    }

    @Override
    public GameMap getGameMap() {
        return gameMap;
    }

    @Override
    public World getBukkitWorld() {
        return gameMap.getWorld();
    }

    @Override
    public Location getDefaultSpawnPoint() {
        return defaultSpawnLocation;
    }

    @Override
    public Set<Team> getTeams() {
        return teams;
    }

    @Override
    public Team getTeam(String configKey) {
        for (Team team : teams) {
            if (team.getConfigKey().equalsIgnoreCase(configKey))
                return team;
        }
        return null;
    }

    @Override
    public void addTeam(Team team) {
        teams.add(team);
    }

    @Override
    public void removeTeam(Team team) {
        teams.remove(team);
    }

    @Override
    public Border getBorder() {
        return border;
    }

    @Override
    public void setBorder(Border border) {
        this.border = border;
    }

    @Override
    public List<SuperItem> getSuperItems() {
        return new ArrayList<>(superItems);
    }

    @Override
    public void addSuperItem(SuperItem item) {
        superItems.add(item);
    }

    @Override
    public void setDefaultSpawnLocation(Location defaultSpawnLocation) {
        this.defaultSpawnLocation = defaultSpawnLocation;
    }

    public void addTeams(Set<Team> teams) {
        this.teams.addAll(teams);
    }

    @Override
    public String getConfigKey() {
        return configKey;
    }

    public void swapTeam(GamePlayer player, Team newTeam) {
        Team oldTeam = player.getTeam();
        if (oldTeam != null)
            player.getTeam().removePlayer(player);

        newTeam.addPlayer(player);
    }

}
