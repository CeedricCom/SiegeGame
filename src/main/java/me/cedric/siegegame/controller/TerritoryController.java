package me.cedric.siegegame.controller;

import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.model.teams.territory.Territory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class TerritoryController implements Listener {

    private final Territory territory;
    private final WorldGame worldGame;

    public TerritoryController(WorldGame worldGame, Territory territory) {
        this.worldGame = worldGame;
        this.territory = territory;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!event.hasChangedBlock())
            return;

        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        Location to = event.getTo();

        if (gamePlayer == null)
            return;

        if (territory.isInside(to) && !territory.isInside(event.getFrom())) {
            gamePlayer.getDisplayer().displayInsideClaims(worldGame, territory);
            return;
        }

        if (territory.isInside(event.getFrom()) && !territory.isInside(to))
            gamePlayer.getDisplayer().removeDisplayInsideClaims();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        Location to = event.getTo();

        if (gamePlayer == null)
            return;

        if (territory.isInside(to)) {
            gamePlayer.getDisplayer().displayInsideClaims(worldGame, territory);
            return;
        }

        if (territory.isInside(event.getFrom()) && !territory.isInside(to))
            gamePlayer.getDisplayer().removeDisplayInsideClaims();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        evaluateCancel(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        evaluateCancel(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        Location loc = event.getInventory().getLocation();

        if (loc == null)
            return;

        if (isInteractProhibited(loc.getBlock()))
            return;

        evaluateCancel((Player) event.getPlayer(), loc, event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketFill(PlayerBucketFillEvent event) {
        evaluateCancel(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        evaluateCancel(event.getPlayer(), event.getBlock().getLocation(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();

        // is the below check necessary?
//        if (item != null && isInteractProhibited(item))
//            evaluateCancel(event.getPlayer(), event.getPlayer().getLocation(), event);

        if (block != null && isInteractProhibited(block))
            evaluateCancel(event.getPlayer(), block.getLocation(), event);
    }

    private void evaluateCancel(Player player, Location location, Cancellable event) {
        GamePlayer gamePlayer = worldGame.getPlayer(player.getUniqueId());

        if (gamePlayer == null)
            return;

        if (gamePlayer.getBukkitPlayer().hasPermission(Permissions.CLAIMS_BYPASS.getPermission()))
            return;

        if (location == null || !territory.isInside(location.getWorld(), location.getBlockX(), location.getBlockZ()))
            return;

        if (gamePlayer.isDead() || !gamePlayer.hasTeam())
            event.setCancelled(true);

        if (gamePlayer.hasTeam() && gamePlayer.getTeam().getSafeArea().getBoundingBox().isColliding(location))
            event.setCancelled(true);

        if (gamePlayer.getTeam().getIdentifier().equalsIgnoreCase(territory.getTeam().getConfigKey()))
            return;

        event.setCancelled(true);
        gamePlayer.getDisplayer().displayActionCancelled();
    }

    private boolean isInteractProhibited(Block block) {
        if (block.getState() instanceof Sign)
            return true;

        if (block.getBlockData() instanceof Powerable) {
            return true;
        }

        if (block.getState() instanceof InventoryHolder)
            return true;

        Material blockType = block.getType();
        return switch (blockType) {
            case CANDLE, CAKE, END_CRYSTAL, HONEYCOMB, ITEM_FRAME, ARMOR_STAND -> true;
            default -> false;
        };

    }

}
