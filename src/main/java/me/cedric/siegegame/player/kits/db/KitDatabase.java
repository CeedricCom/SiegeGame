package me.cedric.siegegame.player.kits.db;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.kits.Kit;
import me.cedric.siegegame.player.kits.PlayerKitManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class KitDatabase {

    private final SiegeGamePlugin plugin;
    private final String DB_URL;
    private final File databaseFile;

    private static final String SCHEMA = "CREATE TABLE IF NOT EXISTS Kit (" +
            "    uniqueId    VARCHAR(36)        NOT NULL    PRIMARY KEY," +
            "    player        VARCHAR(36)," +
            "    mapName        VARCHAR(20)," +
            "    items       VARCHAR(1000));";

    private static final String DROP_TABLES = "DROP TABLE IF EXISTS KitItem;" + "DROP TABLE IF EXISTS Kit;" + "DROP TABLE IF EXISTS SiegePlayer;";


    public KitDatabase(SiegeGamePlugin plugin) {
        this.plugin = plugin;
        databaseFile = new File(plugin.getDataFolder(), "database");
        DB_URL = "jdbc:h2:" + plugin.getDataFolder().getAbsolutePath() + File.separator + "database";
    }

    public void initialise() throws SQLException {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException x) {
            plugin.getLogger().severe("Could not load kits database. Driver not found. Disabling...");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            x.printStackTrace();
            return;
        }

        Connection conn = DriverManager.getConnection(DB_URL);
        Statement statement = conn.createStatement();

        statement.executeUpdate(SCHEMA);

        statement.close();
        conn.close();
    }

    public void save(PlayerKitManager kitManager) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement statement = conn.createStatement();

            for (Kit kit : kitManager.getKits()) {
                String kitStatement = "MERGE INTO Kit VALUES('" + kit.getKitUUID() + "','"
                        + kitManager.getPlayerUUID() + "','" +
                        kit.getMapIdentifier() + "','" +
                        kit.getRawString() + "');";
                statement.addBatch(kitStatement);
            }

            statement.executeBatch();
            statement.close();
            conn.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public PlayerKitManager load(UUID uuid) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Kit WHERE Kit.player='" + uuid.toString() + "';");

            List<Kit> kits = new ArrayList<>();
            while (resultSet.next()) {
                UUID kitUUID = UUID.fromString(resultSet.getString("uniqueId"));
                String mapName = resultSet.getString("mapName");
                String rawString = resultSet.getString("items");
                kits.add(new Kit(mapName, rawString, kitUUID));
            }

            PlayerKitManager kitManager = new PlayerKitManager(uuid);
            kits.forEach(kitManager::addKit);

            return kitManager;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(Kit kit) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement statement = conn.createStatement();

            String deleteKitStatement = "DELETE FROM Kit WHERE Kit.uniqueId='" + kit.getKitUUID() + "';";

            statement.executeUpdate(deleteKitStatement);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
