package me.cedric.siegegame.modules.capturepoint;

import java.util.List;

public class MathUtil {
    //gets distance squared between two points p1 and p2 in cartesian coordinates
    public static double getDistanceSquared(double X1, double Y1, double Z1, double X2, double Y2, double Z2) {
        return getDistanceSquared(X1-X2,Y1-Y2,Z1-Z2);
    }
    //gets distance squared between two points using the difference between points, dX should be x1-x2, dy - y1-y2 and dz z1-z2
    public static double getDistanceSquared(double dX, double dY, double dZ) {
        return dX*dX + dY*dY + dZ*dZ;
    }
    //gets the distance between two vectors by putting it into the difference formula
    public static double getDistanceSquared(Vector3D location, Vector3D location2) {
        double dX = location.getX() - location2.getX();
        double dY = location.getY() - location2.getY();
        double dZ = location.getZ() - location2.getZ();
        return getDistanceSquared(dX,dY,dZ);
    }
    //gets the distance between two points in the XZ horizontal plane
    public static double getDistanceSquared(double X1, double X2, double Z1, double Z2) {
        double dX = X1-X2;
        double dZ = Z1-Z2;
        return getDistanceSquared(dX,0,dZ);
    }
    //gets the minimum distance in degrees between two angles.
    public static double getDistanceBetweenAngles(double theta, double phi) {
        double distance = Math.abs(theta-phi) % 360;
        if(distance>180) {
            return 360-distance;
        }
        return distance;
    }
    //gets the length of the hypotenuse of an nth dimensional triangle
    //array should be be in the form of dX
    public static double hypot(final double... array) {
        return Math.sqrt(hypotSquared(array));
    }
    public static double hypotSquared(final double... array) {
        double total = 0;
        for(int i=0;i<array.length;i++) {
            total += Math.pow(array[i],2.0);
        }
        return total;
    }
    //gets the lowest absolute number excluding negatives
    public static double lowestAbs(double... numbers) {
        double value = numbers[0];
        for(double n : numbers) {
            if(Math.abs(n)<value) {
                value = n;
            }
        }
        return value;
    }
    //takes the average of n numbers
    public static double average(double... numbers) {
        double total = 0;
        for(double n: numbers) {
            total+=n;
        }
        return total/numbers.length;
    }
    //takes the highest number including negatives of a list of numbers
    public static double highest(List<Double> numbers) {
        double value = numbers.get(0);
        for(double n: numbers) {
            if(n > value) {
                value = n;
            }
        }
        return value;
    }
    //converts ping to number of packets, rounds packets up to benefit the player
    public static int pingFormula(int ping) {
        return (int) Math.ceil((double)ping/  0.05);
    }
    //gets the lowest absolute value of two numbers
    public static double positiveSmaller(double a, double b) {
        if(Math.abs(a)<Math.abs(b)) {
            return a;
        }
        return b;
    }

    /**
     *
     * gets the yaw i.e the angle between the two positions in the XZ plane as viewed from the playerlocation
     * Assume two players are in 2D space, the angle that is returned is the angle that player1 sees the target on the XZ plane
     * the angle should be between -pi and pi
     *
     * @param playerLocation the primary location
     * @param targetLocation the location being looked at
     * @return the angle in the XZ plane that the secondary target is viewed from the primary target (0 < x < 180)
     */
    public static double getYawBetweenTwoPostions(Vector3D playerLocation, Vector3D targetLocation) {
        double dX = targetLocation.getX() - playerLocation.getX();
        double dZ = targetLocation.getZ() - playerLocation.getZ();
        return ((Math.atan2(dZ,dX) * 180/Math.PI) + 90 );
    }

    /** generates a random number by a seed
     *
     * @param seed the number used to seed the RNG algorithm, using the same seed multiple times will yield the same result
     * @return a random double between 0 and 1
     */
    public static double random(int seed) {
        double x = Math.sin(seed)*10000;
        return x-Math.floor(x);
    }


    /**
     *  gets the pitch i.e the perpendicular angle between the XZ plane and the y axis from the playerlocation's perspective
     *  Assume two players are in 3D space, the angle that is returned is the angle that player 1 sees in the y direction
     *  i.e if the target is higher and higher above player1 then the angle will be bigger.
     * @param playerLocation the location of the primary target
     * @param targetLocation the location of the target being looked at
     * @return the vertical angle (XZ perpendicular to y) as viewed by the primary target (-90 < x < 90)
     */
    public static double getPitchBetweenTwoPositions(Vector3D playerLocation, Vector3D targetLocation) {
        double dX = targetLocation.getX() - playerLocation.getX();
        double dY = targetLocation.getY() - playerLocation.getY();
        double dZ = targetLocation.getZ() - playerLocation.getZ();
        double dist = Math.sqrt(getDistanceSquared(dX,dY,dZ));
        return -Math.atan2(dY,dist) * 180/Math.PI;
    }
}
