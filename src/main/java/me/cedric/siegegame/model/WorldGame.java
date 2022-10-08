package me.cedric.siegegame.model;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.death.DeathManager;
import me.cedric.siegegame.display.shop.ShopGUI;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.PlayerManager;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.superitems.SuperItemManager;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class WorldGame {

    private final SiegeGame plugin;
    private final Set<Team> teams = new HashSet<>();
    private final SuperItemManager superItemManager;
    private final DeathManager deathManager;
    private PlayerManager playerManager;
    private final ShopGUI shopGUI;

    public WorldGame(SiegeGame plugin) {
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

    public void removeTeam(String configKey) {
        teams.removeIf(team -> team.getConfigKey().equalsIgnoreCase(configKey));
    }

    public void assignRandomTeams() {
        Random r = new Random();

        List<GamePlayer> list = new ArrayList<>(playerManager.getPlayers());

        while (list.size() != 0) {
            for (Team team : teams) {
                if (list.size() == 0)
                    break;

                int chosenPlayer = list.size() == 1 ? 0 : r.nextInt(0, list.size() - 1);
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

        for (Team t : teams) {
            player.getBorderHandler().addBorder(t.getSafeArea());
        }

        player.getBorderHandler().addBorder(team.getSafeArea());

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

    public Team getTeam(String configKey) {
        return teams.stream().filter(team -> team.getConfigKey().equalsIgnoreCase(configKey)).findAny().orElse(null);
    }
}
