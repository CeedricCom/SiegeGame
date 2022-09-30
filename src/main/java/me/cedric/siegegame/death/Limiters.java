package me.cedric.siegegame.death;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import me.cedric.siegegame.SiegeGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Limiters implements Listener {

    private final DeathManager deathManager;
    private final SiegeGame plugin;

    public Limiters(SiegeGame plugin, DeathManager deathManager) {
        this.plugin = plugin;
        this.deathManager = deathManager;
    }

    @EventHandler
    public void onPickupItems(PlayerAttemptPickupItemEvent event) {
        if (!deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (!deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !deathManager.isPlayerDead(event.getDamager().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || !deathManager.isPlayerDead(event.getEntity().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onProjectile(PlayerLaunchProjectileEvent event) {
        if (!deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (!deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!deathManager.isPlayerDead(event.getEntity().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!event.getInventory().getType().equals(InventoryType.PLAYER) || !deathManager.isPlayerDead(event.getPlayer().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!deathManager.isPlayerDead(event.getWhoClicked().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!deathManager.isPlayerDead(event.getWhoClicked().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        if (!deathManager.isPlayerDead(event.getWhoClicked().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        if (!deathManager.isPlayerDead(event.getWhoClicked().getUniqueId()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryPickup(InventoryPickupItemEvent event) {
        if (!deathManager.isPlayerDead(event.getItem().getOwner()))
            return;

        if (!deathManager.isPlayerDead(event.getItem().getThrower()))
            return;

        event.setCancelled(true);
    }
}
