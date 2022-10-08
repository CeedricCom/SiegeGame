package me.cedric.siegegame.territory;

import me.cedric.siegegame.model.map.GameMap;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class Polygon {

    private final Set<Vector2D> vectors = new HashSet<>();
    private GameMap gameMap;

    public Polygon(GameMap gameMap, Vector2D p1, Vector2D p2) {
        this.gameMap = gameMap;
        addSquare(p1, p2);
    }

    public void addSquare(Vector2D p1, Vector2D p2) {
        Vector2D min = new Vector2D(Math.min(p1.getX(), p2.getX()), Math.min(p1.getZ(), p2.getZ()));
        Vector2D max = new Vector2D(Math.max(p1.getX(), p2.getX()), Math.max(p1.getZ(), p2.getZ()));

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                vectors.add(new Vector2D(x, z));
            }
        }
    }

    public void clear() {
        vectors.clear();
    }

    public boolean isColliding(Vector2D v, World world) {
        return vectors.stream().anyMatch(vector -> vector.getX() == v.getX() && vector.getZ() == v.getZ() && world.equals(gameMap.getWorld()));
    }
}
