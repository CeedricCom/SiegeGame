package me.cedric.siegegame.modules.capturepoint;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;

public class EffectControlArea extends ControlAreaHandler implements Listener {

    private Team lastTeamOnCap = null;
    private Team currentOnCap = null;
    private PotionEffect potionEffect;
    private final SiegeGamePlugin plugin;

    public EffectControlArea(SiegeGamePlugin plugin, Cuboid cuboid, int maxStages, int groundLayer, GameMap map, String name, String displayName, PotionEffect effect) {
        super(plugin, cuboid, maxStages, groundLayer, map, name, displayName);
        this.potionEffect = effect;
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onPlayerCap(GamePlayer gamePlayer) {

    }

    @Override
    public void onTeamCap(Team team) {
        if (lastTeamOnCap == null || currentOnCap == null) {
            currentOnCap = team;
            lastTeamOnCap = team;
        }

        if (!team.equals(currentOnCap)) {
            for (GamePlayer gamePlayer : currentOnCap.getPlayers()) {
                gamePlayer.getBukkitPlayer().removePotionEffect(potionEffect.getType());
            }

            lastTeamOnCap = currentOnCap;
            currentOnCap = team;
        }

        for (GamePlayer gamePlayer : currentOnCap.getPlayers()) {
            Player player = gamePlayer.getBukkitPlayer();
            player.addPotionEffect(potionEffect);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();

        Player player = event.getPlayer();
        GamePlayer gamePlayer = match.getWorldGame().getPlayer(player.getUniqueId());

        if (gamePlayer == null || !gamePlayer.hasTeam() || !gamePlayer.getTeam().equals(currentOnCap))
            return;

        player.addPotionEffect(potionEffect);
    }
}
