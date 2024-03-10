package me.cedric.siegegame.view.display.placeholderapi;

import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLinePickupEvent;
import me.filoghost.holographicdisplays.api.hologram.line.HologramLinePickupListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlaceholderPickupListener implements HologramLinePickupListener {
    private final ItemStack item;
    private final Hologram hologram;

    public PlaceholderPickupListener(ItemStack item, Hologram hologram) {
        this.item = item;
        this.hologram = hologram;
    }

    @Override
    public void onPickup(HologramLinePickupEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().firstEmpty() != -1)
            player.getInventory().addItem(item);
        else
            player.getWorld().dropItem(player.getLocation(), item);
        hologram.delete();
    }
}
