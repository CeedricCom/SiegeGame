package me.cedric.siegegame.fake.fakeblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FakeBlock implements IFakeBlock {

    @NotNull
    private Material material;
    @NotNull private final Location location;
    private boolean visible;

    public FakeBlock(Material material, World world, int x, int y, int z) {
        this.material = material;
        this.location = new Location(world,x,y,z);
        this.visible = false;
    }

    @NotNull @Override
    public synchronized Material getMaterial() {
        return material;
    }

    @Override
    public boolean setMaterial(Material material) {
        Objects.requireNonNull(material);
        if(material.equals(this.material))
            return false;

        this.material = material;
        return true;
    }

    @NotNull @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public World getWorld() {
        return location.getWorld();
    }

    @Override
    public synchronized boolean isVisible() {
        return visible;
    }

    @Override
    public synchronized boolean setVisible(boolean visible) {
        if(visible==this.visible)
            return false;

        this.visible = visible;
        return true;
    }


}
