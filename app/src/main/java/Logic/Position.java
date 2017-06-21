package Logic;

/**
 * Created by Le Pham Minh Duc on 6/19/2017.
 */

public class Position {
    public double x;
    public double y;
    public double z;

    public Position(double _x, double _y, double _z) {
        x = _x;
        y = _y;
        z = _z;
    }

    public static Position getPosition(double veloX, double veloY, double veloZ, double deltaTime) {
        return new Position(veloX * deltaTime / 1000, veloY * deltaTime / 1000, veloZ * deltaTime / 1000);
    }

    public void add(Position p) {
        x += p.x;
        y += p.y;
        z += p.z;
    }
}
