package me.cedric.siegegame.model;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.SiegeGamePlugin;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public final class GameManager {

    private final Set<SiegeGameMatch> siegeGameMatches = new HashSet<>();
    private final Queue<SiegeGameMatch> gameMatchQueue = new LinkedList<>();
    private final SiegeGamePlugin plugin;
    private SiegeGameMatch currentMatch;
    private SiegeGameMatch lastMatch = null;

    public GameManager(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    public void addGame(SiegeGameMatch siegeGameMatch) {
        siegeGameMatches.add(siegeGameMatch);
        gameMatchQueue.add(siegeGameMatch);
    }

    public void removeGame(String identifier) {
        siegeGameMatches.removeIf(siegeGameMatch -> siegeGameMatch.getIdentifier().equalsIgnoreCase(identifier));
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

    public void startNextGame() {
        if (getCurrentMatch() != null)
            endGame(currentMatch);

        SiegeGameMatch gameMatch = gameMatchQueue.poll();

        if (gameMatch == null) {
            plugin.getLogger().severe("NO MAP AT THE HEAD OF QUEUE. COULD NOT START GAME");
            return;
        }

        currentMatch = gameMatch;
        currentMatch.startGame();
    }

    private void endGame(SiegeGameMatch siegeGameMatch) {
        siegeGameMatch.endGame();
        lastMatch = siegeGameMatch;
        gameMatchQueue.add(siegeGameMatch);
        currentMatch = null;
    }
}
