package me.cedric.siegegame.world;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.teams.Team;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;

public interface WorldGame {

    World getBukkitWorld();

    Location getDefaultSpawnPoint();

    void setDefaultSpawnLocation(Location location);

    String getConfigKey();

    Set<Team> getTeams();

    Team getTeam(String configKey);

    void addTeam(Team team);

    void removeTeam(Team team);
}
