package com.example.heruhe.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchImage implements Runnable{

    public interface BitmapReady{
        void onReady (ArrayList<Bitmap> bitmaps);
    }

    private BitmapReady bitmapReady;
    private ArrayList<String> uris;

    public FetchImage(BitmapReady bitmapReady, ArrayList<String> uris) {
        this.bitmapReady = bitmapReady;
        this.uris = uris;
    }

    @Override
    public void run() {
        ArrayList<Bitmap> bitmaps = getBitmapFromUrl(uris);
        bitmapReady.onReady(bitmaps);
    }


    private ArrayList<Bitmap> getBitmapFromUrl(ArrayList<String> uris){
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        try{
            for(String uri : uris){
                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                bitmaps.add(bitmap);
                connection.disconnect();

            }
        }catch(Exception exception){
            //Log.i("URL-ERR",exception.getMessage());
        }

        return bitmaps;
    }
}
