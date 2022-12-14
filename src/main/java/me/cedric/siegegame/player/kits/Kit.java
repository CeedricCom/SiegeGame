package me.cedric.siegegame.player.kits;

import org.bukkit.inventory.ItemStack;

public class Kit {

    private final String mapIdentifier;
    private ItemStack[] contents;

    public Kit(ItemStack[] contents, String mapIdentifier) {
        this.contents = contents;
        this.mapIdentifier = mapIdentifier;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public String getMapIdentifier() {
        return mapIdentifier;
    }
}
