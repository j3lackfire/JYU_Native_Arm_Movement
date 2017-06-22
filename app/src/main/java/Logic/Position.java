package Logic;

import android.util.Log;

import com.watsonarmtest.jyu.watson_stroke_arm.SetupMode;

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

    public static Position getPosition(double veloX, double veloY, double veloZ, long deltaTime) {
        return new Position(veloX * deltaTime / 1000, veloY * deltaTime / 1000, veloZ * deltaTime / 1000);
    }

    public static Position getPositionFromAcceleration(double acceX, double acceY, double acceZ, long deltaTime) {
        double deltaTimeSquare = (deltaTime / 1000d) * (deltaTime / 1000d);
        Log.v(SetupMode.TAG, "Delta time square: " + deltaTimeSquare);
        return new Position(acceX * deltaTimeSquare, acceY * deltaTimeSquare, acceZ * deltaTimeSquare);
    }

    public void add(Position p) {
        x += p.x;
        y += p.y;
        z += p.z;
    }
}
