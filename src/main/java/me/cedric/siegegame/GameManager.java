package me.cedric.siegegame;

import com.google.common.collect.ImmutableSet;
import me.cedric.siegegame.player.PlayerData;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;

import java.util.*;

public final class GameManager {

    private final Set<WorldGame> worlds = new HashSet<>();
    private final Queue<WorldGame> worldQueue = new LinkedList<>();
    private final SiegeGame plugin;
    private WorldGame currentMap;
    private WorldGame lastMap = null;

    public GameManager(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void removeWorld(WorldGame worldGame) {
        worlds.add(worldGame);
    }

    public void addWorld(WorldGame worldGame) {
        worlds.add(worldGame);
        worldQueue.add(worldGame);
    }

    public WorldGame getNextMap() {
        return worldQueue.peek();
    }

    public WorldGame getCurrentMap() {
        return currentMap;
    }

    public void startNextMap() {
        WorldGame worldGame = worldQueue.poll();
        worldGame.getBukkitWorld().setSpawnLocation(worldGame.getDefaultSpawnPoint());
        assignTeams(worldGame);

        for (Team team : worldGame.getTeams()) {
            for (PlayerData playerData : team.getPlayers()) {
                if (team.getTeamTown().getSpawnOrNull() == null) {
                    playerData.getBukkitPlayer().teleport(worldGame.getDefaultSpawnPoint());
                    System.out.println("TEAM " + team.getTeamTown().getName() + " DOES NOT HAVE A TOWN SPAWN. TELEPORTING TO WORLD SPAWN!");
                    continue;
                }

                playerData.getBukkitPlayer().teleport(team.getTeamTown().getSpawnOrNull());
            }
        }

        this.lastMap = currentMap;
        this.currentMap = worldGame;
        worldQueue.add(lastMap);
    }

    public void assignTeams(WorldGame worldGame) {
        Random r = new Random();
        ImmutableSet<PlayerData> players = plugin.getPlayerManager().getPlayers();

        int amountTeams = worldGame.getTeams().size();
        int amountPeople = players.size();

        int peoplePerTeam = amountPeople / amountTeams;

        List<PlayerData> remainingPlayers = new ArrayList<>();

        for (PlayerData playerData : players) {
            int i = r.nextInt(0, (amountTeams - 1));
            Team team = worldGame.getTeams().asList().get(i);

            if (team.getPlayers().size() >= peoplePerTeam) {
                remainingPlayers.add(playerData);
                continue;
            }

            team.addPlayer(playerData);
        }

        int i = 0;
        for (PlayerData player : remainingPlayers) {
            if (i >= (amountTeams - 1))
                i = 0;
            worldGame.getTeams().asList().get(i).addPlayer(player);
            i++;
        }

    }

    public WorldGame getLastMap() {
        return lastMap;
    }
}
