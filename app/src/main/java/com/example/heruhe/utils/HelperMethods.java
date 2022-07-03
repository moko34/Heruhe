package com.example.heruhe.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.heruhe.AuthActivity;
import com.example.heruhe.R;
import com.example.heruhe.ui.dashboard.UserDashboardFragment;
import com.example.heruhe.ui.dashboard.posts.Post;
import com.example.heruhe.ui.dashboard.posts.PostMetaData;

import com.example.heruhe.ui.dashboard.posts.UserPost;
import com.example.heruhe.ui.home.HomeFragment;
import com.example.heruhe.ui.home.HomeFragmentAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HelperMethods {

    private static final int IMAGE_BUTTON_TRANSLATE_Y_CONSTANT=10;


    public interface  PostIsReady{
        void isReady(ArrayList<Post> posts);
    }

    public interface LoadSingleBitmap{
        void bitmapIsReady(Bitmap bitmap);
    }

    public interface GenerateBitmapCallback{
        void done(ArrayList<PostMetaData> postMetaDataArrayList);
    }

    public interface  ImageButtonClicked{
        void onClick(int index);
    }


    /**
     * Loads a bitmap from the web.
     * @param uri Uri of image
     * @param loadProfilePhoto for scaling the image for profile image or cover photo
     *                         Pass true to scale to profile image size
     * @throws IOException network error
     */

    public static void decodeBitmap (String uri, boolean loadProfilePhoto, @Nullable Integer dimension, LoadSingleBitmap loadSingleBitmap) throws IOException {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                InputStream inputStream = new java.net.URL(uri).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                int width = 200;
                int height = 200;
                if (dimension != null) {
                    width = dimension;
                    height = dimension;
                }
                inputStream.close();
                int finalWidth = width;
                int finalHeight = height;
                handler.post(() -> {
                    if (!loadProfilePhoto) {
                        loadSingleBitmap.bitmapIsReady(bitmap);
                    } else {
                        loadSingleBitmap.bitmapIsReady(drawRoundShape(Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight,false)));
                    }
                });
            }catch (Exception exception){
                Log.i("NET-ERR",exception.getMessage() + "");
            }
        });

    }

    public static Bitmap decodeBitmapForDashboard(String uri, boolean loadProfilePhoto, @Nullable Integer dimension) throws IOException{
        InputStream inputStream = new java.net.URL(uri).openStream();
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        int width = 200;
        int height = 200;
        if (dimension != null) {
            width = dimension;
            height = dimension;
        }
        inputStream.close();
        if(!loadProfilePhoto){
            return bitmap;
        }

        return drawRoundShape(Bitmap.createScaledBitmap(bitmap, width, height,false));
    }


    /**
     * Draws a circular version of a bitmap
     * @param bitmap Bitmap to draw circle of
     * @return round shaped bitmap
     */
    public static Bitmap drawRoundShape(Bitmap bitmap) {
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        Canvas c = new Canvas(circleBitmap);

        c.drawCircle(bitmap.getScaledWidth(c)/2f , bitmap.getScaledHeight(c)/2f , bitmap.getScaledWidth(c)/2f, paint);
        return circleBitmap;
    }


    /**
     * Generates bitmaps of an array of user posts objects
     * @param posts an arraylist of user posts objects from which bitmaps will be generated
     * @param generateBitmapCallback callback to trigger once bitmaps have been loaded
     */

    public static void generateBitmaps(ArrayList<UserPost> posts,GenerateBitmapCallback generateBitmapCallback) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        ArrayList<PostMetaData> postMetaData = new ArrayList<>();

        Log.i("ASDFT",posts.size() + "hg");
        executor.execute(() -> {

            for (UserPost post : posts) {
                try {
                    Bitmap bitmap = HelperMethods.decodeBitmapForDashboard(post.getImageUrl().toString(), false, null);
                    postMetaData.add(new PostMetaData(bitmap, post.getId(),false));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i("JSON", postMetaData.size() + "ghj");
            handler.post(() -> generateBitmapCallback.done(postMetaData));
        });

    }

    public static void closeKeyboard(Context context,Activity activity){
        View view = activity.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    public static int getActiveViewIndex(ArrayList<PostMetaData> postMetaData){
        for(PostMetaData post:postMetaData){
            if(post.isActive()){
                return postMetaData.indexOf(post);
            }
        }

        return 0;
    }

    public static int getImageControllerTranslation(int arraySize){
        int[] translations= new int[arraySize];
       return 1 % 5 ;
    }


    public static void generateImageButtons(LinearLayout layout, ArrayList<PostMetaData> metaDataArrayList, Context context,ImageButtonClicked buttonClicked){
        layout.removeAllViews();
        LinearLayout.LayoutParams layoutParams =new  LinearLayout.LayoutParams(15,15);
        layoutParams.setMargins(10,4,10,4);
        int [] translations = generateViewsTranslations(metaDataArrayList.size());
        //ADD TEXTVIEW TO
        for(int i = 0;i<metaDataArrayList.size();i++){
            TextView textView = new TextView(context);
            textView.setSelected(false);
            textView.setId(i);
            if(metaDataArrayList.size() >= 6){
                textView.setTranslationY(translations[i]);
            }
            textView.setBackground(AppCompatResources.getDrawable(context, R.drawable.image_oval));
            int finalI = i;
            textView.setOnClickListener(view -> buttonClicked.onClick(finalI));
            if(metaDataArrayList.get(i).isActive())textView.setSelected(true);

            layout.addView(textView,i,layoutParams);
        }

    }


    public static void updateImageContainer(LinearLayout imageContainer,Context context,ArrayList<PostMetaData> imagesData, int nextIndex, @org.jetbrains.annotations.Nullable Animation animation, boolean fromImageButton){
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

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


    public static void updateImageButtonContainer(LinearLayout buttonContainer,int activeIndex){
       int childrenSize = buttonContainer.getChildCount();
       for(int i = 0;i<childrenSize;i++){
           if(i == activeIndex){
            buttonContainer.getChildAt(activeIndex).setSelected(true);
           }else {
               buttonContainer.getChildAt(i).setSelected(false);
           }

       }
    }


    public static int[] generateViewsTranslations(int arraySize){
        int [] translations=new int[arraySize];

        //MEDIAN
        int median = arraySize / 2;
        //IF SIZE IS AN ODD NUMBER
        if(arraySize % 2 != 0){

            for (int index=0;index<arraySize;index++){
                 //IF INDEX IS LESS THAN MEDIAN INCREMENT TRANSLATION BY INDEX
                if(index <= median){
                    translations[index]= (index * IMAGE_BUTTON_TRANSLATE_Y_CONSTANT * -1);

                }else{
                  //IF GREATER THAN MEDIAN TRANSLATION SHOULD DECREMENT
                  int differenceFromMedian=index-median;
                  int correspondingValue=translations[(median - differenceFromMedian)];

                    translations[index]= correspondingValue;
                }

                Log.i("ASDFG",translations[0] + "index" + index +"ty" + translations[0]);
            }
        }else {
            for(int index=0;index<arraySize;index++){
                //IF INDEX IS LESS THAN MEDIAN INCREMENT TRANSLATION
                if(index<median){
                    translations[index]= (index * IMAGE_BUTTON_TRANSLATE_Y_CONSTANT * -1);
                }else {
                    int differenceFromMedian=index - median;
                    int correspondingValue=translations[(median-differenceFromMedian)-1];
                    translations[index]=correspondingValue;
                }
            }

        }

        return translations;
    }


    /**
     * Fetches @param users data with user posts from the back4app server
     * @param users ParseUsers array
     * @return a new ParseUsers array with the posts of users included
     * @throws ParseException ParseUser parse error
     */
    private static ArrayList<ParseUser> loadUserData(ArrayList<ParseUser> users) throws ParseException {

        ArrayList<ParseUser> followersWithData = new ArrayList<>();
        for(ParseUser user:users){

            try {
                ParseUser currentUser =  ParseUser.getQuery().
                        include(AuthActivity.POSTS).get(user.getObjectId());
                followersWithData.add(currentUser);

            }catch (Exception exception){
                Log.i("FOLLOW-ERR",exception.getMessage());
            }

        }
        return followersWithData;
    }

    /**
     * Checks if a post is already part of @posts
     * @param postId the id of the post to be added
     * @param posts the arraylist of posts
     * @return true if post is already part of the array and false otherwise
     */

    private static boolean checkIfPostExists(String postId, ArrayList<Post> posts){

        for(Post post : posts){

            if(Objects.equals(post.getPostId(), postId)) {

                return true;
            }
        }
        return false;
    }


    /**
     * Compares the last updated dates of all users in @param users to
     * to gain the oldest date a user was created. The resulting date is matched against
     * today's date to gain the number of days from now. The date of each day is then added to an
     * arraylist and returned
     * @param users ParseUsers array
     * @return an array of dates of days between today's date and the oldest date
     */

    private static ArrayList<Date> getOldestDates(ArrayList<ParseUser> users){
        Date currentOld = new Date();


        for(ParseUser user : users){
            if(user.getCreatedAt().before(currentOld)){

                currentOld=user.getCreatedAt();
            }
        }
        double oneDayInMillis = 60 * 60 * 24 * 1000;

        double differenceInDate = new Date().getTime() - currentOld.getTime();

        double numberOfDays = differenceInDate/oneDayInMillis;

        int numOfDays= (int) (numberOfDays);

        ArrayList<Date> dates = new ArrayList<>();

        for (int index = 0; index <= numOfDays; index++){
            if(index == 0){

                //MAX NUMBER OF DAYS IN MILLISECONDS
                long maxNumberOfDays = numOfDays * HomeFragment.A_DAY_IN_MILLS;
                //REMAINING MILLISECONDS AFTER MAX DAYS
                long remainingMills =currentOld.getTime() + maxNumberOfDays;
                dates.add(new Date(remainingMills));
            }else{
                //GO BACK BY INDEX NUMBER OF DAYS
                long curDateInMills =dates.get(0).getTime() - (index * HomeFragment.A_DAY_IN_MILLS);
                dates.add(new Date(curDateInMills));

            }
        }

        return  dates;

    }


    /**
     * Compares the date a post was created to @thresholdDate  to arrange
     * posts based  on recency
     * @param post post to compare date of
     * @param thresholdDate oldest date
     * @return true if the post date created is more recent than the threshold
     */
    private static boolean compareDatesFromPost(ParseObject post, Date thresholdDate){
        long createdAt =  post.getCreatedAt().getTime();
        return createdAt > thresholdDate.getTime();
    }


    /**
     * Loads @param following posts and applies the logic of comparing dates to
     * arrange posts based on recency
     * @param following users with all posts loaded
     * @return all @param posts arranged based on recency
     */

    private static ArrayList<Post> loadFollowingPost(ArrayList<ParseUser> following){

        ArrayList<Post> latestPosts = new ArrayList<>();

        ArrayList<Date> numberOfDays = getOldestDates(following);


        for(Date date : numberOfDays){

            //LOOP FOR RECENT POST FROM RECENT FOLLOWER
            for(ParseUser user:following){


                //LOOP THROUGH FOR RECENT POSTS
                ArrayList<ParseObject> posts = new ArrayList<>(Objects.requireNonNull(user.getList(AuthActivity.POSTS)));
                for(ParseObject post:posts){

                    if(compareDatesFromPost(post,date)){
                        if(!checkIfPostExists(post.getObjectId(),latestPosts))
                            latestPosts.add(new Post(Objects.requireNonNull(user.getParseFile(UserDashboardFragment.PROFILE_IMAGE_KEY)).getUrl(),post));
                    }
                }

                //}
            }



        }
        return latestPosts;

    }


    /**
     * Fetches @param userArrayList user's data including user's post from the server
     * and loads all posts of the current user's following.
     * The recycler view is updated with the resulting data
     * @param userArrayList following
     */
    public static void fetchPosts (ArrayList<ParseUser> userArrayList,PostIsReady postIsReady){
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());


        executor.execute(() -> {
            ArrayList<ParseUser> followingWithData = new ArrayList<>();
            try {
                followingWithData = loadUserData(userArrayList);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ArrayList<ParseUser> finalFollowingWithData = followingWithData;
            handler.post(() -> {
                ArrayList<Post> allPosts = loadFollowingPost(finalFollowingWithData);
                postIsReady.isReady(allPosts);
            });
        });
    }


    /**
     * Transforms @date to a user friendly format
     * @param date date to transform
     * @return the first object is the date prefix thus day of the if less @date is less
     * than a week old and the second object is the timestamp
     */
    public static String[] getDateString(Date date){
        long todayInMills = new Date().getTime();
        long currentDateInMills = date.getTime();
        Calendar calendar = Calendar.getInstance();
        String[] daysOfTheWeek = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        long differenceInTime = todayInMills - currentDateInMills;
        //DATE FORMAT FOR TIME FORMATTING
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        //FORMATTED TIME
        String formattedTime = dateFormat.format(date);
        String [] strings= new String[2];

        Log.i("ASDF",differenceInTime + "");
        //TODAY
        if(differenceInTime <= HomeFragment.A_DAY_IN_MILLS){
            strings[0]="Today";
            strings[1]=formattedTime;
            return strings;
        }
        //A DAY AGO
        if(differenceInTime <= (HomeFragment.A_DAY_IN_MILLS * 2)){

            strings[0]="Yesterday";
            strings[1]=formattedTime;
            return strings;
        }
        //TWO DAYS AGO
        if (differenceInTime <= (HomeFragment.A_DAY_IN_MILLS * 3)){
            calendar.setTime(date);
            int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);

            strings[0]=daysOfTheWeek[Integer.parseInt(String.valueOf(dayIndex))-1];
            strings[1]=formattedTime;
            return strings;
        }
        //3 DAYS AGO
        if (differenceInTime <= (HomeFragment.A_DAY_IN_MILLS * 4)){
            calendar.setTime(date);
            int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
            strings[0]=daysOfTheWeek[Integer.parseInt(String.valueOf(dayIndex))-1];
            strings[1]=formattedTime;
            return strings;
        }
        //4 DAYS AGO
        if(differenceInTime <= (HomeFragment.A_DAY_IN_MILLS * 5)){
            calendar.setTime(date);
            int dayIndex= calendar.get(Calendar.DAY_OF_WEEK);
            //BUG FIX ON ARRAY OUT OF BOUND
            strings[0]=daysOfTheWeek[Integer.parseInt(String.valueOf(dayIndex))-1];
            strings[1]=formattedTime;
            return strings;
        }
        //5 DAYS AGO
        if(differenceInTime <= (HomeFragment.A_DAY_IN_MILLS * 6) ){
            calendar.setTime(date);
            int dayIndex=calendar.get(Calendar.DAY_OF_WEEK);
            strings[0]=daysOfTheWeek[Integer.parseInt(String.valueOf(dayIndex))-1];
            strings[1]=formattedTime;
            return strings;
        }

        DateFormat myDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        strings[0]=myDateFormat.format(date);
        strings[1]="";
        return strings;

    }






}
