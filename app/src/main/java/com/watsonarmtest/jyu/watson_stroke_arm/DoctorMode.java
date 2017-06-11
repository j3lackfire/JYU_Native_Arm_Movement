package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DoctorMode extends AppCompatActivity {

    //report field
    private TextView reportView;
    public static String userSavedKey = "USER_SAVED_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_mode);

        Button backToMainPageButton = (Button) findViewById(R.id.button_back_to_main_page);
        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainPage();
            }
        });

        Button buttonSaveUserInput = (Button) findViewById(R.id.button_save_user_input);
        buttonSaveUserInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        reportView = (TextView) findViewById(R.id.report_text);
        //get the previously saved data
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedInputData = sharedPref.getString(userSavedKey, "NOT EXISTED !!!!");
        savedInputData = "The user previously inputted value is:\n----------------\n" + savedInputData + "\n--------------";
        reportView.setText(savedInputData);
    }

    private void goToMainPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveUserData() {
        //the user input field
        EditText userInputField = (EditText) findViewById(R.id.user_input_text);
        //get it out as a string
        String displayText = userInputField.getText().toString();
        displayText = "The user input is:\n--------------------------\n" + displayText + "\n------------------";
        //display it on the screen so I know it works
        TextView reportText = (TextView) findViewById(R.id.report_text);
        reportText.setText(displayText);
        //save it to the hard drive
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(userSavedKey, userInputField.getText().toString());
        editor.commit();

        //Use this to read data from the shared preferences
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        String reportText = sharedPreferences.getString(userSavedKey, "NOT EXISTED");

    }

}
