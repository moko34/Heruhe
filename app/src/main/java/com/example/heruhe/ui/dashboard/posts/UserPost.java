package com.example.heruhe.ui.dashboard.posts;

import android.net.Uri;

import java.io.Serializable;

public class UserPost  {
    private String id;
    private Uri imageUrl;
    private boolean isSelected;


    public UserPost(String id, Uri imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.isSelected = false;
    }

    public static UserPost createFromJson(String id,String uri){
        return new UserPost(id,Uri.parse(uri));
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }



    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }
}
