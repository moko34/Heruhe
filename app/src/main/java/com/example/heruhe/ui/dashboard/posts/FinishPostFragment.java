package com.example.heruhe.ui.dashboard.posts;

import static com.example.heruhe.R.color.custom_primary_accent_color;


import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import android.widget.TextView;

import com.example.heruhe.AuthActivity;
import com.example.heruhe.R;
import com.example.heruhe.databinding.FragmentFinishPostBinding;

import com.example.heruhe.utils.HelperMethods;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;


public class FinishPostFragment extends Fragment {

    private ArrayList<UserPost> pickedImages;
    private FragmentFinishPostBinding binding;
    public static final String CREATOR ="creator";
    public static final String COMMENTS="comments";
    public static final String LIKES ="likes";
    public static final String DESCRIPTION ="description";
    public static final String IMAGES ="images";





    public interface PostAdded{
        void onComplete();
    }


    private PostAdded postAdded;



    public FinishPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment FinishPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FinishPostFragment newInstance(ArrayList<UserPost> selectedImages,PostAdded postAdded) {
        FinishPostFragment fragment = new FinishPostFragment();
        fragment.postAdded=postAdded;
        fragment.pickedImages=selectedImages;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFinishPostBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.finishImage2.setImageURI(pickedImages.get(0).getImageUrl());
        if(pickedImages.size() > 1) {
            binding.finishImage1.setImageURI(pickedImages.get(1).getImageUrl());
        }

        binding.btnCreatePost.setOnClickListener(view1 -> publish());

        binding.edtFinishCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               if(!charSequence.toString().trim().equals("")){
                  binding.btnCreatePost.setEnabled(true);
                  return;
               }
               binding.btnCreatePost.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
    }

    private void showLoadingState(){
        binding.animLayout.setVisibility(View.VISIBLE);
        binding.txtAnimLeft.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.move_left));
        binding.txtAnimRight.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.move_right));
        binding.txtAnimCenter.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.move_around));
    }

    private void hideLoadingState(){
        binding.animLayout.setVisibility(View.GONE);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void publish (){
      //
        HelperMethods.closeKeyboard(requireContext(),requireActivity());
        binding.edtFinishCaption.clearFocus();
     try{
           showLoadingState();
           ArrayList<ParseFile> files = decodeImages(pickedImages);

           if(files.size()>0){
               ParseObject post = new ParseObject("Post");
               post.put(CREATOR, ParseUser.getCurrentUser().getUsername());
               post.put(LIKES,new ArrayList<ParseUser>());
               post.put(COMMENTS,new ArrayList<ParseObject>());
               post.put(DESCRIPTION,binding.edtFinishCaption.getText().toString());
               post.put(IMAGES,files);
               post.saveInBackground();

               ParseUser user = ParseUser.getCurrentUser();

               user.add(AuthActivity.POSTS,post);

               user.saveInBackground(e -> {
                hideLoadingState();
                   AlertDialog alertDialog;
                   if(e == null){
                       alertDialog = createAlert("Your post was added", "Hurray", false);
                   }else {
                       alertDialog = createAlert("Error", e.getMessage() + "", true);
                       alertDialog.show();
                       return;
                   }
                   alertDialog.show();
                   new Handler().postDelayed(() -> {
                       alertDialog.dismiss();
                       postAdded.onComplete();
                   },200);
                   //   onDestroy();
               });
           }
       }catch (Exception exception){

           Log.i("POST_ERR",exception.getMessage());
       }


    }


    private ArrayList<ParseFile> decodeImages(ArrayList<UserPost> imageUris){
        ArrayList<ParseFile> files = new ArrayList<>();
        for(UserPost post : imageUris){
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext() .getContentResolver(),post.getImageUrl());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                byte[] data = outputStream.toByteArray();
                ParseFile image = new ParseFile(post.getId() + ".jpeg",data);
               // image.save();
                image.saveInBackground();
                files.add(image);
            }catch (Exception exception){
                Log.i("dsa",exception.getMessage());
            }

        }
        return files;
    }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private AlertDialog createAlert (String message, String title, boolean isError){
            //SHOW ALERT WITH MESSAGE AND TITLE

            AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
            TextView txt_message = new TextView(requireContext());
            txt_message.setText(message);
            txt_message.setTextColor(isError ? Color.RED:requireContext().getColor(custom_primary_accent_color));

            txt_message.setTextSize(20f);
            txt_message.setPadding(4,10,4,10);
            txt_message.setGravity(Gravity.CENTER);
            dialog.setView(txt_message);
            dialog.setTitle(title);
            dialog.setPositiveButton(getString(R.string.okay), (dialogInterface, i) -> dialogInterface.dismiss());
            return dialog.create();

    }





}