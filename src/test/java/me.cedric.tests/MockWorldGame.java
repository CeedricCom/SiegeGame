package me.cedric.tests;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class MockWorldGame implements WorldGame {

    private Location location = new Location(null, 0, 100, 0);
    private final Set<Team> teams = new HashSet<>();
    private String configKey = "mockgame";

    @Override
    public World getBukkitWorld() {
        return null;
    }

    @Override
    public Location getDefaultSpawnPoint() {
        return location;
    }

    @Override
    public void setDefaultSpawnLocation(Location location) {
        this.location = location;
    }

    public void clear() {
        teams.clear();
    }

    @Override
    public String getConfigKey() {
        return configKey;
    }

    @Override
    public ImmutableSet<Team> getTeams() {
        return ImmutableSet.copyOf(teams);
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
}
