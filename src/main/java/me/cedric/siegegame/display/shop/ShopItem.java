package me.cedric.siegegame.display.shop;

import org.bukkit.inventory.ItemStack;

public class ShopItem implements Buyable {

    private Purchase purchase;
    private final int price;
    private final int slot;
    private final ItemStack displayItem;

    public ShopItem(Purchase purchase, ItemStack displayItem, int price, int slot) {
        this.purchase = purchase;
        this.price = price;
        this.slot = slot;
        this.displayItem = displayItem;
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
}
