package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.border.BorderDisplay;
import me.cedric.siegegame.border.BoundingBox;
import me.cedric.siegegame.border.FakeBlockManager;
import me.cedric.siegegame.border.FakeBorderWall;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BorderHandler {

    private final SiegeGame plugin;
    private final GamePlayer player;
    private final FakeBlockManager fakeBlockManager;
    private final LastSafe<Player> playerLastSafe;
    private final List<LastSafe<Projectile>> playerProjectiles;
    private final Map<Border, BorderDisplay> borders = new HashMap<>();

    public BorderHandler(SiegeGame plugin, GamePlayer player) {
        this.plugin = plugin;
        this.player = player;
        this.fakeBlockManager = new FakeBlockManager(plugin, player.getBukkitPlayer());
        this.playerProjectiles = new ArrayList<>();
        this.playerLastSafe = new LastSafe<>(player.getBukkitPlayer().getLocation(), player.getBukkitPlayer());
    }

    public FakeBlockManager getFakeBlockManager() {
        return fakeBlockManager;
    }

    public BorderDisplay getBorderDisplay(Border border) {
        return borders.get(border);
    }

    public void addBorder(Border border) {
        borders.put(border, new FakeBorderWall(player, border, 10, 5, Material.GLASS));
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
            int expand = borderBox.isInverse() ? 1 : -1;

            if (borderBox.clone().expand(expand).isColliding(location))
                b.add(entry.getValue().getBorder());
        }

        return b;
    }

    private boolean isAtSpawn() {
        return false;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public Location getLastSafe() {
        return playerLastSafe.getLocation();
    }

    public void setLastSafe(Location lastSafe) {
        playerLastSafe.setLocation(lastSafe);
    }

    public void trackProjectile(Projectile projectile) {
        playerProjectiles.add(new LastSafe<>(projectile.getLocation(), projectile));
    }

    public void setProjectileLastSafe(UUID uuid, Location location) {
        LastSafe<Projectile> p = getProjectile(uuid);
        if (p == null)
            return;
        p.setLocation(location);
    }

    public boolean isTrackingProjectile(UUID uuid) {
        return getProjectile(uuid) != null;
    }

    public Location getProjectileLastSafe(UUID uuid) {
        return getProjectile(uuid).getLocation();
    }

    public void stopTrackingProjectile(UUID uuid) {
        playerProjectiles.removeIf(projectileLastSafe -> projectileLastSafe.getEntity().getUniqueId().equals(uuid));
    }

    private LastSafe<Projectile> getProjectile(UUID uuid) {
        return playerProjectiles.stream().filter(projectileLastSafe -> projectileLastSafe.getEntity().getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public void clear() {
        borders.clear();
        playerProjectiles.clear();
    }


}
