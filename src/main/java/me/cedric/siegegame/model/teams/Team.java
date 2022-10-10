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
    private Territory territory;
    private Location safeSpawn;
    private Border safeArea;
    private String identifier;
    private String name;
    private Color color;

    public Team(WorldGame worldGame, TeamFactory factory) {
        this.factory = factory;
        this.worldGame = worldGame;
        this.safeArea = factory.getSafeArea();
        this.identifier = factory.getConfigKey();
        this.name = factory.getName();
        this.color = factory.getColor();
        this.safeSpawn = factory.getSafeSpawn();
        this.territory = factory.getTerritory();
    }

    public ImmutableSet<GamePlayer> getPlayers() {
        return ImmutableSet.copyOf(players);
    }

    public WorldGame getWorldGame() {
        return worldGame;
    }

    public void addPlayer(GamePlayer player) {
        players.add(player);
        player.setTeam(this);
    }

    public void removePlayer(GamePlayer player) {
        players.remove(player);
        player.setTeam(null);
    }

    public void reset() {
        players.clear();
        safeArea = factory.getSafeArea();
        identifier = factory.getConfigKey();
        name = factory.getName();
        color = factory.getColor();
    }

    public Territory getTerritory() {
        return territory;
    }

    public void setTerritory(Territory territory) {
        this.territory = territory;
    }

    public Border getSafeArea() {
        return safeArea;
    }

    public Location getSafeSpawn() {
        return safeSpawn.clone();
    }

    public void setSafeSpawn(Location safeSpawn) {
        this.safeSpawn = safeSpawn;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Color getColor() {
        return color;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int i) {
        points += i;
    }
}
