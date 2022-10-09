package me.cedric.siegegame.border;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.border.fake.FakeBlockManager;
import me.cedric.siegegame.border.fake.FakeBorderWall;
import me.cedric.siegegame.border.lastsafe.EntityTracker;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class BorderHandler {

    private final SiegeGamePlugin plugin;
    private final GamePlayer player;
    private final FakeBlockManager fakeBlockManager;
    private final EntityTracker entityTracker;
    private final Map<Border, BorderDisplay> borders = new HashMap<>();

    public BorderHandler(SiegeGamePlugin plugin, GamePlayer player) {
        this.plugin = plugin;
        this.player = player;
        this.entityTracker = new EntityTracker();
        this.fakeBlockManager = new FakeBlockManager(plugin, player.getBukkitPlayer());
    }

    public FakeBlockManager getFakeBlockManager() {
        return fakeBlockManager;
    }

    public BorderDisplay getBorderDisplay(Border border) {
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
        for (Map.Entry<Border, BorderDisplay> entry : borders.entrySet()) {
            Border border = entry.getKey();
            if (border.getBoundingBox().isColliding(location.clone().toVector()))
                return true;
        }
        return false;
    }

    public Set<Border> getCollidingBorder(Location location) {
        Set<Border> b = new HashSet<>();
        for (Map.Entry<Border, BorderDisplay> entry : borders.entrySet()) {
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
