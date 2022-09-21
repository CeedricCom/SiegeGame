package me.cedric.siegegame.border;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.permissions.Permissions;
import me.cedric.siegegame.player.BorderHandler;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderListener implements Listener {

    private final SiegeGame plugin;

    public BorderListener(SiegeGame plugin) {
        this.plugin = plugin;
    }

    // TODO: - Fix pearls being able to go through walls

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(event.getPlayer().getUniqueId());

        if (gamePlayer == null)
            return;

        if (!shouldCheck(gamePlayer))
            return;

        WorldGame worldGame = plugin.getGameManager().getWorldGame(gamePlayer.getBukkitPlayer().getWorld());

        if (worldGame == null)
            return;

        if (!worldGame.getBukkitWorld().equals(gamePlayer.getBukkitPlayer().getWorld()))
            return;

        Border border = worldGame.getBorder();

        if (changedBlock(event)) {
            gamePlayer.getBorderHandler().getFakeBorderWall().setBorder(border);
            gamePlayer.getBorderHandler().getFakeBorderWall().update(worldGame);
        }

        if (!analyseMove(gamePlayer, border)) {
            rollback(gamePlayer);
        }

        gamePlayer.getBorderHandler().setLastSafe(gamePlayer.getBukkitPlayer(), event.getTo().clone());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();

        if (!(projectile.getShooter() instanceof Player))
            return;

        Player player = (Player) projectile.getShooter();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!gamePlayer.getBorderHandler().isTrackingProjectile(projectile.getUniqueId()))
                    gamePlayer.getBorderHandler().trackProjectile(projectile);

                BorderHandler borderHandler = gamePlayer.getBorderHandler();
                Location lastSafe = borderHandler.getProjectileLastSafe(projectile.getUniqueId());
                BoundingBox border = gamePlayer.getBorderHandler().getBorder().getBoundingBox().clone().expand(-1);

                if (!border.isColliding(projectile.getLocation())) {
                    if (projectile instanceof EnderPearl) {
                        lastSafe.setYaw(player.getLocation().getYaw());
                        lastSafe.setPitch(player.getLocation().getPitch());
                        player.teleport(lastSafe);
                    }

                    borderHandler.stopTrackingProjectile(projectile.getUniqueId());
                    projectile.remove();
                    cancel();
                    return;
                }

                // changed block
                if (!projectile.getLocation().equals(lastSafe) && border.isColliding(projectile.getLocation())) {
                    borderHandler.setProjectileLastSafe(projectile.getUniqueId(), projectile.getLocation());
                }

                if (projectile.isDead()) {
                    borderHandler.stopTrackingProjectile(projectile.getUniqueId());
                    cancel();
                }

            }
        }.runTaskTimer(plugin, 0, 1);



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
            if (player.getTeam() != null && player.getTeam().getTeamTown().getSpawnOrNull() != null)
                player.getBukkitPlayer().teleport(player.getTeam().getTeamTown().getSpawnOrNull());
            return;
        }
        player.getBukkitPlayer().teleport(player.getBorderHandler().getLastSafe());
    }

    private boolean changedBlock(PlayerMoveEvent event) {
        Location to = event.getTo().clone();
        Location from = event.getFrom().clone();
        return !to.equals(from);
    }

    private boolean shouldCheck(GamePlayer gamePlayer) {
        Location lastSafe = gamePlayer.getBorderHandler().getLastSafe();

        if (lastSafe == null)
            return true;

        return !gamePlayer.getBukkitPlayer().hasPermission(Permissions.BORDER_BYPASS.getPermission());
    }

}
