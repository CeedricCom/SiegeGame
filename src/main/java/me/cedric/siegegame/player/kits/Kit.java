package me.cedric.siegegame.player.kits;

import de.tr7zw.nbtapi.NBTItem;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.model.game.WorldGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kit {

    private final String mapIdentifier;
    private final UUID uuid;
    private ItemStack[] contents = new ItemStack[0];

    public Kit(String mapIdentifier, UUID kitUUID) {
        this.mapIdentifier = mapIdentifier;
        this.uuid = kitUUID;
    }

    public ItemStack[] getContents() {
        return copyInventory(contents);
    }

    public void setContents(ItemStack[] contents, WorldGame worldGame) {
        this.contents = removePaidItems(contents, worldGame);
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents.clone();
    }

    public String getMapIdentifier() {
        return mapIdentifier;
    }

    private ItemStack[] removePaidItems(ItemStack[] items, WorldGame worldGame) {
        List<ItemStack> newList = new ArrayList<>();
        for (ItemStack item : items.clone()) {

            if (item == null || item.getType().equals(Material.AIR)) {
                newList.add(new ItemStack(Material.AIR));
                continue;
            }

            if (isFree(item.clone(), worldGame))
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

            if (!shopItem.includesNBT() && shopItem.getDisplayItem().getType().equals(item.getType()))
                return shopItem;

            if (!hasNBTCompound("siegegame-item", item.clone()))
                continue;

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

    public UUID getKitUUID() {
        return uuid;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;

        if (!(that instanceof Kit other))
            return false;

        return other.getMapIdentifier().equalsIgnoreCase(getMapIdentifier());
    }

    public static ItemStack[] copyInventory(Player player) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                items.add(new ItemStack(Material.AIR));
                continue;
            }

            items.add(itemStack.clone());
        }

        return items.toArray(new ItemStack[0]);
    }

    public static ItemStack[] copyInventory(ItemStack[] inventory) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack itemStack : inventory) {
            if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
                items.add(new ItemStack(Material.AIR));
                continue;
            }

            items.add(itemStack.clone());
        }

        return items.toArray(new ItemStack[0]);
    }
}
