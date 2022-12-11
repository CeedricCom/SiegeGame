package me.cedric.siegegame;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import me.cedric.siegegame.command.KitsCommand;
import me.cedric.siegegame.command.RallyCommand;
import me.cedric.siegegame.player.border.blockers.BlockChangePacketAdapter;
import me.cedric.siegegame.player.border.PlayerBorderListener;
import me.cedric.siegegame.command.ResourcesCommand;
import me.cedric.siegegame.command.SiegeGameCommand;
import me.cedric.siegegame.config.ConfigLoader;
import me.cedric.siegegame.config.GameConfig;
import me.cedric.siegegame.display.placeholderapi.SiegeGameExpansion;
import me.cedric.siegegame.player.PlayerListener;
import me.cedric.siegegame.model.GameManager;
import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.common.plugin.ApiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class SiegeGamePlugin extends BukkitPlugin {

    private ApiPlugin apiPlugin;
    private ConfigLoader configLoader;
    private GameManager gameManager;

    @Override
    public void onPluginEnable() {
        this.apiPlugin = this;
        this.gameManager = new GameManager(this);
        this.configLoader = new ConfigLoader(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerBorderListener(this), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new BlockChangePacketAdapter(this));

        apiPlugin.registerCommand(new SiegeGameCommand(this), "siegegame", "sg", "siegeg");
        apiPlugin.registerCommand(new ResourcesCommand(this), "resources", "r", "rs");
        apiPlugin.registerCommand(new RallyCommand(this), "rally");
        apiPlugin.registerCommand(new KitsCommand(this), "kits", "kit");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new SiegeGameExpansion(this).register();

        configLoader.initializeAndLoad();
    }

    @Override
    public void onPluginDisable() {
        gameManager.endGame(true, false);
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

    public ICombatLogX getCombatLogX() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("CombatLogX");
        return (ICombatLogX) plugin;
    }
}
