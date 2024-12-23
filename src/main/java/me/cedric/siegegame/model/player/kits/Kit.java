package me.cedric.siegegame.model.player.kits;

import de.tr7zw.nbtapi.NBTItem;
import me.cedric.siegegame.view.display.shop.ShopItem;
import me.cedric.siegegame.model.game.WorldGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kit {

    private final UUID uuid;
    private final String mapIdentifier;
    private final List<ItemStack> contents;

    public Kit(String mapIdentifier, List<ItemStack> contents, UUID kitUUID) {
        this.mapIdentifier = mapIdentifier;
        this.uuid = kitUUID;
        this.contents = contents;
    }

    public void setContents(List<ItemStack> contents) {
        this.contents.clear();
        this.contents.addAll(contents);
    }

    public void setContents(ItemStack[] contents, WorldGame worldGame) {
        setContents(contents(contents, worldGame));
    }

    public List<ItemStack> getContents() {
        return contents;
    }

    public String getMapIdentifier() {
        return mapIdentifier;
    }

    private static List<ItemStack> contents(ItemStack[] items, WorldGame worldGame) {
        List<ItemStack> newList = new ArrayList<>();
        for (ItemStack item : items.clone()) {
            if (item == null || item.getType().equals(Material.AIR)) {
                newList.add(null);
                continue;
            }

            ShopItem shopItem = getShopItem(item, worldGame);
            if (shopItem == null || shopItem.getPrice() > 0) {
                newList.add(new ItemStack(Material.AIR));
                continue;
            }

            if (shopItem.includesNBT())
                newList.add(shopItem.getDisplayItem());
            else
                newList.add(new ItemStack(shopItem.getDisplayItem().getType(), shopItem.getDisplayItem().getAmount()));
        }

        return newList;
    }

    private static ShopItem getShopItem(ItemStack item, WorldGame worldGame) {
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

    private static boolean hasNBTCompound(String compound, ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getCompound(compound) != null;
    }

    public UUID getKitUUID() {
        return uuid;
    }

    public static Kit fromInventory(ItemStack[] inventoryContents, WorldGame worldGame, String mapIdentifier) {
        List<ItemStack> itemContents = contents(inventoryContents, worldGame);
        return new Kit(mapIdentifier, itemContents, UUID.randomUUID());
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
