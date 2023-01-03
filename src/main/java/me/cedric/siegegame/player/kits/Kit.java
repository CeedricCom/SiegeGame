package me.cedric.siegegame.player.kits;

import de.tr7zw.nbtapi.NBTItem;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.model.game.WorldGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private final String mapIdentifier;
    private ItemStack[] contents = new ItemStack[0];

    public Kit(String mapIdentifier) {
        this.mapIdentifier = mapIdentifier;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents, WorldGame worldGame) {
        this.contents = removePaidItems(contents, worldGame);
    }

    public String getMapIdentifier() {
        return mapIdentifier;
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
        ShopItem shopItem = getShopItem(itemStack, worldGame);
        return shopItem != null && shopItem.getPrice() <= 0;
    }

    private ShopItem getShopItem(ItemStack item, WorldGame worldGame) {
        for (ShopItem shopItem : worldGame.getShopGUI().getShopItems()) {
            if (!hasNBTCompound("siegegame-item", item.clone()))
                return null;

            NBTItem shopNBT = new NBTItem(shopItem.getDisplayItem());
            NBTItem nbtItem = new NBTItem(item.clone());

            String identifier = nbtItem.getCompound("siegegame-item").getString("identifier");
            String shopItemID = shopNBT.getCompound("siegegame-item").getString("identifier");

            if (identifier.equalsIgnoreCase(shopItemID))
                return shopItem;
        }

        return null;
    }

    private boolean hasNBTCompound(String compound, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getCompound(compound) != null;
    }
}
