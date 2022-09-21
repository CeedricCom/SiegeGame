package me.cedric.siegegame.teams;

import com.google.common.collect.ImmutableSet;
import com.palmergames.bukkit.towny.object.Town;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Color;

import java.util.HashSet;
import java.util.Set;

public class TeamImpl implements Team {

    private Color color;
    private Town town;
    private String name;
    private final WorldGame worldGame;
    private final String configKey;
    private final Set<GamePlayer> players = new HashSet<>();

    public TeamImpl(WorldGame worldGame, Color color, Town town, String name, String configKey) {
        this.color = color;
        this.town = town;
        this.name = name;
        this.configKey = configKey;
        this.worldGame = worldGame;
    }

    @Override
    public ImmutableSet<GamePlayer> getPlayers() {
        return ImmutableSet.copyOf(players);
    }

    @Override
    public WorldGame getWorldGame() {
        return worldGame;
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
    public Town getTeamTown() {
        return town;
    }

    @Override
    public void setTown(Town town) {
        this.town = town;
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
        player.setTeam(this);
    }

    @Override
    public void removePlayer(GamePlayer player) {
        players.remove(player);
    }

    @Override
    public void clear() {
        players.clear();
    }
}
