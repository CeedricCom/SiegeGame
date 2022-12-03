package me.cedric.siegegame.modules.superitems;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.Module;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SuperItemManager implements Module {

    private final SiegeGamePlugin plugin;
    private final WorldGame worldGame;
    private final List<SuperItem> superItems = new ArrayList<>();

    public SuperItemManager(SiegeGamePlugin plugin, WorldGame worldGame) {
        this.plugin = plugin;
        this.worldGame = worldGame;
    }

    private void registerSuperItems() {
        superItems.add(new KnockbackStick(plugin, "kb-stick", worldGame, this));
        superItems.add(new SharpnessVI(plugin, "sharp-6", worldGame, this));

        for (SuperItem superItem : superItems)
            superItem.initialise(plugin);
    }

    public void clear() {
        superItems.clear();
    }

    public SuperItem getSuperItem(String key) {
        for (SuperItem superItem : superItems) {
            if (superItem.getKey().equalsIgnoreCase(key))
                return superItem;
        }
        return null;
    }

    public Set<SuperItem> getSuperItems() {
        return new HashSet<>(superItems);
    }

    public void assignSuperItems(boolean ignoreHasOwner) {

        int superItemCounter = 0;
        for (Team team : worldGame.getTeams()) {
            if (superItemCounter > superItems.size() - 1)
                continue;

            assignSuperItem(superItems.get(superItemCounter), team, ignoreHasOwner);

            if (superItems.stream().noneMatch(superItem -> superItem.getOwner() != null && superItem.getOwner().getTeam().equals(team))) {
                int finalSuperItemCounter = superItemCounter;
                team.getPlayers().stream().findAny().ifPresent(player -> superItems.get(finalSuperItemCounter).giveTo(player));
            }

            superItemCounter++;
        }
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

    public boolean hasSuperItem(GamePlayer gamePlayer) {
        return superItems.stream().anyMatch(superItem -> superItem.getOwner() != null &&
                superItem.getOwner().getUUID().equals(gamePlayer.getUUID()));
    }

    public boolean hasSuperItem(Team team) {
        return superItems.stream().anyMatch(superItem ->
                superItem.getOwner() != null &&
                superItem.getOwner().hasTeam() &&
                superItem.getOwner().getTeam().equals(team));
    }

    @Override
    public void initialise(SiegeGamePlugin plugin, WorldGame worldGame) {
        registerSuperItems();
    }

    @Override
    public void onStartGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        assignSuperItems(true);
    }

    @Override
    public void onEndGame(SiegeGamePlugin plugin, WorldGame worldGame) {
        for (SuperItem superItem : superItems)
            superItem.remove();
    }
}
