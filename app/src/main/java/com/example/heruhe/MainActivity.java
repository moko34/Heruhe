package com.example.heruhe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.heruhe.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private int height;
    private final int code = 1000;
    public static final String USERNAME_KEY = "username";


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        BottomNavigationView  navView = findViewById(R.id.nav_view);
        height=navView.getHeight();



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard,R.id.navigation_search,R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        requestPermissions();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        String info = Objects.requireNonNull(navController.getCurrentDestination()).getId() + "";
        String date = R.id.navigation_dashboard + "";
        return super.onCreateOptionsMenu(menu);
    }

    public void goHome(){
        navController.navigate(R.id.navigation_home);
    }

    public void setActionBarTitle(String title) {

        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION},code);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == code){
            if(grantResults.length <= 0){
              requestPermissions();
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    public  boolean requirePermissions(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions();
            return false;
        }

        return true;
    }


    public int[] getDeviceWidthAndHeight(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width= metrics.widthPixels;
        int height = metrics.heightPixels;
        int[] widthHeight=new int[2];
        widthHeight[0] = width;
        widthHeight[1]= height;
        return widthHeight;
    }


    public Context getMainActivityContext(){
        return MainActivity.this;
    }


    public void switchToAuthActivity(){
      Intent intent = new Intent(MainActivity.this,AuthActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
      finish();
    }

    public int getBottomNavHeight(){
        return height;
    }

    public void navigateToDashBoardWithId(String username){
       Bundle bundle = new Bundle();
       bundle.putString(USERNAME_KEY,username);
       navController.navigate(R.id.navigation_dashboard,bundle);
       navController.clearBackStack(R.id.navigation_search);

    }
}