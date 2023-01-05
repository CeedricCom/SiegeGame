package me.cedric.siegegame.util;

import com.google.common.base.MoreObjects;
import me.cedric.siegegame.model.teams.territory.Vector2D;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class Box2D {

    private final Vector2D min;
    private final Vector2D max;

    public Box2D(Vector2D p1, Vector2D p2) {
        this.min = new Vector2D(Math.min(p1.getX(),p2.getX()), Math.min(p1.getZ(),p2.getZ()));
        this.max = new Vector2D(Math.max(p1.getX(),p2.getX()), Math.max(p1.getZ(),p2.getZ()));
    }

    public double getMaxX() {
        return max.getX();
    }

    public double getMaxZ() {
        return max.getZ();
    }

    public double getMinX() {
        return min.getX();
    }

    public double getMinZ() {
        return min.getZ();
    }

    public void setMaxX(double x) {
        this.max.setX(x);
    }

    public void setMaxZ(double z) {
        this.max.setZ(z);
    }

    public void setMinX(double x) {
        this.min.setX(x);
    }

    public void setMinZ(double z) {
        this.min.setZ(z);
    }

    public void update(Consumer<Box2D> box) {
        box.accept(this);
    }

    public boolean isColliding(@Nullable Box2D b) {
        if(b==null)
            return false;

        return (min.getX() <= b.max.getX() && max.getX() >= b.min.getX()) &&
                (min.getZ() <= b.max.getZ() && max.getZ() >= b.min.getZ());
    }

    public boolean isColliding(Vector2D b) {
        return (b.getX() >= min.getX() && b.getX() <= max.getX()) &&
                (b.getZ() >= min.getZ() && b.getZ() <= max.getZ());
    }

    public boolean isColliding(Location l) {
        return isColliding(l.toVector());
    }

    public boolean isCollidingIgnoreInverse(Vector p) {
        return (p.getX() >= min.getX() && p.getX() <= max.getX()) &&
                (p.getZ() >= min.getZ() && p.getZ() <= max.getZ());
    }

    public boolean isColliding(Vector p) {
        return (p.getX() >= min.getX() && p.getX() <= max.getX()) &&
                (p.getZ() >= min.getZ() && p.getZ() <= max.getZ());
    }

    public boolean contains(@Nullable Box2D b) {
        if(b==null)
            return false;

        return (max.getX() >= b.max.getX() && min.getX() <= b.min.getX()) &&
                (max.getZ() >= b.max.getZ() && min.getZ() <= b.min.getZ());
    }

    public Vector2D getCenter() {
        return new Vector2D(center(min.getX(), max.getX()), center(min.getZ(),max.getZ()));
    }

    public Location externalProject(Location location) {
        Location currentLocation = location.clone();
        if (currentLocation.getX() < getMinX())
            currentLocation.setX(getMinX());

        if (currentLocation.getX() > getMaxX())
            currentLocation.setX(getMaxX());

        if (currentLocation.getZ() < getMinZ())
            currentLocation.setZ(getMinZ());

        if (currentLocation.getZ() > getMaxZ())
            currentLocation.setZ(getMaxZ());

        return currentLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Box2D that = (Box2D) o;
        return Objects.equals(min, that.min) && Objects.equals(max, that.max);
    }

    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("minX",min.getX())
                .add("minZ",min.getZ())
                .add("maxX",max.getX())
                .add("maxZ",max.getZ())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    private double center(double x1, double x2) {
        return (x1+x2)/2;
    }

    @NotNull
    public Box2D expand(double expansion) {
        return this.expand(expansion, expansion, expansion, expansion);
    }
    @NotNull
    public Box2D expand(double negativeX, double negativeZ, double positiveX, double positiveZ) {
        if (negativeX == 0.0D && negativeZ == 0.0D && positiveX == 0.0D && positiveZ == 0.0D) {
            return this;
        }
        double newMinX = this.getMinX() - negativeX;
        double newMinZ = this.getMinZ() - negativeZ;
        double newMaxX = this.getMaxX() + positiveX;
        double newMaxZ = this.getMaxZ() + positiveZ;

        // limit shrinking:
        Vector2D center = getCenter();
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
        return this.resize(newMinX, newMinZ, newMaxX, newMaxZ);
    }

    @NotNull
    public Box2D resize(double x1, double z1, double x2, double z2) {
        NumberConversions.checkFinite(x1, "x1 not finite");
        NumberConversions.checkFinite(z1, "z1 not finite");
        NumberConversions.checkFinite(x2, "x2 not finite");
        NumberConversions.checkFinite(z2, "z2 not finite");

        min.setX( Math.min(x1, x2));
        min.setZ(Math.min(z1, z2));
        max.setX(Math.max(x1, x2));
        max.setZ(Math.max(z1, z2));
        return this;
    }

    @NotNull
    @Override
    public Box2D clone() {
        return new Box2D(this.min.clone(), this.max.clone());
    }

}
