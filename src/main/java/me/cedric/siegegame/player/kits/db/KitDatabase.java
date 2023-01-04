package me.cedric.siegegame.player.kits.db;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.player.kits.Kit;
import me.cedric.siegegame.player.kits.PlayerKitManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class KitDatabase {

    private final SiegeGamePlugin plugin;
    private final String DB_URL;
    private final File databaseFile;

    private static final String CREATE_TABLES = "CREATE TABLE IF NOT EXISTS SiegePlayer (" + "    uniqueId    VARCHAR(36)        NOT NULL    PRIMARY KEY" + ");" + "CREATE TABLE IF NOT EXISTS Kit (" + "    uniqueId    VARCHAR(36)        NOT NULL    PRIMARY KEY," + "    mapName        VARCHAR(255)," + "    player        VARCHAR(36)," + "" + "    CONSTRAINT FK_Kit_Player FOREIGN KEY (player) REFERENCES SiegePlayer(uniqueId)" + "        ON UPDATE CASCADE ON DELETE CASCADE" + ");" + "CREATE TABLE IF NOT EXISTS KitItem (" + "    slot    INTEGER        NOT NULL," + "    kit        VARCHAR(36)        NOT NULL," + "    item    VARCHAR(1000)," + "    " + "    CONSTRAINT FK_Kit_Item FOREIGN KEY (kit) REFERENCES Kit(uniqueId) " + "        ON UPDATE CASCADE ON DELETE CASCADE," + "    CONSTRAINT Pk_Kit_Item PRIMARY KEY(kit,slot)" + ");";
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

        statement.executeUpdate(CREATE_TABLES);

        statement.close();
        conn.close();
    }

    public void save(PlayerKitManager kitManager) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement statement = conn.createStatement();

            String insertUUIDStatement = "MERGE INTO SiegePlayer VALUES('" + kitManager.getPlayerUUID().toString() + "');";

            statement.addBatch(insertUUIDStatement);

            for (Kit kit : kitManager.getKits()) {
                String kitStatement = "MERGE INTO Kit VALUES('" + kit.getKitUUID() + "','" + kit.getMapIdentifier() + "','" + kitManager.getPlayerUUID().toString() + "');";
                statement.addBatch(kitStatement);

                ItemStack[] contents = kit.getContents();
                for (int i = 0; i < contents.length; i++) {
                    ItemStack item = contents[i];
                    String serializedItem = item.getType().equals(Material.AIR) ? "" : Base64.getEncoder().encodeToString(item.serializeAsBytes());
                    String itemStatement = "MERGE INTO KitItem VALUES(" + i + ",'" + kit.getKitUUID().toString() + "','" + serializedItem + "');";
                    statement.addBatch(itemStatement);
                }
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
                kits.add(new Kit(mapName, kitUUID));
            }

            PlayerKitManager kitManager = new PlayerKitManager(uuid);
            for (Kit kit : kits) {
                ResultSet rs = statement.executeQuery("SELECT * FROM KitItem WHERE KitItem.kit = '" + kit.getKitUUID().toString() + "';");
                List<ItemStack> contents = new ArrayList<>();

                while (rs.next()) {
                    String serializedItem = rs.getString("item");
                    ItemStack item = serializedItem.isEmpty() ? new ItemStack(Material.AIR) : ItemStack.deserializeBytes(Base64.getDecoder().decode(serializedItem));
                    contents.add(item);
                }

                kit.setContents(contents.toArray(new ItemStack[0]));
                kitManager.addKit(kit);
            }

            return kitManager;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
