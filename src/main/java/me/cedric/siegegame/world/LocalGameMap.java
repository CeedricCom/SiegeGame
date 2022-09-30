package me.cedric.siegegame.world;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.AlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.InvalidNameException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermissionChange;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.object.WorldCoord;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;

public class LocalGameMap implements GameMap {

    private final File source;
    private File activeWorldFolder;
    private World bukkitWorld;
    private final String displayName;

    public LocalGameMap(File source, String displayName) {
        this.source = source;
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean load() {
        if (isLoaded())
            return true;

        this.activeWorldFolder = new File(
                Bukkit.getWorldContainer().getParentFile(), // Root server folder
                source.getName() + "_active_" + System.currentTimeMillis());
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

        if (!copyTowns())
            return false;

        return isLoaded();
    }

    private boolean copyTowns() {
        TownyWorld world = TownyUniverse.getInstance().getWorld(source.getName());
        TownyWorld dest = TownyUniverse.getInstance().getWorld(activeWorldFolder.getName());

        assert dest != null;

        if (world == null)
            return false;

        for (Town town : world.getTowns().values()) {
            try {
                for (TownBlock townBlock : town.getTownBlocks()) {
                    WorldCoord coord = townBlock.getWorldCoord();
                    TownyUniverse.getInstance().addTownBlock(new TownBlock(coord.getX(), coord.getZ(), dest));
                    TownBlock tb = dest.getTownBlock(coord.getX(), coord.getZ());
                    tb.setTown(town);
                    tb.save();
                }
                town.save();
            } catch (NotRegisteredException x) {
                System.out.println("Problem copying town claims over.");
                System.out.println(x.getMessage());
            }
        }

        return true;
    }

    private void deleteTownClaims() {
        TownyWorld dest = TownyUniverse.getInstance().getWorld(activeWorldFolder.getName());

        if (dest == null)
            return;
        for (TownBlock townBlock : dest.getTownBlocks()) {
            if (townBlock.getTownOrNull() == null)
                continue;
            Town town = townBlock.getTownOrNull();
            town.removeTownBlock(townBlock);
            town.save();
        }

        // If we don't delete the townblock folder that corresponds to the temporary world, towny tries to load it after the world is deleted and goes into safe mode
        File file = new File(Bukkit.getPluginsFolder() + File.separator + "Towny" + File.separator + "data" + File.separator + "townblocks", activeWorldFolder.getName());
        delete(file);
    }

    @Override
    public void unload() {
        deleteTownClaims();

        if (bukkitWorld != null)
            Bukkit.unloadWorld(bukkitWorld, false);
        if (activeWorldFolder != null)
            delete(activeWorldFolder);

        bukkitWorld = null;
        activeWorldFolder = null;
    }

    @Override
    public boolean restoreFromSource() {
        unload();
        return load();
    }

    @Override
    public boolean isLoaded() {
        return bukkitWorld != null;
    }

    @Override
    public World getWorld() {
        return bukkitWorld;
    }

    private static void delete(File file) {
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
