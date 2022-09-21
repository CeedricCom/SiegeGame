package me.cedric.siegegame.border;

import com.google.common.base.MoreObjects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class BoundingBox {

    private Vector min;
    private Vector max;
    private final World world;

    public BoundingBox(World world ,Vector p1, Vector p2) {
        this.min = new Vector(Math.min(p1.getX(),p2.getX()),Math.min(p1.getY(), p2.getY()),Math.min(p1.getZ(),p2.getZ()));
        this.max = new Vector(Math.max(p1.getX(),p2.getX()),Math.max(p1.getY(), p2.getY()),Math.max(p1.getZ(),p2.getZ()));
        this.world = world;
    }

    public BoundingBox(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this(world,new Vector(x1,y1,z1), new Vector(x2,y2,z2));
    }

    public static BoundingBox fromBukkit(Player player) {
        org.bukkit.util.BoundingBox box = player.getBoundingBox();
        return new BoundingBox(player.getWorld(),box.getMin(),box.getMax());
    }

    public static BoundingBox playerLocation(World world , Location location) {
        double size = 0.3;
        double height = 1.8;
        Vector min = new Vector(location.getX()-size,location.getY(),location.getZ()-size);
        Vector max = new Vector(location.getX()+size,location.getY()+height,location.getZ()+size);
        return new BoundingBox(world,min,max);
    }

    public double getMaxX() {
        return max.getX();
    }

    public double getMaxY() {
        return max.getY();
    }

    public double getMaxZ() {
        return max.getZ();
    }

    public double getMinX() {
        return min.getX();
    }

    public double getMinY() {
        return min.getY();
    }

    public double getMinZ() {
        return min.getZ();
    }

    public void setMaxX(double x) {
        this.max.setX(x);
    }

    public void setMaxY(double y) {
        this.max.setY(y);
    }

    public void setMaxZ(double z) {
        this.max.setZ(z);
    }

    public void setMinX(double x) {
        this.min.setX(x);
    }

    public void setMinY(double y) {
        this.min.setY(y);
    }

    public void setMinZ(double z) {
        this.min.setZ(z);
    }

    public void update(Consumer<BoundingBox> box) {
        box.accept(this);
    }

    public World getWorld() {
        return world;
    }

    public boolean isColliding(@Nullable BoundingBox b) {
        if(b==null)
            return false;

        if(!world.equals(b.world))
            return false;

        return (min.getX() <= b.max.getX() && max.getX() >= b.min.getX()) &&
                (min.getY() <= b.max.getY() && max.getY() >= b.min.getY()) &&
                (min.getZ() <= b.max.getZ() && max.getZ() >= b.min.getZ());
    }

    public boolean isColliding(Location l) {
        return isColliding(l.toVector());
    }

    public boolean isColliding(Vector p) {
        return (p.getX() >= min.getX() && p.getX() <= max.getX()) &&
                (p.getY() >= min.getY() && p.getY() <= max.getY()) &&
                (p.getZ() >= min.getZ() && p.getZ() <= max.getZ());
    }

    public boolean contains(@Nullable BoundingBox b) {
        if(b==null)
            return false;

        if(!world.equals(b.world))
            return false;

        return (max.getX() >= b.max.getX() && min.getX() <= b.min.getX()) &&
                (max.getY() >= b.max.getY() && min.getY() <= b.min.getY()) &&
                (max.getZ() >= b.max.getZ() && min.getZ() <= b.min.getZ());
    }

    public Vector getCenter() {
        return new Vector(
                center(min.getX(), max.getX()),
                center(min.getY(),max.getY()),
                center(min.getZ(),max.getZ())
        );
    }

    public Location externalProject(Location location) {
        Location currentLocation = location.clone();
        if (currentLocation.getX() < getMinX())
            currentLocation.setX(getMinX());

        if (currentLocation.getX() > getMaxX())
            currentLocation.setX(getMaxX());

        if (currentLocation.getY() < getMinY())
            currentLocation.setY(getMinY());

        if (currentLocation.getY() > getMaxY())
            currentLocation.setY(getMaxY());

        if (currentLocation.getZ() < getMinZ())
            currentLocation.setZ(getMinZ());

        if (currentLocation.getZ() > getMaxZ())
            currentLocation.setZ(getMaxZ());

        return currentLocation;
    }

    public static BoundingBox load(String world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        return new BoundingBox(Bukkit.getWorld(world),minX,minY,minZ,maxX,maxY,maxZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundingBox that = (BoundingBox) o;
        return Objects.equals(min, that.min) && Objects.equals(max, that.max) && Objects.equals(world, that.world);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("world",world)
                .add("minX",min.getX())
                .add("minY",min.getY())
                .add("minZ",min.getZ())
                .add("maxX",max.getX())
                .add("maxY",max.getY())
                .add("maxZ",max.getZ())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, world);
    }

    private double center(double x1, double x2) {
        return (x1+x2)/2;
    }

    @NotNull
    public BoundingBox expand(double expansion) {
        return this.expand(expansion, expansion, expansion, expansion, expansion, expansion);
    }
    @NotNull
    public BoundingBox expand(double negativeX, double negativeY, double negativeZ, double positiveX, double positiveY, double positiveZ) {
        if (negativeX == 0.0D && negativeY == 0.0D && negativeZ == 0.0D && positiveX == 0.0D && positiveY == 0.0D && positiveZ == 0.0D) {
            return this;
        }
        double newMinX = this.getMinX() - negativeX;
        double newMinY = this.getMinY() - negativeY;
        double newMinZ = this.getMinZ() - negativeZ;
        double newMaxX = this.getMaxX() + positiveX;
        double newMaxY = this.getMaxY() + positiveY;
        double newMaxZ = this.getMaxZ() + positiveZ;

        // limit shrinking:
        Vector center = getCenter();
        if (newMinX > newMaxX) {
            double centerX = center.getX();
            if (newMaxX >= centerX) {
                newMinX = newMaxX;
            } else if (newMinX <= centerX) {
                newMaxX = newMinX;
            } else {
                newMinX = centerX;
                newMaxX = centerX;
            }
        }
        if (newMinY > newMaxY) {
            double centerY = center.getY();
            if (newMaxY >= centerY) {
                newMinY = newMaxY;
            } else if (newMinY <= centerY) {
                newMaxY = newMinY;
            } else {
                newMinY = centerY;
                newMaxY = centerY;
            }
        }
        if (newMinZ > newMaxZ) {
            double centerZ = center.getZ();
            if (newMaxZ >= centerZ) {
                newMinZ = newMaxZ;
            } else if (newMinZ <= centerZ) {
                newMaxZ = newMinZ;
            } else {
                newMinZ = centerZ;
                newMaxZ = centerZ;
            }
        }
        return this.resize(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    @NotNull
    public BoundingBox resize(double x1, double y1, double z1, double x2, double y2, double z2) {
        NumberConversions.checkFinite(x1, "x1 not finite");
        NumberConversions.checkFinite(y1, "y1 not finite");
        NumberConversions.checkFinite(z1, "z1 not finite");
        NumberConversions.checkFinite(x2, "x2 not finite");
        NumberConversions.checkFinite(y2, "y2 not finite");
        NumberConversions.checkFinite(z2, "z2 not finite");

        min.setX( Math.min(x1, x2));
        min.setY(Math.min(y1, y2));
        min.setZ(Math.min(z1, z2));
        max.setX(Math.max(x1, x2));
        max.setY(Math.max(y1, y2));
        max.setZ(Math.max(z1, z2));
        return this;
    }

    @NotNull
    @Override
    public BoundingBox clone() {
        return new BoundingBox(this.world, this.min.clone(), this.max.clone());
    }
}

