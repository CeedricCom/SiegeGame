package me.cedric.siegegame.model.teams.territory;

public class Vector2D {

    private int x;
    private int z;

    public Vector2D(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public Vector2D(double x, double z) {
        this.x = (int) x;
        this.z = (int) z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public void add(int x, int z) {
        this.x += x;
        this.z += z;
    }

    public void subtract(int x, int z) {
        this.x -= x;
        this.z -= z;
    }

    public void add(Vector2D v) {
        this.x += v.getX();
        this.z += v.getZ();
    }

    public void subtract(Vector2D v) {
        this.x -= v.getX();
        this.z -= v.getZ();
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setX(double x) {
        this.x = (int) x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setZ(double z) {
        this.z = (int) z;
    }

    @Override
    public String toString() {
        return x + "," + z;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vector2D))
            return false;
        Vector2D other = (Vector2D) obj;
        return other.getX() == this.x && other.getZ() == this.z;
    }

    @Override
    public Vector2D clone() {
        return new Vector2D(x, z);
    }
}
