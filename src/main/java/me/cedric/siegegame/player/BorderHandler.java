package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.border.FakeBlockManager;
import me.cedric.siegegame.border.FakeBorderWall;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BorderHandler {

    private final SiegeGame plugin;
    private final WorldGame worldGame;
    private final GamePlayer player;
    private final FakeBlockManager fakeBlockManager;
    private final FakeBorderWall fakeBorderWall;
    private LastSafe<Player> playerLastSafe;
    private List<LastSafe<Projectile>> playerProjectiles;
    private Border border;

    public BorderHandler(SiegeGame plugin, GamePlayer player, WorldGame worldGame) {
        this.plugin = plugin;
        this.worldGame = worldGame;
        this.player = player;
        this.fakeBorderWall = new FakeBorderWall(player, 10, 5, Material.GLASS);
        this.fakeBlockManager = new FakeBlockManager(plugin, player.getBukkitPlayer());
        this.playerProjectiles = new ArrayList<>();
        this.playerLastSafe = new LastSafe<>(player.getBukkitPlayer().getLocation(), player.getBukkitPlayer());
    }

    public FakeBlockManager getFakeBlockManager() {
        return fakeBlockManager;
    }

    public FakeBorderWall getFakeBorderWall() {
        return fakeBorderWall;
    }

    public Border getBorder() {
        return border == null ? worldGame.getBorder() : border;
    }

    private boolean isAtSpawn() {
        return false;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public WorldGame getWorldGame() {
        return worldGame;
    }

    public void setBorder(Border border) {
        this.border = border;
    }

    public Location getLastSafe() {
        return playerLastSafe.getLocation();
    }

    public void setLastSafe(Player player, Location lastSafe) {
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


}
