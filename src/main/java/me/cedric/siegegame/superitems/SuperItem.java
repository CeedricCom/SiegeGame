package me.cedric.siegegame.superitems;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import me.deltaorion.bukkit.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public abstract class SuperItem implements Listener {

    private final static String compoundKey = "superitem";

    protected final SiegeGame plugin;
    private final String key;
    private GamePlayer owner = null;

    protected SuperItem(SiegeGame plugin, String key) {
        this.plugin = plugin;
        this.key = key;
    }

    protected abstract void display(GamePlayer owner);

    protected abstract void removeDisplay(GamePlayer owner);

    protected abstract ItemStack itemStack();

    protected abstract void initialize(SiegeGame plugin);

    public abstract String getDisplayName();

    public void remove() {
        if (owner != null) {
            owner.getBukkitPlayer().getInventory().removeItem(getItem());
            removeDisplay(owner);
        }
    }

    public String getKey() {
        return key;
    }

    private ItemStack getItem() {
        ItemStack item = itemStack().clone();
        ItemBuilder builder = new ItemBuilder(item.clone())
                .transformNBT(nbtItem -> nbtItem.setString(compoundKey, key));
        return builder.build().clone();
    }

    public void giveTo(GamePlayer newOwner) {
        if (this.owner != null)
            remove();
        this.owner = newOwner;
        if (!newOwner.getBukkitPlayer().getInventory().addItem(getItem()).isEmpty()) {
            newOwner.getBukkitPlayer().getWorld().dropItem(newOwner.getBukkitPlayer().getLocation(), getItem());
        }
        display(newOwner);
    }

    public GamePlayer getOwner() {
        return owner;
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (droppedItem.clone().equals(getItem()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (!event.getItem().getItemStack().equals(getItem()))
            return;

        Player player = (Player) event.getEntity();

        if (this.owner == null) {
            plugin.getLogger().warning("Super item is dropped but has no owner! Letting it be picked up");
            plugin.getLogger().warning("Player who picked up will become the new owner. Name: " + player.getName());
            giveTo(plugin.getPlayerManager().getPlayer(player.getUniqueId()));
            event.setCancelled(true);
            event.getItem().remove();
            return;
        }

        if (!player.getUniqueId().equals(this.owner.getUUID()))
            event.setCancelled(true);
    }

}
