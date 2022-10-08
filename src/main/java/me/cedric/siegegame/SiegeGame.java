package me.cedric.siegegame;

import com.comphenix.protocol.ProtocolLibrary;
import me.cedric.siegegame.border.BlockChangePacketAdapter;
import me.cedric.siegegame.border.BorderListener;
import me.cedric.siegegame.command.ResourcesCommand;
import me.cedric.siegegame.command.SiegeGameCommand;
import me.cedric.siegegame.config.ConfigLoader;
import me.cedric.siegegame.display.placeholderapi.SiegeGameExpansion;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.PlayerListener;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.model.GameManager;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.common.plugin.ApiPlugin;
import org.bukkit.Bukkit;

public final class SiegeGame extends BukkitPlugin {

    private ApiPlugin apiPlugin;
    private ConfigLoader configLoader;
    private GameManager gameManager;

    @Override
    public void onPluginEnable() {
        this.apiPlugin = this;
        this.gameManager = new GameManager(this);
        this.configLoader = new ConfigLoader(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BorderListener(this), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new BlockChangePacketAdapter(this));

        apiPlugin.registerCommand(new SiegeGameCommand(this), "siegegame", "sg", "siegeg");
        apiPlugin.registerCommand(new ResourcesCommand(this), "resources", "r", "rs");

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SiegeGameExpansion(this).register();
        }

        configLoader.initializeAndLoad();
    }

    @Override
    public void onPluginDisable() {

        if (gameManager.getCurrentMatch() == null)
            return;

        for (SiegeGameMatch match : gameManager.getLoadedMatches()) {

            for (GamePlayer gamePlayer : match.getWorldGame().getDeathManager().getDeadPlayers())
                match.getWorldGame().getDeathManager().revivePlayer(gamePlayer);

            for (SuperItem superItem : match.getWorldGame().getSuperItemManager().getSuperItems())
                superItem.remove();

            if (match.getGameMap().isWorldLoaded())
                match.getGameMap().unload();
        }
    }

    public ApiPlugin getApiPlugin() {
        return apiPlugin;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }
}
