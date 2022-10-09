package me.cedric.siegegame.territory;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.model.teams.TeamFactory;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class Territory {

    private final Polygon polygon;
    private final TeamFactory owner;

    public Territory(SiegeGamePlugin plugin, Polygon polygon, TeamFactory owner) {
        this.polygon = polygon;
        this.owner = owner;
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

    public TeamFactory getTeam() {
        return owner;
    }
}
