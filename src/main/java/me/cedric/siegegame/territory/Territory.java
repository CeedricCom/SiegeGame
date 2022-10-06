package me.cedric.siegegame.territory;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.territory.polygon.Polygon;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class Territory {

    private final SiegeGame plugin;
    private final Polygon polygon;
    private final Team team;
    private final WorldGame worldGame;
    private final TerritoryBlockers territoryBlockers;

    public Territory(SiegeGame plugin, WorldGame worldGame, Team owningTeam) {
        this.team = owningTeam;
        this.plugin = plugin;
        this.worldGame = worldGame;
        this.polygon = new Polygon(plugin, worldGame.getGameMap(), team.getConfigKey() + "_" + owningTeam.getConfigKey());

        this.territoryBlockers = new TerritoryBlockers(plugin, this);
        plugin.getServer().getPluginManager().registerEvents(territoryBlockers, plugin);

        load();
    }

    public boolean isInside(World world, int x, int z) {
        return polygon.isColliding(new Vector2D(x, z), world);
    }

    public boolean isInside(Location location) {
        return isInside(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    public boolean isInside(Player player) {
        return isInside(player.getLocation());
    }

    public boolean isInside(GamePlayer player) {
        return isInside(player.getBukkitPlayer());
    }

    public void addSquare(Vector2D p1, Vector2D p2) {
        this.polygon.addSquare(p1, p2);
    }

    public Team getTeam() {
        return team;
    }

    public void save() {
        polygon.save();
    }

    public void load() {
        polygon.load();
    }
}
