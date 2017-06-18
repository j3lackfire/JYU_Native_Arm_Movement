package Logic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Le Pham Minh Duc on 6/13/2017.
 */

public class MySensorManager {
    private int accelerationSensorType;

    public SensorManager sensorManager;
    public Sensor acceSensor;
    public Sensor gyroSensor;

    private long previousTime = -1;
    /*
    The data from the Android sensor is saved here
    Because we can not access the sensor data directly (I don't know why)
    We can only access it only when the sensor value is changed.
    That's why I have to saved it here
    */
    private double[] cachedAccelerationData = {-1,-1,-1};
    private double[] cachedGyroData = {-1,-1,-1};

    /*
    Configuration data for the sensor recording step
    */
    private long refreshTimeMili = 50; //50 mili seconds - 0.05 seconds
    private long positionTimer = 0;

    private long stationaryTimer = 0;
    private long maxStationaryTime = 2000;

    //if the current value goes beyond this value, mark as the phone is still moving.
    private double maximumDeltaAcceleration = 0.5;
    private double maximumDeltaGyro = 1;

    private double[] savedAccelerationData = {-1,-1,-1};
    private double[] savedGyroData = {-1,-1,-1};

    private boolean shouldVibratePhone = false;

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
                cachedAccelerationData[i] = event.values[i];
            }
        } else {
            for (int i = 0; i < 3; i++) {
                cachedGyroData[i] = event.values[i];
            }
        }
        roundNumber();
    }

    public void updateSensorManager(long deltaTime) {
        positionTimer += deltaTime;
        stationaryTimer += deltaTime;
        if (positionTimer >= refreshTimeMili) {
            //check if there are significant movement
            if (isSignificantMovementDetected()) {
                //reset the timer
                stationaryTimer = 0;
                //vibrate the phone
                shouldVibratePhone = true;
            } else {
                shouldVibratePhone = false;
            }
            //save the new data
            savedAccelerationData = cachedAccelerationData;
            savedGyroData = cachedGyroData;
        }
    }

    public void resetAllTimer() {
        stationaryTimer = 0;
        positionTimer = 0;
    }

    public boolean shouldSaveUserPositionData() { return stationaryTimer >= maxStationaryTime; }

    public long getVibrateTime() { return shouldVibratePhone ? refreshTimeMili : -1; }

    public boolean isSignificantMovementDetected() {
        for (int i = 0; i < 3; i ++) {
            if (cachedAccelerationData[i] > maximumDeltaAcceleration) {
                return true;
            }
        }
        double deltaGyro = 0;
        for (int i = 0; i < 3; i ++) {
            double delta = (savedGyroData[i] - cachedGyroData[i]) * (savedGyroData[i] - cachedGyroData[i]);
            deltaGyro += delta;
        }
        deltaGyro = Math.sqrt(deltaGyro);
        if (deltaGyro >= maximumDeltaGyro) {
            return true;
        }
        return false;

    }

    private void roundNumber() {
        for (int i = 0; i < 3; i ++) {
            cachedAccelerationData[i] = Math.round(cachedAccelerationData[i]*100)/100d;
            cachedGyroData[i] = Math.round(cachedGyroData[i]*100d)/100d;
        }
    }

    public String getSensorDataJSON() {
        SavedValue savedValue = new SavedValue();
        savedValue.setTimeStamp(previousTime);
        savedValue.setAcce(cachedAccelerationData[0], cachedAccelerationData[1], cachedAccelerationData[2]);
        savedValue.setGyro(cachedGyroData[0], cachedGyroData[1], cachedGyroData[2]);
        return savedValue.myGetJsonString();
    }

    public String getSensorDataJSONPretty() {
        SavedValue savedValue = new SavedValue();
        savedValue.setTimeStamp(previousTime);
        savedValue.setAcce(cachedAccelerationData[0], cachedAccelerationData[1], cachedAccelerationData[2]);
        savedValue.setGyro(cachedGyroData[0], cachedGyroData[1], cachedGyroData[2]);
        return savedValue.myGetJsonStringPretty();
    }

    public double[] getGyroData() { return cachedGyroData; }

    public double[] getAccelerationData() { return cachedAccelerationData; }
}
