package com.mattibragge.sspv;

public class Score {

    private String song_name, cover_link;
    private int rank;
    private double accuracy, pp;

    public Score(String n, String c, int r, double a, double p) {
        song_name = n;
        cover_link = c;
        rank = r;
        accuracy = a;
        pp = p;
    }

    public String getSongName() {
        return song_name;
    }

    public String getCoverLink() {
        return cover_link;
    }

    public int getRank() {
        return rank;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getPp() {
        return pp;
    }
}
