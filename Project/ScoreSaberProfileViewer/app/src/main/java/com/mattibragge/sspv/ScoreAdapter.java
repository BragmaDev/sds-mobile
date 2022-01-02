package com.mattibragge.sspv;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

public class ScoreAdapter extends BaseAdapter {

    Context context;
    LayoutInflater m_inflater;
    ArrayList<Score> scores;
    ImageView cover_iv;

    public ScoreAdapter(Context c, ArrayList<Score> s) {
        context = c;
        scores = s;
        m_inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return scores.size();
    }

    @Override
    public Object getItem(int i) {
        return scores.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = m_inflater.inflate(R.layout.score_layout, null);
        cover_iv = (ImageView) v.findViewById(R.id.coverIv);
        TextView song_name_tv= (TextView) v.findViewById(R.id.songNameTv);
        TextView score_rank_tv = (TextView) v.findViewById(R.id.scoreRankTv);
        TextView score_stats_tv = (TextView) v.findViewById(R.id.scoreStatsTv);
        TextView difficulty_tv = (TextView) v.findViewById(R.id.difficultyTv);

        Score s = scores.get(i);

        // Fetching the cover image
        Glide.with(context).load(s.getCoverLink()).into(cover_iv);

        String name = s.getSongName();
        song_name_tv.setText(name);

        String rank_text = "#" + s.getRank();
        score_rank_tv.setText(rank_text);

        String stats_text = String.format(Locale.getDefault(), "%.2f%%, %.1fpp", s.getAccuracy(), s.getPp());
        score_stats_tv.setText(stats_text);

        String difficulty_text;
        switch (s.getDifficulty()) {
            case 1:
                difficulty_text = "Easy";
                break;
            case 3:
                difficulty_text = "Normal";
                break;
            case 5:
                difficulty_text = "Hard";
                break;
            case 7:
                difficulty_text = "Expert";
                break;
            case 9:
                difficulty_text = "Expert+";
                break;
            default:
                difficulty_text = "";
        }
        difficulty_tv.setText(difficulty_text);

        return v;
    }
}
