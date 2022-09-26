package me.cedric.siegegame.border;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ProjectileFollowTask extends BukkitRunnable {
    
    private final GamePlayer player;
    private final SiegeGame plugin;
    private final WorldGame worldGame;
    private final Projectile projectile;

    public ProjectileFollowTask(SiegeGame plugin, GamePlayer player, WorldGame worldGame, Projectile projectile) {
        this.player = player;
        this.plugin = plugin;
        this.worldGame = worldGame;
        this.projectile = projectile;
    }

    @Override
    public void run() {
        if (!player.getBorderHandler().isTrackingProjectile(projectile.getUniqueId()))
            player.getBorderHandler().trackProjectile(projectile);

        BorderHandler borderHandler = player.getBorderHandler();
        Location lastSafe = borderHandler.getProjectileLastSafe(projectile.getUniqueId()).clone();

        if (!checkBorder(worldGame.getBorder(), lastSafe.toVector())) {
            deleteProjectileAndCancel(projectile, lastSafe, borderHandler);
        }

        for (Team team : worldGame.getTeams()) {

            Border safeArea = team.getSafeArea();

            if (!checkBorder(safeArea, lastSafe.toVector()))
                continue;

            deleteProjectileAndCancel(projectile, lastSafe, borderHandler);
            return;
        }

        // changed block
        if (!projectile.getLocation().equals(lastSafe)) {
            borderHandler.setProjectileLastSafe(projectile.getUniqueId(), projectile.getLocation());
        }

        if (projectile.isDead()) {
            borderHandler.stopTrackingProjectile(projectile.getUniqueId());
            cancel();
        }
    }

    private boolean checkBorder(Border safeArea, Vector lastSafe) {
        int distance = safeArea.isInverse() ? 3 : -2;
        if (safeArea.getBoundingBox().clone().expand(distance).isCollidingIgnoreInverse(lastSafe) && safeArea.isInverse()) {
            return true;
        }
        return safeArea.getBoundingBox().clone().expand(distance).isCollidingIgnoreInverse(lastSafe) && !safeArea.isInverse();
    }

    private void deleteProjectileAndCancel(Projectile projectile, Location lastSafe, BorderHandler borderHandler) {
        if (projectile instanceof EnderPearl) {
            lastSafe.setYaw(player.getBukkitPlayer().getLocation().getYaw());
            lastSafe.setPitch(player.getBukkitPlayer().getLocation().getPitch());
            player.getBukkitPlayer().teleport(lastSafe);
        }

        borderHandler.stopTrackingProjectile(projectile.getUniqueId());
        projectile.remove();
        this.cancel();
        player.getBukkitPlayer().sendMessage(ChatColor.RED + "You cannot use projectiles near a border or your safe area");
    }
}
