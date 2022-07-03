package com.example.heruhe.ui.dashboard.posts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heruhe.R;

import java.util.ArrayList;

public class PostMetaDataAdapter extends RecyclerView.Adapter<PostMetaDataViewHolder> {

    public interface OnPostClick{
        void onClick(PostMetaData postMetaData);
    }
    private OnPostClick onPostClick;
    private ArrayList<PostMetaData> metaData;
    private Context context;

    public PostMetaDataAdapter(OnPostClick onPostClick, ArrayList<PostMetaData> metaData,Context context) {
        this.onPostClick = onPostClick;
        this.metaData = metaData;
        this.context=context;
    }

    @NonNull
    @Override
    public PostMetaDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.
                from(parent.getContext()).inflate(R.layout.post_layout,parent,false);

        return new PostMetaDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostMetaDataViewHolder holder, int position) {
        holder.getImageView().setImageBitmap(metaData.get(position).getBitmap());
        holder.getImageView().setOnClickListener(view -> {
            onPostClick.onClick(metaData.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return metaData.size();
    }
}
