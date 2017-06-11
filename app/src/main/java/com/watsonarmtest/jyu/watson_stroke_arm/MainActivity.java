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
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
    }

    private void enterSetupMode() {
        Intent intent = new Intent(this, SetupMode.class);
        startActivity(intent);
    }


    private void enterDoctorMode() {
        Intent intent = new Intent(this, DoctorMode.class);
        startActivity(intent);
    }
}
