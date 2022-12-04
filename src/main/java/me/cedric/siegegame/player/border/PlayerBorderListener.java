package me.cedric.siegegame.player.border;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.border.blockers.EntityTracker;
import me.cedric.siegegame.player.border.blockers.ProjectileFollowTask;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.ChatColor;
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

        if (event.hasChangedBlock())
            handler.getBorders().forEach(border -> handler.getBorderDisplay(border).update());

        if (handler.getBorders().stream().anyMatch(border -> !analyseMove(event.getTo(), border)))
            rollback(gamePlayer);

        gamePlayer.getBorderHandler().getEntityTracker().setLastPosition(event.getPlayer().getUniqueId(), event.getTo().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        Projectile projectile = event.getEntity();
        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        if (!(projectile.getShooter() instanceof Player player))
            return;

        List<EntityType> projectiles = plugin.getGameConfig().getBlacklistedProjectiles();
        if (!projectiles.contains(projectile.getType()))
            return;

        GamePlayer gamePlayer = gameMatch.getWorldGame().getPlayer(player.getUniqueId());

        gamePlayer.getBorderHandler().getEntityTracker().trackEntity(projectile);
        ProjectileFollowTask followTask = new ProjectileFollowTask(plugin, gamePlayer, gameMatch, projectile);
        followTask.runTaskTimer(plugin, 0, 1);
    }

    private boolean analyseMove(Location location, Border border) {
        if (border.canLeave()) // if you can leave the border, movement is always good
            return true;

        // If you are inside a border and it is not inverse (regular border), movement is good
        // Otherwise, you are inside an inverse border, movement is bad
        if (border.getBoundingBox().isColliding(location))
            return !border.isInverse();

        // If we get here player is outside any borders and should be teleported to last safe
        return false;
    }

    private void rollback(GamePlayer player) {
        EntityTracker entityTracker = player.getBorderHandler().getEntityTracker();
        player.getBukkitPlayer().teleport(entityTracker.getLastPosition(player.getUUID()));
        player.getBukkitPlayer().sendMessage(ChatColor.RED + "You have been rolled back for getting through a border.");
    }

    private boolean shouldCheck(GamePlayer gamePlayer) {
        EntityTracker entityTracker = gamePlayer.getBorderHandler().getEntityTracker();
        Location lastSafe = entityTracker.getLastPosition(gamePlayer.getUUID());

        if (lastSafe == null)
            return true;

        return !gamePlayer.getBukkitPlayer().hasPermission(Permissions.BORDER_BYPASS.getPermission());
    }

}
