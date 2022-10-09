package me.cedric.siegegame.display.shop;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import me.cedric.siegegame.model.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI {

    private final List<ShopItem> shopItems = new ArrayList<>();
    private final WorldGame worldGame;
    private String guiName = "Shop";
    private ChestGui chestGui;
    private StaticPane pane;
    private int rows = 3;

    public ShopGUI(WorldGame worldGame) {
        this.worldGame = worldGame;
        createGUI();
    }

    public void addItem(ShopItem button) {
        shopItems.add(button);
        this.pane.addItem(new GuiItem(button.getDisplayItem(), inventoryClickEvent -> {
            GamePlayer gamePlayer = worldGame.getPlayer(inventoryClickEvent.getWhoClicked().getUniqueId());
            if (gamePlayer != null)
                button.handlePurchase(gamePlayer);
            inventoryClickEvent.setCancelled(true);
        }), button.getSlot() % 9, button.getSlot() / 9);
    }

    public void removeItem(ItemStack item) {
        shopItems.removeIf(shopItem -> shopItem.getDisplayItem().equals(item));
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

    public void clear() {
        this.pane.clear();
    }

    public void setGUIName(String guiName) {
        this.guiName = guiName;
    }

    public ChestGui getGUI() {
        return chestGui;
    }

}
