package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import Logic.SetupLogic;
import Logic.SetupStep;

public class SetupMode extends AppCompatActivity {

    private Button buttonNextStep;

    private Button buttonSaveUserPositionData;
    private EditText userPositionData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

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

        userPositionData = (EditText) findViewById(R.id.user_position_data);

        buttonSaveUserPositionData = (Button) findViewById(R.id.button_save_user_position_data);
        buttonSaveUserPositionData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserPositionData();
            }
        });

        SetupLogic.getInstance().prepareLogic();

        String setupReportDisplayText =
                "Current set up step: " + SetupLogic.getInstance().getCurrentSetupStep().name() + "\n-----------------------\n" +
                SetupLogic.getInstance().getInstructionText() + "\n" +
                getAllPositionData();
        TextView setupReportText = (TextView) findViewById(R.id.setup_report_text);
        setupReportText.setText(setupReportDisplayText);
        hideInputField();
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
        if (setupKey.equals("Invalid")) {
            //throw some exception or something here
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(setupKey, userPositionData.getText().toString());
            editor.commit();
        }
    }

    private void onNextStepButtonClicked() {
        SetupLogic.getInstance().toNextStep();
        //display the instruction to the user
        if (SetupLogic.getInstance().getCurrentSetupStep() == SetupStep.Right_Hand_Down) {
            showInputField();
        }
        String setupReportDisplayText =
                "Current set up step: " + SetupLogic.getInstance().getCurrentSetupStep().name() + "\n-----------------------\n" +
                SetupLogic.getInstance().getInstructionText();
        TextView setupReportText = (TextView) findViewById(R.id.setup_report_text);
        setupReportText.setText(setupReportDisplayText);

        if (SetupLogic.getInstance().isSetupFinish()) {
            hideInputField();
            //save all the data.
        }
    }

    private void showInputField() {
        userPositionData.setVisibility(View.VISIBLE);
        buttonSaveUserPositionData.setVisibility(View.VISIBLE);
    }

    private void hideInputField() {
        userPositionData.setVisibility(View.INVISIBLE);
        buttonSaveUserPositionData.setVisibility(View.INVISIBLE);
    }

    private String getAllPositionData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        return
            "Right hand down: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Right_Hand_Down), "-") +
            "\nRight hand front: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Right_Hand_Front), "-") +
            "\nRight hand up: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Right_Hand_Up), "-") +
            "\nLeft hand down: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Left_Hand_Down), "-") +
            "\nLeft hand front: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Left_Hand_Down), "-") +
            "\nLeft hand up: " + sharedPref.getString(SetupLogic.getInstance().getSetupKey(SetupStep.Left_Hand_Down), "-");
    }
}
