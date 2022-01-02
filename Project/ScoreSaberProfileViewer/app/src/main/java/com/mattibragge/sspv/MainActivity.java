package com.mattibragge.sspv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> profile_names = new ArrayList<>();
    HashMap<String, String> profile_ids = new HashMap<>();
    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == -1) {
                AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("Error");
                alert.setMessage("Players could not be fetched");
                alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int which) {
                                d.dismiss();
                            }
                        });
                alert.show();
            } else {
                updateList();
            }
        }
    };
    ProgressDialog dialog;
    ListView profiles_lv;
    EditText id_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        id_et = findViewById(R.id.idEt);
        profiles_lv = findViewById(R.id.profilesLv);


        // Fetching profiles and setting up the ListView
        FetchProfileNames fetch = new FetchProfileNames();
        fetch.start();
        updateList();
        profiles_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                id_et.setText( profile_ids.get(profile_names.get(i)) );
            }
        });
        Button view_profile_btn = findViewById(R.id.viewProfileBtn);
        view_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!id_et.getText().toString().equals("")) {
                    Intent show_profile_activity = new Intent(getApplicationContext(), ProfileActivity.class);
                    show_profile_activity.putExtra("com.mattibragge.sspv.PROFILE_ID", id_et.getText().toString());
                    startActivity(show_profile_activity);
                }
            }
        });
    }

    private void updateList() {
        ProfileAdapter adapter = new ProfileAdapter(this, profile_names);
        profiles_lv.setAdapter(adapter);
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
                    dialog.setMessage("Fetching Players");
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

            parseResponse(response);
            handler.sendEmptyMessage(0);
        }

        // This method parses the JSON response and puts the data in profile_names and profile_ids
        private void parseResponse(StringBuffer response) {
            if (response.length() == 0) {
                handler.sendEmptyMessage(-1);
                return;
            }

            try {
                JSONObject obj = new JSONObject(response.toString());
                System.out.println(response.toString());
                JSONArray arr = obj.getJSONArray("players");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject player = arr.getJSONObject(i);
                    String name = player.getString("name");
                    String id = player.getString("id");
                    profile_ids.put(name, id);
                    profile_names.add(name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

