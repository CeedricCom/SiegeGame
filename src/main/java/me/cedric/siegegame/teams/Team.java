package me.cedric.siegegame.teams;

import com.google.common.collect.ImmutableSet;
import com.palmergames.bukkit.towny.object.Town;
import me.cedric.siegegame.player.PlayerData;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Color;

public interface Team {

    WorldGame getWorldGame();

    ImmutableSet<PlayerData> getPlayers();

    String getName();

    void setName(String name);

    String getConfigKey();

    Town getTeamTown();

    void setTown(Town town);

    Color getColor();

    void setColor(Color color);

    void addPlayer(PlayerData player);

    void removePlayer(PlayerData player);

    void clear();

}
