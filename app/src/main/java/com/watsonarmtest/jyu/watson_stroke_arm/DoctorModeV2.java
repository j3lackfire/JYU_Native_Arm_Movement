package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import Logic.DoctorLogicV2;
import Logic.DoctorStep;
import Logic.DoctorStepV2;
import Logic.MySensorManager;
import Logic.MySensorManagerV2;

/*
 * It seems like the first approach with Setup mode and Doctor mode is NOT working very well because
 * the sensor data are very noisy and inaccurate.
 * It is impossible to track the data from the sensor and gives out accurate data of the position of the phone
 * The only thing that we can be sure is to know if the phone is moving or not.
 * So, here comes the second approach, the Doctor mode v2. This step is nice since it does not even require
 * the setup mode or anything, it just works.
 */
public class DoctorModeV2 extends AppCompatActivity implements SensorEventListener {
    //Emergency number to call. Should be 112 but right now, it's Duc's number
    private String emergencyNumber = "911";
    private final int REQUEST_CALL_PHONE_PERMISSION = 0; //used for the request phone call permission call back

    //sensor and stuff
    private MySensorManagerV2 mySensorManager;
    private MediaPlayer nextStepAudio = null;

    //Handler, a custom thread that will run side by side with this main thread
    private Handler handler;
    private Vibrator vibrator;

    private long startTime; //the time the application start, cache in for calculation
    private long currentTime; //current time since the main activity has started.

    //UI and button and stuffs
    private Button nextStepButton;
    private Button callEmergencyButton;
    private TextView textInformation;

    //cached variables, for calculating and stuffs
    private boolean isStepFailed = false;
    private boolean isCurrentStepCompleted = false;
    private boolean isAudioPlayed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_mode_v2);

        //Sensor and other logic stuffs
        handler = new Handler();
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);

        mySensorManager = new MySensorManagerV2((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        //prepare the doctor logic
        DoctorLogicV2.getInstance().prepareDoctorLogic();

        //the buttons and UI stuffs
        textInformation = (TextView) findViewById(R.id.v2_current_information);
        textInformation.setText(DoctorLogicV2.getInstance().getInstructionText());

        Button backToMainPageButton = (Button) findViewById(R.id.v2_button_main_page);
        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainPage();
            }
        });

        nextStepButton = (Button) findViewById(R.id.v2_button_next_step);
        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toNextStep();
            }
        });

        callEmergencyButton = (Button) findViewById(R.id.v2_button_call_emergency);
        callEmergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callEmergencyNumber();
            }
        });
        callEmergencyButton.setVisibility(Button.INVISIBLE);
    }

    //-----------------Buttons stuffs ----------------
    private void goToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //hide the call emergency button
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
            // REQUEST_CALL_PHONE_PERMISSION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE_PERMISSION);
        }
    }

    //the request permission call back.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CALL_PHONE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    callEmergencyNumber();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //------------Audio stuffs ----------------
    private void playAudio(DoctorStepV2 currentSetupStep) {
        if (nextStepAudio != null) {
            nextStepAudio.stop();
        }
        switch (currentSetupStep) {
            case Calibration:
                //should said something
                nextStepAudio = MediaPlayer.create(DoctorModeV2.this, R.raw.next_step);
                break;
            case Right_Hand_Down:
                nextStepAudio = MediaPlayer.create(DoctorModeV2.this,R.raw.right_hand_down_position);
                break;
            case Right_Hand_Front:
                nextStepAudio = MediaPlayer.create(DoctorModeV2.this,R.raw.right_hand_front_position);
                break;
            case Right_Hand_Up:
                nextStepAudio = MediaPlayer.create(DoctorModeV2.this,R.raw.right_hand_up_position);
                break;
            case Left_Hand_Down:
                nextStepAudio = MediaPlayer.create(DoctorModeV2.this,R.raw.left_hand_down_position);
                break;
            case Left_Hand_Front:
                nextStepAudio = MediaPlayer.create(DoctorModeV2.this,R.raw.left_hand_front_position);
                break;
            case Left_Hand_Up:
                nextStepAudio = MediaPlayer.create(DoctorModeV2.this,R.raw.left_hand_up_position);
                break;
            case Finish:
                nextStepAudio = MediaPlayer.create(DoctorModeV2.this,R.raw.finish);
                break;

            case Reading_Instruction:
            default:
                nextStepAudio = null;

        }
        nextStepAudio.start();
    }

    private void playFailAudio() {
        if (nextStepAudio != null ){
            nextStepAudio.stop();
        }
        nextStepAudio = MediaPlayer.create(DoctorModeV2.this, R.raw.failed_press_next);
        nextStepAudio.start();
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
        mySensorManager.onSensorChanged(event);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // Do nothing
    }

    //------------logic stuffs.-----------------------------
    private void toNextStep() {
        //get the position data and display it as string
        DoctorLogicV2.getInstance().toNextStep();
        mySensorManager.toNextStep();
        if (!isAudioPlayed) {
            playAudio(DoctorLogicV2.getInstance().getCurrentDoctorStep() );
        }

        isStepFailed = false;
        isCurrentStepCompleted = false;
        isAudioPlayed = false;
        hideEmergency();

        String display = "Current setup step: " + DoctorLogicV2.getInstance().getCurrentDoctorStep();

        if (DoctorLogicV2.getInstance().getCurrentDoctorStep() != DoctorStepV2.Calibration) {
            nextStepButton.setVisibility(View.INVISIBLE);
        }

        if (DoctorLogicV2.getInstance().getCurrentDoctorStep() == DoctorStepV2.Finish) {
            display += "\n------------\nThe doctor mode is finish, you are healthy.";
            nextStepButton.setVisibility(Button.INVISIBLE);
        } else {
            display += "\n---------\n" + DoctorLogicV2.getInstance().getInstructionText();
        }
        textInformation.setText(display);
    }

    private void onStepFailed() {
        callEmergencyButton.setVisibility(Button.VISIBLE);
        nextStepButton.setVisibility(View.VISIBLE);

        playFailAudio();
        isStepFailed = true;
        isCurrentStepCompleted = true;
    }

    private void onStepCompleted() {
        playAudio(DoctorLogicV2.getInstance().getNextDoctorStep());
        isCurrentStepCompleted = true;
    }

    //--------Second thread to count the time--------------
    public Runnable runnable = new Runnable() {

        public void run() {
            long deltaTime = currentTime;
            currentTime = SystemClock.uptimeMillis() - startTime;
            deltaTime = currentTime - deltaTime;
            String outputString = "Current step: " + DoctorLogicV2.getInstance().getCurrentDoctorStep() +
                    "\n----------------\nCurrent time: " + currentTime +
                    "\nStationary timer: " + mySensorManager.getStationaryTimer();
            if (DoctorLogicV2.getInstance().isTrackingMotion()) {
                mySensorManager.updateSensorManager(deltaTime);
                if (!isCurrentStepCompleted) {
                    if (mySensorManager.getVibrateTime() > 0) {
                        vibrator.vibrate(mySensorManager.getVibrateTime());
                    }
                    if (mySensorManager.isStepCompleted())  {
                        onStepCompleted();
                    }
                    if (mySensorManager.isStepFail()) {
                        onStepFailed();
                    }
                } else {
                    if (isStepFailed) {
                        outputString += "\n-------------\nCurrent step failed" +
                                "\nProcess to the next position and press NEXT STEP" +
                                "\nPress call emergency if you feel dangerous";
                    } else {
                        outputString += "\n-----------\n" +
                                "Current step completed, follow the instruction to the next step!";
                        if (mySensorManager.isSignificantMovementDetected()) {
                            vibrator.vibrate(mySensorManager.getVibrateTime());
                            toNextStep();
                        }
                    }
                }
            }
            textInformation.setText(outputString);
            handler.postDelayed(this, 0);
        }

    };
}
