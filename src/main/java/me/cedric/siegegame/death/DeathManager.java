package me.cedric.siegegame.death;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.config.Settings;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DeathManager {

    private final SiegeGame plugin;
    private final HashMap<GamePlayer, RespawnTask> deadPlayers = new HashMap<>();
    private final WorldGame worldGame;

    public DeathManager(SiegeGame plugin, WorldGame worldGame) {
        this.plugin = plugin;
        this.worldGame = worldGame;
    }

    public void initialize() {
        plugin.getServer().getPluginManager().registerEvents(new RespawnListener(plugin, worldGame, this), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Limiters(plugin, this), plugin);
    }

    public void makeSpectator(Player player) {
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer.isDead())
            return;

        Location respawnLocation = player.getLocation();

        if (gamePlayer.hasTeam())
            respawnLocation = gamePlayer.getTeam().getSafeSpawn();
        else {
            SiegeGameMatch gameMatch = plugin.getGameManager().getCurrentMatch();
            if (gameMatch != null)
                respawnLocation = gameMatch.getGameMap().getDefaultSpawn();
        }

        makeSpectator(player, respawnLocation);
    }

    private void makeSpectator(Player player, Location respawnLocation) {
        if (player == null)
            return;

        player.setExp(0);
        player.setAllowFlight(true);
        player.setFlying(true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId()))
                continue;

            p.hidePlayer(plugin, player);
        }

        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (deadPlayers.containsKey(gamePlayer)) {
            deadPlayers.get(gamePlayer).cancel();
            deadPlayers.remove(gamePlayer);
        }

        RespawnTask respawnTask = new RespawnTask(plugin, this, gamePlayer, (int) Settings.RESPAWN_TIMER.getValue(), respawnLocation);
        deadPlayers.put(gamePlayer, respawnTask);

        respawnTask.start();

        String title = ChatColor.translateAlternateColorCodes('&', "&4&lYOU DIED!");
        String subtitle = ChatColor.translateAlternateColorCodes('&', "&cYou will respawn in " + Settings.RESPAWN_TIMER.getValue() + " seconds");
        player.showTitle(Title.title(Component.text(title), Component.text(subtitle)));

        gamePlayer.setDead(true);

        for (String s : (List<String>) Settings.DEATH_COMMANDS.getValue()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        }
    }

    public void revivePlayer(GamePlayer gamePlayer) {
        if (!deadPlayers.containsKey(gamePlayer))
            return;

        RespawnTask task = deadPlayers.get(gamePlayer);
        revivePlayer(gamePlayer, task.getOriginalRespawn());
    }

    private void revivePlayer(GamePlayer gamePlayer, Location originalRespawn) {
        Player player = gamePlayer.getBukkitPlayer();
        player.setFlying(false);
        player.setExp(0);
        player.setFlying(false);
        player.setAllowFlight(false);

        player.teleport(originalRespawn);

        for (PotionEffect effect : new ArrayList<>(player.getActivePotionEffects())) {
            player.removePotionEffect(effect.getType());
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getUniqueId().equals(player.getUniqueId()))
                continue;

            p.showPlayer(plugin, player);
        }

        gamePlayer.grantNightVision();

        deadPlayers.remove(gamePlayer);
        gamePlayer.setDead(false);

        for (String s : (List<String>) Settings.RESPAWN_COMMANDS.getValue()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        }
    }

    public boolean isPlayerDead(UUID uuid) {
        GamePlayer gamePlayer = worldGame.getPlayer(uuid);
        return deadPlayers.containsKey(gamePlayer);
    }

    public boolean isPlayerDead(GamePlayer gamePlayer) {
        return deadPlayers.containsKey(gamePlayer);
    }


    public void setPausePlayer(GamePlayer gamePlayer, boolean b) {
        deadPlayers.get(gamePlayer).setPause(b);
    }

    public Set<GamePlayer> getDeadPlayers() {
        return new HashSet<>(deadPlayers.keySet());
    }
}
