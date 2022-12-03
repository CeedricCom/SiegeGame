package me.cedric.siegegame.config;

import me.cedric.siegegame.SiegeGamePlugin;
import me.cedric.siegegame.player.border.Border;
import me.cedric.siegegame.util.BoundingBox;
import me.cedric.siegegame.display.shop.ShopItem;
import me.cedric.siegegame.model.teams.Team;
import me.cedric.siegegame.territory.Polygon;
import me.cedric.siegegame.territory.Territory;
import me.cedric.siegegame.territory.Vector2D;
import me.cedric.siegegame.model.SiegeGameMatch;
import me.cedric.siegegame.model.game.WorldGame;
import me.cedric.siegegame.model.map.GameMap;
import me.cedric.siegegame.model.map.FileMapLoader;
import me.cedric.siegegame.model.teams.TeamFactory;
import me.deltaorion.bukkit.item.EMaterial;
import me.deltaorion.bukkit.item.ItemBuilder;
import me.deltaorion.common.config.ConfigSection;
import me.deltaorion.common.config.FileConfig;
import me.deltaorion.common.config.InvalidConfigurationException;
import me.deltaorion.common.config.yaml.YamlAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import java.awt.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ConfigLoader implements GameConfig {

    private final SiegeGamePlugin plugin;

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
    private static final String MAPS_SECTION_TERRITORY_KEY = "territory";

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
    private static final String SHOP_SECTION_INCLUDES_ITEM_EXACT = "includes-item-exact";
    private static final String SHOP_SECTION_POTION_EFFECTS_KEY = "potion-effects";
    private static final String SHOP_SECTION_POTION_COLOR_KEY = "potion-color";
    private static final String SHOP_SECTION_AMOUNT_KEY = "amount";

    private FileConfig configYml;
    private static final String CONFIG_POINTS_PER_KILL_KEY = "points-per-kill";
    private static final String CONFIG_POINTS_TO_END_KEY = "points-to-end";
    private static final String CONFIG_LEVELS_PER_KILL_KEY = "levels-per-kill";
    private static final String RESPAWN_TIMER_KEY = "respawn-timer";
    private static final String DEATH_COMMANDS_KEY = "death-commands";
    private static final String RESPAWN_COMMANDS_KEY = "respawn-commands";
    private static final String START_COMMANDS_KEY = "start-game-commands";
    private static final String END_COMMANDS_KEY = "end-game-commands";
    private static final String BLACKLISTED_PROJECTILES_KEY = "blacklisted-projectiles";

    public ConfigLoader(SiegeGamePlugin plugin) {
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
        plugin.getLogger().info("Config loaded.");
    }

    private void loadMaps() {
        ConfigSection section = mapsYml.getConfigurationSection(MAPS_SECTION_KEY);

        for (String mapKey : mapsYml.getConfigurationSection(MAPS_SECTION_KEY).getKeys(false)) {

            ConfigSection mapSection = section.getConfigurationSection(mapKey);

            String worldName = mapSection.getString(MAPS_SECTION_WORLD_NAME_KEY);

            if (worldName == null) {
                plugin.getApiPlugin().getPluginLogger().severe("Could not retrieve world name for map key " + mapKey + " - Skipping!");
                continue;
            }

            loadWorld(worldName, mapSection);
        }

        plugin.getGameManager().shuffleQueue();
        plugin.getLogger().info("Maps loaded.");
    }

    public void loadWorld(String worldName, ConfigSection section) {
        ConfigSection defaultSpawnSection = section.getConfigurationSection(MAPS_SECTION_DEFAULT_SPAWN_KEY);
        int x = defaultSpawnSection.getInt(MAPS_SECTION_DEFAULT_SPAWN_X);
        int y = defaultSpawnSection.getInt(MAPS_SECTION_DEFAULT_SPAWN_Y);
        int z = defaultSpawnSection.getInt(MAPS_SECTION_DEFAULT_SPAWN_Z);
        Location defaultSpawn = new Location(null, x, y, z);

        ConfigSection worldBorderSection = section.getConfigurationSection(MAPS_SECTION_MAP_MAPBORDER_KEY);
        int x1 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_X1_KEY);
        int y1 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_Y1_KEY);
        int z1 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_Z1_KEY);

        int x2 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_X2_KEY);
        int y2 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_Y2_KEY);
        int z2 = worldBorderSection.getInt(MAPS_SECTION_MAP_MAPBORDER_Z2_KEY);

        String displayName = section.getString(MAPS_SECTION_WORLD_DISPLAY_NAME_KEY);

        Vector corner1Vector = new Vector(x1, y1, z1);
        Vector corner2Vector = new Vector(x2, y2, z2);

        FileMapLoader fileMapLoader = new FileMapLoader(plugin, new File(Bukkit.getWorldContainer().getParentFile(), worldName));
        Border border = new Border(new BoundingBox(fileMapLoader.getWorld(), corner1Vector, corner2Vector));
        GameMap gameMap = new GameMap(fileMapLoader, displayName, new HashSet<>(), border, defaultSpawn);
        WorldGame worldGame = new WorldGame(plugin);

        ConfigSection teamsSection = section.getConfigurationSection(MAPS_SECTION_WORLD_TEAMS_KEY);

        List<ShopItem> shopItems = loadShop(shopYml.getConfigurationSection(SHOP_SECTION_KEY));
        String shopName = shopYml.getString(SHOP_SECTION_SHOP_NAME_KEY);
        worldGame.getShopGUI().setGUIName(ChatColor.translateAlternateColorCodes('&', shopName));
        shopItems.stream().forEach(shopItem -> worldGame.getShopGUI().addItem(shopItem));

        List<TeamFactory> factories = loadTeams(worldGame, gameMap, teamsSection);
        factories.forEach(gameMap::addTeam);
        factories.forEach(teamFactory -> worldGame.addTeam(new Team(worldGame, teamFactory)));

        plugin.getGameManager().addGame(new SiegeGameMatch(plugin, worldGame, gameMap, section.getName()));
    }

    private List<ShopItem> loadShop(ConfigSection section) {
        List<ShopItem> shopItems = new ArrayList<>();
        for (String key : section.getKeys(false)) {
            ConfigSection configSection = section.getConfigurationSection(key);
            String material = configSection.getString(SHOP_SECTION_MATERIAL_KEY);
            int slot = configSection.getInt(SHOP_SECTION_SLOT_KEY);
            int price = configSection.getInt(SHOP_SECTION_PRICE_KEY);
            int amount = configSection.getInt(SHOP_SECTION_AMOUNT_KEY);
            String displayName = configSection.getString(SHOP_SECTION_DISPLAY_NAME_KEY);
            List<String> listOfLore = configSection.getStringList(SHOP_SECTION_LORE_KEY);
            List<String> itemFlags = configSection.getStringList(SHOP_SECTION_HIDE_ITEM_FLAGS_KEY);
            List<String> enchantments = configSection.getStringList(SHOP_SECTION_ENCHANTMENTS_KEY);
            List<String> commands = configSection.getStringList(SHOP_SECTION_COMMAND_LIST_KEY);
            boolean includesItem = Boolean.parseBoolean(configSection.getString(SHOP_SECTION_INCLUDES_ITEM));
            boolean includesItemExact = Boolean.parseBoolean(configSection.getString(SHOP_SECTION_INCLUDES_ITEM_EXACT));

            List<String> lore = new ArrayList<>();
            for (String s : listOfLore) {
                lore.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            ItemBuilder itemBuilder = new ItemBuilder(EMaterial.matchMaterial(material.toUpperCase()))
                    .setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName))
                    .setLore(lore);

            itemBuilder.setAmount(amount);

            for (String flag : itemFlags) {
                itemBuilder.addFlags(ItemFlag.valueOf(flag.toUpperCase()));
            }

            if (material.equalsIgnoreCase("POTION") || material.equalsIgnoreCase("SPLASH_POTION")) {
                itemBuilder.transformMeta(itemMeta -> {
                    PotionMeta potionMeta = (PotionMeta) itemMeta;
                    // messy asf but whatever
                    for (String s : configSection.getStringList(SHOP_SECTION_POTION_EFFECTS_KEY)) {
                        String[] args = s.split(",");
                        potionMeta.addCustomEffect(new PotionEffect(PotionEffectType.getByKey(NamespacedKey.minecraft(args[0])), Integer.parseInt(args[1]) * 20, Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]), Boolean.parseBoolean(args[4]), Boolean.parseBoolean(args[5])), true);
                    }

                    potionMeta.setColor(bukkitColor(configSection.getString(SHOP_SECTION_POTION_COLOR_KEY)));
                });
            }

            for (String enchantment : enchantments) {
                String[] s = enchantment.split(";");
                Enchantment ench = Enchantment.getByKey(NamespacedKey.minecraft(s[0]));
                int level = Integer.parseInt(s[1]);
                itemBuilder.addEnchantment(ench, level);
            }

            ItemStack item = itemBuilder.build();
            ShopItem button = new ShopItem(gamePlayer -> {
                Player player = gamePlayer.getBukkitPlayer();
                if (includesItem) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage("You do not have any empty inventory slots");
                    }

                    if (includesItemExact)
                        gamePlayer.getBukkitPlayer().getInventory().addItem(item.clone());
                    else
                        gamePlayer.getBukkitPlayer().getInventory().addItem(new ItemStack(item.getType(), amount));
                }

                for (String command : commands) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }, item, price, slot);

            shopItems.add(button);
        }

        return shopItems;
    }

    private List<TeamFactory> loadTeams(WorldGame worldGame, GameMap gameMap, ConfigSection section) {
        List<TeamFactory> factories = new ArrayList<>();
        for (String key : section.getKeys(false)) {

            ConfigSection currentTeamSection = section.getConfigurationSection(key);
            String name = currentTeamSection.getString(MAPS_SECTION_WORLD_TEAMS_NAME);
            String hexColor = currentTeamSection.getString(MAPS_SECTION_WORLD_TEAMS_COLOR);

            ConfigSection spawnAreaSection = currentTeamSection.getConfigurationSection(MAPS_SECTION_TEAMS_SPAWN);
            int x1 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_X1);
            int y1 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_Y1);
            int z1 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_Z1);
            int x2 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_X2);
            int y2 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_Y2);
            int z2 = spawnAreaSection.getInt(MAPS_SECTION_TEAMS_SPAWN_Z2);
            Border safeArea = new Border(new BoundingBox(null, x1, y1, z1, x2, y2, z2));
            safeArea.setCanLeave(true);
            safeArea.setInverse(true);

            ConfigSection safeSpawnSection = currentTeamSection.getConfigurationSection(MAPS_SECTION_TEAMS_SAFE_SPAWN);
            int safeSpawnX = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_X1);
            int safeSpawnY = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_Y1);
            int safeSpawnZ = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_Z1);
            float yaw = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_YAW);
            float pitch = safeSpawnSection.getInt(MAPS_SECTION_TEAMS_SAFE_SPAWN_PITCH);
            Location safeSpawn = new Location(null, safeSpawnX, safeSpawnY, safeSpawnZ, yaw, pitch);

            // TERRITORY IS A LIST OF STRINGS
            // x1,z1,x2,z2 ON EACH ELEMENT
            // territory:
            //   - '69420, 420, 69, 420'
            List<String> stringCoords = currentTeamSection.getStringList(MAPS_SECTION_TERRITORY_KEY);
            Polygon polygon = null;
            for (String rawCoords : stringCoords) {
                String[] coords = rawCoords.split(",");
                try {
                    int coordX1 = Integer.parseInt(coords[0].trim());
                    int coordZ1 = Integer.parseInt(coords[1].trim());
                    int coordX2 = Integer.parseInt(coords[2].trim());
                    int coordZ2 = Integer.parseInt(coords[3].trim());

                    if (polygon == null)
                        polygon = new Polygon(gameMap, new Vector2D(coordX1, coordZ1), new Vector2D(coordX2, coordZ2));
                    else
                        polygon.addSquare(new Vector2D(coordX1, coordZ1), new Vector2D(coordX2, coordZ2));
                } catch (Exception x) {
                    plugin.getLogger().severe("Could not parse territory coordinates for key -- Skipping! Team will likely have no claims " + key);
                }
            }

            TeamFactory factory = new TeamFactory(safeArea, safeSpawn, name, key, color(hexColor));
            factory.setTerritory(new Territory(plugin, polygon, factory));
            factories.add(factory);
        }

        return factories;
    }

    private Color color(String hexColor) {
        return Color.decode(hexColor);
    }

    private org.bukkit.Color bukkitColor(String hexColor) {
        Color color = color(hexColor);
        return org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
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

    @Override
    public int getPointsPerKill() {
        return configYml.getInt(CONFIG_POINTS_PER_KILL_KEY);
    }

    @Override
    public int getLevelsPerKill() {
        return configYml.getInt(CONFIG_LEVELS_PER_KILL_KEY);
    }

    @Override
    public int getPointsToEnd() {
        return configYml.getInt(CONFIG_POINTS_TO_END_KEY);
    }

    @Override
    public int getRespawnTimer() {
        return configYml.getInt(RESPAWN_TIMER_KEY);
    }

    @Override
    public List<EntityType> getBlacklistedProjectiles() {
        List<EntityType> types = new ArrayList<>();
        for (String s : configYml.getStringList(BLACKLISTED_PROJECTILES_KEY)) {
            EntityType entityType = EntityType.valueOf(s.toUpperCase());
            System.out.println(s);
            types.add(entityType);
        }

        return types;
    }

    @Override
    public void reloadConfig() {
        try {
            configYml = FileConfig.loadConfiguration(new YamlAdapter(), new File(plugin.getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}














