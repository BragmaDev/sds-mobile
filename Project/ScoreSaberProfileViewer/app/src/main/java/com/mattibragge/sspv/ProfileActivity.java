package com.mattibragge.sspv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    String id, name, country, pfp_link;
    double pp, avg_acc;
    int rank, country_rank;
    ImageView picture_iv;
    ArrayList<Score> scores = new ArrayList<Score>();
    ListView scores_lv;
    FetchImage fetch_pfp;
    FetchScores fetch_scores = new FetchScores();

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case -1:
                    AlertDialog alert = new AlertDialog.Builder(ProfileActivity.this).create();
                    alert.setTitle("Error");
                    alert.setMessage("Information could not be fetched");
                    alert.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int which) {
                                    d.dismiss();
                                    finish();
                                }
                            });
                    alert.show();
                    break;
                case 1:
                    picture_iv.setImageBitmap(fetch_pfp.getBitmap());
                    fetchScores();
                    break;
                case 2:
                    updateList();
                    break;
                default:
                    updateInfo();
                    fetchPicture();
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
            scores_lv = (ListView) findViewById(R.id.scoresLv);

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

        profile_name_tv.setText(name);
        country_tv.setText(codeToEmoji(country));
        String rank_text = "#" + rank + " (#" + country_rank + " in " + country + ")";
        profile_rank_tv.setText(rank_text);
        pp_tv.setText(String.format(Locale.getDefault(), "%.1fpp", pp));
        accuracy_tv.setText(String.format(Locale.getDefault(), "Avg. accuracy: %.1f%%", avg_acc));
    }

    // Updates the score list
    private void updateList() {
        ScoreAdapter adapter = new ScoreAdapter(this, scores);
        scores_lv.setAdapter(adapter);
    }

    // Takes a country code and returns a corresponding flag emoji
    // Source used: https://attacomsian.com/blog/how-to-convert-country-code-to-emoji-in-java
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
    private void fetchPicture() {
        fetch_pfp = new FetchImage(pfp_link, handler);
        fetch_pfp.start();
    }

    // Starts fetching the scores
    private void fetchScores() {
        fetch_scores = new FetchScores();
        fetch_scores.start();
    }

    // Gets profile information from the API based on the player ID
    // Source used: https://www.youtube.com/watch?v=5lNQLR53UtY
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
            if (response.length() == 0) {
                handler.sendEmptyMessage(-1);
                return;
            }

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

    // Gets profile information from the API based on the player ID
    class FetchScores extends Thread {
        StringBuffer response = new StringBuffer();

        @Override
        public void run() {

            // Showing the progress bar
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog = new ProgressDialog(ProfileActivity.this);
                    dialog.setMessage("Fetching Scores");
                    dialog.setCancelable(false);
                    dialog.show();
                }
            });

            HttpURLConnection conn = null;
            try {
                URL url = new URL("https://scoresaber.com/api/player/" + id + "/scores?sort=top&page=1");
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
            handler.sendEmptyMessage(2);
        }

        // This method parses the JSON response and fills the scores arraylist with score objects
        private void parseResponse(StringBuffer response) {
            if (response.length() == 0) {
                handler.sendEmptyMessage(-1);
                return;
            }

            try {
                JSONObject obj = new JSONObject(response.toString());
                JSONArray arr = obj.getJSONArray("playerScores");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject score = arr.getJSONObject(i).getJSONObject("score");
                    JSONObject leaderboard = arr.getJSONObject(i).getJSONObject("leaderboard");
                    String song_name = leaderboard.getString("songName");
                    String cover_link = leaderboard.getString("coverImage");
                    int score_rank = score.getInt("rank");
                    int difficulty = leaderboard.getJSONObject("difficulty").getInt("difficulty");
                    double score_acc = (double)score.getInt("baseScore") / (double)leaderboard.getInt("maxScore") * 100;
                    double score_pp = score.getDouble("pp");
                    Score s = new Score(song_name, cover_link, score_rank, difficulty, score_acc, score_pp);
                    scores.add(s);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}