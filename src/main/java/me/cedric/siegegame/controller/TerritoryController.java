package me.cedric.siegegame.controller;

import me.cedric.siegegame.enums.Permissions;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.player.GamePlayer;
import me.cedric.siegegame.model.teams.territory.Territory;
import me.deltaorion.bukkit.item.EMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TerritoryController implements Listener {

    private final Territory territory;
    private final WorldGame worldGame;

    private final static List<Material> interactProhibited = new ArrayList<>();

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

        if (!interactProhibited.contains(loc.getBlock().getType()))
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

        if (item != null && interactProhibited.contains(item.getType()))
            evaluateCancel(event.getPlayer(), event.getPlayer().getLocation(), event);

        if (block != null && interactProhibited.contains(block.getType()))
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

    static {
        // Regular blocks
        interactProhibited.add(EMaterial.HONEYCOMB.getBukkitMaterial());
        interactProhibited.add(EMaterial.ARMOR_STAND.getBukkitMaterial());
        interactProhibited.add(EMaterial.END_CRYSTAL.getBukkitMaterial());
        interactProhibited.add(EMaterial.CAKE.getBukkitMaterial());
        interactProhibited.add(EMaterial.CANDLE.getBukkitMaterial());
        interactProhibited.add(EMaterial.CHEST.getBukkitMaterial());

        // Pressure plates
        interactProhibited.add(EMaterial.ACACIA_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.MANGROVE_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.LIGHT_WEIGHTED_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.DARK_OAK_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.BIRCH_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.POLISHED_BLACKSTONE_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.WARPED_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.OAK_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.SPRUCE_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.HEAVY_WEIGHTED_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.STONE_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.JUNGLE_PRESSURE_PLATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.CRIMSON_PRESSURE_PLATE.getBukkitMaterial());

        // Buttons
        interactProhibited.add(EMaterial.ACACIA_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.DARK_OAK_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.STONE_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.POLISHED_BLACKSTONE_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.JUNGLE_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.BIRCH_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.CRIMSON_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.MANGROVE_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.OAK_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.SPRUCE_BUTTON.getBukkitMaterial());
        interactProhibited.add(EMaterial.WARPED_BUTTON.getBukkitMaterial());

        // Signs
        interactProhibited.add(EMaterial.SIGN_BLOCK.getBukkitMaterial());
        interactProhibited.add(EMaterial.JUNGLE_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.CRIMSON_WALL_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.DARK_OAK_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.WARPED_WALL_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.OAK_WALL_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.MANGROVE_WALL_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.SPRUCE_WALL_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.BIRCH_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.MANGROVE_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.ACACIA_WALL_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.SPRUCE_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.ACACIA_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.CRIMSON_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.WARPED_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.JUNGLE_WALL_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.DARK_OAK_WALL_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.OAK_SIGN.getBukkitMaterial());
        interactProhibited.add(EMaterial.BIRCH_WALL_SIGN.getBukkitMaterial());

        // Fence gates
        interactProhibited.add(EMaterial.OAK_FENCE_GATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.SPRUCE_FENCE_GATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.BIRCH_FENCE_GATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.JUNGLE_FENCE_GATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.ACACIA_FENCE_GATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.DARK_OAK_FENCE_GATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.CRIMSON_FENCE_GATE.getBukkitMaterial());
        interactProhibited.add(EMaterial.WARPED_FENCE_GATE.getBukkitMaterial());

        // Doors and trapdoors
        interactProhibited.add(EMaterial.IRON_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.OAK_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.SPRUCE_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.BIRCH_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.JUNGLE_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.ACACIA_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.DARK_OAK_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.CRIMSON_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.WARPED_DOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.IRON_TRAPDOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.OAK_TRAPDOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.SPRUCE_TRAPDOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.BIRCH_TRAPDOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.JUNGLE_TRAPDOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.ACACIA_TRAPDOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.DARK_OAK_TRAPDOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.CRIMSON_TRAPDOOR.getBukkitMaterial());
        interactProhibited.add(EMaterial.WARPED_TRAPDOOR.getBukkitMaterial());
    }

}
