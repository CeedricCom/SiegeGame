package me.cedric.siegegame.display.shop;

import org.bukkit.inventory.ItemStack;

public interface Buyable {

    ItemStack getItemStack();

    int getPrice();

}
