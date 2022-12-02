package me.cedric.siegegame.modules.abilityitems.items;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.modules.abilityitems.AbilityItem;
import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MortyAntiQuickie implements AbilityItem {

    private NamespacedKey namespacedKey;

    @Override
    public void initialise(SiegeGamePlugin plugin) {
        namespacedKey = new NamespacedKey(plugin, "siegegame");
    }

    @Override
    public ItemStack getItem() {
        ItemBuilder builder = new ItemBuilder(EMaterial.TOTEM_OF_UNDYING)
                .addLoreLine(ChatColor.DARK_GRAY + "Functions as a regular Totem of Undying.")
                .addLoreLine(ChatColor.YELLOW + "Hold this to revive yourself and ")
                .addLoreLine(ChatColor.YELLOW + "add absorption hearts")
                .setDisplayName(getDisplayName());
        ItemStack item = builder.build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(getNamespacedKey(), PersistentDataType.STRING, getIdentifier());
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.LIGHT_PURPLE + "Morty's Anti Quickdrop";
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public String getIdentifier() {
        return "morty-antiquickie";
    }
}
