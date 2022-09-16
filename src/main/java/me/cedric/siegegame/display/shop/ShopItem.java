package me.cedric.siegegame.display.shop;

import org.bukkit.inventory.ItemStack;

public class ShopItem implements Buyable {

    private final ItemStack item;
    private final int price;
    private final int slot;

    public ShopItem(ItemStack item, int price, int slot) {
        this.item = item;
        this.price = price;
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public int getSlot() {
        return slot;
    }
}
