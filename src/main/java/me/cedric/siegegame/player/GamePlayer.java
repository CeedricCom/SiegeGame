package me.cedric.siegegame.player;

import com.lunarclient.bukkitapi.LunarClientAPI;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.border.PlayerBorderHandler;
import me.cedric.siegegame.fake.FakeBlockManager;
import me.cedric.siegegame.display.Displayer;
import me.cedric.siegegame.model.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class GamePlayer {

    private final SiegeGamePlugin plugin;
    private final UUID uuid;
    private final PlayerBorderHandler playerBorderHandler;
    private final FakeBlockManager fakeBlockManager;
    private final Displayer displayer;
    private boolean dead = false;
    private Team team;

    public GamePlayer(UUID uuid, SiegeGamePlugin plugin) {
        this.uuid = uuid;
        this.team = null;
        this.plugin = plugin;
        this.playerBorderHandler = new PlayerBorderHandler(plugin, this);
        this.displayer = new Displayer(plugin, this);
        this.fakeBlockManager = new FakeBlockManager(plugin, getBukkitPlayer());
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

    public PlayerBorderHandler getBorderHandler() {
        return playerBorderHandler;
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

    public FakeBlockManager getFakeBlockManager() {
        return fakeBlockManager;
    }

    public boolean isLunarClient() {
        return LunarClientAPI.getInstance().isRunningLunarClient(uuid);
    }

    public void grantNightVision() {
        getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
    }

    public void reset() {
        getBukkitPlayer().setLevel(0);
        getBukkitPlayer().getInventory().clear();
        getBukkitPlayer().getEnderChest().clear();
        grantNightVision();
        getDisplayer().wipeScoreboard();
    }
}
