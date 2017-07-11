package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Go to set up mode
        Button setupModeButton = (Button) findViewById(R.id.button_setup_mode);
        setupModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterSetupMode();
            }
        });

        //go to doctor mode
        Button doctorModeButton = (Button) findViewById(R.id.button_doctor_mode);
        doctorModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterDoctorMode();
            }
        });

        //goto doctor mode v2
        Button doctorModeV2Button = (Button) findViewById(R.id.button_doctor_mode_v2);
        doctorModeV2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterDoctorModeV2();
            }
        });

        //go to test mode
        Button testModeButton = (Button) findViewById(R.id.button_enter_test_mode);
        testModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterTestMode();
            }
        });
    }

    private void enterSetupMode() {
        Intent intent = new Intent(this, SetupMode.class);
        startActivity(intent);
    }


    private void enterDoctorMode() {
        Intent intent = new Intent(this, DoctorMode.class);
        startActivity(intent);
    }

    private void enterDoctorModeV2() {
        Intent intent = new Intent(this, DoctorModeV2.class);
        startActivity(intent);
    }

    private void enterTestMode() {
        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }
}
