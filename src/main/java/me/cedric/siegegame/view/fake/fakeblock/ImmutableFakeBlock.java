package me.cedric.siegegame.view.fake.fakeblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class ImmutableFakeBlock implements IFakeBlock {

    private final IFakeBlock block;

    public ImmutableFakeBlock(IFakeBlock block) {
        this.block = block;
    }

    public ImmutableFakeBlock(Material material, World world, int x, int y, int z) {
        this(new FakeBlock(material, world, x, y, z));
    }

    @Override
    public Material getMaterial() {
        return block.getMaterial();
    }

    @Override
    public boolean setMaterial(Material material) {
        return false;
    }

    @Override
    public Location getLocation() {
        return block.getLocation();
    }

    @Override
    public World getWorld() {
        return block.getWorld();
    }

    @Override
    public boolean isVisible() {
        return block.isVisible();
    }

    @Override
    public boolean setVisible(boolean visible) {
        return block.isVisible();
    }
}

