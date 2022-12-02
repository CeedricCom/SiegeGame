package me.cedric.siegegame.modules.capturepoint;

public class Cuboid {

    private final Vector3D p1;
    private final Vector3D p2;


    public Cuboid(final Vector3D p1, final Vector3D p2) {
        this.p1 = new Vector3D(Math.min(p1.getX(),p2.getX()),Math.min(p1.getY(), p2.getY()),Math.min(p1.getZ(),p2.getZ()));
        this.p2 = new Vector3D(Math.max(p1.getX(),p2.getX()),Math.max(p1.getY(), p2.getY()),Math.max(p1.getZ(),p2.getZ()));
    }

    //checks for a collision of a point in the XZ horizontal plane using AABB collision
    //to check for a block convert the block to a vector3D
    public boolean colliding2d(final Vector3D point) {
        return point.getX() <= this.getMaxX() && point.getX() >= this.getMinX()
                && point.getZ() <= this.getMaxZ() && point.getZ()>=this.getMinZ();
    }
    //checks if a point is inside of the cuboid using AABB collision
    public boolean colliding3d(final Vector3D point) {
        return point.getX() <= this.getMaxX() && point.getX() >= this.getMinX()
                && point.getZ() <= this.getMaxZ() && point.getZ()>=this.getMinZ()
                && point.getY() <= this.getMaxY() && point.getY()>=this.getMinY();

    }
    //checks if two cuboids are colliding on the XZ horizontal plane
    public boolean colliding2d(final Cuboid cube) {
        return this.getMinX() <= cube.getMaxX() && this.getMaxX() >= cube.getMinX() &&
                this.getMinZ() <= cube.getMaxZ() &&  this.getMaxZ() >= this.getMinZ();
    }
    //checks if two cuboids are colliding on the using AABB collision
    public boolean colliding3d(final Cuboid cube) {
        return this.getMinX() <= cube.getMaxX() && this.getMaxX() >= cube.getMinX() &&
                this.getMinZ() <= cube.getMaxZ() &&  this.getMaxZ() >= this.getMinZ() &&
                this.getMinY() <= cube.getMaxY() &&  this.getMaxY() >= this.getMinY();
    }
    public double getLengthX() {
        return getMaxX()-getMinX();
    }

    public double getLengthY() {
        return getMaxY()-getMinY();
    }

    public double getLengthZ() {
        return getMaxZ()-getMinZ();
    }

    //getters and setters
    public Vector3D getP1() {
        return p1;
    }

    public Vector3D getP2() {
        return p2;
    }

    public double getMaxX() {
        return Math.max(p1.getX(), p2.getX());
    }

    public double getMinX() {
        return Math.min(p1.getX(),p2.getX());
    }
    public double getMaxY() {
        return Math.max(p1.getY(), p2.getY());
    }
    public double getMinY() {
        return Math.min(p1.getY(),p2.getY());
    }
    public double getMaxZ() {
        return Math.max(p1.getZ(), p2.getZ());
    }
    public double getMinZ() {
        return Math.min(p1.getZ(),p2.getZ());
    }

}
