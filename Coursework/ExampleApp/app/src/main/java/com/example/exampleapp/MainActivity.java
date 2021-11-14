package com.example.exampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button add_btn = (Button) findViewById(R.id.addBtn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText first_num_edit_text = (EditText) findViewById(R.id.firstNumEditText);
                EditText second_num_edit_text = (EditText) findViewById(R.id.secondNumEditText);
                TextView result_text_view = (TextView) findViewById(R.id.resultTextView);

                int num_1 = Integer.parseInt(first_num_edit_text.getText().toString());
                int num_2 = Integer.parseInt(second_num_edit_text.getText().toString());
                int result = num_1 + num_2;
                result_text_view.setText(String.format("%d", result));
            }
        });
    }
}