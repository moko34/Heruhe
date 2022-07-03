package com.example.heruhe.ui.home;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heruhe.R;

public class HomeViewHolder extends RecyclerView.ViewHolder {
    private final TextView txtUsername,txtFollow,txtDate,txtLikes,txtDescription;
    private final ImageView profile_image;
    private final ImageButton like;
    private final ImageButton comment;
    private final LinearLayout linearLayout;
    private final LinearLayout imageController;
    private final ConstraintLayout constraintLayout;


    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);
        txtDate=itemView.findViewById(R.id.txt_post_date);
        txtUsername=itemView.findViewById(R.id.txt_post_creator);
        txtFollow=itemView.findViewById(R.id.txt_follow_me);
        txtLikes=itemView.findViewById(R.id.txt_likes_count);
        txtDescription=itemView.findViewById(R.id.home_post_description);
        profile_image=itemView.findViewById(R.id.img_post_creator_image);
        like=itemView.findViewById(R.id.heart);
        comment=itemView.findViewById(R.id.comment);
        linearLayout=itemView.findViewById(R.id.home_post_image_container);
        imageController=itemView.findViewById(R.id.home_active_image);
        constraintLayout=itemView.findViewById(R.id.home_constraint);
        //txtImageIndex=itemView.findViewById(R.id.home_post_image_index);


    }

  //  public TextView getTxtImageIndex() {
   //     return txtImageIndex;
   // }

    public LinearLayout getImageController() {
        return imageController;
    }

    public ConstraintLayout getConstraintLayout() {
        return constraintLayout;
    }

    public TextView getTxtUsername() {
        return txtUsername;
    }

    public TextView getTxtFollow() {
        return txtFollow;
    }

    public TextView getTxtDate() {
        return txtDate;
    }

    public TextView getTxtLikes() {
        return txtLikes;
    }

    public TextView getTxtDescription() {
        return txtDescription;
    }

    public ImageView getProfileImage() {
        return profile_image;
    }

    public ImageButton getLike() {
        return like;
    }

    public ImageButton getComment() {
        return comment;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }
}
