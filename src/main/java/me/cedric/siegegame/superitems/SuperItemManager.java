package me.cedric.siegegame.superitems;

import me.cedric.siegegame.SiegeGame;

import java.util.HashSet;
import java.util.Set;

public class SuperItemManager {

    private final SiegeGame plugin;

    public SuperItemManager(SiegeGame plugin) {
        this.plugin = plugin;
    }

    private final Set<SuperItem> superItems = new HashSet<>();

    public void initialize() {
        superItems.add(new SharpnessVI(plugin));

        for (SuperItem item : superItems) {
            item.initialize(plugin);
        }
    }

    public Set<SuperItem> getSuperItems() {
        return new HashSet<>(superItems);
    }
}
