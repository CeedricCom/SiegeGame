package me.cedric.siegegame.territory.polygon;

import me.cedric.siegegame.SiegeGame;
import me.cedric.siegegame.territory.Vector2D;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

public abstract class PolygonFileHandler {

    private final SiegeGame plugin;
    private final String fileName;
    private final File directory;
    private final File territoryPointsFile;
    private boolean isLoaded = false;

    protected PolygonFileHandler(SiegeGame plugin, String fileName) {
        this.fileName = fileName;
        this.plugin = plugin;

        this.directory = new File(plugin.getDataFolder(), "territories");
        directory.mkdirs();
        this.territoryPointsFile = new File(directory, fileName + ".txt");
    }

    public boolean unload(Set<Vector2D> vectors) {
        if (!isLoaded)
            return false;
        createFile();
        try (FileWriter writer = new FileWriter(territoryPointsFile)) {
            for (Vector2D vector : vectors) {
                writer.write(vector.toString() + "\n");
            }
            isLoaded = false;
            clear();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean load() {
        if (isLoaded)
            return false;
        createFile();
        try {
            Scanner scanner = new Scanner(territoryPointsFile);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] s = data.split(",");
                int x = Integer.parseInt(s[0]);
                int z = Integer.parseInt(s[1]);
                add(new Vector2D(x, z));
            }
            scanner.close();
            deleteFile();
            isLoaded = true;
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected abstract void add(Vector2D vector);

    protected abstract void clear();

    private void createFile() {
        try {
            if (!territoryPointsFile.exists()) {
                directory.mkdirs();
                territoryPointsFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    private boolean deleteFile() {
        return territoryPointsFile.delete();
    }

}
