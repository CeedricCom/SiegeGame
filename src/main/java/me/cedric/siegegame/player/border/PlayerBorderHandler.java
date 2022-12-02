package me.cedric.siegegame.player.border;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.border.blockers.EntityTracker;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.fake.FakeBorderWall;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerBorderHandler {

    private final SiegeGamePlugin plugin;
    private final GamePlayer player;
    private final EntityTracker entityTracker;
    private final Map<Border, FakeBorderWall> borders = new HashMap<>();

    public PlayerBorderHandler(SiegeGamePlugin plugin, GamePlayer player) {
        this.plugin = plugin;
        this.player = player;
        this.entityTracker = new EntityTracker();
    }

    public FakeBorderWall getBorderDisplay(Border border) {
        return borders.get(border);
    }

    public void addBorder(Border border) {
        borders.put(border, new FakeBorderWall(player, border, 10, 5, Material.RED_STAINED_GLASS));
    }

    public Set<Border> getBorders() {
        return new HashSet<>(borders.keySet());
    }

    public void removeBorder(Border key) {
        borders.remove(key);
    }

    public boolean isCollidingAnyBorder(Location location) {
        for (Map.Entry<Border, FakeBorderWall> entry : borders.entrySet()) {
            Border border = entry.getKey();
            if (border.getBoundingBox().isColliding(location.clone().toVector()))
                return true;
        }
        return false;
    }

    public Set<Border> getCollidingBorder(Location location) {
        Set<Border> b = new HashSet<>();
        for (Map.Entry<Border, FakeBorderWall> entry : borders.entrySet()) {
            Border border = entry.getKey();

            BoundingBox borderBox = border.getBoundingBox();
            int expand = border.isInverse() ? 1 : -1;

            if (borderBox.clone().expand(expand).isColliding(location))
                b.add(entry.getValue().getBorder());
        }

        return b;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public void clear() {
        borders.clear();
    }

    public EntityTracker getEntityTracker() {
        return entityTracker;
    }
}
