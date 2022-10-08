package me.cedric.siegegame.superitems;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.model.WorldGame;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.player.GamePlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SuperItemManager {

    private final SiegeGame plugin;
    private final WorldGame worldGame;
    private final List<SuperItem> superItems = new ArrayList<>();

    public SuperItemManager(SiegeGame plugin, WorldGame worldGame) {
        this.plugin = plugin;
        this.worldGame = worldGame;
    }

    public void addSuperItem(String key) {
        switch (key.toLowerCase()) {
            case "sharp-6": {
                superItems.add(new SharpnessVI(plugin, "sharp-6", worldGame));
                break;
            }
            case "kb-stick": {
                KnockbackStick stick = new KnockbackStick(plugin, "kb-stick", worldGame);
                superItems.add(stick);
                stick.initialize(plugin);
                break;
            }
        }
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
}
