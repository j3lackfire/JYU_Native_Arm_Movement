package Logic.Version2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by Le Pham Minh Duc on 7/7/2017.
 */

public class MySensorManagerV2 {

    //The sensor manager from android API
    private SensorManager sensorManager;
    private Sensor acceSensor;
    private Sensor gyroSensor;

    private long previousTime = -1;
    /*
    The data from the Android sensor is saved here
    Because we can not access the sensor data directly (there is no Android's API to access it)
    We can only access it only when the sensor value is changed.
    That's why I have to cached it here
    */
    private double[] cachedAccelerationData = {-1,-1,-1};
    private double[] cachedGyroData = {-1,-1,-1};
    private double[] cachedRotationData = {-1,-1,-1,-1};

    private long positionTimer = 0; //cached value, don't worry about it.
    private long cachedDeltaTime = -1;
    private long stationaryTimer = 0; //Time the phone has been stationary.
    private long currentStepTimer = 0; //how long the user have been at this step
    private final long maximumStepTimer = 10000; //the maximum time the use can stay in a step. If excedd this number, step is fail
    /*
    Configuration data for the sensor recording step
    */
    //update rate of the sensor.
    private final long refreshTimeMili = 50; //50 mili seconds - 0.05 seconds

    private final long maxStationaryTime = 2000; //milliseconds, 2000 is 2 second
    //Stationary time for doctor mode
    private long doctorStationaryTime = 5000; // hold the phone still for

    //*******************************
    //----TOMMI ---- IT'S HERE ------
    //*******************************
    //if the current value goes beyond this value, mark as the phone is still moving.
    private final double maximumDeltaAcceleration = 0.5;
    //*******************************
    // CHANGE THIS VALUE TOMMI !!!!
    //*******************************


    private final double maximumDeltaGyro = 1;

    private boolean shouldVibratePhone = false;

    private double[] calibratedSensorValue = {0,0,0};

    //called on On Created
    public MySensorManagerV2(SensorManager _sensorManager) {
        sensorManager = _sensorManager;

        acceSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //values for calculating the position.
        cachedDeltaTime = -1;
    }

    //The class calling this must implement SensorEventListener interface
    //called on OnResume
    public void registerSensors(SensorEventListener listener) {
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);
    }

    //called on on pause
    public void unregisterListener(SensorEventListener listener) {
        sensorManager.unregisterListener(listener);
    }

    //called on on Sensor changed
    //this function should be called several time per second
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LINEAR_ACCELERATION:
                for (int i = 0; i < 3; i++) {
                    cachedAccelerationData[i] = event.values[i];
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                for (int i = 0; i < 3; i++) {
                    cachedGyroData[i] = event.values[i];
                }
                break;
        }
        roundNumber();
    }

    // ------------- Main update function -----------------
    public void updateSensorManager(long deltaTime) {
        positionTimer += deltaTime;
        stationaryTimer += deltaTime;
        currentStepTimer += deltaTime;
        shouldVibratePhone = false;
        if (positionTimer >= refreshTimeMili) {
            if (isPhoneMovementDetected()) {
                stationaryTimer = 0;
                shouldVibratePhone = true;
            }
        }
    }

    public boolean isStepCompleted() { return stationaryTimer >= maxStationaryTime; }

    public boolean isStepFail() {
        return currentStepTimer > maximumStepTimer;
    }

    public long getStationaryTimer() {return stationaryTimer;}

    //next step is set, reset everything
    public void toNextStep() {
        stationaryTimer = 0;
        positionTimer = 0;
        currentStepTimer = 0;
    }

    //check if the phone is stationary or not.
    private boolean isPhoneMovementDetected() {
        for (int i = 0; i < 3; i ++) {
            if (Math.abs(cachedAccelerationData[i]) > maximumDeltaAcceleration) {
                return true;
            }
        }
//        double deltaGyro = 0;
//        for (int i = 0; i < 3; i ++) {
//            double delta = (savedGyroData[i] - cachedGyroData[i]) * (savedGyroData[i] - cachedGyroData[i]);
//            deltaGyro += delta;
//        }
//        deltaGyro = Math.sqrt(Math.abs(deltaGyro));
//        if (deltaGyro >= maximumDeltaGyro) {
//            return true;
//        }
        return false;

    }

    public boolean isSignificantMovementDetected() {
        for (int i = 0; i < 3; i ++) {
            if (Math.abs(cachedAccelerationData[i]) > maximumDeltaAcceleration * 2) {
                return true;
            }
        }
        return false;
    }

    public long getVibrateTime() { return shouldVibratePhone ? refreshTimeMili * 2 : -1; }

    //utilities function to make display easier to read
    private void roundNumber() {
        for (int i = 0; i < 3; i ++) {
            cachedAccelerationData[i] = Math.round(cachedAccelerationData[i]*100)/100d;
            cachedGyroData[i] = Math.round(cachedGyroData[i]*100d)/100d;
        }
    }

    public void doCalibration() {

    }

}
