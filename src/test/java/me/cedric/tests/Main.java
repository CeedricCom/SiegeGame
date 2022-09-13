package me.cedric.tests;

import com.google.common.collect.Comparators;
import me.cedric.siegegame.GameManager;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.PlayerData;
import me.cedric.siegegame.player.PlayerManager;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Color;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class Main {

    private final SiegeGame siegeGame = new SiegeGame();
    private final GameManager manager = new GameManager(siegeGame);
    private final MockWorldGame mockWorldGame = new MockWorldGame();
    private final PlayerManager playerManager = new PlayerManager(siegeGame);

    @Test
    public void test() {
        fail();
        createTeams(mockWorldGame, 20);
        createPlayers(playerManager, 10);
        assertTrue(areTeamsFair(mockWorldGame));

        playerManager.clear();
        mockWorldGame.clear();
        createTeams(mockWorldGame, 2);
        createPlayers(playerManager, 10);
        assertTrue(areTeamsFair(mockWorldGame));

        playerManager.clear();
        mockWorldGame.clear();
        createTeams(mockWorldGame, 2);
        createPlayers(playerManager, 9);
        assertTrue(areTeamsFair(mockWorldGame));
    }

    private boolean areTeamsFair(WorldGame worldGame) {
        int players = playerManager.getPlayers().size();
        if (worldGame.getTeams().size() > players) {
            // if there are more teams than players, there should be only 1 player on each team and some with zero
            System.out.println("MORE TEAMS THAN PLAYERS");
            return worldGame.getTeams().stream().anyMatch(team -> team.getPlayers().size() > 1);
        }

        // players >= teams
        int mod = worldGame.getTeams().size() % players;
        if (mod == 0) {
            // FAIR FIGHTS (equal teams)
            int min = worldGame.getTeams().stream().min(Comparator.comparingInt(o -> o.getPlayers().size())).get().getPlayers().size();
            int max = worldGame.getTeams().stream().max(Comparator.comparingInt(o -> o.getPlayers().size())).get().getPlayers().size();
            System.out.println("FAIR FIGHTS. MIN: " + min + " MAX: " + max);
            return min == max;
        }

        // OUTNUMBERED FIGHTS - THERE SHOULDNT BE A DIFFERENCE OF MORE THAN 1 PERSON
        int min = worldGame.getTeams().stream().min(Comparator.comparingInt(o -> o.getPlayers().size())).get().getPlayers().size();
        int max = worldGame.getTeams().stream().max(Comparator.comparingInt(o -> o.getPlayers().size())).get().getPlayers().size();
        System.out.println("OUTNUMBERED FIGHTS. MIN: " + min + " MAX: " + max);
        return (min + 2) >= max;
    }

    private void createTeams(WorldGame worldGame, int howMany) {
        for (int i = 0; i < (howMany - 1); i++) {
            MockTeam mock = new MockTeam(worldGame, Color.fromRGB(0, i, 0), i + "", i + "");
            worldGame.addTeam(mock);
        }
    }

    private void createPlayers(PlayerManager manager, int howMany) {
        for (int i = 0; i < (howMany - 1); i++) {
            manager.addPlayer(UUID.randomUUID());
        }
    }

}
