package me.cedric.siegegame.player.kits;

import com.google.common.collect.ImmutableList;
import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.kits.db.KitDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class KitStorage {

    private final HashMap<UUID, PlayerKitManager> kits = new HashMap<>();
    private final SiegeGamePlugin plugin;
    private final KitDatabase kitDatabase;

    public KitStorage(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        this.kitDatabase = new KitDatabase(plugin);

        try {
            kitDatabase.initialise();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasKitManager(UUID uuid) {
        return kits.containsKey(uuid);
    }

    public PlayerKitManager getKitManager(UUID uuid) {
        return kits.get(uuid);
    }

    public void load(Player player, String currentMap) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            PlayerKitManager kitManager = kitDatabase.load(player.getUniqueId());
            kits.put(player.getUniqueId(), kitManager);

            if (currentMap == null)
                return;

            Kit kit = kitManager.getKit(currentMap);

            if (kit == null)
                return;

            Bukkit.getScheduler().runTask(plugin, () -> player.getInventory().setContents(kit.getContents()));

            kits.put(player.getUniqueId(), kitManager);
        });

    }

    public void unload(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!kits.containsKey(uuid))
                return;
            kitDatabase.save(kits.get(uuid));
            kits.remove(uuid);
        });
    }

    public boolean setKit(UUID uuid, String mapIdentifier, WorldGame worldGame, ItemStack[] contents) {
        PlayerKitManager playerKitManager = kits.get(uuid);
        if (playerKitManager == null) {
            playerKitManager = new PlayerKitManager(uuid);
            kits.put(uuid, playerKitManager);
        }

        Kit kit = playerKitManager.getKit(mapIdentifier);
        if (kit == null) {
            kit = new Kit(mapIdentifier, UUID.randomUUID());
            playerKitManager.addKit(kit);
        }

        kit.setContents(contents, worldGame);
        kitDatabase.save(playerKitManager);
        System.out.println("saved.");
        return true;
    }

    public void removeKit(UUID uuid, String mapIdentifier) {
        if (!kits.containsKey(uuid))
            return;

        PlayerKitManager kitManager = kits.get(uuid);
        Kit kit = kitManager.getKitExact(mapIdentifier);

        // doesnt exist in the first place
        if (kit == null)
            return;

        kitManager.removeKit(kit.getMapIdentifier());
        kitDatabase.delete(kit);
    }

}
