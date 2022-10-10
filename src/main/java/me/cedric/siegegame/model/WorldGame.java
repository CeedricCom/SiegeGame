package me.cedric.siegegame.model;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.death.DeathManager;
import me.cedric.siegegame.display.shop.ShopGUI;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.PlayerManager;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.superitems.SuperItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class WorldGame {

    private final SiegeGamePlugin plugin;
    private final Set<Team> teams = new HashSet<>();
    private final SuperItemManager superItemManager;
    private final DeathManager deathManager;
    private PlayerManager playerManager;
    private final ShopGUI shopGUI;

    public WorldGame(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        this.superItemManager = new SuperItemManager(plugin, this);
        this.deathManager = new DeathManager(plugin, this);
        this.shopGUI = new ShopGUI(this);
        this.playerManager = new PlayerManager(plugin);

        deathManager.initialize();
    }

    public void addPlayer(UUID uuid) {
        playerManager.addPlayer(uuid);
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
        Iterator<GamePlayer> iterator = list.iterator();

        while (iterator.hasNext()) {
            for (Team team : teams) {
                if (list.size() == 0)
                    break;

                int chosenPlayer = list.size() == 1 ? 0 : r.nextInt(0, list.size() - 1);
                GamePlayer player = list.get(chosenPlayer);
                assignTeam(player, team);
                iterator.next();
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

    public SuperItemManager getSuperItemManager() {
        return superItemManager;
    }

    public void updateAllScoreboards() {
        for (GamePlayer gamePlayer : playerManager.getPlayers()) {
            gamePlayer.getDisplayer().updateScoreboard();
        }
    }

    public ImmutableSet<GamePlayer> getPlayers() {
        return ImmutableSet.copyOf(playerManager.getPlayers());
    }

    public DeathManager getDeathManager() {
        return deathManager;
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

        for (GamePlayer gamePlayer : getPlayers())
            gamePlayer.reset();

        getSuperItemManager().assignSuperItems(true);

        updateAllScoreboards();
    }

    public void endGame() {
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

        for (SuperItem superItem : superItemManager.getSuperItems())
            superItem.remove();
    }
}
