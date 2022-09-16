package me.cedric.tests;

import me.cedric.siegegame.GameManager;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.config.ConfigLoader;
import me.cedric.siegegame.player.PlayerManager;
import me.cedric.siegegame.world.WorldGame;
import me.deltaorion.common.config.FileConfig;
import me.deltaorion.common.config.InvalidConfigurationException;
import me.deltaorion.common.config.yaml.YamlAdapter;
import org.bukkit.Color;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Comparator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {

    private final SiegeGame siegeGame = null;
    private final GameManager manager = new GameManager(siegeGame);
    private final MockWorldGame mockWorldGame = new MockWorldGame();
    private final PlayerManager playerManager = new PlayerManager(siegeGame);

    @Test
    public void teamAssignmentTests() {
        createTeams(mockWorldGame, 20);
        createPlayers(playerManager, 10);
        manager.assignTeams(mockWorldGame, playerManager.getPlayers(), playerManager);
        assertTrue(areTeamsFair(mockWorldGame));
        //System.out.println("TEST 1 PASSED");

        playerManager.clear();
        mockWorldGame.clear();
        createTeams(mockWorldGame, 2);
        createPlayers(playerManager, 10);
        manager.assignTeams(mockWorldGame, playerManager.getPlayers(), playerManager);
        assertTrue(areTeamsFair(mockWorldGame));

        //System.out.println("TEST 2 PASSED");

        playerManager.clear();
        mockWorldGame.clear();
        createTeams(mockWorldGame, 5);
        createPlayers(playerManager, 9);
        manager.assignTeams(mockWorldGame, playerManager.getPlayers(), playerManager);
        assertTrue(areTeamsFair(mockWorldGame));

        //System.out.println("TEST 3 PASSED");
    }

    private boolean areTeamsFair(WorldGame worldGame) {
        int players = playerManager.getPlayers().size();
        if (worldGame.getTeams().size() > players) {
            // if there are more teams than players, there should be only 1 player on each team and some with zero
            //System.out.println("MORE TEAMS THAN PLAYERS");
            return worldGame.getTeams().stream().allMatch(team -> team.getPlayers().size() <= 1);
        }

        // players >= teams
        int mod = players % worldGame.getTeams().size();
        if (mod == 0) {
            // FAIR FIGHTS (equal teams)
            // All teams should have the same number of people. This number is players / amountOfTeams
            // therefore all lengths of team's players lists should match that number
            //System.out.println("FAIR FIGHTS: HERE ARE TEAM SIZES:");
            //worldGame.getTeams().forEach(team -> System.out.println(team.getPlayers().size()));
            return worldGame.getTeams().stream().allMatch(team -> team.getPlayers().size() == (players / worldGame.getTeams().size()));
        }

        // OUTNUMBERED FIGHTS - THERE SHOULDNT BE A DIFFERENCE OF MORE THAN 1 PERSON
        int min = worldGame.getTeams().stream().min(Comparator.comparingInt(o -> o.getPlayers().size())).get().getPlayers().size();
        int max = worldGame.getTeams().stream().max(Comparator.comparingInt(o -> o.getPlayers().size())).get().getPlayers().size();
        //System.out.println("OUTNUMBERED FIGHTS. MIN: " + min + " MAX: " + max);
        return (min + 1) == max;
    }

    private void createTeams(WorldGame worldGame, int howMany) {
        for (int i = 0; i < howMany; i++) {
            MockTeam mock = new MockTeam(worldGame, Color.fromRGB(0, i, 0), i + "", i + "");
            worldGame.addTeam(mock);
        }
    }

    private void createPlayers(PlayerManager manager, int howMany) {
        for (int i = 0; i < howMany; i++) {
            manager.addPlayer(UUID.randomUUID());
        }
    }

}
