package me.cedric.siegegame.modules.abilityitems.items;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.modules.abilityitems.AbilityItem;
import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class QxtiStick implements AbilityItem {

    private NamespacedKey namespacedKey;

    @Override
    public void initialise(SiegeGamePlugin plugin) {
        namespacedKey = new NamespacedKey(plugin, "siegegame");
    }

    @Override
    public ItemStack getItem() {
        ItemBuilder itemBuilder = new ItemBuilder(EMaterial.STICK)
                .addEnchantment(Enchantment.KNOCKBACK, 5)
                .addLoreLine(ChatColor.YELLOW + "Use this to knock enemies")
                .addLoreLine(ChatColor.YELLOW + "away from you!")
                .setDisplayName(getDisplayName());
        ItemStack item = itemBuilder.build();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(getNamespacedKey(), PersistentDataType.STRING, getIdentifier());
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.AQUA + "qxti stick";
    }

    @Override
    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public String getIdentifier() {
        return "qxti-stick";
    }
}
