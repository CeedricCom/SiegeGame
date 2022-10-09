package me.cedric.siegegame.player;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.border.BorderHandler;
import me.cedric.siegegame.display.Displayer;
import me.cedric.siegegame.model.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class GamePlayer {

    private final SiegeGame plugin;
    private final UUID uuid;
    private final BorderHandler borderHandler;
    private final Displayer displayer;
    private boolean dead = false;
    private Team team;

    public GamePlayer(UUID uuid, SiegeGame plugin) {
        this.uuid = uuid;
        this.team = null;
        this.plugin = plugin;
        this.borderHandler = new BorderHandler(plugin, this);
        this.displayer = new Displayer(plugin, this);
    }

    public Player getBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Team getTeam() {
        return team;
    }

    public boolean hasTeam() {
        return team != null;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public UUID getUUID() {
        return uuid;
    }

    public BorderHandler getBorderHandler() {
        return borderHandler;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public Displayer getDisplayer() {
        return displayer;
    }

    public void grantNightVision() {
        getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
    }
}
