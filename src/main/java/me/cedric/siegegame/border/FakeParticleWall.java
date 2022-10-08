package me.cedric.siegegame.border;

import me.cedric.siegegame.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;

public class FakeParticleWall extends FakeBorderWall {

    public FakeParticleWall(GamePlayer player, Border border, int width, int height, Material material) {
        super(player, border, width, height, material);
    }

    @Override
    protected void buildWalls() {
        FakeBlockManager fakeBlockManager = player.getBorderHandler().getFakeBlockManager();
        fakeBlockManager.removeAll();
        World world = player.getBukkitPlayer().getWorld();
        for (Wall wall : walls) {
            createParticleWall(world,wall);
        }


    }

    private void createParticleWall(World world, Wall wall) {
        for (int xz = wall.getMinXZ(); xz < wall.getMaxXZ(); xz++) {
            for (int y = wall.getMinY(); y < wall.getMaxY(); y++) {
                int x = wall.getX(xz);
                int z = wall.getZ(xz);
                world.spawnParticle(Particle.CRIT_MAGIC, new Location(world, x,y,z), 20);
            }
        }
    }
}
