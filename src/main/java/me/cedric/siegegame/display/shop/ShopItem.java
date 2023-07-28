package me.cedric.siegegame.display.shop;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopItem implements Buyable {

    private Purchase purchase;
    private final int price;
    private final int slot;
    private final ItemStack displayItem;
    private final boolean includesNBT;
    private String itemID;

    public ShopItem(Purchase purchase, String itemID, ItemStack displayItem, int price, int slot, boolean includesNBT) {
        this.purchase = purchase;
        this.price = price;
        this.slot = slot;
        this.includesNBT = includesNBT;
        this.displayItem = displayItem;
        this.itemID = itemID;
    }

    @Override
    public ItemStack getDisplayItem() {
        return displayItem.clone();
    }

    @Override
    public Purchase getPurchase() {
        return purchase;
    }

    public int getPrice() {
        return price;
    }

    public int getSlot() {
        return slot;
    }

    public boolean includesNBT() {
        return includesNBT;
    }

    public String getIdentifier() {
        return itemID;
    }

}
