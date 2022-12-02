package me.cedric.siegegame.player.border;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.border.blockers.EntityTracker;
import me.cedric.siegegame.player.border.blockers.ProjectileFollowTask;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PlayerBorderListener implements Listener {

    private final SiegeGamePlugin plugin;

    public PlayerBorderListener(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        if (!gameMatch.getWorld().equals(gamePlayer.getBukkitPlayer().getWorld()))
            return;

        PlayerBorderHandler handler = gamePlayer.getBorderHandler();
        Location location = gamePlayer.getBukkitPlayer().getLocation();

        if (event.hasChangedBlock()) {
            handler.getBorders().forEach(border -> handler.getBorderDisplay(border).update());
        }

        if (handler.getCollidingBorder(location).stream().anyMatch(border -> !analyseMove(gamePlayer, border)))
            rollback(gamePlayer);

        gamePlayer.getBorderHandler().getEntityTracker().setLastPosition(event.getPlayer().getUniqueId(), event.getTo().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        Projectile projectile = event.getEntity();
        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        if (!(projectile.getShooter() instanceof Player))
            return;

        List<EntityType> projectiles = plugin.getGameConfig().getBlacklistedProjectiles();
        if (!projectiles.contains(projectile.getType()))
            return;

        Player player = (Player) projectile.getShooter();
        GamePlayer gamePlayer = gameMatch.getWorldGame().getPlayer(player.getUniqueId());

        gamePlayer.getBorderHandler().getEntityTracker().trackEntity(projectile);
        ProjectileFollowTask followTask = new ProjectileFollowTask(plugin, gamePlayer, gameMatch, projectile);
        followTask.runTaskTimer(plugin, 0, 1);
    }

    private boolean analyseMove(GamePlayer player, Border gameBorder) {
        if (!BoundingBox.fromBukkit(player.getBukkitPlayer()).isColliding(gameBorder.getBoundingBox())) {
            if (!gameBorder.canLeave()) {
                return false;
            } else {
                if (!gameBorder.getBoundingBox().isColliding(BoundingBox.fromBukkit(player.getBukkitPlayer()))) {
                    return gameBorder.canLeave();
                }
            }
        }

        return true;
    }

    private void rollback(GamePlayer player) {
        EntityTracker entityTracker = player.getBorderHandler().getEntityTracker();
        if (entityTracker.getLastPosition(player.getUUID()) == null) {
            if (player.hasTeam())
                player.getBukkitPlayer().teleport(player.getTeam().getSafeSpawn());
            return;
        }
        player.getBukkitPlayer().teleport(entityTracker.getLastPosition(player.getUUID()));
    }

    private boolean shouldCheck(GamePlayer gamePlayer) {
        EntityTracker entityTracker = gamePlayer.getBorderHandler().getEntityTracker();
        Location lastSafe = entityTracker.getLastPosition(gamePlayer.getUUID());

        if (lastSafe == null)
            return true;

        return !gamePlayer.getBukkitPlayer().hasPermission(Permissions.BORDER_BYPASS.getPermission());
    }

}
