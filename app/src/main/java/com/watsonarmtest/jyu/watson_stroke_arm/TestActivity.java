package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity implements SensorEventListener {
    //settings
    private long minimumRefreshRate = 200; //200 miliseconds
    //ramp-speed - play with this value until satisfied
    private float kFilteringFactor = 0.1f;

    //sensor stuffs
    SensorManager sensorManager;
    Sensor linearAccelerationSensor;
    private long startRecordingTime = -1;
    private long previousTimeStamp = -1;
    private long cachedDeltaTime = -1;

    private ArrayList<Float> averageAcceX;
    private ArrayList<Float> averageAcceY;
    private ArrayList<Float> averageAcceZ;

    private double[] previousVelocity = new double[]{0,0,0};
    private double[] previousPosition = new double[]{0,0,0};
    private float[] accel = new float[]{0,0,0};

    //email stuffs
    private String defaultEmail = "";
    private String savedEmailKey = "SAVED_USER_EMAIL";

    private Button buttonRecording;
    private String accelerationData = "";
    private TextView dataText;
    private boolean isRecordingData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //sensors
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        startRecordingTime = -1;
        previousTimeStamp = -1;
        averageAcceX = new ArrayList<>();
        averageAcceY = new ArrayList<>();
        averageAcceZ = new ArrayList<>();

        //buttons
        buttonRecording = (Button) findViewById(R.id.button_recording);
        buttonRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecordingButtonPressed();
            }
        });

        Button buttonSendEmail = (Button) findViewById(R.id.button_send_data_by_email);
        buttonSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataByEmail();
            }
        });

        dataText = (TextView)findViewById(R.id.acceleration_data_text);

        loadUserEmail();
        isRecordingData = false;
    }

    private void onRecordingButtonPressed() {
        if (isRecordingData) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        isRecordingData = true;
        buttonRecording.setText("Stop recording");
        //clear all of the acceleration data
        startRecordingTime = -1;
        accelerationData = "Time,x,y,z,Vx,Vy,Vz,Px,Py,Pz,a,b\n0,0,0,0,0,0,0,0,0,0,0,0";
        dataText.setText(accelerationData);
        resetCachedValueData();
    }

    private void stopRecording() {
        isRecordingData = false;
        buttonRecording.setText("Start recording");
    }

    // ----------------- sensor stuffs ------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override //import tant function
    public void onSensorChanged(SensorEvent event) {
        long timeStampMili = event.timestamp / 1000000;
        if (isRecordingData) {
            if (startRecordingTime == -1) {
                startRecordingTime = timeStampMili;
                previousTimeStamp = timeStampMili;
            }
            long deltaTime = timeStampMili - previousTimeStamp;
            if (cachedDeltaTime == -1) {
                cachedDeltaTime = deltaTime;
            } else {
                cachedDeltaTime += deltaTime;
            }
            previousTimeStamp = timeStampMili;

            addCacheValueData(event.values);
            if (cachedDeltaTime >= minimumRefreshRate) {
                float[] averageAccelerationData = getAverageAccelerationData();
                averageAccelerationData = denoiseData(averageAccelerationData);
                accelerationData += "\n" + getDataInCSVFormat(previousTimeStamp - startRecordingTime, averageAccelerationData);
                //calculate the velocity and position
                double[] deltaPos = calculateDeltaPosition(previousVelocity, averageAccelerationData, cachedDeltaTime);
                double[] deltaVel = calculateDeltaVelocity(averageAccelerationData, cachedDeltaTime);

                double[] currentVelocity = new double[]{0d,0d,0d};
                double[] currentPosition = new double[]{0d,0d,0d};

                for (int i = 0; i < 3; i ++) {
                    currentVelocity[i] = previousVelocity[i] + deltaVel[i];
                    currentPosition[i] = previousPosition[i] + deltaPos[i];
                }
                accelerationData +=
                        currentVelocity[0] + "," +
                        currentVelocity[1] + "," +
                        currentVelocity[2] + "," +
                        currentPosition[0] + "," +
                        currentPosition[1] + "," +
                        currentPosition[2];

                for (int i = 0; i < 3; i ++) {
                    previousVelocity[i] = currentVelocity[i];
                    previousPosition[i] = currentPosition[i];
                }
                //displaying
                dataText.setText(dataText.getText().toString() + "\n" + getDataCSVShort(previousTimeStamp - startRecordingTime, getAverageAccelerationData()));

                resetCachedValueData();
            }
        }
    }

    private double[] calculateDeltaPosition(double[] vel, float[] acce, long deltaTimeMili ) {
        double[] returnDoubles = new double[] {0,0,0};
        double deltaTime = deltaTimeMili / 1000d;
        for (int i = 0; i < 3; i ++) {
            returnDoubles[i] = vel[i] * deltaTime + acce[i] * deltaTime * deltaTime / 2;
        }
        return returnDoubles;
    }

    private double[] calculateDeltaVelocity(float[] acceleration, long deltaTimeMili) {
        double[] returnDoubles = new double[]{0,0,0};
        double deltaTime = deltaTimeMili / 1000d;
        for (int i = 0; i < 3; i ++) {
            returnDoubles[i] = acceleration[i] * deltaTime;
        }
        return returnDoubles;
    }

    private void addCacheValueData(float[] acceData) {
        averageAcceX.add(acceData[0]);
        averageAcceY.add(acceData[1]);
        averageAcceZ.add(acceData[2]);
    }

    private float[] getAverageAccelerationData() {
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

    private void resetCachedValueData() {
        averageAcceX.clear();
        averageAcceY.clear();
        averageAcceZ.clear();
        cachedDeltaTime = -1;
    }

    private String getDataInCSVFormat(long timeStampMili, float[] values) {
        return timeStampMili + "," + values[0] + "," + values[1] + "," + values[2] + ",";
    }

    private String getDataCSVShort(long timeStampMili, float[] values) {
        return timeStampMili + "," + roundNumber(values[0]) + "," + roundNumber(values[1]) + "," + roundNumber(values[2]);
    }

    private double roundNumber(double d) {
            return Math.round(d * 100) / 100d;
    }

    private float[] denoiseData(float[] acceleration) {
        float[] result = new float[3];
        accel[0] = acceleration[0] * kFilteringFactor + accel[0] * (1.0f - kFilteringFactor);
        accel[1] = acceleration[1] * kFilteringFactor + accel[1] * (1.0f - kFilteringFactor);
        accel[2] = acceleration[2] * kFilteringFactor + accel[2] * (1.0f - kFilteringFactor);
        result[0] = acceleration[0] - accel[0];
        result[1] = acceleration[1] - accel[1];
        result[2] = acceleration[2] - accel[2];
        return result;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // Do nothing
    }

    //------------- email stuffs -------------------------
    public void sendDataByEmail() {
        EditText emailText = (EditText) findViewById(R.id.edit_text_email);
        String email = emailText.getText().toString();
        if (email.equals("")) {
            email = defaultEmail;
        } else {
            //save the email so we don't have to type it again later.
            saveUserEmail(email);
        }
        sendEmailTo(email,"Acceleration data",accelerationData);
    }

    private void sendEmailTo(String email, String title, String content) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, title);
        i.putExtra(Intent.EXTRA_TEXT, content);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(TestActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserEmail(String email) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(savedEmailKey, email);
        editor.commit();
        Log.v(SetupMode.TAG, "User email is saved: " + email);
    }

    private void loadUserEmail() {
        String savedUserEmail;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        savedUserEmail = sharedPref.getString(savedEmailKey, "");
        if (!savedUserEmail.equals("")) {
            EditText emailText = (EditText) findViewById(R.id.edit_text_email);
            emailText.setText(savedUserEmail);
        }
    }

}
