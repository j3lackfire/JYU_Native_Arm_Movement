package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import Logic.MySensorManager;
import Logic.SetupLogic;
import Logic.SetupStep;

public class SetupMode extends AppCompatActivity implements SensorEventListener {

    private Button buttonNextStep;

    private MySensorManager mySensorManager;
    private Vibrator vibrator;
    //Handler, stuff that will run for very long
    private Handler handler;
    private long startTime;
    private long currentTime;

    private boolean isDataSaved;

    private float refreshFrequency = 50; //0.05 second
    private long positionTimer = 0;

    private double[] savedAccelerationData = {-1,-1,-1};
    private double[] savedGyroData = {-1,-1,-1};

    private long stationaryTimer = 0;
    private long maxStationaryTime = 2000;

    private double maximumDeltaAcceleration = 0.5;
    private double maximumDeltaGyro = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        isDataSaved = false;

        //handler for time action and threading
        handler = new Handler();
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        //sensor and stuff
        mySensorManager = new MySensorManager((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //button and stuff
        Button backToMainPageButton = (Button) findViewById(R.id.button_back_to_main_page);
        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonNextStep = (Button) findViewById(R.id.setup_button_next_step);
        buttonNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                onNextStepButtonClicked();
            }
        });


        SetupLogic.getInstance().prepareLogic();

        String setupReportDisplayText =
                "Current set up step: " + SetupLogic.getInstance().getCurrentSetupStep().name() + "\n-----------------------\n" +
                SetupLogic.getInstance().getInstructionText() + "\n" +
                getAllSavedPositionData() + "\n\n\n\n\n\n\n\n\n\n\n\n\n";

        TextView setupReportText = (TextView) findViewById(R.id.setup_report_text);
        setupReportText.setText(setupReportDisplayText);
    }

    @Override
    public void onBackPressed() {
        goToMainPage();
    }

    private void goToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveUserPositionData() {
        String setupKey = SetupLogic.getInstance().getCurrentSetupKey();
        isDataSaved = true;
        if (setupKey.equals("Invalid")) {
            //throw some exception or something here
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(setupKey, mySensorManager.getSensorDataJSON());
            editor.commit();

            //display the data
            String setupReportDisplayText =
                "Current set up step: " + SetupLogic.getInstance().getCurrentSetupStep().name() + "\n" +
                SetupLogic.getInstance().getInstructionText() + "\n--------------------\n" +
                "Current time: " + currentTime + "\n" +
                mySensorManager.getSensorDataJSONPretty();
            TextView setupReportText = (TextView) findViewById(R.id.setup_report_text);
            setupReportText.setText(setupReportDisplayText);

        }
    }

    private void onNextStepButtonClicked() {
        SetupLogic.getInstance().toNextStep();
        //display the instruction to the user
        isDataSaved = false;

        String setupReportDisplayText =
                "_______Current set up step: " + SetupLogic.getInstance().getCurrentSetupStep().name() + "\n" +
                SetupLogic.getInstance().getInstructionText() + "\n--------------------\n" +
                "Current time: " + currentTime + "\n";

        TextView setupReportText = (TextView) findViewById(R.id.setup_report_text);
        setupReportText.setText(setupReportDisplayText);

        if (SetupLogic.getInstance().isSetupFinish()) {
            //save all the data.
        }
    }

    private String getAllSavedPositionData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return
            "Right hand down: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Right_Hand_Down), "-") +
            "\nRight hand front: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Right_Hand_Front), "-") +
            "\nRight hand up: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Right_Hand_Up), "-") +
            "\nLeft hand down: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Left_Hand_Down), "-") +
            "\nLeft hand front: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Left_Hand_Down), "-") +
            "\nLeft hand up: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Left_Hand_Down), "-");
    }

    //sensor stuffs
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
        if (SetupLogic.getInstance().isTrackingMotion()) {
            mySensorManager.onSensorChanged(event, currentTime);
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // Do nothing
    }

    //Runnable, like a custom thread
    public Runnable runnable = new Runnable() {
        public void run() {
            long deltaTime = currentTime;
            currentTime = SystemClock.uptimeMillis() - startTime;
            deltaTime = currentTime - deltaTime;
            if (SetupLogic.getInstance().isTrackingMotion()) {
                if (!isDataSaved) {
                    //display all the text
                    String setupReportDisplayText =
                        "Current set up step: " + SetupLogic.getInstance().getCurrentSetupStep().name() + "\n" +
                        SetupLogic.getInstance().getInstructionText() + "\n--------------------\n" +
                        "Current time: " + currentTime/1000 + "\n" +
                        mySensorManager.getSensorDataJSONPretty();

                    TextView setupReportText = (TextView) findViewById(R.id.setup_report_text);
                    setupReportText.setText(setupReportDisplayText);

                    //track the motion and stuffs
                    positionTimer += deltaTime;
                    stationaryTimer += deltaTime;
                    if (positionTimer >= refreshFrequency) {
                        //check if there are significant movement
                        if (isSignificantMovementDetected()) {
                            //reset the timer
                            stationaryTimer = 0;
                            //vibrate the phone
                            vibrator.vibrate(40);
                        }
                        //save the new data anyway
                        savedAccelerationData = mySensorManager.getAccelerationData();;
                        savedGyroData = mySensorManager.getGyroData();
                    }
                    if (stationaryTimer >= maxStationaryTime) {
                        saveUserPositionData();
                        stationaryTimer = 0;
                    }
                }
            }
            handler.postDelayed(this, 0);
        }
    };

    private boolean isSignificantMovementDetected() {
        for (int i = 0; i < 3; i ++) {
            if (mySensorManager.getAccelerationData()[i] > maximumDeltaAcceleration) {
                return true;
            }
        }
        double deltaGyro = 0;
        double[] cachedSensorData = mySensorManager.getGyroData();
        for (int i = 0; i < 3; i ++) {
            double delta = (savedGyroData[i] - cachedSensorData[i]) * (savedGyroData[i] - cachedSensorData[i]);
            deltaGyro += delta;
        }
        deltaGyro = Math.sqrt(deltaGyro);
        if (deltaGyro >= maximumDeltaGyro) {
            return true;
        }
        return false;
    }
}
