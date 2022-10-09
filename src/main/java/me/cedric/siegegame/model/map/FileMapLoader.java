package me.cedric.siegegame.model.map;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

public class FileMapLoader {

    private final File source;
    private File activeWorldFolder;
    private World bukkitWorld = null;
    private int number = 1;

    public FileMapLoader(File source) {
        this.source = source;
    }

    public boolean load() {
        if (isLoaded())
            return true;

        this.activeWorldFolder = new File(
                Bukkit.getWorldContainer().getParentFile(), // Root server folder
                source.getName() + "_active_" + number);
        number += 1;
        try {
            FileUtils.copyDirectory(source, activeWorldFolder, pathname -> !(pathname.getName().endsWith("session.lock") || pathname.getName().endsWith("uid.dat")));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        this.bukkitWorld = Bukkit.createWorld(new WorldCreator(activeWorldFolder.getName()));

        if (bukkitWorld == null)
            return false;

        bukkitWorld.setAutoSave(false);
        bukkitWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        bukkitWorld.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        bukkitWorld.setDifficulty(Difficulty.NORMAL);
        bukkitWorld.setClearWeatherDuration(Integer.MAX_VALUE);

        return isLoaded();
    }

    public void unload() {
        if (bukkitWorld != null)
            Bukkit.unloadWorld(bukkitWorld, false);
        if (activeWorldFolder != null)
            delete(activeWorldFolder);

        bukkitWorld = null;
        activeWorldFolder = null;
    }

    public boolean restoreFromSource() {
        unload();
        return load();
    }

    public boolean isLoaded() {
        return bukkitWorld != null;
    }

    public World getWorld() {
        return bukkitWorld;
    }

    private void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)
                return;

            for (File child : files) {
                delete(child);
            }
        }

        file.delete();
    }
}
