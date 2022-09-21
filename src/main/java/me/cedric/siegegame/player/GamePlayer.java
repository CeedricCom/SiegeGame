package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.border.BoundingBox;
import me.cedric.siegegame.border.FakeBlockManager;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GamePlayer {

    private final UUID uuid;
    private final BorderHandler borderHandler;
    private Team team;
    private final SiegeGame plugin;
    private Location lastSafe = null;

    public GamePlayer(UUID uuid, SiegeGame plugin) {
        this.uuid = uuid;
        this.team = null;
        this.plugin = plugin;
        this.borderHandler = new BorderHandler(plugin, this, getWorldGame());
    }

    private WorldGame getWorldGame() {
        if (team == null)
            return plugin.getGameManager().getWorldGame(getBukkitPlayer().getWorld());
        return team.getWorldGame();
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Team getTeam() {
        return team;
    }

    public boolean hasTeam() {
        return team != null;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public UUID getUUID() {
        return uuid;
    }

    public BorderHandler getBorderHandler() {
        return borderHandler;
    }
}
