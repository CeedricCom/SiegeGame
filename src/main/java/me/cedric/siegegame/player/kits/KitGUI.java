package me.cedric.siegegame.player.kits;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import me.deltaorion.bukkit.item.ItemBuilder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitGUI {

    private final SiegeGamePlugin plugin;
    private final WorldGame worldGame;

    public KitGUI(SiegeGamePlugin plugin, WorldGame worldGame) {
        this.plugin = plugin;
        this.worldGame = worldGame;
        createGUI();
    }

    private ChestGui chestGui;

    public void createGUI() {
        chestGui = new ChestGui(3, "Kits");
        StaticPane staticPane = new StaticPane(9, 3);
        GuiItem item = new GuiItem(new ItemBuilder(Material.BOOK)
                .setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Kits information")
                .addLoreLine("")
                .addLoreLine(ChatColor.YELLOW + "Click on the green concrete")
                .addLoreLine(ChatColor.YELLOW + "to save your current")
                .addLoreLine(ChatColor.YELLOW + "inventory as your default kit")
                .addLoreLine("")
                .addLoreLine(ChatColor.YELLOW + "This kit will be set automatically ")
                .addLoreLine(ChatColor.YELLOW + "when you respawn")
                .build(), event -> event.setCancelled(true));

        GuiItem greenPanel = new GuiItem(new ItemBuilder(Material.GREEN_CONCRETE)
                .setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Set kit")
                .addLoreLine("")
                .addLoreLine(ChatColor.GREEN + "Click here to set your current")
                .addLoreLine(ChatColor.GREEN + "inventory as your default kit.")
                .build(),
                event -> {
                    Player player = (Player) event.getWhoClicked();
                    GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());
                    ItemStack[] inventory = player.getInventory().getContents();
                    gamePlayer.getPlayerKitManager().setKit(inventory, worldGame, "all");
                    player.sendMessage(ChatColor.GREEN + "Kit set!");
                    player.playSound(Sound.sound(Key.key("entity.player.levelup"), Sound.Source.PLAYER, 1.0F, 1.5F));
                    player.closeInventory();
                    event.setCancelled(true);
        });

        staticPane.addItem(item, 4, 0);
        staticPane.addItem(greenPanel, 4, 2);
        staticPane.fillWith(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("").build(), event -> event.setCancelled(true));
        chestGui.addPane(staticPane);
    }

    public ChestGui getChestGUI() {
        return chestGui.copy();
    }
}























