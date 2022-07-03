package com.example.heruhe.ui.dashboard.posts;

import android.util.Log;


import com.example.heruhe.utils.HelperMethods;

import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import java.util.Objects;

public class Post {
    private final String creator;
    private final String creatorProfileImage;
    private String description;
    private long commentsCount;
    private long likesCount;
    private final String postId;
    private final ArrayList<UserPost> imageUris;
    private final String[] date;


    public Post(String creatorProfileImage, ParseObject post) {

        this.creator = Objects.requireNonNull(post.getString(FinishPostFragment.CREATOR));


        this.creatorProfileImage = creatorProfileImage;
        this.description=post.getString(FinishPostFragment.DESCRIPTION);
        this.likesCount = Objects.requireNonNull(post.getList(FinishPostFragment.LIKES)).size();
        this.commentsCount = Objects.requireNonNull(post.getList(FinishPostFragment.COMMENTS)).size();
        this.postId = post.getObjectId();
        this.date= HelperMethods.getDateString(post.getCreatedAt());
        this.imageUris=getUriFromJson(post.getJSONArray(FinishPostFragment.IMAGES));


    }

    /*
     Creates a new Post
     */



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }

    public long getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(long commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getCreator() {
        return creator;
    }


    public String getPostId() {
        return postId;
    }

    public ArrayList<UserPost> getImageUris() {
        return imageUris;
    }

    public String getCreatorProfileImage() {
        return creatorProfileImage;
    }

    public String[] getDate() {
        return date;
    }

    private ArrayList<UserPost> getUriFromJson(JSONArray jsonArray){
        ArrayList<UserPost> uris = new ArrayList<>();
        if(jsonArray != null){
            for(int i = 0; i< jsonArray.length();i++){
               try{
                   JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                   uris.add(UserPost.createFromJson(jsonObject.getString("name"),jsonObject.getString("url")));
               }catch (JSONException exception){
                   Log.i("JSON_POST_ERR",exception.getMessage());
               }

            }
        }
       return  uris;
    }



}



