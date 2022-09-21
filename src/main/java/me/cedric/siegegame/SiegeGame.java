package me.cedric.siegegame;

import com.comphenix.protocol.ProtocolLibrary;
import me.cedric.siegegame.border.BlockChangePacketAdapter;
import me.cedric.siegegame.border.BorderListener;
import me.cedric.siegegame.command.SiegeGameCommand;
import me.cedric.siegegame.config.ConfigLoader;
import me.cedric.siegegame.config.WorldLoadListener;
import me.cedric.siegegame.display.shop.ShopGUI;
import me.cedric.siegegame.player.PlayerListener;
import me.cedric.siegegame.player.PlayerManager;
import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.common.plugin.ApiPlugin;

public final class SiegeGame extends BukkitPlugin {

    private ApiPlugin apiPlugin;
    private ConfigLoader configLoader;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private ShopGUI shopGUI;

    @Override
    public void onPluginEnable() {
        this.apiPlugin = this;
        this.gameManager = new GameManager(this);
        this.playerManager = new PlayerManager(this);
        this.configLoader = new ConfigLoader(this);
        this.shopGUI = new ShopGUI(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new WorldLoadListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BorderListener(this), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new BlockChangePacketAdapter(this));

        apiPlugin.registerCommand(new SiegeGameCommand(this), "siegegame", "sg", "siegeg");
        apiPlugin.registerDependency("Towny", true);

        configLoader.initializeAndLoad();
    }

    public ApiPlugin getApiPlugin() {
        return apiPlugin;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }
}
