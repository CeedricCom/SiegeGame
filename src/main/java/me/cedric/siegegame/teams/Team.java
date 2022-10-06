package me.cedric.siegegame.teams;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.territory.Territory;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Location;

import java.awt.Color;

public interface Team {

    WorldGame getWorldGame();

    ImmutableSet<GamePlayer> getPlayers();

    String getName();

    void setName(String name);

    String getConfigKey();

    Territory getTerritory();

    Location getSafeSpawn();

    Color getColor();

    void setColor(Color color);

    void addPlayer(GamePlayer player);

    void removePlayer(GamePlayer player);

    void clear();

    Border getSafeArea();

    int getPoints();

    void addPoints(int i);
}
