package me.cedric.siegegame.display.shop;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.cedric.siegegame.SiegeGame;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private final List<ShopItem> shopItems = new ArrayList<>();
    private final SiegeGame plugin;
    private String guiName = "Shop";

    public ShopGUI(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void addItem(ShopItem button) {
        shopItems.add(button);
    }

    public void removeItem(ItemStack item) {
        shopItems.remove(item);
    }

    public List<ShopItem> getButtons() {
        return new ArrayList<>(shopItems);
    }

    public void initialize() {
        ChestGui chestGui = new ChestGui(9, guiName);
        StaticPane pane = new StaticPane(0, 0, 9, 6);

        for (ShopItem item : shopItems) {
            int x = item.getSlot();
            pane.addItem(new GuiItem(item.getItemStack()), x % 8, x / 6);
        }

        chestGui.addPane(new StaticPane(0, 0, 9, 6));
        chestGui.setOnGlobalClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));


    }

    public void setGUIName(String guiName) {
        this.guiName = guiName;
    }
}
