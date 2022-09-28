package me.cedric.siegegame;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.EmptyTownException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

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

    public boolean isOngoingGame() {
        return getCurrentMap() != null;
    }

    public void startNextMap() {
        boolean wait = false;
        if (getCurrentMap() != null) {
            endGame(getCurrentMap());
            wait = true;
        }
        WorldGame worldGame = worldQueue.poll();

        if (worldGame == null) {
            plugin.getLogger().severe("NO MAP AT THE HEAD OF QUEUE. COULD NOT START GAME");
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            worldGame.getBukkitWorld().setSpawnLocation(worldGame.getDefaultSpawnPoint());
            assignTeams(worldGame);

            for (Team team : worldGame.getTeams()) {
                for (GamePlayer gamePlayer : team.getPlayers()) {
                    if (team.getTeamTown().getSpawnOrNull() == null) {
                        gamePlayer.getBukkitPlayer().teleport(worldGame.getDefaultSpawnPoint());
                        plugin.getLogger().info("TEAM " + team.getTeamTown().getName() + " DOES NOT HAVE A TOWN SPAWN. TELEPORTING TO WORLD SPAWN!");
                        continue;
                    }

                    gamePlayer.getBukkitPlayer().teleport(team.getTeamTown().getSpawnOrNull());
                }
            }

            plugin.getPlayerManager().getPlayers().forEach(gamePlayer -> gamePlayer.getBorderHandler().addBorder(worldGame.getBorder()));
            worldGame.getTeams().forEach(team -> plugin.getPlayerManager().getPlayers().forEach(gamePlayer -> gamePlayer.getBorderHandler().addBorder(team.getSafeArea())));

            this.currentMap = worldGame;
            if (this.lastMap != null)
                this.lastMap.getGameMap().restoreFromSource();
        }, wait ? 30 * 20 : 20);
    }

    private void endGame(WorldGame worldGame) {
        List<GamePlayer> players = plugin.getPlayerManager().getPlayers();

        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.hasTeam() && gamePlayer.getTeam().getPoints() == SiegeGame.POINTS_TO_END) {
                gamePlayer.getBukkitPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "VICTORY", ChatColor.YELLOW + "L dogs im S tier player");
            } else {
                gamePlayer.getBukkitPlayer().sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "DEFEAT", ChatColor.RED + "L gg random");
            }

            Resident resident = TownyUniverse.getInstance().getResident(gamePlayer.getUUID());
            if (resident != null)
                resident.removeTown();
            gamePlayer.getBukkitPlayer().setLevel(0);
            gamePlayer.getBorderHandler().clear();
            gamePlayer.getTeam().addPoints(-gamePlayer.getTeam().getPoints());
            gamePlayer.setTeam(null);
            gamePlayer.getBukkitPlayer().getInventory().clear();
        }

        lastMap = worldGame;
        worldQueue.add(worldGame);
        currentMap = null;
    }

    public void assignTeams(WorldGame worldGame, List<GamePlayer> players) {
        Random r = new Random();

        List<GamePlayer> list = new ArrayList<>(players);
        Set<Team> teams = worldGame.getTeams();

        while (list.size() != 0) {
            for (Team team : teams) {
                if (list.size() == 0)
                    break;

                int chosenPlayer = list.size() == 1 ? 0 : r.nextInt(0, list.size() - 1);
                GamePlayer player = list.get(chosenPlayer);

                Resident resident = TownyAPI.getInstance().getResident(player.getUUID());
                if (resident != null) {
                    try {
                        resident.setTown(team.getTeamTown());
                    } catch (AlreadyRegisteredException ignored) {}
                }

                team.addPlayer(player);
                list.remove(chosenPlayer);
            }
        }
    }

    public void assignToATeam(WorldGame worldGame, GamePlayer player) {

    }

    public void assignTeams(WorldGame worldGame) {
        assignTeams(worldGame, plugin.getPlayerManager().getPlayers());
    }

    public WorldGame getLastMap() {
        return lastMap;
    }

    public WorldGame getWorldGame(World world) {
        return worlds.stream().filter(worldGame -> worldGame.getBukkitWorld() != null &&
                worldGame.getBukkitWorld().equals(world)).findFirst().orElse(null);
    }

    public Set<WorldGame> getLoadedWorlds() {
        return new HashSet<>(worlds);
    }
}
