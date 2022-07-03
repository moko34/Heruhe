package com.example.heruhe.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heruhe.MainActivity;
import com.example.heruhe.R;
import com.example.heruhe.ui.dashboard.posts.Post;
import com.example.heruhe.ui.dashboard.posts.PostMetaData;
import com.example.heruhe.utils.GestureListener;
import com.example.heruhe.utils.HelperMethods;

import java.io.IOException;
import java.util.ArrayList;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeViewHolder> {
    private final ArrayList<Post> posts;
    private final Context context;
    private final Activity activity;
    private final int IMAGE_BUTTON_TRANSLATE_CONSTANT=10;
    private final int IMAGE_BUTTON_HEIGHT=20;

    public HomeFragmentAdapter (ArrayList<Post> posts,Context context,Activity activity){
        this.posts = posts;
        this.context=context;
        this.activity=activity;
    }


    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_post_layout,parent,false);
        return new HomeViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Post currentPost = posts.get(holder.getAdapterPosition());
        Log.i("WERT",HelperMethods.getImageControllerTranslation(5) + "");

       try {
            int[] widthAndHeight = ((MainActivity)(activity)).getDeviceWidthAndHeight();
            ConstraintLayout.LayoutParams constraintLayoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (widthAndHeight[1] * 0.5));
            constraintLayoutParams.endToEnd= androidx.constraintlayout.widget.R.id.parent;
            constraintLayoutParams.startToStart= androidx.constraintlayout.widget.R.id.parent;
            constraintLayoutParams.topToBottom=R.id.home_first_actions_layout;
            holder.getLinearLayout().setLayoutParams(constraintLayoutParams);

            if(currentPost.getCreatorProfileImage() != null)
             HelperMethods.
                   decodeBitmap(currentPost.getCreatorProfileImage(), true, 140, new HelperMethods.LoadSingleBitmap() {
                       @Override
                       public void bitmapIsReady(Bitmap bitmap) {
                           holder.getProfileImage().setImageBitmap(bitmap);
                       }
       });
            holder.getTxtUsername().setText(currentPost.getCreator());
            holder.getTxtDate().setText(decodeStringFormat(currentPost.getDate()[0],currentPost.getDate()[1]));
            holder.getTxtDescription().setText(context.getString(R.string.post_description,currentPost.getDescription(),currentPost.getCreator()));
            if(currentPost.getLikesCount() > 0){
               holder.getTxtLikes().setText(context.getString(R.string.likes_format,currentPost.getLikesCount()));
            }else {
               holder.getTxtLikes().setVisibility(View.GONE);
            }
            int[] data = HelperMethods.generateViewsTranslations(posts.size());
           // Log.i("ASDFG",data.length + "");
            HelperMethods.generateBitmaps(currentPost.getImageUris(), postMetaDataArrayList -> {
                    ImageView imageView = new ImageView(context);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    imageView.setImageBitmap(postMetaDataArrayList.get(0).getBitmap());
                    imageView.setLayoutParams(layoutParams);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    holder.getLinearLayout().addView(imageView,0);
                    //SET FIRST ITEM AS ACTIVE
                    postMetaDataArrayList.get(0).setActive(true);
                    HelperMethods.generateImageButtons(holder.getImageController(), postMetaDataArrayList, context, index -> {
                        //CLEAR ACTIVE STATUS OF PREVIOUS ACTIVE ITEM
                        postMetaDataArrayList.get(HelperMethods.getActiveViewIndex(postMetaDataArrayList)).setActive(false);
                        //UPDATE IMAGEVIEW
                        HelperMethods.updateImageContainer(holder.getLinearLayout(),context,postMetaDataArrayList,index,null,true);
                        //UPDATE BUTTONS
                        HelperMethods.updateImageButtonContainer(holder.getImageController(),index);
                    });
                    GestureDetector gestureDetector= new GestureDetector(context,new GestureListener(holder.getLinearLayout(), holder.getImageController(), context,postMetaDataArrayList),new Handler(Looper.getMainLooper()));
                    holder.getLinearLayout().setOnTouchListener((view, motionEvent) -> {
                        view.performClick();
                        gestureDetector.onTouchEvent(motionEvent);
                        return true;
                    });
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    private String decodeStringFormat (String arg1,String arg2){
        return context.getString(R.string.format_date,arg1,arg2);
    }






}
