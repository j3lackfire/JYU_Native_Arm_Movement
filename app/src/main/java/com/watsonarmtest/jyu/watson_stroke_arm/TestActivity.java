package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Intent;
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
        accelerationData = "Hello, this is a test email !";
    }

    public void onRecordingButtonPressed() {

    }


    public void sendDataByEmail() {
        EditText emailText = (EditText) findViewById(R.id.edit_text_email);
        String email = emailText.getText().toString();
        if (email.equals("")) {
            email = defaultEmail;
        }
        sendEmailTo(email,"Acceleration data",accelerationData);
    }

    public void sendEmailTo(String email, String title, String content) {
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

}
