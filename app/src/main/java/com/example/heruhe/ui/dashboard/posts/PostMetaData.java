package com.example.heruhe.ui.dashboard.posts;

import android.graphics.Bitmap;

import org.jetbrains.annotations.Nullable;

public class PostMetaData {

    private final Bitmap  bitmap;
    private final String id;
    private boolean isActive;

    public PostMetaData(Bitmap bitmap, String id , boolean isActive) {
        this.bitmap = bitmap;
        this.id = id;
        //USED IN SWIPING BETWEEN IMAGES
        this.isActive=isActive;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getId() {
        return id;
    }
}
