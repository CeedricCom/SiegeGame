package me.cedric.siegegame.modules.capturepoint;

import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;

public class Vector3D {

    // Basic class to handle vectors in 3 dimensions

    private double X;
    private double Y;
    private double Z;

    public Vector3D(double X, double Y, double Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    //Returns a vector3D from a location
    public static Vector3D getFromPlayerLocation(Location l) {
        return new Vector3D(l.getX(), l.getY(), l.getZ());
    }

    public static Vector3D getFromBlockLocation(Location l) {
        return new Vector3D(l.getX(), l.getY(), l.getZ()).center();
    }

    //returns a vector3D from a block location
    public static Vector3D getFromPlayerLocation(BlockPosition l) {
        return new Vector3D(l.getX(), l.getY(), l.getZ());
    }

    public String toString() {
        return this.X + "," + this.Y + "," + this.Z;
    }

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public void setY(double y) {
        Y = y;
    }

    public void setZ(double z) {
        Z = z;
    }

    public double getY() {
        return Y;
    }

    public double getZ() {
        return Z;
    }

    public void addX(double amount) {
        X = X + amount;
    }

    public void addY(double amount) {
        Y = Y + amount;
    }

    public void addZ(double amount) {
        Z = Z + amount;
    }

    public void set(double X, double Y, double Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public void set(Vector3D l) {
        this.X = l.getX();
        this.Y = l.getY();
        this.Z = l.getZ();
    }

    /**
     * Multiplies the vector by a scalar
     *
     * @param N The number which to multiply by
     */
    public void scalarMultiply(double N) {
        this.X = this.X * N;
        this.Y = this.Y * N;
        this.Z = this.Z * N;
    }

    //adds a point to a vector
    public void add(double X, double Y, double Z) {
        this.X = this.X + X;
        this.Y = this.Y + Y;
        this.Z = this.Z + Z;
    }

    public Location toLocation(World world) {
        return new Location(world, this.X, this.Y, this.Z);
    }


    //adds two vectors
    public void add(Vector3D V) {
        add(V.getX(), V.getY(), V.getZ());
    }

    //Subtracts V from this i.e. This + -V
    public void subtract(Vector3D V) {
        add(-V.getX(), -V.getY(), -V.getZ());
    }

    private double getBlockX(double X) {
        return Math.floor(X);
    }

    public static int getBlockXZ(double X) {
        return (int) Math.floor(X);
    }

    //Floors and alters this vector permanently.
    public void floor() {
        this.X = getBlockX(X);
        this.Y = getBlockX(Y);
        this.Z = getBlockX(Z);
    }

    //returns the floor of this vector
    public Vector3D getFloor() {
        return new Vector3D(getBlockX(this.X), getBlockX(this.Y), getBlockX(this.Z));
    }

    //returns the center of this vector, note this only works if the vector is a block-position!
    public Vector3D center() {
        return new Vector3D(this.X + 0.5, this.Y, this.Z + 0.5);
    }

    //checks if two vectors are equal
    public boolean equals(Vector3D V) {
        return V.getX() == X && V.getY() == Y && V.getZ() == Z;
    }

    public Vector3D clone() {
        return new Vector3D(this.X, this.Y, this.Z);
    }

    public void setClone(Vector3D v) {
        this.X = v.getX();
        this.Y = v.getY();
        this.Z = v.getZ();
    }
}

