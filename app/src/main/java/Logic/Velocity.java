package Logic;

/**
 * Created by Le Pham Minh Duc on 6/19/2017.
 */

public class Velocity {
    public double x,y,z;

    public Velocity(double _x, double _y, double _z) {
        x = _x;
        y = _y;
        z = _z;
    }

    public static Velocity getVelocity(double acceX, double acceY, double acceZ, double time) {
        return new Velocity(acceX * time/1000, acceY * time/1000, acceZ * time/1000);
    }

    public void add(Velocity v) {
        x += v.x;
        y += v.y;
        z += v.z;
    }
}
