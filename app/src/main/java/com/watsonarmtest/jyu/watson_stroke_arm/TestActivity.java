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
import android.widget.Toast;

import Logic.SetupLogic;

public class TestActivity extends AppCompatActivity implements SensorEventListener {
    //sensor stuffs
    SensorManager sensorManager;
    Sensor linearAccelerationSensor;
    private long initialTimeStamp = -1;
    private long previousTimeStamp = -1;

    //email stuffs
    private String defaultEmail = "j3lackfire@gmail.com";
    private String savedEmailKey = "SAVED_USER_EMAIL";

    private Button buttonRecording;
    private String accelerationData = "";
    private boolean isRecordingData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //sensors
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        initialTimeStamp = -1;
        previousTimeStamp = -1;

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
        accelerationData = "Time,x,y,z";
    }

    private void stopRecording() {
        isRecordingData = false;
        buttonRecording.setText("Start recording");
    }


    // ----------------- sensor stuffs ------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override //import tant function
    public void onSensorChanged(SensorEvent event) {
        if (initialTimeStamp == -1) {
            initialTimeStamp = event.timestamp;
            previousTimeStamp = event.timestamp;
        }
        long deltaTime = event.timestamp - previousTimeStamp;
        previousTimeStamp = event.timestamp;

        if (isRecordingData) {
            accelerationData += "\n" + getDataInCSVFormat(previousTimeStamp, event.values);
        }
    }

    private String getDataInCSVFormat(long timeStampNano, float[] values) {
        long oneMillion = 1000000;
        long timeStampMili = timeStampNano / oneMillion;
        return timeStampMili + "," + values[0] + "," + values[1] + "," + values[2];
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
