package me.cedric.siegegame.modules.abilityitems;

import me.cedric.siegegame.view.display.placeholderapi.PlaceholderPickupListener;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.cedric.siegegame.SiegeGamePlugin;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class FloatingItem {

    private Location location;
    private final ItemStack item;
    private final String title;
    private final SiegeGamePlugin plugin;
    private Hologram hologram;

    public FloatingItem(SiegeGamePlugin plugin, Location location, String title, ItemStack item) {
        this.location = location;
        this.item = item;
        this.plugin = plugin;
        this.title = title;
    }

    public Location getLocation() {
        return location;
    }

    public void create() {
        if (isCreated())
            return;

        hologram = HolographicDisplaysAPI.get(plugin).createHologram(location);
        hologram.getLines().appendText(title);
        ItemHologramLine itemLine = hologram.getLines().appendItem(item);
        itemLine.setPickupListener(new PlaceholderPickupListener(item, hologram));

    }

    public boolean isCreated() {
        return (hologram != null && !hologram.isDeleted());
    }

    public void setLocation(Location location) {
        this.location = location;
        if (isCreated())
            hologram.setPosition(location);
    }
}
