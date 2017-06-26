package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TestActivity extends AppCompatActivity {

    private String defaultEmail = "j3lackfire@gmail.com";
    private Button buttonRecording;
    private String accelerationData = "";

    private String savedEmailKey = "SAVED_USER_EMAIL";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

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

        String savedUserEmail = "";
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        savedUserEmail = sharedPref.getString(savedUserEmail, "");
        if (!savedUserEmail.equals("")) {
            EditText emailText = (EditText) findViewById(R.id.edit_text_email);
            emailText.setText(savedUserEmail);
        }
        accelerationData = "Hello, this is a test email !";
    }

    public void onRecordingButtonPressed() {

    }


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
    }

}
