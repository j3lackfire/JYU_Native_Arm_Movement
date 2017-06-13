package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


public class DoctorMode extends AppCompatActivity implements SensorEventListener {

    //report field
    private TextView currentDataView;
    private TextView savedDataView;
    public static String savedSensorDataKey = "SAVED_SENSOR_DATA";

    //Handler, stuff that will run for very long
    private Handler handler;
    private long startTime;
    private long currentTime;

    //sensor and stuff
    private SensorManager sensorManager;

    private long previousTimeStamp = -1;
    private float[] previousSensorData = {-1,-1,-1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_mode);

        handler = new Handler();
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
        //register the sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Button backToMainPageButton = (Button) findViewById(R.id.button_back_to_main_page);
        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainPage();
            }
        });

        Button buttonSaveSensorData = (Button) findViewById(R.id.button_save_sensor_data);
        buttonSaveSensorData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        currentDataView = (TextView) findViewById(R.id.current_sensor_data);
        savedDataView = (TextView) findViewById(R.id.saved_sensor_data);
        //get the previously saved data
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedSensorData = sharedPref.getString(savedSensorDataKey, "NOT EXISTED !!!!");
        savedSensorData = "The previously saved data is:\n----------------\n" + savedSensorData + "\n--------------";
        savedDataView.setText(savedSensorData);
    }


    private void goToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveUserData() {
        //the user input field
        //get it out as a string
        String savedSensorData = "Time:" + previousTimeStamp +
                "\nx : " + previousSensorData[0] +
                "\ny : " + previousSensorData[1] +
                "\nz : " + previousSensorData[2];

        //display it on the screen
        TextView reportText = (TextView) findViewById(R.id.saved_sensor_data);
        reportText.setText("The user input is:\n--------------------------\n" + savedSensorData + "\n------------------");
        //save it to the hard drive
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(savedSensorDataKey, savedSensorData);
        editor.commit();

        //Use this to read data from the shared preferences
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String reportText = sharedPreferences.getString(userSavedKey, "NOT EXISTED");

    }

    //sensor stuffs
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String outputString =
                "Time: " + currentTime + "\n-------------------------\n" +
                "Current sensor data - accelerometer: \nTime stamp: " + event.timestamp +
                "\nx : " + event.values[0] +
                "\ny : " + event.values[1] +
                "\nz : " + event.values[2];
        currentDataView.setText(outputString);

        previousTimeStamp = event.timestamp;
        previousSensorData[0] = event.values[0];
        previousSensorData[1] = event.values[1];
        previousSensorData[2] = event.values[2];
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // Do nothing
    }

    //Runnable, like a custom thread
    public Runnable runnable = new Runnable() {

        public void run() {

            currentTime = SystemClock.uptimeMillis() - startTime;
//            String outputString =
//                    "Current time: " + currentTime + "\n---------------\n" +
//                    "Time:" + previousTimeStamp +
//                    "\nx : " + previousSensorData[0] +
//                    "\ny : " + previousSensorData[1] +
//                    "\nz : " + previousSensorData[2];
//            currentDataView.setText(outputString);
            handler.postDelayed(this, 0);
        }

    };


}
