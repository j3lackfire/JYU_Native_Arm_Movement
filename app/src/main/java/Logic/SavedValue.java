package Logic;

/**
 * Created by Le Pham Minh Duc on 6/13/2017.
 */

public class SavedValue {
    private long timeStamp;

    private float acceX;
    private float acceY;
    private float acceZ;

    private float gyroX;
    private float gyroY;
    private float gyroZ;

    public SavedValue() {
        timeStamp = -1;

        acceX = -1;
        acceY = -1;
        acceZ = -1;

        gyroX = -1;
        gyroY = -1;
        gyroZ = -1;
    }

    public void setTimeStamp(long _time) {
        timeStamp = _time;
    }

    public void setAcce(float x,float y, float z) {
        acceX = x;
        acceY = y;
        acceZ = z;
    }

    public void setGyro(float x,float y, float z) {
        gyroX = x;
        gyroY = y;
        gyroZ = z;
    }
}
