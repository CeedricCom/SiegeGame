package me.cedric.siegegame.border.fakeblock;

import me.cedric.siegegame.border.fakeblock.IFakeBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class ImmutableFakeBlock implements IFakeBlock {

    private final IFakeBlock block;

    public ImmutableFakeBlock(IFakeBlock block) {
        this.block = block;
    }

    @Override
    public Material getMaterial() {
        return block.getMaterial();
    }

    @Override
    public boolean setMaterial(Material material) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }
}

