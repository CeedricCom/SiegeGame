package me.cedric.siegegame.model.player.kits;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.WorldGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KitController {

    private final HashMap<UUID, PlayerKitManager> kits = new HashMap<>();
    private final SiegeGamePlugin plugin;
    private final KitService service;

    public KitController(SiegeGamePlugin plugin, KitService service) {
        this.plugin = plugin;
        this.service = service;
    }

    public boolean hasKitManager(UUID uuid) {
        return kits.containsKey(uuid);
    }

    public PlayerKitManager getKitManager(UUID uuid) {
        if (kits.get(uuid) != null)
            return kits.get(uuid);

        List<Kit> playerKits = service.fetchPlayerKits(uuid);
        PlayerKitManager playerKitManager = new PlayerKitManager(uuid);
        playerKits.forEach(playerKitManager::addKit);

        kits.put(uuid, playerKitManager); // add to "cache" if not already there
        return playerKitManager;
    }

    public Kit getKit(UUID player, String map) {
        PlayerKitManager kitManager = getKitManager(player);
        return kitManager.getKit(map);
    }

    public void applyPlayerKit(Player player, WorldGame currentMap) {
        if (currentMap == null)
            return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Kit kit = getKit(player.getUniqueId(), currentMap.getMapIdentifier());

            if (kit == null)
                return;

            Bukkit.getScheduler().runTask(plugin, () -> player.getInventory().setContents(kit.getContents().toArray(ItemStack[]::new)));
        });
    }

    public void unload(UUID uuid) {
        kits.remove(uuid);
    }

    public void saveKit(UUID uuid, String mapIdentifier, WorldGame worldGame, ItemStack[] contents) {
        PlayerKitManager playerKitManager = kits.get(uuid);
        if (playerKitManager == null) {
            playerKitManager = new PlayerKitManager(uuid);
            kits.put(uuid, playerKitManager);
        }

        Kit kit = playerKitManager.getKitExact(mapIdentifier);

        if (kit != null) // kit already exists
            removeKit(uuid, kit.getMapIdentifier());

        kit = Kit.fromInventory(contents, worldGame, mapIdentifier);
        playerKitManager.addKit(kit);

        Kit finalKit = kit;
        playerKitManager.getKits().forEach(kit1 -> {
            service.save(finalKit, uuid);
        });
    }

    public void removeKit(UUID uuid, String mapIdentifier) {
        if (!kits.containsKey(uuid))
            return;

        PlayerKitManager kitManager = kits.get(uuid);
        Kit kit = kitManager.getKitExact(mapIdentifier);

        // Doesn't exist in the first place
        if (kit == null)
            return;

        kitManager.removeKit(kit.getMapIdentifier());
        service.delete(kit.getKitUUID());
    }

}
