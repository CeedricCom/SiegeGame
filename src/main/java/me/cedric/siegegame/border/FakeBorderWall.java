package me.cedric.siegegame.border;

import me.cedric.siegegame.player.GamePlayer;
import me.cedric.siegegame.world.WorldGame;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class FakeBorderWall extends BorderDisplay {

    private final int width;
    private final int height;
    private final Material material;
    private final List<Wall> walls;
    private boolean wallVisible;

    public FakeBorderWall(GamePlayer player, Border border, int width, int height, Material material) {
        super(player, border);
        this.width = width;
        this.height = height;
        this.material = material;
        this.walls = new ArrayList<>();
        wallVisible = false;
    }

    public void update(WorldGame worldGame, Border border) {
        Location location = player.getBukkitPlayer().getLocation();

        boolean destroy = false;
        boolean update = false;

        if (shouldDisplay(border,location)) {
            if (wallVisible)
                update = true;
            wallVisible = true;
        } else {
            if (!wallVisible)
                return;
            wallVisible = false;
            destroy = true;
        }

        if (destroy) {
            //System.out.println("Destroying");
            destroy(worldGame, border);
            return;
        }

        if(update) {
            //System.out.println("Updating");
            updateWalls(border.getBoundingBox());
            return;
        }

        //System.out.println("Creating");
        create(worldGame, border);
    }

    private boolean shouldDisplay(Border border, Location location) {
        if(border.isInverse()) {
            return shouldDisplayInverse(border,location);
        } else {
            return shouldDisplayNorm(border,location);
        }
    }

    private boolean shouldDisplayInverse(Border border, Location location) {
        BoundingBox borderBox = border.getBoundingBox();
        BoundingBox minBox = borderBox.clone().expand(MIN_DISTANCE);
        return !borderBox.isColliding(location) && minBox.isColliding(location);
    }

    private boolean shouldDisplayNorm(Border border, Location location) {
        BoundingBox borderBox = border.getBoundingBox();
        BoundingBox minBox = borderBox.clone().expand(-MIN_DISTANCE);
        return borderBox.isColliding(location) && !minBox.isColliding(location);
    }

    @Override
    public void destroy(WorldGame worldGame, Border border) {
        FakeBlockManager fakeBlockManager = player.getBorderHandler().getFakeBlockManager();
        fakeBlockManager.removeAll();
        World world = player.getBukkitPlayer().getWorld();
        for (Wall wall : walls) {
            destroyWall(fakeBlockManager,world,wall);
        }

        walls.clear();
        fakeBlockManager.update();
    }

    private void destroyWall(FakeBlockManager manager, World world, Wall wall) {
        for (int xz = wall.minXZ; xz < wall.maxXZ; xz++) {
            for (int y = wall.minY; y < wall.maxY; y++) {
                int x = wall.getX(xz);
                int z = wall.getZ(xz);
                manager.removeBlock(world, x, y, z);
            }
        }
    }


    private void updateWalls(BoundingBox borderBox) {
        WallProjection wallProjection = projectXZ(borderBox,player.getBukkitPlayer().getLocation());
        List<Wall> oldWalls = new ArrayList<>(walls);
        walls.clear();
        //System.out.println("------ CREATING WALLS --------");
        createWall(borderBox, wallProjection.XZ, wallProjection.perpendicular, wallProjection.Y, wallProjection.xDimension,0, width,wallProjection.facingPositive);
        buildUpdate(oldWalls,walls);
    }

    private void buildUpdate(List<Wall> oldWalls, List<Wall> newWalls) {
        //new walls
        World world = player.getBukkitPlayer().getWorld();
        FakeBlockManager manager = player.getBorderHandler().getFakeBlockManager();
        for(Wall newWall : newWalls) {
            //find the old wall
            Wall oldWall = getEquivalentWall(oldWalls,newWall);
            if(oldWall==null) {
                //there is no old equivalent, place the whole new wall
                createWall(manager,world,newWall);
            } else {
                //otherwise mark the changes
                changeDiff(manager,world,oldWall,newWall);
            }
        }

        for(Wall oldWall : oldWalls) {
            //remove any old walls
            Wall newWall = getEquivalentWall(newWalls,oldWall);
            if(newWall==null)
                destroyWall(manager,world,oldWall);
        }

        manager.update();
    }

    private Wall getEquivalentWall(List<Wall> oldWalls, Wall newWall) {
        for(Wall wall : oldWalls) {
            if((wall.xDimension == newWall.xDimension) && (wall.facingPositive == newWall.facingPositive))
                return wall;
        }

        return null;
    }

    @Override
    public void create(WorldGame worldGame, Border border) {
        WallProjection wallProjection = projectXZ(border.getBoundingBox(), player.getBukkitPlayer().getLocation());

        walls.clear();
        createWall(border.getBoundingBox(), wallProjection.XZ, wallProjection.perpendicular, wallProjection.Y, wallProjection.xDimension, 0, width,wallProjection.facingPositive);
        buildWalls();
    }


    private void buildWalls() {
        FakeBlockManager fakeBlockManager = player.getBorderHandler().getFakeBlockManager();
        fakeBlockManager.removeAll();
        World world = player.getBukkitPlayer().getWorld();
        for (Wall wall : walls) {
            createWall(fakeBlockManager,world,wall);
        }

        fakeBlockManager.update();
    }

    private void createWall(FakeBlockManager manager, World world , Wall wall) {
        for (int xz = wall.minXZ; xz < wall.maxXZ; xz++) {
            for (int y = wall.minY; y < wall.maxY; y++) {
                int x = wall.getX(xz);
                int z = wall.getZ(xz);
                manager.addBlock(material, world, x, y, z);
            }
        }
    }

    private void changeDiff(FakeBlockManager manager ,World world,Wall oldWall, Wall newWall) {
        //if there are blocks in  the new wall that are not present in the old wall add them
        //if there are blocks present in the old wall that are not present in the new wall, remove them
        //look at the extremities
        if(oldWall.maxXZ > newWall.maxXZ) {
            removeDiffOldXZ(manager,world,oldWall,newWall.maxXZ,oldWall.maxXZ);
        } else if(newWall.maxXZ > oldWall.maxXZ) {
            addNewDiffXZ(manager,world,newWall,oldWall.maxXZ,newWall.maxXZ);
        }

        if(oldWall.minXZ < newWall.minXZ) {
            removeDiffOldXZ(manager,world,oldWall,oldWall.minXZ,newWall.minXZ);
        } else if(newWall.minXZ < oldWall.minXZ) {
            addNewDiffXZ(manager,world,newWall,newWall.minXZ,oldWall.minXZ);
        }

        if(oldWall.maxY > newWall.maxY) {
            removeOldDiffY(manager,world,oldWall,newWall.maxY,oldWall.maxY);
        } else if(newWall.maxY > oldWall.maxY) {
            addNewDiffY(manager,world,newWall,oldWall.maxY,newWall.maxY);
        }

        if(oldWall.minY < newWall.minY) {
            removeOldDiffY(manager,world,oldWall,oldWall.minY,newWall.minY);
        } else if(newWall.minY < oldWall.minY) {
            addNewDiffY(manager,world,newWall,newWall.minY,oldWall.minY);
        }
    }

    private void removeOldDiffY(FakeBlockManager manager, World world, Wall oldWall, int minY, int maxY) {
        for(int xz = oldWall.minXZ; xz < oldWall.maxXZ;xz++) {
            for (int y = minY; y < maxY; y++) {
                int x = oldWall.getX(xz);
                int z = oldWall.getZ(xz);
                manager.removeBlock(world, x, y, z);
            }
        }
    }

    private void addNewDiffY(FakeBlockManager manager, World world, Wall newWall, int minY, int maxY) {
        for(int xz = newWall.minXZ; xz < newWall.maxXZ;xz++) {
            for (int y = minY; y < maxY; y++) {
                int x = newWall.getX(xz);
                int z = newWall.getZ(xz);
                manager.addBlock(material,world, x, y, z);
            }
        }
    }

    private void addNewDiffXZ(FakeBlockManager manager, World world, Wall newWall, int minXZ, int maxXZ) {
        for(int xz = minXZ; xz < maxXZ;xz++) {
            for (int y = newWall.minY; y < newWall.maxY; y++) {
                int x = newWall.getX(xz);
                int z = newWall.getZ(xz);
                manager.addBlock(material,world, x, y, z);
            }
        }
    }

    private void removeDiffOldXZ(FakeBlockManager manager, World world, Wall oldWall, int minXZ, int maxXZ) {
        for(int xz = minXZ; xz < maxXZ;xz++) {
            for (int y = oldWall.minY; y < oldWall.maxY; y++) {
                int x = oldWall.getX(xz);
                int z = oldWall.getZ(xz);
                manager.removeBlock(world, x, y, z);
            }
        }
    }

    private void createWall(BoundingBox borderBox, int xz, int perpendicular, int y, boolean xDimension, int wallCount, int width, boolean facingPositive) {
        if (wallCount >= 4)
            return;

        int borderMinXZ = 0;
        int borderMaxXZ = 0;

        //get the x or z coordinate of the wall
        int minXZ = xz - width;
        int maxXZ = xz + width;
        int minY = y - height;
        int maxY = y + height;

        //the wall is facing an x direction, therefore clamp to the x directions
        if (xDimension) {
            borderMinXZ = (int) borderBox.getMinX() - 1;
            borderMaxXZ = (int) borderBox.getMaxX();
        } else {
            //else clamp to the z dimensions
            borderMinXZ = (int) borderBox.getMinZ() - 1;
            borderMaxXZ = (int) borderBox.getMaxZ();
        }

        if (minXZ < borderMinXZ) {
            int newWidth = Math.abs(borderMinXZ - minXZ)/2;
            minXZ = borderMinXZ;

            if(maxXZ<borderMinXZ) {
                maxXZ = borderMinXZ;
            }

            createWall(borderBox, getNewMiddle(perpendicular,newWidth,facingPositive), minXZ, y, !xDimension, wallCount + 1,newWidth,false);
        }

        if (maxXZ > borderMaxXZ) {
            int newWidth = Math.abs(maxXZ - borderMaxXZ)/2;
            maxXZ = borderMaxXZ;

            if(minXZ > borderMaxXZ) {
                minXZ = borderMaxXZ;
            }
            createWall(borderBox, getNewMiddle(perpendicular,newWidth,facingPositive), maxXZ, y, !xDimension, wallCount + 1, newWidth,true);
        }

        maxY = (int) Math.min(maxY,borderBox.getMaxY());
        minY = (int) Math.min(minY,borderBox.getMaxY());

        maxY = (int) Math.max(maxY,borderBox.getMinY());
        minY = (int) Math.max(minY,borderBox.getMinY());

        walls.add(new Wall(minXZ, maxXZ, perpendicular, minY, maxY, xDimension,facingPositive));
    }

    private int getNewMiddle(int perpendicular, int newWidth, boolean facingPositive) {
        if(facingPositive)
            return perpendicular-newWidth;

        return perpendicular + newWidth;
    }

    private WallProjection projectXZ(BoundingBox borderBox, Location playerLoc) {
        double[] distances = new double[4];
        //see which part of the wall the player is closest too
        //distances[0] = Math.abs(playerLoc.getX() - borderBox.getMinX());
        //facing negative z
        distances[2] = pDistance(playerLoc.getX(),playerLoc.getZ(),borderBox.getMinX(), borderBox.getMinZ(), borderBox.getMaxX(),borderBox.getMinZ());
        //facing positive z
        distances[3] = pDistance(playerLoc.getX(),playerLoc.getZ(),borderBox.getMinX(), borderBox.getMaxZ(), borderBox.getMaxX(),borderBox.getMaxZ());
        //facing negative x
        distances[0] = pDistance(playerLoc.getX(),playerLoc.getZ(),borderBox.getMinX(), borderBox.getMinZ(),borderBox.getMinX(),borderBox.getMaxZ());
        //facing positive x
        distances[1] = pDistance(playerLoc.getX(),playerLoc.getZ(),borderBox.getMaxX(), borderBox.getMinZ(),borderBox.getMaxX(),borderBox.getMaxZ());

        //get the lowest distance
        double lowest = distances[0];
        for (double dist : distances) {
            if (dist <= lowest) {
                lowest = dist;
            }
        }

        if (lowest == distances[0]) {
            playerLoc.setX(borderBox.getMinX()); //facing negative x
            return new WallProjection(playerLoc.getBlockZ(), playerLoc.getBlockX()-1, playerLoc.getBlockY(), false,false);
        }

        if (lowest == distances[1]) {
            playerLoc.setX(borderBox.getMaxX()); //facing positive x
            return new WallProjection(playerLoc.getBlockZ(), playerLoc.getBlockX(), playerLoc.getBlockY(), false,true);
        }
        if (lowest == distances[2]) {
            playerLoc.setZ(borderBox.getMinZ());//facing negative z
            return new WallProjection(playerLoc.getBlockX(), playerLoc.getBlockZ()-1, playerLoc.getBlockY(), true,false);
        }
        if (lowest == distances[3]) {
            playerLoc.setZ(borderBox.getMaxZ()); //facing positive z
            return new WallProjection(playerLoc.getBlockX(), playerLoc.getBlockZ(), playerLoc.getBlockY(), true,true);
        }

        throw new IllegalStateException();
    }

    private double pDistance(double x, double y, double x1, double y1, double x2, double y2) {
        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        }
        else if (param > 1) {
            xx = x2;
            yy = y2;
        }
        else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = x - xx;
        double dy = y - yy;
        return dx * dx + dy * dy;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Border getBorder() {
        return border;
    }

    private static final class WallProjection {
        private final int XZ;
        private final int perpendicular;
        private final int Y;
        private final boolean xDimension;
        private final boolean facingPositive;

        private WallProjection(int XZ, int perpendicular, int Y, boolean xDimension, boolean facingPositive) {
            this.XZ = XZ;
            this.perpendicular = perpendicular;
            this.Y = Y;
            this.xDimension = xDimension;
            this.facingPositive = facingPositive;
        }

        public int getXZ() {
            return XZ;
        }

        public int getPerpendicular() {
            return perpendicular;
        }

        public int getY() {
            return Y;
        }

        public boolean xDimension() {
            return xDimension;
        }

        public boolean isFacingPositive() {
            return facingPositive;
        }
    }

    private static final class Wall {
        private final int minXZ;
        private final int maxXZ;
        private final int minY;
        private final int maxY;
        private final int perpendicular;
        //whether the wall is on the x-direction, that it the wall is facing
        //a z direction!
        private final boolean xDimension;
        private final boolean facingPositive;

        private Wall(int minXZ, int maxXZ, int perpendicular, int minY, int maxY, boolean xDimension, boolean facingPositive) {
            this.minXZ = minXZ;
            this.maxXZ = maxXZ;
            this.minY = minY;
            this.maxY = maxY;
            this.perpendicular = perpendicular;
            this.xDimension = xDimension;
            this.facingPositive = facingPositive;
        }

        public int getMinXZ() {
            return minXZ;
        }

        public int getMaxXZ() {
            return maxXZ;
        }

        public int getMinY() {
            return minY;
        }

        public int getMaxY() {
            return maxY;
        }

        public boolean isxDimension() {
            return xDimension;
        }

        public int getPerpendicular() {
            return perpendicular;
        }

        public int getX(int xz) {
            if (xDimension)
                return xz;

            return perpendicular;
        }


        public int getZ(int xz) {
            if (xDimension)
                return perpendicular;

            return xz;
        }
    }
}
