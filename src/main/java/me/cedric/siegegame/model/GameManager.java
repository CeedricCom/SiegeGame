package me.cedric.siegegame.model;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.config.Settings;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.model.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class GameManager {

    private final Set<SiegeGameMatch> siegeGameMatches = new HashSet<>();
    private final Queue<SiegeGameMatch> gameMatchQueue = new LinkedList<>();
    private final SiegeGame plugin;
    private SiegeGameMatch currentMatch;
    private SiegeGameMatch lastMatch = null;

    public GameManager(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void addGame(SiegeGameMatch siegeGameMatch) {
        siegeGameMatches.add(siegeGameMatch);
        gameMatchQueue.add(siegeGameMatch);
    }

    public void removeGame(String configKey) {
        siegeGameMatches.removeIf(siegeGameMatch -> siegeGameMatch.getConfigKey().equalsIgnoreCase(configKey));
    }

    public SiegeGameMatch getNextMatch() {
        return gameMatchQueue.peek();
    }

    public SiegeGameMatch getCurrentMatch() {
        return currentMatch;
    }

    public SiegeGameMatch getLastMatch() {
        return lastMatch;
    }

    public Set<SiegeGameMatch> getLoadedMatches() {
        return ImmutableSet.copyOf(siegeGameMatches);
    }

    public void startNextMap() {
        boolean wait = false;
        if (getCurrentMatch() != null) {
            endGame(currentMatch);
            wait = true;
        }

        SiegeGameMatch gameMatch = gameMatchQueue.poll();

        if (gameMatch == null) {
            plugin.getLogger().severe("NO MAP AT THE HEAD OF QUEUE. COULD NOT START GAME");
            return;
        }

        this.currentMatch = gameMatch;

        if (!currentMatch.getGameMap().isWorldLoaded()) {
            currentMatch.getGameMap().load();
            wait = true;
        }

        for (Player player : Bukkit.getOnlinePlayers())
            currentMatch.getWorldGame().addPlayer(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            currentMatch.getWorldGame().assignRandomTeams();

            for (GamePlayer gamePlayer : currentMatch.getWorldGame().getPlayers()) {
                gamePlayer.getBukkitPlayer().teleport(gamePlayer.getTeam().getSafeSpawn());
                gamePlayer.getBukkitPlayer().setLevel(0);
                gamePlayer.getBukkitPlayer().getInventory().clear();
                gamePlayer.getBukkitPlayer().getEnderChest().clear();
                gamePlayer.getDisplayer().updateScoreboard();
            }

            currentMatch.getWorldGame().getSuperItemManager().assignSuperItems(true);

            for (String s : (List<String>) Settings.START_GAME_COMMANDS.getValue()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
            }

            if (this.lastMatch != null)
                this.lastMatch.getGameMap().resetMap();
        }, wait ? 30 * 20 : 20);
    }

    private void endGame(SiegeGameMatch siegeGameMatch) {
        Set<GamePlayer> players = getCurrentMatch().getWorldGame().getPlayers();

        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.hasTeam() && gamePlayer.getTeam().getPoints() >= (int) Settings.POINTS_TO_END.getValue()) {
                gamePlayer.getBukkitPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "VICTORY", ChatColor.YELLOW + "gg ez yall are dog z tier rands");
            } else {
                gamePlayer.getBukkitPlayer().sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "DEFEAT", ChatColor.RED + "L gg random");
            }

            Team team = gamePlayer.getTeam();
            team.removePlayer(gamePlayer);

            gamePlayer.getBukkitPlayer().setLevel(0);
            gamePlayer.getBorderHandler().clear();
            gamePlayer.getBukkitPlayer().getInventory().clear();
            gamePlayer.getBukkitPlayer().getEnderChest().clear();
            gamePlayer.getDisplayer().wipeScoreboard();
            gamePlayer.getBukkitPlayer().sendMessage(ChatColor.DARK_AQUA + "[ceedric.com]" + ChatColor.GOLD + "on gaia gods i would fk u up on eu boxing 1v1 z tier fkin rand dogs i swear my zuesimortal clicker can put u in 30 hit combo like dog random , I AM GAIA DEMON please dont disrespect me fkin dog rand i am known gaia player i swear on morudias gods ur a fkin rand ON HYTES ur my fkin dog Z tier dog randoms");
        }

        for (SuperItem superItem : siegeGameMatch.getWorldGame().getSuperItemManager().getSuperItems()) {
            superItem.remove();
        }

        lastMatch = siegeGameMatch;
        gameMatchQueue.add(siegeGameMatch);
        currentMatch = null;

        for (String s : (List<String>) Settings.END_GAME_COMMANDS.getValue())
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
    }
}
