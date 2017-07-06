package Logic;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by Le Pham Minh Duc on 6/13/2017.
 */

public class MySensorManager {
    private int accelerationSensorType;

    private SensorManager sensorManager;
    private Sensor acceSensor;
    private Sensor gyroSensor;
    private Sensor rotationSensor;

    private long previousTime = -1;
    /*
    The data from the Android sensor is saved here
    Because we can not access the sensor data directly (there is no Android's API to access it)
    We can only access it only when the sensor value is changed.
    That's why I have to cached it here
    */
    private double[] cachedAccelerationData = {-1,-1,-1};
    //because there are many type of event changes, so we need to have accurate deltaTime value to calculate the position
    private long previouslyChangedAccelerationTime = -1;
    private double[] cachedGyroData = {-1,-1,-1};
    private double[] cachedRotationData = {-1,-1,-1,-1};

    private  boolean isStepFail = false;

    private long positionTimer = 0; //cached value, don't worry about it.
    private long cachedDeltaTime = -1;
    private long stationaryTimer = 0; //Time the phone has been stationary.
    private long currentStepTimer = 0; //how long the user have been at this step
    private long maximumStepTimer = 5000; //the maximum time the use can stay in a step. If excedd this number, step is fail
    //average out the acceleration value out so we can have a more stable stats. Not seem to be working very well though
    private ArrayList<Float> averageAcceX = new ArrayList<>();
    private ArrayList<Float> averageAcceY = new ArrayList<>();
    private ArrayList<Float> averageAcceZ = new ArrayList<>();
    /*
    Configuration data for the sensor recording step
    */
    //update rate of the sensor.
    private long refreshTimeMili = 50; //50 mili seconds - 0.05 seconds
    //the sensor value is recorded and updated every 40 or so mili seconds.
    //we need to collect serveral values at once and then average them out
    private long sensorRefreshRate = 200; //200 miliseconds

    //Stationary time for Setup mode
    private long maxStationaryTime = 2000; //milliseconds, 2000 is 2 second
    //Stationary time for doctor mode
    private long doctorStationaryTime = 5000; // hold the phone still for

    //if the current value goes beyond this value, mark as the phone is still moving.
    private double maximumDeltaAcceleration = 0.5;
    private double maximumDeltaGyro = 1;

    private double[] savedGyroData = {-1,-1,-1};
    private boolean shouldVibratePhone = false;

    //called on On Created
    public MySensorManager(SensorManager _sensorManager) {
        accelerationSensorType = Sensor.TYPE_LINEAR_ACCELERATION;
        sensorManager = _sensorManager;

        acceSensor = sensorManager.getDefaultSensor(accelerationSensorType);
        gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        //values for calculating the position.
        previouslyChangedAccelerationTime = -1;
        cachedDeltaTime = -1;
        isStepFail = false;
    }

    //The class calling this must implement SensorEventListener interface
    //called on OnResume
    public void registerSensors(SensorEventListener listener) {
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(accelerationSensorType), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_FASTEST);
    }

    //called on on pause
    public void unregisterListener(SensorEventListener listener) {
        sensorManager.unregisterListener(listener);
    }

    //called on on Sensor changed
    //this function should be called several time per second
    public void onSensorChanged(SensorEvent event, long time) {
        previousTime = time;
        //the event time stamp is in Nano Second.
        long timeStampMili = event.timestamp / 1000000;

        if (event.sensor.getType() == accelerationSensorType) { //sensor event type is accelerometer
            for (int i = 0; i < 3; i++) {
                cachedAccelerationData[i] = event.values[i];
            }

            if (previouslyChangedAccelerationTime == -1) {
                previouslyChangedAccelerationTime = timeStampMili;
            }
            long deltaTime = timeStampMili - previouslyChangedAccelerationTime;
            previouslyChangedAccelerationTime = timeStampMili;
            if (cachedDeltaTime <= 0) {
                cachedDeltaTime = deltaTime;
            } else {
                cachedDeltaTime += deltaTime;
            }
            addCacheValueData(event.values);
            if (cachedDeltaTime >= sensorRefreshRate) {
                float[] averageData = getAverageData();
                PositionManager.getInstance().updatePosition(
                        averageData[0],
                        averageData[1],
                        averageData[2],
                        cachedDeltaTime);
                resetCachedValueData();
            }
        } else {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                for (int i = 0; i < 4; i++) {
                    cachedRotationData[i] = event.values[i];
                }
            }
            else {
                for (int i = 0; i < 3; i++) {
                    cachedGyroData[i] = event.values[i];
                }
            }
        }
        roundNumber();
    }

    //to be used in the on sensor changed function to reduce the noise of the sensor data.
    private void addCacheValueData(float[] acceData) {
        averageAcceX.add(acceData[0]);
        averageAcceY.add(acceData[1]);
        averageAcceZ.add(acceData[2]);
    }

    //same as the function above.
    private float[] getAverageData() {
        float averageX = 0;
        float averageY = 0;
        float averageZ = 0;
        for (int i = 0; i < averageAcceX.size(); i ++) {
            averageX += averageAcceX.get(i);
            averageY += averageAcceY.get(i);
            averageZ += averageAcceZ.get(i);
        }
        averageX /= averageAcceX.size();
        averageY /= averageAcceX.size();
        averageZ /= averageAcceX.size();
        return new float[] {averageX, averageY, averageZ};
    }

    //same .... After getting the data, we need to reset it
    private void resetCachedValueData() {
        averageAcceX.clear();
        averageAcceY.clear();
        averageAcceZ.clear();
        cachedDeltaTime = -1;
    }

    //function to be called on every frame.
    //used to check the timer and responsible for changing the states of the mode.
    public void updateSensorManagerSetupMode(long deltaTime) {
        positionTimer += deltaTime;
        stationaryTimer += deltaTime;
        if (positionTimer >= refreshTimeMili) {
            //check if there are significant movement
            if (isPhoneMovementDetected()) {
                //reset the timer
                stationaryTimer = 0;
                //vibrate the phone
                shouldVibratePhone = true;
            } else {
                shouldVibratePhone = false;
            }
            //save the new data
//            savedAccelerationData = cachedAccelerationData;
            savedGyroData = cachedGyroData;
        }
    }

    public void updateSensorManagerDoctorMode(long deltaTime) {
        positionTimer += deltaTime;
        stationaryTimer += deltaTime;
        currentStepTimer += deltaTime;
        shouldVibratePhone = false;
        if (positionTimer >= refreshTimeMili) {
            //if the system is tracking motion, check if the phone has reached the target position
            //if the system is not tracking motion, check if there are any significant movement appear
            if (DoctorLogic.getInstance().shouldTrackPosition()) {
                //if the user takes too long to reach the position, it's fail
                if (isPhoneMovementDetected()) {
                    shouldVibratePhone = true;
                }
                if (currentStepTimer > maximumStepTimer) {
                    isStepFail = true;
                }
            } else {
                //check if there are significant movement
                if (isPhoneMovementDetected()) {
                    shouldVibratePhone = true;
                }
                //for the right hand down and left hand down step,
                // we need the user to move the phone to the first position, and hold the phone still for 2 seconds
                if (DoctorLogic.getInstance().getCurrentDoctorStep() == DoctorStep.Right_Hand_Down ||
                        DoctorLogic.getInstance().getCurrentDoctorStep() == DoctorStep.Left_Hand_Down) {
                    if (isPhoneMovementDetected()) {
                        stationaryTimer = 3000;
                    }
                } else {
                    if (isSignificantMovementDetected()) {
                        isStepFail = true;
                        //reset the timer
                        stationaryTimer = 0;
                        //vibrate the phone
                        shouldVibratePhone = true;
                    } else {
                        shouldVibratePhone = false;
                    }
                }
            }
            //save the new data
//            savedAccelerationData = cachedAccelerationData;
            savedGyroData = cachedGyroData;
        }
    }

    public boolean isThisStepFail() { return isStepFail; }

    public long getStationaryTimer() {return stationaryTimer;}

    public boolean isDoctorStepCompleted() { return stationaryTimer >= doctorStationaryTime; }

    //if the phone is moving, check if the target moving is reached.
    private boolean isTargetPositionReached() {
        double targetedZPos = DoctorLogic.getInstance().getCurrentSavedValue().frontBack;
        // if it is very close to the target position
        //if the difference between "current position" and "saved position" is small,
        return (Math.abs(PositionManager.getInstance().getCurrentPosition()[2] - targetedZPos) < 0.05);
    }

    //next step is set, reset everything
    public void toNextStep() {
        stationaryTimer = 0;
        positionTimer = 0;
        currentStepTimer = 0;
        isStepFail = false;
    }

    //if the phone is stationary for long enough of time
    //register the position and jump to the next step
    public boolean shouldRegisterUserPosition() { return stationaryTimer >= maxStationaryTime; }

    public boolean isStationaryLongEnough() { return stationaryTimer >= doctorStationaryTime; }

    //if the phone is moving, vibrate the phone
    public long getVibrateTime() { return shouldVibratePhone ? refreshTimeMili * 2 : -1; }

    //check if the phone is stationary or not.
    private boolean isPhoneMovementDetected() {
        for (int i = 0; i < 3; i ++) {
            if (Math.abs(cachedAccelerationData[i]) > maximumDeltaAcceleration) {
                return true;
            }
        }
        double deltaGyro = 0;
        for (int i = 0; i < 3; i ++) {
            double delta = (savedGyroData[i] - cachedGyroData[i]) * (savedGyroData[i] - cachedGyroData[i]);
            deltaGyro += delta;
        }
        deltaGyro = Math.sqrt(Math.abs(deltaGyro));
        if (deltaGyro >= maximumDeltaGyro) {
            return true;
        }
        return false;

    }

    private  boolean isSignificantMovementDetected() {
        for (int i = 0; i < 3; i ++) {
            if (Math.abs(cachedAccelerationData[i]) > maximumDeltaAcceleration * 2) {
                return true;
            }
        }
        return false;
    }

    //utilities function to make display easier to read
    private void roundNumber() {
        for (int i = 0; i < 3; i ++) {
            cachedAccelerationData[i] = Math.round(cachedAccelerationData[i]*100)/100d;
            cachedGyroData[i] = Math.round(cachedGyroData[i]*100d)/100d;
        }
    }

    //JSON data is outed as time and position in 3 dimension
    public String getSensorDataJSON() {
        SavedValue savedValue = new SavedValue();
        savedValue.setTimeStamp(previousTime);
        double[] savedPos = PositionManager.getInstance().getSavedPosition();
        savedValue.setPos(savedPos[0], savedPos[1], savedPos[2]);
        return savedValue.myGetJsonString();
    }

    //same as above but pretty JSON
    public String getSensorDataJSONPretty() {
        SavedValue savedValue = new SavedValue();
        savedValue.setTimeStamp(previousTime);
        double[] savedPos = PositionManager.getInstance().getCurrentPosition();
        savedValue.setPos(savedPos[0], savedPos[1], savedPos[2]);
        return savedValue.myGetJsonStringPretty();
    }

//    public double[] getGyroData() { return cachedGyroData; }
//
//    public double[] getAccelerationData() { return cachedAccelerationData; }
}
