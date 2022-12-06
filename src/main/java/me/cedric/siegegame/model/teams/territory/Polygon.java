package me.cedric.siegegame.model.teams.territory;

import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.util.Box2D;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class Polygon {

    private final Set<Box2D> boxes = new HashSet<>();
    private final GameMap gameMap;

    public Polygon(GameMap gameMap, Vector2D p1, Vector2D p2) {
        this.gameMap = gameMap;
        addSquare(p1, p2);
    }

    public void addSquare(Vector2D p1, Vector2D p2) {
        Vector2D min = new Vector2D(Math.min(p1.getX(), p2.getX()), Math.min(p1.getZ(), p2.getZ()));
        Vector2D max = new Vector2D(Math.max(p1.getX(), p2.getX()), Math.max(p1.getZ(), p2.getZ()));

        Box2D box = new Box2D(min, max);
        boxes.add(box);
    }

    public void clear() {
        boxes.clear();
    }

    public boolean isColliding(Vector2D v, World world) {
        return boxes.stream().anyMatch(box -> box.isColliding(v) && world.equals(gameMap.getWorld()));
    }
}
