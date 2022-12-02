package me.cedric.siegegame;

import com.comphenix.protocol.ProtocolLibrary;
import me.cedric.siegegame.player.border.blockers.BlockChangePacketAdapter;
import me.cedric.siegegame.player.border.PlayerBorderListener;
import me.cedric.siegegame.command.ResourcesCommand;
import me.cedric.siegegame.command.SiegeGameCommand;
import me.cedric.siegegame.config.ConfigLoader;
import me.cedric.siegegame.config.GameConfig;
import me.cedric.siegegame.death.DeathManager;
import me.cedric.siegegame.display.placeholderapi.SiegeGameExpansion;
import me.cedric.siegegame.player.PlayerListener;
import me.cedric.siegegame.model.GameManager;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.common.plugin.ApiPlugin;
import org.bukkit.Bukkit;

public final class SiegeGamePlugin extends BukkitPlugin {

    private ApiPlugin apiPlugin;
    private ConfigLoader configLoader;
    private GameManager gameManager;
    private DeathManager deathManager;

    @Override
    public void onPluginEnable() {
        this.apiPlugin = this;
        this.gameManager = new GameManager(this);
        this.configLoader = new ConfigLoader(this);
        deathManager = new DeathManager(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerBorderListener(this), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new BlockChangePacketAdapter(this));

        apiPlugin.registerCommand(new SiegeGameCommand(this), "siegegame", "sg", "siegeg");
        apiPlugin.registerCommand(new ResourcesCommand(this), "resources", "r", "rs");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new SiegeGameExpansion(this).register();

        configLoader.initializeAndLoad();
        deathManager.initialise();
    }

    @Override
    public void onPluginDisable() {
        for (SiegeGameMatch match : gameManager.getLoadedMatches())
            match.endGame();
    }

    public ApiPlugin getApiPlugin() {
        return apiPlugin;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameConfig getGameConfig() {
        return configLoader;
    }

    public DeathManager getDeathManager() {
        return deathManager;
    }
}
