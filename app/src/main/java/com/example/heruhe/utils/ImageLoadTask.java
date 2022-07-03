package com.example.heruhe.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.heruhe.ui.dashboard.posts.UserPost;

import java.util.ArrayList;
import java.util.Comparator;

public class ImageLoadTask implements Runnable{
    public interface ImageIsLoaded{
       void onFinish(ArrayList<UserPost> images);
    }

    private ImageIsLoaded imageIsLoaded;
    private Context context;

    public ImageLoadTask(ImageLoadTask.ImageIsLoaded imageIsLoaded, Context context) {
        this.imageIsLoaded = imageIsLoaded;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        ArrayList<UserPost> images = fetchImages();
        Log.i("pyui","complete");
        images.sort(new ImageRecencyComparator());
        imageIsLoaded.onFinish(images);

    }


    private ArrayList<UserPost> fetchImages (){
        ArrayList<UserPost>  images = new ArrayList<>();

        Uri uri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = new String[]{ MediaStore.Images.Media._ID};
        String order = MediaStore.Images.Media.DATE_TAKEN;

        try(Cursor cursor = context.getContentResolver().query(uri,projection,null,null,order)){

            int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            while (cursor.moveToNext()){
                long id = cursor.getLong(idColumnIndex);
                Uri newUri = ContentUris.withAppendedId(uri, id);
                images.add(new UserPost(String.valueOf(id),newUri));

            }

        }


        return images;
    }
}

  class ImageRecencyComparator implements Comparator<UserPost> {

    @Override
    public int compare(UserPost image1, UserPost image2) {
        return (int)(Long.parseLong(image2.getId())-Long.parseLong(image1.getId()));
    }


  }
