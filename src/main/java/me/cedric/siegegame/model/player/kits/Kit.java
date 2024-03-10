package me.cedric.siegegame.model.player.kits;

import de.tr7zw.nbtapi.NBTItem;
import me.cedric.siegegame.view.display.shop.ShopGUI;
import me.cedric.siegegame.view.display.shop.ShopItem;
import me.cedric.siegegame.model.game.WorldGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kit {

    private final String mapIdentifier;
    private final String rawString;
    private final UUID uuid;
    private List<ShopItem> contents = new ArrayList<>();

    public Kit(String mapIdentifier, String rawString, UUID kitUUID) {
        this.mapIdentifier = mapIdentifier;
        this.uuid = kitUUID;
        this.rawString = rawString;
    }

    public void setContents(ItemStack[] contents, WorldGame worldGame) {
        this.contents = contents(contents, worldGame);
    }

    public void populateFromRawString(WorldGame worldGame) {
        if (!contents.isEmpty())
            return;

        String[] items = rawString.split(",");
        ShopGUI shopGUI = worldGame.getShopGUI();
        List<ShopItem> shopItems = new ArrayList<>();

        for (String item : items) {
            String[] s = item.split("-");

            String itemID = s[0];
            int slot = Integer.parseInt(s[1]);

            if (itemID.equalsIgnoreCase("empty")) {
                shopItems.add(slot, null);
                continue;
            }

            ShopItem shopItem = shopGUI.getItem(itemID);

            if (shopItem == null || shopItem.getPrice() > 0)
                continue;

            shopItems.add(slot, shopGUI.getItem(itemID));
        }

        this.contents = shopItems;
    }

    public void setContents(List<ShopItem> contents) {
        this.contents = contents;
    }

    public List<ShopItem> getContents() {
        return new ArrayList<>(contents);
    }

    public ItemStack[] getInventoryContents() {
        List<ItemStack> items = new ArrayList<>();
        for (ShopItem shopItem : getContents()) {
            if (shopItem == null) {
                items.add(null);
                continue;
            }

            if (shopItem.includesNBT())
                items.add(shopItem.getDisplayItem());
            else
                items.add(new ItemStack(shopItem.getDisplayItem().getType(), shopItem.getDisplayItem().getAmount()));
        }

        return items.toArray(ItemStack[]::new);
    }

    public String getMapIdentifier() {
        return mapIdentifier;
    }

    public String getRawString() {
        return rawString;
    }

    private static List<ShopItem> contents(ItemStack[] items, WorldGame worldGame) {
        List<ShopItem> newList = new ArrayList<>();
        for (ItemStack item : items.clone()) {
            if (item == null || item.getType().equals(Material.AIR)) {
                newList.add(null);
                continue;
            }

            ShopItem shopItem = getShopItem(item, worldGame);
            if (shopItem == null || shopItem.getPrice() > 0) {
                newList.add(null);
                continue;
            }

            newList.add(shopItem);
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

    public static Kit fromInventory(ItemStack[] contents, WorldGame worldGame, String mapIdentifier) {
        List<ShopItem> shopItemContents = contents(contents, worldGame);
        StringBuilder items = new StringBuilder();

        int i = 0;
        for (ShopItem shopItem : shopItemContents) {
            if (shopItem == null)
                items.append("empty-").append(i);
            else
                items.append(shopItem.getIdentifier()).append("-").append(i);

            if (i < (contents.length - 1))
                items.append(",");

            i++;
        }

        return new Kit(mapIdentifier, items.toString(), UUID.randomUUID());
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
