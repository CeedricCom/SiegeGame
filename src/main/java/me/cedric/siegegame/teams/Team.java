package me.cedric.siegegame.teams;

import com.google.common.collect.ImmutableSet;
import com.palmergames.bukkit.towny.object.Town;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.border.BoundingBox;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Color;
import org.bukkit.Location;

public interface Team {

    WorldGame getWorldGame();

    ImmutableSet<GamePlayer> getPlayers();

    String getName();

    void setName(String name);

    String getConfigKey();

    Town getTeamTown();

    Location getSafeSpawn();

    void setTown(Town town);

    Color getColor();

    void setColor(Color color);

    void addPlayer(GamePlayer player);

    void removePlayer(GamePlayer player);

    void clear();

    Border getSafeArea();

    int getPoints();

    void addPoints(int i);
}
