package me.cedric.tests;

import com.google.common.collect.ImmutableSet;
import com.palmergames.bukkit.towny.object.Town;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Color;

import java.util.HashSet;
import java.util.Set;

public class MockTeam implements Team {

    private Color color;
    private String name;
    private final String configKey;
    private final Set<GamePlayer> players = new HashSet<>();
    private final WorldGame worldGame;

    public MockTeam(WorldGame worldGame, Color color, String name, String configKey) {
        this.color = color;
        this.name = name;
        this.configKey = configKey;
        this.worldGame = worldGame;
    }

    @Override
    public WorldGame getWorldGame() {
        return worldGame;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void addPlayer(GamePlayer player) {
        players.add(player);
    }

    @Override
    public void removePlayer(GamePlayer player) {
        players.remove(player);
    }

    @Override
    public void clear() {
        players.clear();
    }

    @Override
    public Border getSafeArea() {
        return null;
    }

    @Override
    public int getPoints() {
        return 0;
    }

    @Override
    public void addPoints(int i) {

    }

    public Town getTeamTown() {
        return null;
    }

    @Override
    public void setTown(Town town) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getConfigKey() {
        return configKey;
    }

    @Override
    public ImmutableSet<GamePlayer> getPlayers() {
        return ImmutableSet.copyOf(players);
    }
}
