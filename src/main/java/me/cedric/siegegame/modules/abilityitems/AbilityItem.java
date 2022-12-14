package me.cedric.siegegame.modules.abilityitems;

import me.cedric.siegegame.SiegeGamePlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public interface AbilityItem {

    void onStartGame(SiegeGamePlugin plugin);

    ItemStack getItem();

    String getDisplayName();

    NamespacedKey getNamespacedKey();

    String getIdentifier();

    default boolean isItem(ItemStack item) {
        if (!item.hasItemMeta())
            return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(getNamespacedKey()))
            return false;

        return meta.getPersistentDataContainer().get(getNamespacedKey(), PersistentDataType.STRING).equalsIgnoreCase(getIdentifier());
    }

}
