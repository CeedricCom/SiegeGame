package me.cedric.siegegame.player;

import me.cedric.siegegame.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData {

    private final UUID uuid;

    private Team team;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        this.team = null;
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public UUID getUUID() {
        return uuid;
    }
}
