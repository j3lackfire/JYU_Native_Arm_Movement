package com.watsonarmtest.jyu.watson_stroke_arm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Button backToMainPageButton = (Button) findViewById(R.id.back_to_main_page_button);
        backToMainPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFirstPage();
            }
        });

    }

    private void goToFirstPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
