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

    //0 = x, 1 = y, 2 = z
    public static Position calculatePosition(long deltaTime, double[] velocity, double[] acceleration) {
        double time = deltaTime / 1000d;
        //s = s0 + v0 * t + 1/2 * a * t^2
        double posX = velocity[0] * time + acceleration[0] * time * time / 2;
        double posY = velocity[1] * time + acceleration[1] * time * time / 2;
        double posZ = velocity[2] * time + acceleration[2] * time * time / 2;
        return new Position(posX, posY, posZ);
    }


    public void add(Position p) {
        x += p.x;
        y += p.y;
        z += p.z;
    }
}
