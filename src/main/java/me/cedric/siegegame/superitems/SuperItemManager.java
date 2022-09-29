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
        superItems.add(new SharpnessVI(plugin, "sharp-6"));
        superItems.add(new KnockbackStick(plugin, "kb-stick"));

        for (SuperItem item : superItems) {
            item.initialize(plugin);
        }
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
}
