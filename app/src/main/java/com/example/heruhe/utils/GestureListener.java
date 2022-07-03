package com.example.heruhe.utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.example.heruhe.R;
import com.example.heruhe.ui.dashboard.posts.PostMetaData;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private final LinearLayout imageContainer,imageControllerButtons;
    private final Context  context;
    private final ArrayList<PostMetaData> imagesData;

    public GestureListener(LinearLayout imageContainer,LinearLayout imageControllerButtons, Context context, ArrayList<PostMetaData> imagesData) {
        this.imageContainer = imageContainer;
        this.context = context;
        this.imagesData = imagesData;
        this.imageControllerButtons=imageControllerButtons;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        int MIN_SWIPING_DISTANCE = 50;
        int THRESHOLD_VELOCITY = 150;

        Animation translateLeftAnimation= AnimationUtils.loadAnimation(context, R.anim.swipe_translate_left);
        Animation translateRightAnimation=AnimationUtils.loadAnimation(context,R.anim.swipe_translate_right);


        //GET CURRENT ACTIVE IMAGE INDEX
        int activeImageIndex = HelperMethods.getActiveViewIndex(imagesData);


        if (e1.getX() - e2.getX() > MIN_SWIPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
        {
            /* Code that you want to do on swiping left side*/
            int nextIndex= activeImageIndex + 1;
           if(nextIndex < imagesData.size()){
                imagesData.get(activeImageIndex).setActive(false);
                HelperMethods.updateImageContainer(imageContainer,context,imagesData,nextIndex,translateLeftAnimation,false);
               HelperMethods.updateImageButtonContainer(imageControllerButtons,nextIndex);
            }else if(nextIndex == imagesData.size()){
               imagesData.get(nextIndex-1).setActive(true);
               imageContainer.getChildAt(0).startAnimation(translateLeftAnimation);
               HelperMethods.updateImageButtonContainer(imageControllerButtons,nextIndex-1);
           }

            //LINEAR ARRANGEMENT OF BUTTONS
            if(imagesData.size()<= 4){
              /*  HelperMethods.generateImageButtons(imageControllerButtons, imagesData, context, new HelperMethods.ImageButtonClicked() {
                    @Override
                    public void onClick(int index) {
                        HelperMethods.onImageButtonClick(index,imageContainer,imagesData);
                    }
                });

               */
            }


            return false;
        }
        else if (e2.getX() - e1.getX() > MIN_SWIPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
        {
            /* Code that you want to do on swiping right side*/


            int nextIndex = activeImageIndex-1;
            if(nextIndex >= 0){
                imagesData.get(activeImageIndex).setActive(false);
                HelperMethods.updateImageContainer(imageContainer,context,imagesData,nextIndex,translateRightAnimation,false);
                HelperMethods.updateImageButtonContainer(imageControllerButtons,nextIndex);
            }else if(nextIndex + 1 == 0){
                imagesData.get(0).setActive(true);
                imageContainer.getChildAt(0).startAnimation(translateRightAnimation);
                HelperMethods.updateImageButtonContainer(imageControllerButtons,0);
            }
            //LINEAR DISPLAY OF BUTTONS
            if(imagesData.size()<= 4){
            /*    HelperMethods.generateImageButtons(imageControllerButtons, imagesData, context, new HelperMethods.ImageButtonClicked() {
                    @Override
                    public void onClick(int index) {
                        HelperMethods.onImageButtonClick(index,imageContainer,imagesData);
                    }
                });

             */
;
            }


            Log.i("SWIYT",nextIndex + "Right");
            return false;
        }

        if(e1.getY() - e2.getY() > MIN_SWIPING_DISTANCE && Math.abs(velocityY) > THRESHOLD_VELOCITY) {
            // Bottom to top
            //Your code to flip the coin
            return false;
        }  else if (e2.getY() - e1.getY() > MIN_SWIPING_DISTANCE && Math.abs(velocityY) > THRESHOLD_VELOCITY) {
            // Top to bottom
            //
            return false;
        }


        return false; }



    private void updateImageContainer(LinearLayout imageContainer, int nextIndex, @Nullable Animation animation, LinearLayout.LayoutParams layoutParams, boolean fromImageButton){
        imageContainer.removeViewAt(0);
        //ADD NEW IMAGEVIEW
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageBitmap(imagesData.get(nextIndex).getBitmap());
        imagesData.get(nextIndex).setActive(true);
        imageContainer.addView(imageView,0,layoutParams);
        if(!fromImageButton)
        imageView.startAnimation(animation);

    }






}
