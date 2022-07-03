package com.example.heruhe.ui.search;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.heruhe.R;
import com.example.heruhe.databinding.FragmentItemBinding;
import com.example.heruhe.ui.search.placeholder.PlaceholderContent;

import com.example.heruhe.utils.HelperMethods;


import java.io.IOException;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link  com.example.heruhe.ui.search.placeholder.PlaceholderContent.UserSearchItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySearchItemRecyclerViewAdapter extends RecyclerView.Adapter<MySearchItemRecyclerViewAdapter.ViewHolder> {

    public interface UserSearchCardClicked{
        void onClick(String username);
    }

    private UserSearchCardClicked cardClicked;
    private final List<PlaceholderContent.UserSearchItem> mUsers;
    private final Context context;

    public MySearchItemRecyclerViewAdapter(List<PlaceholderContent.UserSearchItem> items, Context context,UserSearchCardClicked cardClicked) {
        this.cardClicked = cardClicked;
        mUsers = items;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mUsers.get(position);
        //FETCH PROFILE IMAGE
        if(mUsers.get(position).getProfileImage() != null){
        try {
            HelperMethods.decodeBitmap(mUsers.get(position).getProfileImage(), true, 160, new HelperMethods.LoadSingleBitmap() {
                @Override
                public void bitmapIsReady(Bitmap bitmap) {
                  holder.mProfileImage.setImageBitmap(bitmap);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        holder.mSeachCard.setOnClickListener(view -> cardClicked.onClick(mUsers.get(position).getUsername()));
        holder.mUsername.setText(mUsers.get(position).getUsername());
        String[] dateArray= mUsers.get(position).getLastUpdated();
        holder.mLastUpdated.setText(context.getString(R.string.last_updated_format,dateArray[0],dateArray[1]));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mUsername;
        public final TextView mLastUpdated;
        public final ImageView mProfileImage;
        public final LinearLayout mSeachCard;
        public PlaceholderContent.UserSearchItem mItem;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            mUsername = binding.searchUsername;
            mLastUpdated = binding.searchLastUpdated;
            mProfileImage=binding.searchUserProfileImage;
            mSeachCard = binding.searchUserItemCard;
        }

    }
}