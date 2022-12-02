package me.cedric.siegegame.modules.abilityitems;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import me.cedric.siegegame.SiegeGamePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

        hologram = HologramsAPI.createHologram(plugin, location);
        hologram.appendTextLine(title);
        ItemLine itemLine = hologram.appendItemLine(item);
        itemLine.setPickupHandler(player -> {
            if (player.getInventory().firstEmpty() != -1)
                player.getInventory().addItem(item);
            else
                player.getWorld().dropItem(player.getLocation(), item);
            hologram.delete();
            hologram = null;
        });

    }

    public boolean isCreated() {
        return hologram != null;
    }

    public void setLocation(Location location) {
        this.location = location;
        if (isCreated())
            hologram.teleport(location);
    }
}
