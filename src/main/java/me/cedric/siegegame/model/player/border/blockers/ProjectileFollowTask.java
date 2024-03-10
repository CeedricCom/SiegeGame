package me.cedric.siegegame.model.player.border.blockers;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.player.border.Border;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.model.player.border.PlayerBorderHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ProjectileFollowTask extends BukkitRunnable {
    
    private final GamePlayer player;
    private final SiegeGamePlugin plugin;
    private final SiegeGameMatch gameMatch;
    private final EntityTracker entityTracker;
    private final Projectile projectile;

    public ProjectileFollowTask(SiegeGamePlugin plugin, GamePlayer player, SiegeGameMatch gameMatch, Projectile projectile) {
        this.player = player;
        this.plugin = plugin;
        this.gameMatch = gameMatch;
        this.projectile = projectile;
        this.entityTracker = player.getBorderHandler().getEntityTracker();
    }

    @Override
    public void run() {
        PlayerBorderHandler playerBorderHandler = player.getBorderHandler();
        Location lastSafe = entityTracker.getLastPosition(projectile.getUniqueId()).clone();
        for (Border border : playerBorderHandler.getBorders()) {
            if (border.canLeave())
                continue;

            if (!checkBorder(border, lastSafe.toVector()))
                deleteProjectileAndCancel(projectile, lastSafe, playerBorderHandler);
        }

        // changed block
        if (!projectile.getLocation().equals(lastSafe))
            entityTracker.setLastPosition(projectile.getUniqueId(), projectile.getLocation());

        if (projectile.isDead()) {
            entityTracker.stopTracking(projectile.getUniqueId());
            cancel();
        }
    }

    private boolean checkBorder(Border safeArea, Vector lastSafe) {
        int distance = safeArea.isInverse() ? 3 : -3;
        if (safeArea.getBoundingBox().clone().expand(distance).isCollidingIgnoreInverse(lastSafe) && safeArea.isInverse()) {
            return true;
        }
        return safeArea.getBoundingBox().clone().expand(distance).isCollidingIgnoreInverse(lastSafe) && !safeArea.isInverse();
    }

    private void deleteProjectileAndCancel(Projectile projectile, Location lastSafe, PlayerBorderHandler playerBorderHandler) {
        if (projectile instanceof EnderPearl) {
            lastSafe.setYaw(player.getBukkitPlayer().getLocation().getYaw());
            lastSafe.setPitch(player.getBukkitPlayer().getLocation().getPitch());
            player.getBukkitPlayer().teleport(lastSafe);
        }

        entityTracker.stopTracking(projectile.getUniqueId());
        projectile.remove();
        this.cancel();
        player.getBukkitPlayer().sendMessage(ChatColor.RED + "You cannot use projectiles near a border or safe area");
    }
}
