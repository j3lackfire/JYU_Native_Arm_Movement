package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import Logic.DoctorLogic;
import Logic.DoctorStep;
import Logic.MySensorManager;

public class DoctorMode extends AppCompatActivity implements SensorEventListener {

    //Emergency number to call. Should be 112 but right now, it's Duc's number
    private String emergencyNumber = "+358469556804";

    //report field
    private TextView currentDataView;
    private TextView savedDataView;

    //Handler, stuff that will run for very long
    private Handler handler;
    private Vibrator vibrator;

    private long startTime;
    private long currentTime;

    //sensor and stuff
    private MySensorManager mySensorManager;

    Button nextStepButton;
    Button callEmergencyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_mode);

        //the logic
        DoctorLogic.getInstance().prepareDoctorLogic();

        //handler for time action and threading
        handler = new Handler();
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        //register the sensor
        mySensorManager = new MySensorManager((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //the buttons and UI stuffs
        Button backToMainPageButton = (Button) findViewById(R.id.button_back_to_main_page);
        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainPage();
            }
        });

        nextStepButton = (Button) findViewById(R.id.doctor_mode_button_next_step);
        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toNextStep();
            }
        });

        callEmergencyButton = (Button) findViewById(R.id.button_call_emergency_number);
        callEmergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEmergencyNumber();
            }
        });
        callEmergencyButton.setVisibility(Button.INVISIBLE);


        currentDataView = (TextView) findViewById(R.id.current_sensor_data);
        savedDataView = (TextView) findViewById(R.id.saved_sensor_data);

        savedDataView.setText("Current setup step: " + DoctorLogic.getInstance().getCurrentDoctorStep());

        //get the previously saved data
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void goToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void toNextStep() {
        //get the position data and display it as string
        DoctorLogic.getInstance().toNextStep();
        if (DoctorLogic.getInstance().getCurrentDoctorStep() == DoctorStep.Finish) {
            String display = "Current setup step: " + DoctorLogic.getInstance().getCurrentDoctorStep();
            display += "\n------------\nThe doctor mode is finish, you are healthy.";
            savedDataView.setText(display);
            nextStepButton.setVisibility(Button.INVISIBLE);
            return;
        }
        String displayString = "Current setup step: " + DoctorLogic.getInstance().getCurrentDoctorStep();
        displayString += "\n---------\nThe saved data for this position is: \n";

        //load the data
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        displayString += sharedPref.getString(DoctorLogic.getInstance().getSetupKey(DoctorLogic.getInstance().getCurrentDoctorStep()), "-");
        savedDataView.setText(displayString);
    }

    private void showEmergency() {
        callEmergencyButton.setVisibility(Button.VISIBLE);
    }

    private void hideEmergency() {
        callEmergencyButton.setVisibility(Button.INVISIBLE);
    }

    private void callEmergencyNumber() {
        //Check if the user has grant the call permission.
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + emergencyNumber));
            startActivity(callIntent);
        } else {
            //request the permission
            // REQUEST_CALL_PHONE is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            int REQUEST_CALL_PHONE = 0;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
        }
    }

    //------------sensor stuffs--------------
    @Override
    protected void onResume() {
        super.onResume();
        mySensorManager.registerSensors(this);
        handler.postDelayed(runnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySensorManager.unregisterListener(this);
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mySensorManager.onSensorChanged(event, currentTime);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // Do nothing
    }

    //--------Second thread to count the time--------------
    public Runnable runnable = new Runnable() {

        public void run() {

            long deltaTime = currentTime;
            currentTime = SystemClock.uptimeMillis() - startTime;
            deltaTime = currentTime - deltaTime;
            String outputString = "Current time: " + currentTime;
            if (DoctorLogic.getInstance().isTrackingMotion()) {
                mySensorManager.updateSensorManagerDoctorMode(deltaTime);
                outputString += "\n" + mySensorManager.getSensorDataJSONPretty();
                if (DoctorLogic.getInstance().shouldTrackPosition()) {
                    //check if the current position match the saved position.
                } else {
                    if (mySensorManager.getVibrateTime() > 0) {
                        vibrator.vibrate(mySensorManager.getVibrateTime());
                        showEmergency();
                    }
//                    if ()
                }
            }

            currentDataView.setText(outputString);
            handler.postDelayed(this, 0);
        }

    };
}
