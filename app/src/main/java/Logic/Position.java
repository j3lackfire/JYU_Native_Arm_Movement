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
//        double deltaTimeSquare = (deltaTime / 1000d) * (deltaTime / 1000d);
//        return new Position(acceX * deltaTimeSquare, acceY * deltaTimeSquare, acceZ * deltaTimeSquare);
        double x1 = acceX * deltaTime *deltaTime;
        double y1 = acceY * deltaTime *deltaTime;
        double z1 = acceZ * deltaTime *deltaTime;
        Log.i(SetupMode.TAG, "Delta time: " + deltaTime + " - Added pos : " + x1 + ", " + y1 + ", " + z1);
        return new Position(acceX * deltaTime * deltaTime, acceY * deltaTime * deltaTime, acceZ * deltaTime * deltaTime);
    }

    public void add(Position p) {
        x += p.x;
        y += p.y;
        z += p.z;
    }

    public void addAndDivideByMillions(Position p) {
        long million = 1000000;
        x += p.x /million;
        y += p.y /million;
        z += p.z /million;
        Log.i(SetupMode.TAG, "Current pos : " + x + ", " + y + ", " + z);
    }
}
