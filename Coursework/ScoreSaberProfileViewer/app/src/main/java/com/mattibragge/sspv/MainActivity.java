package com.mattibragge.sspv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> profiles = new ArrayList<>();
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            updateSpinner();
        }
    };
    ProgressDialog dialog;
    Spinner profile_spn;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profile_spn = findViewById(R.id.profileSpn);
        profile_spn.setAdapter(adapter);
        profile_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        FetchProfileNames fetch = new FetchProfileNames();
        fetch.start();

        Button view_profile_btn = findViewById(R.id.viewProfileBtn);
        view_profile_btn.setOnClickListener(view -> profile_spn.setAdapter(adapter));
    }

    public void updateSpinner() {
        adapter = new ArrayAdapter(MainActivity.this, R.layout.spinner_layout, profiles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profile_spn.setAdapter(adapter);
    }

    // Gets a list of the top 50 players in Finland from the API
    class FetchProfileNames extends Thread {
        StringBuffer response = new StringBuffer();

        @Override
        public void run() {

            // Showing the progress bar
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(MainActivity.this);
                    dialog.setMessage("Fetching Users");
                    dialog.setCancelable(false);
                    dialog.show();
                }
            });

            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://scoresaber.com/api/players?page=1&countries=fi");
                conn = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line + "\n");
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }

            // Hiding the progress bar
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

            profiles = parseResponse(response);
            handler.sendEmptyMessage(0);
        }

        // This function parses the JSON response and returns a list of player names
        private ArrayList<String> parseResponse(StringBuffer response) {
            ArrayList<String> names = new ArrayList<>();
            try {
                JSONArray json = new JSONArray(response.toString());
                for (int i = 0; i < json.length(); i++) {
                    JSONObject obj = json.getJSONObject(i);
                    String name = i+1 + ": " + obj.getString("name");
                    names.add(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return names;
        }
    }

}

