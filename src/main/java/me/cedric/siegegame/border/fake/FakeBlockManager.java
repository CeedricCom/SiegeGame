package me.cedric.siegegame.border.fake;

import me.cedric.siegegame.border.fakeblock.FakeBlock;
import me.cedric.siegegame.border.fakeblock.IFakeBlock;
import me.cedric.siegegame.border.fakeblock.ImmutableFakeBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FakeBlockManager {

    private final Map<Location, IFakeBlock> blocks;
    private final List<IFakeBlock> removeBlocks;
    private final List<IFakeBlock> showBlocks;
    private final Player player;
    private final Plugin plugin;

    public FakeBlockManager(Plugin plugin, Player player) {
        this.player = player;
        this.blocks = new HashMap<>();
        this.removeBlocks = new LinkedList<>();
        this.showBlocks = new LinkedList<>();
        this.plugin = plugin;
    }

    public void addBlock(Material material, World world, int x, int y, int z) {
        IFakeBlock block = new FakeBlock(material,world,x,y,z);
        block.setVisible(true);
        IFakeBlock prev = blocks.get(block.getLocation());
        if (prev == null) {
            blocks.put(block.getLocation(), block);
            showBlocks.add(block);
        } else {
            prev.setVisible(true);
            setMaterial(prev, material);
        }
    }

    public void removeBlock(World world,int x, int y, int z) {
        IFakeBlock block = this.blocks.remove(new Location(world, x, y, z));
        if (block != null) {
            removeBlocks.add(block);
        }
        
    }

    public Collection<IFakeBlock> getBlocks() {
        Set<IFakeBlock> blocks = new HashSet<>();
        for(IFakeBlock block : this.blocks.values()) {
            blocks.add(new ImmutableFakeBlock(block));
        }
        return Collections.unmodifiableSet(blocks);
    }

    @Nullable
    public IFakeBlock getBlockAt(World world, int x, int y, int z) {
        return blocks.get(new Location(world,x,y,z));
    }

    public void setVisible(World world, int x, int y, int z, boolean visible) {
        IFakeBlock block = getBlockAt(world,x,y,z);
        if(block==null)
            return;

        if (block.setVisible(visible)) {
            if(visible) {
                showBlocks.add(block);
            } else {
                removeBlocks.add(block);
            }
        }
    }

    public void setMaterial(World world, int x, int y, int z, Material material) {
        IFakeBlock block = getBlockAt(world,x,y,z);
        if(block==null) {
            addBlock(material,world,x,y,z);
            return;
        }

        setMaterial(block,material);
    }

    private void setMaterial(IFakeBlock block, Material material) {
        if(block.setMaterial(material)) {
            showBlocks.add(block);
        }
    }

    public void setAllVisible(boolean visible) {
        for(IFakeBlock block : blocks.values()) {
            if(block.setVisible(visible)) {
                if(visible) {
                    showBlocks.add(block);
                } else {
                    removeBlocks.add(block);
                }
            }
        }
    }

    public void removeAll() {
        Iterator<IFakeBlock> blockIterator = blocks.values().iterator();
        while(blockIterator.hasNext()) {
            IFakeBlock block = blockIterator.next();
            removeBlocks.add(block);
            blockIterator.remove();
        }
    }

    public void update() {
    List<IFakeBlock> toRemove = new LinkedList<>();
    List<IFakeBlock> toShow = new LinkedList<>();
        Iterator<IFakeBlock> removeIterator = removeBlocks.iterator();
        Iterator<IFakeBlock> showIterator = showBlocks.iterator();
        while(removeIterator.hasNext()) {
            toRemove.add(removeIterator.next());
            removeIterator.remove();
        }

        while (showIterator.hasNext()) {
            toShow.add(showIterator.next());
            showIterator.remove();
        }

        final Map<Location, BlockData> dataMap = new LinkedHashMap<>();
        
        for(IFakeBlock block : toRemove) {
            Location location = block.getLocation();
            dataMap.put(location,location.getWorld().getBlockData(location));
        }

        for(IFakeBlock block : toShow) {
            dataMap.put(block.getLocation(), plugin.getServer().createBlockData(block.getMaterial()));
        }

        player.sendMultiBlockChange(dataMap);
    }

}
