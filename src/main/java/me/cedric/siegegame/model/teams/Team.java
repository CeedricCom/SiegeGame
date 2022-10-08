package me.cedric.siegegame.model.teams;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.territory.Territory;
import me.cedric.siegegame.model.WorldGame;
import org.bukkit.Location;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class Team {

    private final WorldGame worldGame;
    private final Set<GamePlayer> players = new HashSet<>();
    private final TeamFactory factory;
    private int points = 0;

    public Team(WorldGame worldGame, TeamFactory factory) {
        this.factory = factory;
        this.worldGame = worldGame;
    }

    public ImmutableSet<GamePlayer> getPlayers() {
        return ImmutableSet.copyOf(players);
    }

    public WorldGame getWorldGame() {
        return worldGame;
    }

    public String getName() {
        return factory.getName();
    }

    public String getConfigKey() {
        return factory.getConfigKey();
    }

    public Territory getTerritory() {
        return factory.getTerritory();
    }

    public Location getSafeSpawn() {
        return factory.getSafeSpawn();
    }

    public Color getColor() {
        return factory.getColor();
    }

    public void addPlayer(GamePlayer player) {
        players.add(player);
        player.setTeam(this);
    }

    public void removePlayer(GamePlayer player) {
        players.remove(player);
        player.setTeam(null);
    }

    public void clear() {
        players.clear();
    }

    public Border getSafeArea() {
        return factory.getSafeArea();
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int i) {
        points += i;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TeamFactory)
            return factory.equals(obj);
        return super.equals(obj);
    }
}
