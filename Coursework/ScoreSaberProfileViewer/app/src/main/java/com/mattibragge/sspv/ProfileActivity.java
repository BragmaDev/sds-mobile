package com.mattibragge.sspv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    String id, name, country, pfp_link;
    double pp, avg_acc;
    int rank, country_rank;
    ImageView picture_iv;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                default:
                    updateInfo();
                    updatePicture();
            }
        }
    };
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getIntent().hasExtra("com.mattibragge.sspv.PROFILE_ID")) {
            id = getIntent().getExtras().getString("com.mattibragge.sspv.PROFILE_ID");
            picture_iv = (ImageView) findViewById(R.id.pictureIv);

            FetchProfileInfo fetch_info = new FetchProfileInfo();
            fetch_info.start();
        }
    }

    // Updates the profile information
    private void updateInfo() {
        TextView profile_name_tv = (TextView) findViewById(R.id.profileNameTv);
        TextView country_tv = (TextView) findViewById(R.id.countryTv);
        TextView profile_rank_tv = (TextView) findViewById(R.id.profileRankTv);
        TextView pp_tv = (TextView) findViewById(R.id.ppTv);
        TextView accuracy_tv = (TextView) findViewById(R.id.accuracyTv);
        ListView plays_lv = (ListView) findViewById(R.id.playsLv);

        profile_name_tv.setText(name);
        country_tv.setText(codeToEmoji(country));
        String rank_text = "#" + rank + " (#" + country_rank + " in " + country + ")";
        profile_rank_tv.setText(rank_text);
        pp_tv.setText(String.format(Locale.getDefault(), "%.1fpp", pp));
        accuracy_tv.setText(String.format(Locale.getDefault(), "Avg. accuracy: %.1f%%", avg_acc));
    }

    // Takes a country code and returns a corresponding flag emoji
    private String codeToEmoji(String code) {
        int offset = 127397;
        if (code == null) return "";
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < code.length(); i++) {
            sb.appendCodePoint(code.charAt(i) + offset);
        }

        return sb.toString();
    }

    // Starts fetching the profile picture
    private void updatePicture() {
        FetchImage fetch_pfp = new FetchImage(pfp_link, picture_iv);
        fetch_pfp.start();
    }

    // Gets profile information from the API based on the player ID
    class FetchProfileInfo extends Thread {
        StringBuffer response = new StringBuffer();

        @Override
        public void run() {

            // Showing the progress bar
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(ProfileActivity.this);
                    dialog.setMessage("Fetching Information");
                    dialog.setCancelable(false);
                    dialog.show();
                }
            });

            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://scoresaber.com/api/player/" + id + "/full");
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

        // This method parses the JSON response and updates the variables
        private void parseResponse(StringBuffer response) {
            try {
                JSONObject json = new JSONObject(response.toString());
                name = json.getString("name");
                pfp_link = json.getString("profilePicture");
                country = json.getString("country");
                pp = json.getDouble("pp");
                avg_acc = json.getJSONObject("scoreStats").getDouble("averageRankedAccuracy");
                rank = json.getInt("rank");
                country_rank = json.getInt("countryRank");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Gets an image from a URL and puts it to a given ImageView
    class FetchImage extends Thread {
        String url;
        Bitmap bm;
        ImageView img;

        FetchImage(String url, ImageView img) {
            this.url = url;
            this.img = img;
        }

        @Override
        public void run() {

            // Showing the progress bar
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(ProfileActivity.this);
                    dialog.setMessage("Fetching Information");
                    dialog.setCancelable(false);
                    dialog.show();
                }
            });

            // Reading the image from the URL
            try {
                InputStream is = new URL(url).openStream();
                bm = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Hiding the progress bar and setting the image bitmap
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    img.setImageBitmap(bm);
                }
            });
        }
    }
}