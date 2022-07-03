package com.example.heruhe.ui.dashboard.posts;


import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Handler;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.example.heruhe.R;
import com.example.heruhe.databinding.FragmentAddPostBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;


import java.util.ArrayList;



public class AddPostFragment extends Fragment implements PostsAdapter.UserPostSelected {

    private ImageView imageView;
    private LinearLayout image_preview_layout;
    private int preview_image_index = -1;
    private FragmentAddPostBinding binding;


    private static  ArrayList<UserPost> loadedImages;
    private static  ArrayList<UserPost> chosenImages;


    public AddPostFragment() {
        // Required empty public constructor
    }



    public static AddPostFragment newInstance(ArrayList<UserPost> images,ArrayList<UserPost> selectedImages) {
        loadedImages=images;
        chosenImages=selectedImages;
        return new AddPostFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentAddPostBinding.inflate(inflater,container,false);
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = binding.addPostRecyclerView;
        imageView = binding.addPostImageView;
        image_preview_layout= binding.imagePreviewContainer;
        ImageButton  btnTrash = binding.btnDelete;
        btnTrash.setOnClickListener(button->removeImage());
        imageView.setImageURI(loadedImages.get(2).getImageUrl());
        PostsAdapter adapter = new PostsAdapter(loadedImages, this, requireContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    @Override
    public void onSelected(UserPost post, ImageView imageButton) {
     //ADD SELECTED IMAGE DATA TO SELECTED IMAGES
     if(chosenImages.size() < 10 ){
        boolean imageWasRemoved = false;
         if(chosenImages.contains(post)){
             int postIndex = chosenImages.indexOf(post);
             chosenImages.remove(post);
             image_preview_layout.removeViewAt(postIndex);
             imageWasRemoved = true;
         }else {
             chosenImages.add(post);
         }




         //STORE CURRENT INDEX
         int curIndex = chosenImages.size()-1;
         preview_image_index=curIndex;


         //UPDATE IMAGE VIEW WITH CURRENT IMAGE


         if (chosenImages.size() > 0 && !imageWasRemoved){
            image_preview_layout.setVisibility(View.VISIBLE);
             //CREATE IMAGEVIEW COMPONENT
             imageView.setImageURI(chosenImages.get(curIndex).getImageUrl());
             ImageView newImage = new ImageView(getContext());
             LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(70, ViewGroup.LayoutParams.MATCH_PARENT);
             params.setMargins(4,4,4,4);
             newImage.setLayoutParams(params);
             newImage.setId(Integer.parseInt(chosenImages.get(curIndex).getId()));
             newImage.setBackground(AppCompatResources.getDrawable(requireContext(),R.drawable.user_post_card));
             newImage.setImageURI(chosenImages.get(curIndex).getImageUrl());
             newImage.setOnClickListener(this::onImageClick);
             image_preview_layout.addView(newImage,curIndex);
             newImage.startAnimation( AnimationUtils.loadAnimation(requireContext(),R.anim.launch_image));
         }else if (chosenImages.size()== 0){
            image_preview_layout.setVisibility(View.GONE);
            imageView.setImageURI(loadedImages.get(1).getImageUrl());
            preview_image_index=-1;
         }else {
             imageView.setImageURI(chosenImages.get(curIndex).getImageUrl());
         }




     }else {
         Snackbar.make(requireContext(),binding.getRoot(),getString(R.string.add_post_maximum), BaseTransientBottomBar.LENGTH_LONG).show();
     }

    }


    private void onImageClick (View view){
        int id = view.getId();
        for (int index = 0;index < chosenImages.size();index++){
            if (Integer.parseInt(chosenImages.get(index).getId()) == id){
               preview_image_index=index;

               imageView.setImageURI(chosenImages.get(index).getImageUrl());

               return;
            }
        }
    }

    private void removeImage(){
      //UPDATE LINEAR LAYOUT
      if(preview_image_index >= 0 && (chosenImages.size() > 0)){
          chosenImages.remove(preview_image_index);
          Animation exitAnimation = AnimationUtils.loadAnimation(requireContext(),R.anim.image_exit);
          exitAnimation.setAnimationListener(new Animation.AnimationListener() {
              @Override
              public void onAnimationStart(Animation animation) {

              }

              @Override
              public void onAnimationEnd(Animation animation) {
                  if(chosenImages.size() >= 1){
                      imageView.setImageURI(chosenImages.get(chosenImages.size() - 1).getImageUrl());
                  }else{
                      imageView.setImageURI(loadedImages.get(0).getImageUrl());
                      image_preview_layout.setVisibility(View.GONE);

                  }
              }

              @Override
              public void onAnimationRepeat(Animation animation) {

              }
          });
          image_preview_layout.getChildAt(preview_image_index).startAnimation(exitAnimation);
          final int ANIM_EXIT_DURATION = 180;
          new Handler().postDelayed(() -> {

              image_preview_layout.removeViewAt(preview_image_index);
              preview_image_index = chosenImages.size() - 1;
          },ANIM_EXIT_DURATION);
      }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_next){
            assert getParentFragment() != null;
            setHasOptionsMenu(false);
            FragmentTransaction transaction = getParentFragment().getChildFragmentManager().beginTransaction();
        //    transaction.replace(containerView.getId(),new FinishPostFragment()).addToBackStack("Finish");
            transaction.commit();
        }
        return super.onOptionsItemSelected(item);
    }

}