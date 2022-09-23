package me.cedric.siegegame.display.shop;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private final List<ShopItem> shopItems = new ArrayList<>();
    private final SiegeGame plugin;
    private String guiName = "Shop";
    private ChestGui chestGui;
    private StaticPane pane;
    private int rows = 3;

    public ShopGUI(SiegeGame plugin) {
        this.plugin = plugin;
        createGUI();
    }

    public void addItem(ShopItem button) {
        shopItems.add(button);
        this.pane.addItem(new GuiItem(button.getDisplayItem(), inventoryClickEvent -> {
            GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(inventoryClickEvent.getWhoClicked().getUniqueId());
            if (button.handlePurchase(gamePlayer)) {
                plugin.getLogger().info("Player " + gamePlayer.getBukkitPlayer().getName() + " has successfully bought " +
                        button.getDisplayItem().displayName() + " for " + button.getPrice());
            }
            inventoryClickEvent.setCancelled(true);
        }), button.getSlot() % 8, button.getSlot() / rows);
    }

    public void removeItem(ItemStack item) {
        shopItems.remove(item);
    }

    public List<ShopItem> getButtons() {
        return new ArrayList<>(shopItems);
    }

    private void createGUI() {
        this.chestGui = new ChestGui(rows, guiName);
        this.pane = new StaticPane(0, 0, 9, rows);
        chestGui.addPane(pane);
        chestGui.setOnGlobalClick(inventoryClickEvent -> inventoryClickEvent.setCancelled(true));
    }

    public void setGUIName(String guiName) {
        this.guiName = guiName;
    }

    public ChestGui getGUI() {
        return chestGui;
    }

}
