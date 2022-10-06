package me.cedric.siegegame.superitems;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public abstract class SuperItem implements Listener {

    private final static String compoundKey = "superitem";

    protected final SiegeGame plugin;
    private final String key;
    private GamePlayer owner = null;
    private boolean isDropped = false;

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
            Arrays.stream(owner.getBukkitPlayer().getInventory().getContents()).forEach(itemStack -> {
                if (isItem(itemStack))
                    owner.getBukkitPlayer().getInventory().removeItemAnySlot(itemStack);
            });
            removeDisplay(owner);
        }
        this.owner = null;
        plugin.getGameManager().updateAllScoreboards();
    }

    public String getKey() {
        return key;
    }

    private ItemStack getItem() {
        ItemStack item = itemStack().clone();
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, compoundKey), PersistentDataType.STRING, getKey());
        item.setItemMeta(meta);
        return item.clone();
    }

    public void giveTo(GamePlayer newOwner) {
        if (this.owner != null)
            remove();
        this.owner = newOwner;
        if (!newOwner.getBukkitPlayer().getInventory().addItem(getItem()).isEmpty()) {
            dropItem(newOwner.getBukkitPlayer().getLocation());
        }
        display(newOwner);
        plugin.getGameManager().updateAllScoreboards();
    }

    public GamePlayer getOwner() {
        return owner;
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        ItemStack droppedItem = event.getItemDrop().getItemStack();
        if (isItem(droppedItem))
            event.setCancelled(true);
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        if (!isItem(event.getItem().getItemStack()))
            return;

        Player player = (Player) event.getEntity();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (gamePlayer == null) {
            event.setCancelled(true);
            return;
        }

        if (this.owner == null && gamePlayer.hasTeam() && !plugin.getGameManager().ownsSuperItem(gamePlayer)) {
            giveTo(gamePlayer);
            event.setCancelled(true);
            event.getItem().remove();
            isDropped = false;
            return;
        }

        if (this.owner == null || !player.getUniqueId().equals(this.owner.getUUID()))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (gamePlayer == null || !gamePlayer.equals(owner))
            return;

        remove();

        Player killer = player.getKiller();

        event.getDrops().removeIf(this::isItem);

        if (killer == null) {
            dropItem(gamePlayer.getBukkitPlayer().getLocation());
            return;
        }

        GamePlayer gameKiller = plugin.getPlayerManager().getPlayer(killer.getUniqueId());

        if (gameKiller.hasTeam() && plugin.getGameManager().hasSuperItem(gameKiller.getTeam())) {
            dropItem(player.getLocation());
            return;
        }

        giveTo(gameKiller);
    }

    @EventHandler(priority = EventPriority.MONITOR) // very important this stays in monitor, the manager has to add it first and has to have team first
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());

        if (gamePlayer.hasTeam() && !plugin.getGameManager().hasSuperItem(gamePlayer.getTeam()) && this.owner == null && !isDropped())
            giveTo(gamePlayer);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (owner == null || !player.getUniqueId().equals(owner.getUUID()))
            return;

        remove();
        dropItem(player.getLocation());
        plugin.getGameManager().updateAllScoreboards();
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (event.getInventory().getType() == InventoryType.CRAFTING) {
            if (isItem(item) || isItem(cursor) && event.getSlot() == 40) {
                event.setCancelled(true);
                return;
            }
        }

        if ((isItem(item) || isItem(cursor) || event.getClick().isKeyboardClick()) && event.getInventory().getType() != InventoryType.CRAFTING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack item = event.getOldCursor();
        ItemStack cursor = event.getCursor();

        if ((isItem(item) || isItem(cursor)) && event.getInventory().getType() != InventoryType.CRAFTING)
            event.setCancelled(true);
    }

    private boolean isItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getItemMeta() == null)
            return false;
        NamespacedKey namespacedKey = new NamespacedKey(plugin, compoundKey);
        if (!itemStack.getItemMeta().getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING))
            return false;
        return itemStack.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING) != null;
    }

    private void dropItem(Location location) {
        Item item = location.getWorld().dropItem(location, getItem().clone());
        item.setGlowing(true);
        isDropped = true;
        item.setUnlimitedLifetime(true);
    }

    public boolean isDropped() {
        return isDropped;
    }
}
