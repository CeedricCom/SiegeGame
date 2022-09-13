package me.cedric.siegegame.config;

import com.palmergames.bukkit.towny.TownyAPI;
import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.teams.Team;
import me.cedric.siegegame.teams.TeamImpl;
import me.cedric.siegegame.world.WorldGame;
import me.cedric.siegegame.world.WorldGameImpl;
import me.deltaorion.common.config.ConfigSection;
import me.deltaorion.common.config.FileConfig;
import me.deltaorion.common.config.yaml.YamlAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class ConfigLoader {

    private final SiegeGame plugin;
    private FileConfig mapsYml;

    private static final String YML_PATH_DIVIDER = ".";

    private static final String MAPS_SECTION_KEY = "maps";
    private static final String MAPS_SECTION_WORLD_NAME_KEY = "worldname";

    private static final String MAPS_SECTION_DEFAULT_SPAWN_KEY = "defaultspawn";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_X = "x";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_Y = "y";
    private static final String MAPS_SECTION_DEFAULT_SPAWN_Z = "z";

    private static final String MAPS_SECTION_MAP_MAPBORDER_KEY = "worldborder";
    private static final String MAPS_SECTION_MAP_MAPBORDER_CENTER_KEY = "center";
    private static final String MAPS_SECTION_MAP_MAPBORDER_SIZE = "size";
    private static final String MAPS_SECTION_MAP_MAPBORDER_CENTER_X = "x";
    private static final String MAPS_SECTION_MAP_MAPBORDER_CENTER_Z = "z";

    private static final String MAPS_SECTION_WORLD_TEAMS_KEY = "teams";
    private static final String MAPS_SECTION_WORLD_TEAMS_NAME = "name";
    private static final String MAPS_SECTION_WORLD_TEAMS_COLOR = "color";
    private static final String MAPS_SECTION_WORLD_TEAMS_TOWN = "town";

    public ConfigLoader(SiegeGame plugin) {
        this.plugin = plugin;
    }

    public void loadMaps() {
        try {
            this.mapsYml = FileConfig.loadConfiguration(new YamlAdapter(), plugin.getResourceStream("maps.yml"));
        } catch (Exception x) {
            x.printStackTrace();
        }

        for (String mapKey : mapsYml.getConfigurationSection(MAPS_SECTION_KEY).getKeys(false)) {
            String worldName = mapsYml.getString(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_NAME_KEY);

            if (worldName == null) {
                plugin.getApiPlugin().getPluginLogger().severe("Could not retrieve world name for map key " + mapKey + " - Skipping!");
                continue;
            }

            World world = Bukkit.getWorld(worldName);

            if (world == null) {
                plugin.getApiPlugin().getPluginLogger().severe("Could not retrieve world object for map key " + mapKey + " - Skipping!");
                continue;
            }

            int x = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_X);
            int y = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_Y);
            int z = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_KEY + YML_PATH_DIVIDER + MAPS_SECTION_DEFAULT_SPAWN_Z);

            int mapBorderX = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_CENTER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_CENTER_X);
            int mapBorderZ = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_CENTER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_CENTER_Z);
            int mapBorderSize = mapsYml.getInt(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_KEY + YML_PATH_DIVIDER + MAPS_SECTION_MAP_MAPBORDER_SIZE);

            //Border border = new Border(world, x + mapBorderSize, -64, z + mapBorderSize, x - mapBorderSize, 50000000, z - mapBorderSize);

            WorldGameImpl worldGame = new WorldGameImpl(mapKey, world, new Location(world, x, y, z));
            ConfigSection teamsSection = mapsYml.getConfigurationSection(MAPS_SECTION_KEY + YML_PATH_DIVIDER + mapKey + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_KEY);
            Set<Team> teams = loadTeams(worldGame, teamsSection);
            worldGame.addTeams(teams);

            plugin.getGameManager().addWorld(worldGame);
        }
    }

    private Set<Team> loadTeams(WorldGame worldGame, ConfigSection section) {
        Set<Team> teams = new HashSet<>();
        String currentPath = section.getCurrentPath();
        for (String key : section.getKeys(false)) {
            String name = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_NAME);
            String hexColor = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_COLOR);
            String townName = mapsYml.getString(currentPath + YML_PATH_DIVIDER + key + YML_PATH_DIVIDER + MAPS_SECTION_WORLD_TEAMS_TOWN);

            TeamImpl team = new TeamImpl(worldGame, color(hexColor), TownyAPI.getInstance().getTown(townName), name, key);
            teams.add(team);
        }

        return teams;
    }

    private Color color(String hexColor) {
        int r = Integer.valueOf(hexColor.substring(1, 3), 16);
        int g = Integer.valueOf(hexColor.substring(3, 5), 16);
        int b = Integer.valueOf(hexColor.substring(5, 7), 16);

        return Color.fromRGB(r, g, b);
    }
}














