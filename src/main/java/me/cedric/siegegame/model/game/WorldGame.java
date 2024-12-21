package me.cedric.siegegame.model.game;

import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.controller.death.DeathManager;
import me.cedric.siegegame.view.display.shop.ShopGUI;
import me.cedric.siegegame.modules.abilityitems.SuperBreakerModule;
import me.cedric.siegegame.lunarclient.LunarClientModule;
import me.cedric.siegegame.controller.stats.StatsController;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.model.player.PlayerManager;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.controller.TerritoryController;
import me.cedric.siegegame.model.player.kits.Kit;
import me.cedric.siegegame.model.player.kits.PlayerKitManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class WorldGame {

    private final SiegeGamePlugin plugin;
    private final Set<Team> teams = new HashSet<>();
    private final PlayerManager playerManager;
    private final ShopGUI shopGUI;
    private final Set<Module> modules = new HashSet<>();
    private final List<TerritoryController> territoryBlockers = new ArrayList<>();
    private final DeathManager deathManager;
    private final String mapIdentifier;
    private final StatsController stats;

    public WorldGame(SiegeGamePlugin plugin, String mapIdentifier) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(this);
        this.playerManager = new PlayerManager(plugin);
        this.mapIdentifier = mapIdentifier;
        this.deathManager = new DeathManager(plugin, this);
        this.stats = new StatsController(plugin, this);
    }

    private void registerModules() {
        modules.add(new LunarClientModule());
        modules.add(new SuperBreakerModule());
    }

    public String getMapIdentifier() {
        return mapIdentifier;
    }

    public void addPlayer(UUID uuid) {
        playerManager.addPlayer(uuid);
    }

    public List<Module> getModules() {
        return new ArrayList<>(modules);
    }

    public void removePlayer(UUID uuid) {
        playerManager.removePlayer(uuid);
    }

    public GamePlayer getPlayer(UUID uuid) {
        return playerManager.getPlayer(uuid);
    }

    public Set<Team> getTeams() {
        return new HashSet<>(teams);
    }

    public void addTeam(Team team) {
        teams.add(team);
    }

    public void removeTeam(String identifier) {
        teams.removeIf(team -> team.getIdentifier().equalsIgnoreCase(identifier));
    }

    public void assignRandomTeams() {
        Random r = new Random();

        List<GamePlayer> list = new ArrayList<>(playerManager.getPlayers());

        while (!list.isEmpty()) {
            for (Team team : teams) {
                if (list.isEmpty())
                    break;

                int chosenPlayer = list.size() == 1 ? 0 : r.nextInt(list.size() - 1);
                GamePlayer player = list.get(chosenPlayer);
                assignTeam(player, team);
                list.remove(chosenPlayer);
            }
        }
    }

    public void assignTeam(GamePlayer player) {
        // assign player to team with the least amount of players if a team isn't chosen
        teams.stream()
                .min(Comparator.comparingInt(value -> value.getPlayers().size()))
                .ifPresent(selected -> assignTeam(player, selected));
    }

    private void assignTeam(GamePlayer player, Team team) {
        team.addPlayer(player);

        for (Team t : teams)
            player.getBorderHandler().addBorder(t.getSafeArea());

        player.getBorderHandler().addBorder(plugin.getGameManager().getCurrentMatch().getGameMap().getMapBorder());

        player.getBukkitPlayer().sendMessage(ChatColor.DARK_AQUA + "You have been assigned to the following team: " + team.getName());
    }

    public void updateAllScoreboards() {
        for (GamePlayer gamePlayer : playerManager.getPlayers()) {
            gamePlayer.getDisplayer().updateScoreboard();
        }
    }

    public ImmutableSet<GamePlayer> getPlayers() {
        return ImmutableSet.copyOf(playerManager.getPlayers());
    }

    public ImmutableSet<GamePlayer> getActivePlayers() {
        return playerManager.getPlayers().stream()
                .filter(gamePlayer -> !gamePlayer.isDead() && gamePlayer.hasTeam())
                .collect(ImmutableSet.toImmutableSet());
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }

    public Team getTeam(String identifier) {
        return teams.stream().filter(team -> team.getIdentifier().equalsIgnoreCase(identifier)).findAny().orElse(null);
    }

    public void startGame() {
        for (Player player : Bukkit.getOnlinePlayers())
            addPlayer(player.getUniqueId());

        assignRandomTeams();

        for (GamePlayer gamePlayer : getPlayers()) {
            gamePlayer.reset();
            gamePlayer.getBukkitPlayer().teleport(gamePlayer.getTeam().getSafeSpawn());

            PlayerKitManager kitManager = plugin.getGameManager().getKitStorage().getKitManager(gamePlayer.getUUID());

            if (kitManager != null) {
                Kit kit = kitManager.getKit(getMapIdentifier());
                if (kit != null) {
                    kit.populateFromRawString(this);
                    gamePlayer.getBukkitPlayer().getInventory().setContents(kit.getInventoryContents());
                }
            }

            ICombatManager combatManager = plugin.getCombatLogX().getCombatManager();
            if (combatManager.isInCombat(gamePlayer.getBukkitPlayer()))
                combatManager.untag(gamePlayer.getBukkitPlayer(), UntagReason.EXPIRE);
        }

        for (Team team : getTeams()) {
            TerritoryController blockers = new TerritoryController(this, team.getTerritory());
            territoryBlockers.add(blockers);
            plugin.getServer().getPluginManager().registerEvents(blockers, plugin);
        }

        registerModules();

        for (Module module : modules)
            module.onStartGame(plugin, this);

        deathManager.initialise();
        updateAllScoreboards();
        stats.initialize();
    }

    public void endGame() {
        deathManager.shutdown();

        for (Module module : modules)
            module.onEndGame(plugin, this);

        for (GamePlayer gamePlayer : getPlayers()) {

            gamePlayer.reset();

            if (!gamePlayer.hasTeam())
                continue;

            if (gamePlayer.getTeam().getPoints() >= plugin.getGameConfig().getPointsToEnd())
                gamePlayer.getDisplayer().displayVictory();
            else
                gamePlayer.getDisplayer().displayLoss();
        }

        for (Team team : teams)
            team.reset();

        for (TerritoryController blockers : territoryBlockers)
            HandlerList.unregisterAll(blockers);

        stats.shutdown();

        playerManager.clear();
        territoryBlockers.clear();
        modules.clear();
    }


}
