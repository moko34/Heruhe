package com.example.heruhe.ui.dashboard.posts;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heruhe.R;

public class PostMetaDataViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;

    public PostMetaDataViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView=itemView.findViewById(R.id.user_post_image);
    }

    public ImageView getImageView() {
        return imageView;
    }
}
