package me.cedric.siegegame;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.cedric.siegegame.command.SpawnCommand;
import me.cedric.siegegame.command.args.ReloadArg;
import me.cedric.siegegame.command.args.StartGameArg;
import me.cedric.siegegame.command.kits.DeleteKitArgument;
import me.cedric.siegegame.command.kits.KitSetArgument;
import me.cedric.siegegame.command.RallyCommand;
import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.player.border.blockers.BlockChangePacketAdapter;
import me.cedric.siegegame.model.player.border.PlayerBorderListener;
import me.cedric.siegegame.command.ResourcesCommand;
import me.cedric.siegegame.config.ConfigLoader;
import me.cedric.siegegame.config.GameConfig;
import me.cedric.siegegame.model.player.kits.db.PersistenceManager;
import me.cedric.siegegame.view.display.placeholderapi.SiegeGameExpansion;
import me.cedric.siegegame.model.player.PlayerListener;
import me.cedric.siegegame.model.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SiegeGamePlugin extends JavaPlugin {

    private ConfigLoader configLoader;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        PersistenceManager persistenceManager = new PersistenceManager(this);
        persistenceManager.initialise();

        this.gameManager = new GameManager(this, persistenceManager.getKitController());
        this.configLoader = new ConfigLoader(this);

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new PlayerBorderListener(this), this);

        ProtocolLibrary.getProtocolManager().addPacketListener(new BlockChangePacketAdapter(this));

        registerCommands();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            new SiegeGameExpansion(this).register();

        configLoader.initialiseAndLoad();

        if (getGameConfig().getStartGameOnServerStartup())
            Bukkit.getScheduler().runTaskLater(this, () -> getGameManager().startNextGame(), 1L);
    }

    @Override
    public void onDisable() {
        gameManager.endGame(true, false);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public GameConfig getGameConfig() {
        return configLoader;
    }

    private void registerCommands() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(Commands.literal("siegegame")
                    .then(Commands.literal("start").executes(new StartGameArg(this)))
                    .then(Commands.literal("reload").executes(new ReloadArg(this)))
                    .build());

            commands.registrar().register(Commands.literal("kit")
                    .requires(source -> source.getSender() instanceof Player &&
                                                             source.getSender().hasPermission(Permissions.KITS.getPermission()))
                    .then(Commands.literal("set")
                            .then(Commands.argument("map", StringArgumentType.word())
                                    .suggests((context, builder) -> {
                                        builder.suggest("allmaps");
                                        getGameConfig().getMapIDs().forEach(builder::suggest);
                                        return builder.buildFuture();
                                    })
                                    .executes(new KitSetArgument(this))
                            )
                    )
                    .then(Commands.literal("delete").executes(new DeleteKitArgument(this)))
                    .build());

            commands.registrar().register(Commands.literal("resources").executes(new ResourcesCommand(this)).build());
            commands.registrar().register(Commands.literal("rally").executes(new RallyCommand(this)).build());
            commands.registrar().register(Commands.literal("t").then(Commands.literal("spawn").executes(new SpawnCommand(this))).build());
        });
    }

    public ICombatLogX getCombatLogX() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Plugin plugin = pluginManager.getPlugin("CombatLogX");
        return (ICombatLogX) plugin;
    }
}
