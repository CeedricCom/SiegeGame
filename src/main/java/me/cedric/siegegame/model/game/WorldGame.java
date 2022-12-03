package me.cedric.siegegame.model.game;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.death.DeathManager;
import me.cedric.siegegame.display.shop.ShopGUI;
import me.cedric.siegegame.modules.lunarclient.LunarClientModule;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.PlayerManager;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.territory.TerritoryBlockers;
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
    private final List<Module> modules = new ArrayList<>();
    private final List<TerritoryBlockers> territoryBlockers = new ArrayList<>();
    private final DeathManager deathManager;

    public WorldGame(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        this.shopGUI = new ShopGUI(this);
        this.playerManager = new PlayerManager(plugin);
        this.deathManager = new DeathManager(plugin, this);
    }

    private void registerModules() {
        modules.add(new LunarClientModule());
    }

    private void initialiseModules() {
        registerModules();
        for (Module module : modules)
            module.initialise(plugin, this);
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

        while (list.size() != 0) {
            for (Team team : teams) {
                if (list.size() == 0)
                    break;

                int chosenPlayer = list.size() == 1 ? 0 : r.nextInt(list.size() - 1);
                GamePlayer player = list.get(chosenPlayer);
                assignTeam(player, team);
                list.remove(chosenPlayer);
            }
        }
    }

    public void assignTeam(GamePlayer player) {
        // assign player to team with least amount of players if a team isnt chosen
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
        }

        for (Team team : getTeams()) {
            TerritoryBlockers blockers = new TerritoryBlockers(this, team.getTerritory());
            territoryBlockers.add(blockers);
            plugin.getServer().getPluginManager().registerEvents(blockers, plugin);
        }

        initialiseModules();

        for (Module module : modules)
            module.onStartGame(plugin, this);

        deathManager.initialise();
        updateAllScoreboards();
    }

    public void endGame() {
        deathManager.shutdown();

        for (GamePlayer gamePlayer : getPlayers()) {

            gamePlayer.reset();

            if (!gamePlayer.hasTeam())
                continue;

            if (gamePlayer.getTeam().getPoints() >= plugin.getGameConfig().getPointsToEnd())
                gamePlayer.getDisplayer().displayVictory();
            else
                gamePlayer.getDisplayer().displayLoss();

            gamePlayer.getBukkitPlayer().sendMessage(ChatColor.DARK_AQUA + "[ceedric.com]" + ChatColor.GOLD +
                    " on gaia gods i would fk u up on eu boxing 1v1 z tier fkin rand dogs i swear my zuesimortal" +
                    " clicker can put u in 30 hit combo like dog random , I AM GAIA DEMON please dont disrespect me" +
                    " fkin dog rand i am known gaia player i swear on morudias gods ur a fkin rand ON HYTES ur my fkin dog Z tier dog randoms");
        }

        for (Team team : teams)
            team.reset();

        for (Module module : modules)
            module.onEndGame(plugin, this);

        for (TerritoryBlockers blockers : territoryBlockers)
            HandlerList.unregisterAll(blockers);

        playerManager.clear();
        territoryBlockers.clear();
    }


}
