package com.mattibragge.sspv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class FetchImage extends Thread {
    String url;
    Bitmap bm;
    Handler handler;

    FetchImage(String url, Handler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public void run() {
        // Reading the image from the URL
        try {
            InputStream is = new URL(url).openStream();
            bm = BitmapFactory.decodeStream(is);
            handler.sendEmptyMessage(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmap() {
        return bm;
    }
}