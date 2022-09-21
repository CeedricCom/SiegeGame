package me.cedric.siegegame.world;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.EmptyTownException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.teams.Team;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class WorldGameImpl implements WorldGame {

    private Location defaultSpawnLocation;
    private final Set<Team> teams = new HashSet<>();
    private final String configKey;
    private final World bukkitWorld;
    private Border border;

    public WorldGameImpl(String configKey, World world, Border border, Location defaultSpawnLocation) {
        this.defaultSpawnLocation = defaultSpawnLocation;
        this.configKey = configKey;
        this.bukkitWorld = world;
        this.border = border;
    }

    @Override
    public World getBukkitWorld() {
        return bukkitWorld;
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

    public void swapTeam(GamePlayer player, Team newTeam) throws NotRegisteredException, EmptyTownException, AlreadyRegisteredException {
        Team oldTeam = player.getTeam();
        if (oldTeam != null)
            player.getTeam().removePlayer(player);

        Resident resident = TownyAPI.getInstance().getResident(player.getUUID());
        oldTeam.getTeamTown().removeResident(resident);

        resident.setTown(newTeam.getTeamTown());
        newTeam.addPlayer(player);
    }

}
