package me.cedric.siegegame.death;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnTask extends BukkitRunnable {

    private final SiegeGame plugin;
    private final DeathManager deathManager;
    private final GamePlayer gamePlayer;
    private boolean pause = false;
    private int i = 0;
    private final int respawnTimer;
    private final Location originalRespawn;


    public RespawnTask(SiegeGame plugin, DeathManager deathManager, GamePlayer gamePlayer, int respawnTimer, Location originalRespawn) {
        this.plugin = plugin;
        this.deathManager = deathManager;
        this.gamePlayer = gamePlayer;
        this.respawnTimer = respawnTimer;
        this.originalRespawn = originalRespawn;
    }

    @Override
    public void run() {
        Player player = gamePlayer.getBukkitPlayer();

        if (pause || player == null || !player.isOnline()) {
            return;
        }

        if (i >= respawnTimer) {
            deathManager.revivePlayer(gamePlayer);
            super.cancel();
            return;
        }

        sendActionBar(player, "&cYou will respawn in &e" + getRemainingTime() + " seconds");
        i++;
    }

    public void setPause(boolean b) {
        this.pause = b;
    }

    public int getRemainingTime() {
        return respawnTimer - i;
    }

    public void start() {
        super.runTaskTimer(plugin, 0, 20);
    }

    private void sendActionBar(Player player, String message) {
        player.sendActionBar(Component.text(ChatColor.translateAlternateColorCodes('&', message)));
    }

    public Location getOriginalRespawn() {
        return originalRespawn;
    }
}

