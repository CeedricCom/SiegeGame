package me.cedric.siegegame.fake.fakeblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public interface IFakeBlock {
    Material getMaterial();

    boolean setMaterial(Material material);

    Location getLocation();

    World getWorld();

    boolean isVisible();

    boolean setVisible(boolean visible);
}

