package me.cedric.siegegame.config;

import com.palmergames.bukkit.towny.TownyAPI;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.border.Border;
import me.cedric.siegegame.border.BoundingBox;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.superitems.SuperItem;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.teams.TeamImpl;
import me.cedric.siegegame.world.LocalGameMap;
import me.cedric.siegegame.world.WorldGame;
import me.cedric.siegegame.world.WorldGameImpl;
import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.item.ItemBuilder;
import me.deltaorion.common.config.ConfigSection;
import me.deltaorion.common.config.FileConfig;
import me.deltaorion.common.config.InvalidConfigurationException;
import me.deltaorion.common.config.yaml.YamlAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigLoader {

    private final SiegeGame plugin;

    private static final String YML_PATH_DIVIDER = ".";

    private FileConfig mapsYml;
    private static final String MAPS_SECTION_KEY = "maps";
    private static final String MAPS_SECTION_WORLD_NAME_KEY = "worldname";
    private static final String MAPS_SECTION_WORLD_DISPLAY_NAME_KEY = "world-display-name";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_KEY = "defaultspawn";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_X = "x";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_Y = "y";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_Z = "z";
    private static final String MAPS_SECTION_MAP_MAPBORDER_KEY = "worldborder";
    private static final String MAPS_SECTION_MAP_MAPBORDER_X1_KEY = "x1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Y1_KEY = "y1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Z1_KEY = "z1";
    private static final String MAPS_SECTION_MAP_MAPBORDER_X2_KEY = "x2";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Y2_KEY = "y2";
    private static final String MAPS_SECTION_MAP_MAPBORDER_Z2_KEY = "z2";
    private static final String MAPS_SECTION_WORLD_TEAMS_KEY = "teams";
    private static final String MAPS_SECTION_WORLD_TEAMS_NAME = "name";
    private static final String MAPS_SECTION_WORLD_TEAMS_COLOR = "color";
    private static final String MAPS_SECTION_WORLD_TEAMS_TOWN = "town";
    private static final String MAPS_SECTION_TEAMS_SPAWN = "spawn-area";
    private static final String MAPS_SECTION_TEAMS_SPAWN_X1 = "x1";
    private static final String MAPS_SECTION_TEAMS_SPAWN_Y1 = "y1";
    private static final String MAPS_SECTION_TEAMS_SPAWN_Z1 = "z1";
    private static final String MAPS_SECTION_TEAMS_SPAWN_X2 = "x2";
    private static final String MAPS_SECTION_TEAMS_SPAWN_Y2 = "y2";
    private static final String MAPS_SECTION_TEAMS_SPAWN_Z2 = "z2";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN = "safe-spawn";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_X1 = "x";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_Y1 = "y";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_Z1 = "z";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_YAW = "yaw";
    private static final String MAPS_SECTION_TEAMS_SAFE_SPAWN_PITCH = "pitch";
    private static final String MAPS_SECTION_SUPER_ITEM_LIST_KEY = "super-items";

    private FileConfig shopYml;
    private static final String SHOP_SECTION_KEY = "shop";
    private static final String SHOP_SECTION_SHOP_NAME_KEY = "shop-name";
    private static final String SHOP_SECTION_MATERIAL_KEY = "material";
    private static final String SHOP_SECTION_SLOT_KEY = "slot";
    private static final String SHOP_SECTION_PRICE_KEY = "price";
    private static final String SHOP_SECTION_DISPLAY_NAME_KEY = "display-name";
    private static final String SHOP_SECTION_LORE_KEY = "lore";
    private static final String SHOP_SECTION_ENCHANTMENTS_KEY = "enchantments";
    private static final String SHOP_SECTION_HIDE_ITEM_FLAGS_KEY = "item-flags";
    private static final String SHOP_SECTION_COMMAND_LIST_KEY = "commands";
    private static final String SHOP_SECTION_INCLUDES_ITEM = "includes-item";

    private FileConfig configYml;
    private static final String CONFIG_POINTS_PER_KILL_KEY = "points-per-kill";
    private static final String CONFIG_POINTS_TO_END_KEY = "points-to-end";
    private static final String CONFIG_LEVELS_PER_KILL_KEY = "levels-per-kill";

    public ConfigLoader(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void initializeAndLoad() {

        try {
            setupConfig();
        } catch (Exception x) {
            x.printStackTrace();
            return;
        }

        if (!mapsYml.isConfigurationSection(MAPS_SECTION_KEY) || !shopYml.isConfigurationSection(SHOP_SECTION_KEY)) {
            cryAndDisable();
            return;
        }

        loadMaps();
        loadShop();
        loadSettings();
        plugin.getLogger().info("Config loaded.");
    }

    private void loadMaps() {
        for (String mapKey : mapsYml.getConfigurationSection(MAPS_SECTION_KEY).getKeys(false)) {
            String worldName = mapsYml.getString(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_NAME_KEY);

            if (worldName == null) {
                plugin.getApiPlugin().getPluginLogger().severe("Could not retrieve world name for map key " + mapKey + " - Skipping!");
                continue;
            }

            loadWorld(worldName, mapKey);
        }

        plugin.getLogger().info("Maps loaded.");
    }

    public void loadWorld(String worldName, String mapKey) {
        int x = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_X);
        int y = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_Y);
        int z = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_Z);

        int x1 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_X1_KEY);
        int y1 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_Y1_KEY);
        int z1 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_Z1_KEY);

        int x2 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_X2_KEY);
        int y2 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_Y2_KEY);
        int z2 = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_Z2_KEY);

        List<String> superItems = mapsYml.getStringList(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_SUPER_ITEM_LIST_KEY);

        String displayName = mapsYml.getString(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_DISPLAY_NAME_KEY);

        Vector corner1Vector = new Vector(x1, y1, z1);
        Vector corner2Vector = new Vector(x2, y2, z2);

        LocalGameMap localGameMap = new LocalGameMap(new File(Bukkit.getWorldContainer().getParentFile(), worldName), displayName);
        localGameMap.load();

        Border border = new Border(new BoundingBox(localGameMap.getWorld(), corner1Vector, corner2Vector));
        WorldGameImpl worldGame = new WorldGameImpl(mapKey, localGameMap, border, new Location(localGameMap.getWorld(), x, y, z));
        ConfigSection teamsSection = mapsYml.getConfigurationSection(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_KEY);
        Set<Team> teams = loadTeams(worldGame, teamsSection);
        worldGame.addTeams(teams);

        for (String superItemKey : superItems) {
            SuperItem superItem = plugin.getSuperItemManager().getSuperItem(superItemKey);

            if (superItem == null)
                continue;

            worldGame.addSuperItem(superItem);
        }

        plugin.getGameManager().addWorld(worldGame);
    }

    private void loadShop() {
        for (String key : shopYml.getConfigurationSection(SHOP_SECTION_KEY).getKeys(false)) {
            String material = shopYml.getString(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_MATERIAL_KEY);
            int slot = shopYml.getInt(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_SLOT_KEY);
            int price = shopYml.getInt(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_PRICE_KEY);
            String displayName = shopYml.getString(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_DISPLAY_NAME_KEY);
            List<String> listOfLore = shopYml.getStringList(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_LORE_KEY);
            List<String> itemFlags = shopYml.getStringList(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_HIDE_ITEM_FLAGS_KEY);
            List<String> enchantments = shopYml.getStringList(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_ENCHANTMENTS_KEY);
            List<String> commands = shopYml.getStringList(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_COMMAND_LIST_KEY);
            boolean includesItem = shopYml.getBoolean(SHOP_SECTION_KEY + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + SHOP_SECTION_INCLUDES_ITEM);

            List<String> lore = new ArrayList<>();
            for (String s : listOfLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            ItemBuilder itemBuilder = new ItemBuilder(EMaterial.valueOf(material.toUpperCase()))
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName))
                    .setLore(lore);

            for (String flag : itemFlags) {
                itemBuilder.addFlags(ItemFlag.valueOf(flag.toUpperCase()));
            }

            for (String enchantment : enchantments) {
                String[] s = enchantment.split(";");
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(s[0]));
                int level = Integer.parseInt(s[1]);
                itemBuilder.addEnchantment(ench, level);
            }
            itemBuilder.transformNBT(nbtItem -> nbtItem.addCompound("siege-game").setInteger("price", price));

            ItemStack item = itemBuilder.build();
            ShopItem button = new ShopItem(gamePlayer -> {
                Player player = gamePlayer.getBukkitPlayer();
                if (includesItem) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage("You do not have any empty inventory slots");
                    }

                    gamePlayer.getBukkitPlayer().getInventory().addItem(item);
                }

                for (String command : commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }, item, price, slot);

            plugin.getShopGUI().addItem(button);
        }

        String shopName = shopYml.getString(SHOP_SECTION_SHOP_NAME_KEY);
        plugin.getShopGUI().setGUIName(ChatColor.translateAlternateColorCodes('&', shopName));

        plugin.getLogger().info("Shop loaded.");
    }

    private Set<Team> loadTeams(WorldGame worldGame, ConfigSection section) {
        Set<Team> teams = new HashSet<>();
        String currentPath = section.getCurrentPath();
        for (String key : section.getKeys(false)) {
            String name = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_NAME);
            String hexColor = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_COLOR);
            String townName = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_TOWN);

            int x1 = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN_X1);
            int y1 = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN_Y1);
            int z1 = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN_Z1);

            int x2 = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN_X2);
            int y2 = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN_Y2);
            int z2 = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SPAWN_Z2);

            int safeSpawnX = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN_X1);
            int safeSpawnY = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN_Y1);
            int safeSpawnZ = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN_Z1);
            float yaw = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN_YAW);
            float pitch = mapsYml.getInt(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN + YML_PATH_DIVIDER + MAPS_SECTION_TEAMS_SAFE_SPAWN_PITCH);

            Location safeSpawn = new Location(worldGame.getBukkitWorld(), safeSpawnX, safeSpawnY, safeSpawnZ, yaw, pitch);

            Border border = new Border(new BoundingBox(worldGame.getBukkitWorld(), x1, y1, z1, x2, y2, z2));
            border.setCanLeave(true);
            border.setInverse(true);
            TeamImpl team = new TeamImpl(worldGame, border, safeSpawn, color(hexColor), TownyAPI.getInstance().getTown(townName), name, key);
            teams.add(team);
        }

        return teams;
    }

    private void loadSettings() {
        int pointsPerKill = configYml.getInt(CONFIG_POINTS_PER_KILL_KEY);
        int levelsPerKill = configYml.getInt(CONFIG_LEVELS_PER_KILL_KEY);
        int pointsToEnd = configYml.getInt(CONFIG_POINTS_TO_END_KEY);

        Settings.POINTS_PER_KILL.setValue(pointsPerKill);
        Settings.LEVELS_PER_KILL.setValue(levelsPerKill);
        Settings.POINTS_TO_END.setValue(pointsToEnd);
    }

    private Color color(String hexColor) {
        int r = Integer.valueOf(hexColor.substring(1, 3), 16);
        int g = Integer.valueOf(hexColor.substring(3, 5), 16);
        int b = Integer.valueOf(hexColor.substring(5, 7), 16);

        return Color.fromRGB(r, g, b);
    }

    private void cryAndDisable() {
        plugin.getLogger().severe("There was a problem loading the config. Disabling...");
        plugin.disablePlugin();
    }

    private void setupConfig() throws IOException, InvalidConfigurationException {
        File mapFile = new File(plugin.getDataFolder(), "maps.yml");
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!mapFile.exists())
            plugin.saveResource("maps.yml", false);
        if (!shopFile.exists())
            plugin.saveResource("shop.yml", false);
        if (!configFile.exists())
            plugin.saveResource("config.yml", false);

        mapsYml = FileConfig.loadConfiguration(new YamlAdapter(), new File(plugin.getDataFolder(), "maps.yml"));
        shopYml = FileConfig.loadConfiguration(new YamlAdapter(), new File(plugin.getDataFolder(), "shop.yml"));
        configYml = FileConfig.loadConfiguration(new YamlAdapter(), new File(plugin.getDataFolder(), "config.yml"));

        mapsYml.mergeDefaults();
        shopYml.mergeDefaults();
        configYml.mergeDefaults();
    }
}














