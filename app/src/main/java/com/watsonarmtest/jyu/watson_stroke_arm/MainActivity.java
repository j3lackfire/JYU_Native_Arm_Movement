package com.watsonarmtest.jyu.watson_stroke_arm;

import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String reportText = sharedPreferences.getString(SetupMode.userSavedKey, "NOT EXISTED");
        TextView reportField = (TextView) findViewById(R.id.main_page_report_field);
        reportField.setText(reportText);
    }

    private void enterSetupMode() {
        Intent intent = new Intent(this, SetupMode.class);
        startActivity(intent);
    }


    private void enterDoctorMode() {
        Intent intent = new Intent(this, DoctorMode.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
