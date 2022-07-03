package com.example.heruhe.ui.dashboard.posts;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heruhe.R;

import java.util.ArrayList;

public class PostsAdapter extends RecyclerView.Adapter<PostsViewHolder> {
    public interface UserPostSelected{
       void onSelected (UserPost post, ImageView imageView);
    }

    private final ArrayList<UserPost> userPosts;
    private final UserPostSelected userPostSelected;
    private Context context;

    public PostsAdapter(ArrayList<UserPost> userPosts,
                        UserPostSelected userPostSelected, Context context) {
        this.userPosts = userPosts;
        this.userPostSelected = userPostSelected;
        this.context = context;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostsViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.post_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
        UserPost post = userPosts.get(holder.getAdapterPosition());
        ImageView img = holder.getImageView();
        img.setId(Integer.parseInt(post.getId()));
        img.setImageURI(post.getImageUrl());
        holder.getImageView().setOnClickListener(view -> {
            Log.i("tyui",post.isSelected() + post.getId() + "");
            userPostSelected.onSelected(post, img);



        }
);

    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }
}
