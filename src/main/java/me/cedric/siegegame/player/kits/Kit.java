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

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;

        if (!(that instanceof Kit other))
            return false;

        return other.getMapIdentifier().equalsIgnoreCase(getMapIdentifier());
    }
}
