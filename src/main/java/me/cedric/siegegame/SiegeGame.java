package me.cedric.siegegame;

import com.comphenix.protocol.ProtocolLibrary;
import me.cedric.siegegame.border.BlockChangePacketAdapter;
import me.cedric.siegegame.border.BorderListener;
import me.cedric.siegegame.command.ResourcesCommand;
import me.cedric.siegegame.command.SiegeGameCommand;
import me.cedric.siegegame.config.ConfigLoader;
import me.cedric.siegegame.death.DeathManager;
import me.cedric.siegegame.display.placeholderapi.SiegeGameExpansion;
import me.cedric.siegegame.display.shop.ShopGUI;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.PlayerListener;
import me.cedric.siegegame.player.PlayerManager;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.superitems.SuperItemManager;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import me.deltaorion.bukkit.plugin.plugin.BukkitPlugin;
import me.deltaorion.common.plugin.ApiPlugin;
import org.bukkit.Bukkit;

public final class SiegeGame extends BukkitPlugin {

    private ApiPlugin apiPlugin;
    private ConfigLoader configLoader;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private SuperItemManager superItemManager;
    private DeathManager deathManager;
    private ShopGUI shopGUI;

    // TODO: messages (perhaps locale?)

    @Override
    public void onPluginEnable() {
        this.apiPlugin = this;
        this.gameManager = new GameManager(this);
        this.playerManager = new PlayerManager(this);
        this.configLoader = new ConfigLoader(this);
        this.shopGUI = new ShopGUI(this);
        this.superItemManager = new SuperItemManager(this);
        this.deathManager = new DeathManager(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BorderListener(this), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new BlockChangePacketAdapter(this));

        apiPlugin.registerCommand(new SiegeGameCommand(this), "siegegame", "sg", "siegeg");
        apiPlugin.registerCommand(new ResourcesCommand(this), "resources", "r", "rs");

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new SiegeGameExpansion(this).register();
        }

        superItemManager.initialize();
        deathManager.initialize();
        configLoader.initializeAndLoad();
    }

    @Override
    public void onPluginDisable() {
        for (WorldGame worldGame : gameManager.getLoadedWorlds()) {

            for (Team team : worldGame.getTeams())
                team.getTerritory().save();

            worldGame.getGameMap().unload();
        }

        for (GamePlayer gamePlayer : deathManager.getDeadPlayers()) {
            deathManager.revivePlayer(gamePlayer);
        }

        if (gameManager.getCurrentMap() == null)
            return;

        for (SuperItem superItem : gameManager.getCurrentMap().getSuperItems()) {
            superItem.remove();
        }
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

    public SuperItemManager getSuperItemManager() {
        return superItemManager;
    }
}
