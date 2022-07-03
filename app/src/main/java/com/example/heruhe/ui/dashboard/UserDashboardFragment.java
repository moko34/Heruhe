package com.example.heruhe.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.heruhe.AuthActivity;
import com.example.heruhe.MainActivity;
import com.example.heruhe.R;
import com.example.heruhe.databinding.FragmentUserDashboardBinding;
import com.example.heruhe.ui.dashboard.posts.FinishPostFragment;
import com.example.heruhe.ui.dashboard.posts.PostMetaData;
import com.example.heruhe.ui.dashboard.posts.PostsFragment;
import com.example.heruhe.ui.dashboard.posts.UserPost;
import com.example.heruhe.ui.home.HomeFragment;
import com.example.heruhe.utils.HelperMethods;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDashboardFragment extends Fragment implements View.OnClickListener{

    private final String FAKE_FILENAME="some-path";
    public static final String PROFILE_IMAGE_KEY="profileImage";
    private final String COVER_IMAGE_KEY="coverImage";
    private boolean changeProfileImage,changeCoverImage;
    private FragmentContainerView containerView;
    private  ParseFile imageFile;
    private FragmentManager manager;
    private String userId,userObjectId;
    private boolean isFollowingSearchedUser;




    private FragmentUserDashboardBinding binding;



    public UserDashboardFragment()  {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment UserDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDashboardFragment newInstance(@Nullable String username) {
        UserDashboardFragment fragment = new UserDashboardFragment();
        fragment.userId = username;
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUserDashboardBinding.inflate(inflater,container,false);
        containerView=binding.getRoot().findViewById(R.id.user_post_fragment_container);

        if(userId != null){
           ParseUser.getQuery().whereEqualTo(MainActivity.USERNAME_KEY,userId).
                   include(AuthActivity.POSTS).
                   getFirstInBackground((user, e) -> {
                       if(e == null){
                           if(binding != null){
                                userObjectId=user.getObjectId();
                                ArrayList<ParseUser> following = new ArrayList<>(Objects.requireNonNull(ParseUser.getCurrentUser().getList(AuthActivity.FOLLOWING)));
                                for(ParseUser parseUser : following){
                                    if(parseUser.getObjectId().equals(userObjectId))isFollowingSearchedUser=true;
                                }
                               Log.i("DFG",isFollowingSearchedUser + "");
                                 //UPDATE FOLLOW BUTTON
                               if(isFollowingSearchedUser){
                                   binding.btnFollow.setVisibility(View.VISIBLE);
                                   binding.btnFollow.setCompoundDrawables(AppCompatResources.getDrawable(requireContext(),R.drawable.vector_remove_person),null,null,null);
                                   binding.btnFollow.setText(getText(R.string.unfollow));
                               }
                               initUser(user,false);
                               }


                       }
                       else {
                           e.printStackTrace();
                       }
                   });
        }else {
            binding.btnFollow.setVisibility(View.GONE);
            initUser(ParseUser.getCurrentUser(),true
            );
        }



        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode()== -1){

                Intent intent = result.getData();
                if(intent != null && intent.getData() != null){
                    Uri uri = intent.getData();
                    imageFile = saveFile(uri);
                    if(!imageFile.getName().equals(FAKE_FILENAME)){
                        getBitmapFromUrl(imageFile.getUrl(),changeCoverImage,changeProfileImage,false);
                        return;
                    }
                    Snackbar.make(binding.imgCover,"An error occurred try again later ", BaseTransientBottomBar.LENGTH_LONG).show();
                    Log.i("ERT",imageFile.getUrl());

                }
            }

        }
    });



    /**
     * Saves the user profile image or cover photo to the server
     * @param uri file uri
     * @return ParseFile
     */


    private ParseFile saveFile(Uri uri){
        ParseFile image = new ParseFile(FAKE_FILENAME,new byte[]{});

        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext()
                    .getContentResolver(),uri);
            ByteArrayOutputStream byteArrayOutputStream  = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            image = new ParseFile(ParseUser.getCurrentUser().getUsername() + "-profile-pic.jpeg",data);
            image.save();
        }catch (Exception e){
            Log.i("PROFILE-IMAGE-ERR",e.getMessage());
        }

        return image;
    }


    /**
     * Converts a url to bitmap in the background thread
     * The profile or cover imageview are updated with the resulting bitmap
     * @param uri url of image
     * @param changeCoverImage should update cover image
     * @param changeProfileImage should update profile image
     * @param init whether user data has been fetched initially
     */


    private void getBitmapFromUrl(String uri,boolean changeCoverImage,boolean changeProfileImage,boolean init){
        try{
            Executor executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try{
                    Bitmap bitmap;
                    if(changeProfileImage){
                        bitmap = Objects.requireNonNull(HelperMethods.decodeBitmapForDashboard(uri,true,null));
                    }else{
                        bitmap =  Objects.requireNonNull(HelperMethods.decodeBitmapForDashboard(uri,false,null));
                    }

                    handler.post(() -> {
                        ParseUser currentUser = ParseUser.getCurrentUser();
                        if(binding!= null){

                        if(changeProfileImage){
                            //IF USER JUST LAUNCHED THE APPLICATION DON'T UPDATE USERDATA
                            if(!init){
                                currentUser.put(PROFILE_IMAGE_KEY,imageFile);
                            }
                            binding.profileImage.clearAnimation();
                            binding.profileImage.setImageBitmap(bitmap);
                            currentUser.saveInBackground();

                            return;
                        }
                        if(changeCoverImage){
                            if(!init){
                                currentUser.put(COVER_IMAGE_KEY,imageFile);
                            }
                            binding.imgCover.clearAnimation();
                            binding.imgCover.setImageBitmap(bitmap);
                            currentUser.saveInBackground();

                        }

                        }
                    });
                }catch (Exception exception){
                    exception.printStackTrace();
                }

            });

        }catch(Exception exception){
            exception.printStackTrace();
            Toast.makeText(requireContext(), exception.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    /**
     * Fetches the posts of @param user. If the user is not the current user the
     * posts during the fragment creation will be parsed and updated to the user.
     * @param user The parse user to fetch posts of
     * @param isAdmin pass true if @param user is the current logged in user
     */


    private void fetchPosts(ParseUser user,boolean isAdmin) {
        ArrayList<UserPost> loadedPosts=new ArrayList<>();
        if(!isAdmin){
            ArrayList<ParseObject> posts = new ArrayList<>(Objects.requireNonNull(user.getList(AuthActivity.POSTS)));
            parsePosts(loadedPosts,posts);
            updateUIWithPostsData(loadedPosts);
        }else {
            ParseUser.getQuery().include(AuthActivity.POSTS).whereEqualTo(MainActivity.USERNAME_KEY,user.getUsername()).getFirstInBackground((object, e) -> {
                parsePosts(loadedPosts,new ArrayList<>(Objects.requireNonNull(user.getList(AuthActivity.POSTS))));
                updateUIWithPostsData(loadedPosts);
            });

                }

    }


    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        manager=getChildFragmentManager();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding=null;
        manager=null;
        userId=null;
    }

    /**
     * Fetches the number of followers of user from the back4app server
     * @param user ParseUser to get follower count
     */
    private void findFollower(ParseUser user){
        final int[] followersCount = {0};
        ParseUser.getQuery().whereNotEqualTo("objectId",user.getObjectId()).findInBackground((objects, e) -> {
            if(e==null){
            if(binding != null){
            for(int index=0;index<objects.size();index++){
                ArrayList<ParseUser> following = new ArrayList<>(objects.get(index).getList(AuthActivity.FOLLOWING));
                for(ParseUser curUser:following){
                    if(Objects.equals(curUser.getObjectId(), user.getObjectId()))
                    followersCount[0]=followersCount[0] + 1;
                    Log.i("DFG" , curUser.getObjectId() + "op");
                    Log.i("DFG" , user.getObjectId() + "op");
                }
                Log.i("DFG" , following.size() + "opl");
            }
            Log.i("DFG" , objects.size() + "op");
            binding.txtFollowsCount.setText(String.valueOf(followersCount[0]));
            }
            }
        });
    }

    /**
     * Takes an array of parse objects thus posts and parses them into user post objects
     * @param loadedPosts The arraylist to populate with the resulting user post object
     * @param objects The arraylist of parse objects thus posts
     */

    private void parsePosts(ArrayList<UserPost> loadedPosts,ArrayList<ParseObject> objects){
        for (ParseObject post : objects) {

            try {

                loadedPosts.add(UserPost.createFromJson(post.getObjectId(),
                        (String) Objects.requireNonNull(post.getJSONArray(FinishPostFragment.IMAGES)).getJSONObject(0).get("url")));

            } catch (Exception exception) {
                Log.i("JSON-PARSE-ERROR", exception.toString());
            }
        }

    }

    /**
     * Updates the UI with the parse user post objects. The bitmap is also loaded
     * @param loadedPosts The arraylist of user posts
     */

    private void updateUIWithPostsData(ArrayList<UserPost> loadedPosts){
        HelperMethods.generateBitmaps(loadedPosts, postMetaDataArrayList -> {
            if(containerView != null && manager != null){
                if(!manager.isDestroyed()){
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(containerView.getId(),
                            PostsFragment.newInstance(postMetaDataArrayList));
                    transaction.commit();
                }
            }
        });
    }

    /**
     * Initializes the fragment view with data from the @param user
     * @param user The parse user
     * @param isAdmin pass true if user is the currently logged in user
     */

    private void initUser ( ParseUser user , boolean isAdmin){
        ParseFile profileImage = user.getParseFile(PROFILE_IMAGE_KEY);
        ParseFile coverImage = user.getParseFile(COVER_IMAGE_KEY);

        ((MainActivity)(requireContext())).setActionBarTitle(user.getUsername());
        Animation loadAnimation = AnimationUtils.loadAnimation(requireContext(),R.anim.fade_load_anim);
        if(profileImage != null){

            binding.profileImage.startAnimation(loadAnimation);
            getBitmapFromUrl(profileImage.getUrl(),false,true,true);
        }
        if(coverImage != null){
            binding.imgCover.startAnimation(loadAnimation);
            getBitmapFromUrl(coverImage.getUrl(),true,false,true);
        }


        int postCount = Objects.requireNonNull(user.getList(AuthActivity.POSTS)).size();

        int followingCount = Objects.requireNonNull(user.getList(AuthActivity.FOLLOWING)).size();

        findFollower(user);


        if(binding != null) {

            binding.txtPostCount.setText(String.valueOf(postCount));
            binding.txtFollowingCount.setText(String.valueOf(followingCount));


            if (postCount > 0) {
                binding.txtNoPost.setVisibility(View.GONE);
               // binding.userPostFragmentContainer.startAnimation(loadAnimation);
                containerView.setVisibility(View.VISIBLE);
                if (!manager.isDestroyed()) {
                    fetchPosts(user,isAdmin);
                }

            } else {
                binding.txtNoPost.setVisibility(View.VISIBLE);
                if (containerView != null) {
                    containerView.setVisibility(View.GONE);
                }
            }

            if (isAdmin) {
                binding.profileImage.setOnClickListener(view12 -> {
                    changeProfileImage = true;
                    changeCoverImage = false;
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    activityResultLauncher.launch(intent);
                });
                binding.imgCover.setOnClickListener(view1 -> {
                    changeCoverImage = true;
                    changeProfileImage = false;
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    activityResultLauncher.launch(intent);
                });
            }else {
                binding.btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followOrUnfollowUser(userObjectId);
                        findFollower(user);
                    }
                });
            }
        }




    }

    @Override
    public void onClick(View view) {
        followOrUnfollowUser(userObjectId);

    }


    private void followOrUnfollowUser(String userId){
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<ParseUser> following = new ArrayList<>(Objects.requireNonNull(currentUser.getList(AuthActivity.FOLLOWING)));

        if(isFollowingSearchedUser) {
            for(int index = 0; index< following.size();index++){
                if(Objects.equals(following.get(index).getObjectId(), userId)){
                   following.remove(index);
                   isFollowingSearchedUser=false;
                   binding.btnFollow.setText(getString(R.string.follow_me));
                   binding.btnFollow.setCompoundDrawables(AppCompatResources.getDrawable(requireContext(),R.drawable.vector_person_add),null,null,null);
                   currentUser.put(AuthActivity.FOLLOWING,following);
                   currentUser.saveInBackground(new SaveCallback() {
                       @Override
                       public void done(ParseException e) {
                        //   e.printStackTrace();
                        //   Log.i("DFG",e.getMessage() + "");
                       }
                   });
                //   findFollower();
                   return;
                }
            }
        }else {
           ParseUser.getQuery().getInBackground(userId, (object, e) -> {
              if(e == null){
                  isFollowingSearchedUser=true;
                  binding.btnFollow.setText(getString(R.string.unfollow));
                  binding.btnFollow.setCompoundDrawables(AppCompatResources.getDrawable(requireContext(),R.drawable.vector_remove_person),null,null,null);
                  Objects.requireNonNull(currentUser.getList(AuthActivity.FOLLOWING)).add(object);
                  currentUser.saveInBackground();


              }
           });
        }
    }
}