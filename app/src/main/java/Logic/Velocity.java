package Logic;

/**
 * Created by Le Pham Minh Duc on 6/19/2017.
 */

public class Velocity {
    public double x;
    public double y;
    public double z;

    public Velocity(double _x, double _y, double _z) {
        x = _x;
        y = _y;
        z = _z;
    }

    public static Velocity getVelocity(double acceX, double acceY, double acceZ, long deltaTime) {
        double time = deltaTime / 1000;
        return new Velocity(acceX * time, acceY * time, acceZ * time);
    }

    public void add(Velocity v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }
}
