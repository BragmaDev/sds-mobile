package com.example.quicklauncher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button second_activity_btn = (Button) findViewById(R.id.secondActivityBtn);
        second_activity_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent start_intent = new Intent(getApplicationContext(), SecondActivity.class);
                start_intent.putExtra("com.example.quicklauncher.SOMETHING", "hello");
                startActivity(start_intent);
            }
        });

        Button google_btn = (Button) findViewById(R.id.googleBtn);
        google_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String google = "http://www.google.com";
                Uri web_address = Uri.parse(google);

                Intent go_to_google = new Intent(Intent.ACTION_VIEW, web_address);
                if (go_to_google.resolveActivity(getPackageManager()) != null) {
                    startActivity(go_to_google);
                }
            }
        });
    }
}