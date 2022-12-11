package me.cedric.siegegame.player.kits;

import de.tr7zw.nbtapi.NBTItem;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KitStorage {

    private final HashMap<UUID, ItemStack[]> kits = new HashMap<>();
    private final SiegeGamePlugin plugin;

    public KitStorage(SiegeGamePlugin plugin) {
        this.plugin = plugin;
    }

    public void setKit(UUID uuid, ItemStack[] inventory) {
        SiegeGameMatch match = plugin.getGameManager().getCurrentMatch();
        ItemStack[] contents = removePaidItems(inventory, match.getWorldGame());
        if (!kits.containsKey(uuid))
            kits.put(uuid, contents);
        else
            kits.replace(uuid, contents);
    }

    public void resetKit(UUID uuid) {
        kits.remove(uuid);
    }

    public ItemStack[] getKit(UUID uuid) {
        return kits.get(uuid);
    }

    private ItemStack[] removePaidItems(ItemStack[] items, WorldGame worldGame) {
        List<ItemStack> newList = new ArrayList<>();
        for (ItemStack item : items) {

            if (item == null || item.getType().equals(Material.AIR)) {
                newList.add(new ItemStack(Material.AIR));
                continue;
            }

            if (isFree(item, worldGame))
                newList.add(item);
            else
                newList.add(new ItemStack(Material.AIR));
        }

        return newList.toArray(new ItemStack[0]);
    }

    private boolean isFree(ItemStack itemStack, WorldGame worldGame) {

        NBTItem nbtItem = new NBTItem(itemStack.clone());
        for (ShopItem shopItem : worldGame.getShopGUI().getShopItems()) {
            if (shopItem.getPrice() <= 0)
                continue;

            String compound = "siegegame-item";
            if (hasNBTCompound(compound, itemStack.clone())) {
                // item provided has NBT but shop item doesnt - simply not a match - continue
                if (!hasNBTCompound(compound, shopItem.getDisplayItem())) // This should never be true if config loader works
                    continue;

                NBTItem shopNBT = new NBTItem(shopItem.getDisplayItem());
                String identifier = nbtItem.getCompound("siegegame-item").getString("identifier");
                String shopItemID = shopNBT.getCompound("siegegame-item").getString("identifier");

                if (identifier.equalsIgnoreCase(shopItemID))
                    return false;

            } else {
                // Item does not have NBT
                // return if they are the same type
                if (!shopItem.includesNBT()) // both dont have NBT
                    return !shopItem.getDisplayItem().getType().equals(itemStack.getType());
            }
        }
        return true;
    }

    private boolean hasNBTCompound(String compound, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getCompound(compound) != null;
    }

    public boolean hasKit(UUID uuid) {
        return kits.containsKey(uuid);
    }

}
