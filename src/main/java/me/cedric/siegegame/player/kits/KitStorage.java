package me.cedric.siegegame.player.kits;

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

    private void addKitManager(UUID uuid) {
        if (kits.containsKey(uuid))
            return;

        PlayerKitManager playerKitManager = new PlayerKitManager(uuid);
        kits.put(uuid, playerKitManager);
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

            player.getInventory().setContents(kit.getContents());
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

    public boolean setKit(UUID uuid, WorldGame worldGame, ItemStack[] contents) {
        PlayerKitManager playerKitManager = kits.get(uuid);
        if (playerKitManager == null) {
            playerKitManager = new PlayerKitManager(uuid);
            kits.put(uuid, playerKitManager);
        }

        Kit kit = playerKitManager.getKitExact(worldGame.getMapIdentifier());
        if (kit == null) {
            kit = new Kit(worldGame.getMapIdentifier(), UUID.randomUUID());
            playerKitManager.addKit(kit);
        }

        kit.setContents(contents, worldGame);
        kitDatabase.save(playerKitManager);
        return true;
    }

    public void saveAll() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (PlayerKitManager kitManager : kits.values()) {
                kitDatabase.save(kitManager);
            }
        });
    }

}
