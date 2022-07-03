package com.example.heruhe.ui.dashboard;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentTransaction;


import com.example.heruhe.AuthActivity;
import com.example.heruhe.MainActivity;
import com.example.heruhe.R;

import com.example.heruhe.databinding.FragmentDashboardBinding;
import com.example.heruhe.ui.dashboard.posts.AddPostFragment;
import com.example.heruhe.ui.dashboard.posts.FinishPostFragment;

import com.example.heruhe.ui.dashboard.posts.UserPost;


import com.example.heruhe.utils.ImageLoadTask;
import com.example.heruhe.utils.ImageTaskExecutor;

import com.google.android.material.snackbar.Snackbar;


import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;


import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment implements ImageLoadTask.ImageIsLoaded, FinishPostFragment.PostAdded {

    private FragmentDashboardBinding binding;
    private FragmentTransaction transaction;
    private MenuItem addPostMenu, addPhotoMenu, nextMenu,logoutMenu;

    private final ArrayList<UserPost> selectedImages = new ArrayList<>();



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //SETS MENU ON FRAGMENT
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.dashboard_add_post) {
            boolean permissionGranted = ((MainActivity) (requireActivity())).requirePermissions();
            if (permissionGranted) {
                transaction = getParentFragmentManager().beginTransaction();
                addPhotoMenu.setVisible(false);
                addPostMenu.setVisible(false);
                logoutMenu.setVisible(false);
                nextMenu.setVisible(true);
                ((MainActivity) (requireActivity())).setActionBarTitle("Create Post");
                getParentFragmentManager().beginTransaction().replace(binding.fragmentContainer.getId(),LoadingFragment.newInstance()).commit();
                new ImageTaskExecutor().execute(new ImageLoadTask(this, getContext()));
            }

        }
        //FAKE FOLLOW ACCOUNT
        if(item.getItemId() == R.id.dashboard_add_photo){
            ParseUser.getQuery().getInBackground("XGJh9hfVNt", new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e == null){
                        ParseUser currentUser = ParseUser.getCurrentUser();
                      //  user.add(AuthActivity.FOLLOWERS,currentUser);
                        currentUser.add(AuthActivity.FOLLOWING,user);

                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.i("COUYH",e.getMessage() + "fty");
                            }
                        });
                       // user.saveInBackground();
                    }

                }
            });

        }
        if (item.getItemId() == R.id.menu_next) {
            if (selectedImages.size() <= 0) {
                Snackbar.make(binding.fragmentContainer, getString(R.string.snack_pick_an_image), Snackbar.LENGTH_LONG).show();
                return false;
            }
            nextMenu.setVisible(false);
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(binding.fragmentContainer.getId(), FinishPostFragment.newInstance(selectedImages, this));
            transaction.commit();
        }

        //LOGOUT USER
        if(item.getItemId() == R.id.dashboard_logout){
          ParseUser user = ParseUser.getCurrentUser();
          if (user != null){
          ParseUser.logOut();
          ((MainActivity)(requireActivity())).switchToAuthActivity();
          }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        boolean fragmentIsAttached = getParentFragmentManager().isDestroyed();
        if(binding != null && !fragmentIsAttached){
            FragmentTransaction newTransaction = getParentFragmentManager().beginTransaction();
            newTransaction.replace(binding.fragmentContainer.getId(), UserDashboardFragment.newInstance(null));
            newTransaction.commit();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setHasOptionsMenu(false);
        binding = null;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.dashboard_menu, menu);
        addPostMenu = menu.findItem(R.id.dashboard_add_post);
        addPhotoMenu = menu.findItem(R.id.dashboard_add_photo);
        nextMenu = menu.findItem(R.id.menu_next);
        logoutMenu=menu.findItem(R.id.dashboard_logout);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onFinish(ArrayList<UserPost> images) {

        transaction.add(binding.fragmentContainer.getId(),
                AddPostFragment.newInstance(images, selectedImages));
        transaction.commit();

    }


    @Override
    public void onComplete() {
        ParseUser.getCurrentUser().increment(AuthActivity.POST_COUNT, 1);
        ((MainActivity) (requireActivity())).goHome();
    }




}
