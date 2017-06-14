package Logic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.DecimalFormat;

/**
 * Created by Le Pham Minh Duc on 6/13/2017.
 */

public class MySensorManager {
    private int accelerationSensorType;

    public SensorManager sensorManager;
    public Sensor acceSensor;
    public Sensor gyroSensor;

    private long previousTime = -1;
    private double[] previousAccelerationData = {-1,-1,-1};
    private double[] previousGyroData = {-1,-1,-1};

    //called on On Created
    public MySensorManager(SensorManager _sensorManager) {
        accelerationSensorType = Sensor.TYPE_LINEAR_ACCELERATION;
        sensorManager = _sensorManager;
        acceSensor = sensorManager.getDefaultSensor(accelerationSensorType);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    //The class calling this must implement SensorEventListener interface
    //called on OnResume
    public void registerSensors(SensorEventListener listener) {
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(accelerationSensorType), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    //called on on pause
    public void unregisterListener(SensorEventListener listener) {
        sensorManager.unregisterListener(listener);
    }

    //called on on Sensor changed
    //this function should be called serveral time per second
    public void onSensorChanged(SensorEvent event, long time) {
        previousTime = time;
        if (event.sensor.getType() == accelerationSensorType) { //sensor event type is accelerometer
            for (int i = 0; i < 3; i++) {
                previousAccelerationData[i] = event.values[i];
            }
        } else {
            for (int i = 0; i < 3; i++) {
                previousGyroData[i] = event.values[i];
            }
        }
        roundNumber();
    }


    private void roundNumber() {
        for (int i = 0; i < 3; i ++) {
            previousAccelerationData[i] = Math.round(previousAccelerationData[i]*100)/100d;
            previousGyroData[i] = Math.round(previousGyroData[i]*100d)/100d;
        }
    }

    public String getSensorDataJSON() {
        SavedValue savedValue = new SavedValue();
        savedValue.setTimeStamp(previousTime);
        savedValue.setAcce(previousAccelerationData[0],previousAccelerationData[1],previousAccelerationData[2]);
        savedValue.setGyro(previousGyroData[0],previousGyroData[1],previousGyroData[2]);
        return savedValue.myGetJsonString();
    }

    public String getSensorDataJSONPretty() {
        SavedValue savedValue = new SavedValue();
        savedValue.setTimeStamp(previousTime);
        savedValue.setAcce(previousAccelerationData[0],previousAccelerationData[1],previousAccelerationData[2]);
        savedValue.setGyro(previousGyroData[0],previousGyroData[1],previousGyroData[2]);
        return savedValue.myGetJsonStringPretty();
    }

    public double[] getGyroData() { return previousGyroData; }

    public double[] getAccelerationData() { return previousAccelerationData; }
}
