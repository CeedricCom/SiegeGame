package me.cedric.siegegame;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import me.cedric.siegegame.config.Settings;
import me.cedric.siegegame.display.Displayer;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Comparator;
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

        this.currentMap = worldGame;
        if (!worldGame.getGameMap().isLoaded()) {
            worldGame.getGameMap().load();
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            worldGame.getBukkitWorld().setSpawnLocation(worldGame.getDefaultSpawnPoint());
            assignTeams(worldGame);

            for (Team team : worldGame.getTeams()) {
                for (GamePlayer gamePlayer : team.getPlayers()) {
                    gamePlayer.getBukkitPlayer().teleport(team.getSafeSpawn());
                    gamePlayer.getBukkitPlayer().setLevel(0);
                    gamePlayer.getBukkitPlayer().getInventory().clear();
                    gamePlayer.getBukkitPlayer().getEnderChest().clear();
                    Displayer.updateScoreboard(plugin, gamePlayer, currentMap);
                }
            }

            assignSuperItems(true);

            plugin.getPlayerManager().getPlayers().forEach(gamePlayer -> gamePlayer.getBorderHandler().addBorder(worldGame.getBorder()));
            worldGame.getTeams().forEach(team -> plugin.getPlayerManager().getPlayers().forEach(gamePlayer -> gamePlayer.getBorderHandler().addBorder(team.getSafeArea())));

            for (String s : (List<String>) Settings.START_GAME_COMMANDS.getValue()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
            }

            if (this.lastMap != null)
                this.lastMap.getGameMap().restoreFromSource();
        }, wait ? 30 * 20 : 20);
    }

    public void assignSuperItems(boolean ignoreHasOwner) {
        if (currentMap == null)
            return;

        int superItemCounter = 0;
        for (Team team : currentMap.getTeams()) {
            if (superItemCounter > currentMap.getSuperItems().size() - 1)
                continue;

            assignSuperItem(currentMap.getSuperItems().get(superItemCounter), team, ignoreHasOwner);

            if (currentMap.getSuperItems().stream().noneMatch(superItem -> superItem.getOwner() != null && superItem.getOwner().getTeam().equals(team))) {
                int finalSuperItemCounter = superItemCounter;
                team.getPlayers().stream().findAny().ifPresent(player -> currentMap.getSuperItems().get(finalSuperItemCounter).giveTo(player));
            }

            superItemCounter++;
        }
    }

    public boolean hasSuperItem(Team team) {
        for (GamePlayer gamePlayer : team.getPlayers()) {
            for (SuperItem superItem : plugin.getSuperItemManager().getSuperItems()) {
                if (superItem.getOwner() != null && superItem.getOwner().getUUID().equals(gamePlayer.getUUID()))
                    return true;
            }
        }

        return false;
    }

    public void assignSuperItem(SuperItem superItem, Team team) {
        assignSuperItem(superItem, team, true);
    }

    public void assignSuperItem(SuperItem superItem, Team team, boolean ignoreHasOwner) {
        int chance = 100 / (team.getPlayers().size() == 1 || team.getPlayers().size() == 0 ? 1 : team.getPlayers().size() - 1);
        for (GamePlayer gamePlayer : team.getPlayers()) {
            Random r = new Random();
            if (r.nextInt(0, 100) <= chance) {
                if (superItem.getOwner() == null)
                    superItem.giveTo(gamePlayer);
                else {
                    // has owner
                    if (ignoreHasOwner)
                        superItem.giveTo(gamePlayer);
                }
                break;
            }
        }
    }

    private void endGame(WorldGame worldGame) {
        List<GamePlayer> players = plugin.getPlayerManager().getPlayers();

        for (GamePlayer gamePlayer : players) {
            if (gamePlayer.hasTeam() && gamePlayer.getTeam().getPoints() >= (int) Settings.POINTS_TO_END.getValue()) {
                gamePlayer.getBukkitPlayer().sendTitle(ChatColor.GOLD + "" + ChatColor.BOLD + "VICTORY", ChatColor.YELLOW + "gg ez yall are dog z tier rands");
            } else {
                gamePlayer.getBukkitPlayer().sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "DEFEAT", ChatColor.RED + "L gg random");
            }

            Resident resident = TownyUniverse.getInstance().getResident(gamePlayer.getUUID());
            if (resident != null)
                resident.removeTown();
            gamePlayer.getBukkitPlayer().setLevel(0);
            gamePlayer.getBorderHandler().clear();
            gamePlayer.getTeam().addPoints(-gamePlayer.getTeam().getPoints());
            gamePlayer.getTeam().removePlayer(gamePlayer);
            gamePlayer.getBukkitPlayer().getInventory().clear();
            gamePlayer.getBukkitPlayer().getEnderChest().clear();
            Displayer.wipeScoreboard(plugin, gamePlayer);

            gamePlayer.getBukkitPlayer().sendMessage(ChatColor.DARK_AQUA + "[ceedric.com]" + ChatColor.GOLD + "on gaia gods i would fk u up on eu boxing 1v1 z tier fkin rand dogs i swear my zuesimortal clicker can put u in 30 hit combo like dog random , I AM GAIA DEMON please dont disrespect me fkin dog rand i am known gaia player i swear on morudias gods ur a fkin rand ON HYTES ur my fkin dog Z tier dog randoms");
        }

        for (SuperItem superItem : worldGame.getSuperItems()) {
            superItem.remove();
        }

        lastMap = worldGame;
        worldQueue.add(worldGame);
        currentMap = null;

        for (String s : (List<String>) Settings.END_GAME_COMMANDS.getValue()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        }
    }

    public void updateAllScoreboards() {
        if (currentMap == null)
            return;

        for (Team team : currentMap.getTeams()) {
            for (GamePlayer gamePlayer : team.getPlayers()) {
                Displayer.updateScoreboard(plugin, gamePlayer, currentMap);
            }
        }
    }

    public void assignRandomTeams(WorldGame worldGame, List<GamePlayer> players) {
        Random r = new Random();

        List<GamePlayer> list = new ArrayList<>(players);
        Set<Team> teams = worldGame.getTeams();

        while (list.size() != 0) {
            for (Team team : teams) {
                if (list.size() == 0)
                    break;

                int chosenPlayer = list.size() == 1 ? 0 : r.nextInt(0, list.size() - 1);
                GamePlayer player = list.get(chosenPlayer);
                assignTeam(player, team);
                list.remove(chosenPlayer);
            }
        }
    }

    public void assignTeam(GamePlayer player) {
        // assign player to team with least amount of players if a team isnt chosen
        getCurrentMap().getTeams().stream()
                .min(Comparator.comparingInt(value -> value.getPlayers().size()))
                .ifPresent(selected -> assignTeam(player, selected));
    }

    private void assignTeam(GamePlayer player, Team team) {
        Resident resident = TownyAPI.getInstance().getResident(player.getUUID());
        if (resident != null) {
            try {
                resident.setTown(team.getTeamTown());
            } catch (AlreadyRegisteredException ignored) {}
        }

        team.addPlayer(player);

        for (Team t : getCurrentMap().getTeams()) {
            player.getBorderHandler().addBorder(t.getSafeArea());
        }
        player.getBorderHandler().addBorder(getCurrentMap().getBorder());

        player.getBukkitPlayer().addPotionEffect(
                new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false)
        );

        player.getBukkitPlayer().sendMessage(ChatColor.DARK_AQUA + "You have been assigned to the following team: " + team.getName());
    }

    public void assignTeams(WorldGame worldGame) {
        assignRandomTeams(worldGame, plugin.getPlayerManager().getPlayers());
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
