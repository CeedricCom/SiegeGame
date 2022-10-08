package me.cedric.siegegame.border;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.config.Settings;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class BorderListener implements Listener {

    private final SiegeGame plugin;

    public BorderListener(SiegeGame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        if (match == null)
            return;

        GamePlayer gamePlayer = match.getWorldGame().getPlayer(event.getPlayer().getUniqueId());
        //way too much logic
        //just call gamePlayer.getBorderHandler().update(event);
        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        if (!gameMatch.getWorld().equals(gamePlayer.getBukkitPlayer().getWorld()))
            return;

        BorderHandler handler = gamePlayer.getBorderHandler();
        Location location = gamePlayer.getBukkitPlayer().getLocation();

        if (event.hasChangedBlock()) {
            handler.getBorders().forEach(border -> handler.getBorderDisplay(border).update(gameMatch.getGameMap(), border));
        }

        if (handler.getCollidingBorder(location).stream().anyMatch(border -> !analyseMove(gamePlayer, border)))
            rollback(gamePlayer);

        gamePlayer.getBorderHandler().setLastSafe(event.getTo().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {

        Projectile projectile = event.getEntity();
        SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();

        if (gameMatch == null)
            return;

        if (!(projectile.getShooter() instanceof Player))
            return;

        List<String> projectiles = (List<String>) Settings.BLACKLISTED_PROJECTILES.getValue();

        if (!projectiles.contains(projectile.getType().name()))
            return;

        Player player = (Player) projectile.getShooter();
        GamePlayer gamePlayer = gameMatch.getWorldGame().getPlayer(player.getUniqueId());

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
        if (player.getBorderHandler().getLastSafe() == null) {
            if (player.hasTeam())
                player.getBukkitPlayer().teleport(player.getTeam().getSafeSpawn());
            return;
        }
        player.getBukkitPlayer().teleport(player.getBorderHandler().getLastSafe());
    }

    private boolean shouldCheck(GamePlayer gamePlayer) {
        Location lastSafe = gamePlayer.getBorderHandler().getLastSafe();

        if (lastSafe == null)
            return true;

        return !gamePlayer.getBukkitPlayer().hasPermission(Permissions.BORDER_BYPASS.getPermission());
    }

}
