package com.example.heruhe;

import static com.example.heruhe.R.color.custom_primary_accent_color;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heruhe.utils.HelperMethods;
import com.google.android.material.textfield.TextInputLayout;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;


import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {

    private final Pattern USERNAME_PATTERN = Pattern.compile("^(?=.*[a-z])[a-z\\d_]{4,}$");
    private final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d])(?=.*[%$@*#/&])[a-zA-Z\\d%$@*#/&]{8,}$");
    private final Pattern PASSWORD_SYMBOL_CHECK_PATTERN = Pattern.compile("^(?=.*[%$@*#])[a-zA-Z\\d%$@*#/&]+$");
    private final Pattern PASSWORD_LOWERCASE_CHECK_PATTERN = Pattern.compile("^(?=.*[a-z])[a-zA-Z\\d%$@*#/&]+$");
    private final Pattern PASSWORD_UPPERCASE_CHECK_PATTERN = Pattern.compile("^(?=.*[A-Z])[a-zA-Z\\d%$@*#/&]+$");
    private final Pattern PASSWORD_NUMBER_CHECK_PATTERN = Pattern.compile("^(?=.*[\\d])[a-zA-Z\\d%$@*#/&]+$");
    private final Pattern PASSWORD_LENGTH_CHECK_PATTERN = Pattern.compile("^(?=.*[a-zA-Z\\d%$@*#/&]).{8,}$");

    public static final String POST_COUNT="postCount";
    public static final String POSTS= "posts";
    public static final String FOLLOWING="following";
    public static final String FOLLOWERS_COUNT = "followersCount";
    public static final String FOLLOWERS = "followers";
    private final String USERNAME_KEY="username";
    private final String PASSWORD_KEY = "password";
    private final String STATE_IS_READY = "state_is_ready";
    private boolean stateIsBeingSaved=false;


    private LinearLayout anim_layout;
    private TextInputLayout usernameLayout,passwordLayout,emailLayout,confirmPasswordLayout;
    private EditText edtUsername,edtPassword,edtConfirmPassword,edtEmail;
    private Button btnAuth,btnAuthOption;
    private TextView authCaption,txtAuthOption,txt_anim_left,txt_anim_right,txt_anim_round;

    private final long duration = 260;

    private boolean loginIsActive = true;
    private Bundle bundle;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);
        Objects.requireNonNull(getSupportActionBar()).hide();


        initViews();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(bundle!=null){
            String username= bundle.getString(USERNAME_KEY);
            String password= bundle.getString(PASSWORD_KEY);
            if(!loginIsActive && stateIsBeingSaved)
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void done(ParseUser user, ParseException exception) {
                    if(exception != null){
                        AlertDialog dialog = createAlert(getString(R.string.email_error_login,exception.getMessage(),""),
                                getString(R.string.error),true);
                        dialog.show();
                        ParseUser.logOut();
                        return;
                    }
                    if(user.getBoolean("emailVerified")){
                        Intent intent = new Intent(AuthActivity.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                }
            });
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(USERNAME_KEY,edtUsername.getText().toString());
        outState.putString(PASSWORD_KEY,edtPassword.getText().toString());
        stateIsBeingSaved=true;
        bundle=outState;
        super.onSaveInstanceState(outState);
    }



    @Override
    protected void onStart() {
        super.onStart();
        switchToMainActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bundle=null;
    }

    private void switchToMainActivity(){
        ParseUser user = ParseUser.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(AuthActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
    }


    private void initViews(){
       usernameLayout = findViewById(R.id.username_layout);
       passwordLayout = findViewById(R.id.password_layout);
       emailLayout = findViewById(R.id.layout_email_username);
       confirmPasswordLayout= findViewById(R.id.confirm_password_layout);
       edtUsername= findViewById(R.id.edt_username);
       edtEmail = findViewById(R.id.edt_email_username);
       edtPassword = findViewById(R.id.edt_password);
       edtConfirmPassword= findViewById(R.id.edt_confirm_password);
       btnAuth = findViewById(R.id.btn_auth);
       txt_anim_left = findViewById(R.id.txt_anim_left);
       txt_anim_right = findViewById(R.id.txt_anim_right);
       txt_anim_round = findViewById(R.id.txt_anim_center);
       anim_layout=findViewById(R.id.anim_layout);
       btnAuthOption=findViewById(R.id.btn_auth_options);
       txtAuthOption = findViewById(R.id.txt_auth_options);
       authCaption = findViewById(R.id.txt_caption);
       btnAuthOption.setOnClickListener(this);
       btnAuth.setOnClickListener(this);

       edtConfirmPassword.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void afterTextChanged(Editable editable) {
            validateUserInput();
           }
       });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {

            if(view.getId()==R.id.btn_auth_options) {

                btnAuthOption.startAnimation(AnimationUtils.
                        loadAnimation(this, R.anim.button_bump));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //CHECK AUTH MODE FOR SIGN_IN OR SIGN_UP
                        if (btnAuth.getText().toString().equals(getString(R.string.login))) {
                            //SWITCH TO SIGN UP MODE
                            switchToSignUp();
                        } else {
                            //SWITCH TO SIGN IN MODE
                            switchToLogin();
                        }
                    }
                }, duration);
                return;
            }

            if(view.getId()==R.id.btn_auth) {
                boolean isAuthenticated = validateUserInput();
                if (isAuthenticated) {
                    if (loginIsActive) {
                        String password = edtPassword.getText().toString();
                        String username = edtEmail.getText().toString();
                        boolean passwordIsValid = PASSWORD_PATTERN.matcher(password).matches();
                        if (passwordIsValid) {
                            signInUser(password, username);
                            Log.i("PASS", "pass");
                        } else {
                            AlertDialog dialog = createAlert(getString(R.string.invalid_credentials), getString(R.string.error), true);
                            dialog.show();
                        }
                    } else {
                        String email = edtEmail.getText().toString();
                        String password = edtPassword.getText().toString();
                        String username = edtUsername.getText().toString();
                        signUpUser(username, password, email);
                    }

                }
            }


    }

    private void switchToSignUp(){
      confirmPasswordLayout.setVisibility(View.VISIBLE);
      usernameLayout.setVisibility(View.VISIBLE);
      emailLayout.setHint(R.string.email);
      btnAuth.setText(R.string.signup);
      btnAuthOption.setText(getString(R.string.login));
      txtAuthOption.setText(R.string.login_option);
      authCaption.setText(R.string.signup_caption);
      loginIsActive=false;
      stateIsBeingSaved=false;
    }

    private void switchToLogin(){
      confirmPasswordLayout.setVisibility(View.GONE);
      usernameLayout.setVisibility(View.GONE);
      emailLayout.setHint(R.string.email_username);
      btnAuth.setText(R.string.login);
      txtAuthOption.setText(R.string.signup_option);
      btnAuthOption.setText(getString(R.string.signup));
      authCaption.setText(R.string.login_caption);
      loginIsActive=true;
      stateIsBeingSaved=false;
    }

    private boolean validatePassword(String password){
        passwordLayout.setError(null);

        if(password.equals("")){
           passwordLayout.setError(getString(R.string.password_error));
           return false;
        }

        if(!PASSWORD_LOWERCASE_CHECK_PATTERN.matcher(password).matches()){
           passwordLayout.setError(getString(R.string.password_error_lowercase));
           return false;
        }
        if(!PASSWORD_UPPERCASE_CHECK_PATTERN.matcher(password).matches()){
           passwordLayout.setError(getString(R.string.password_error_uppercase));
           return false;
        }

        if(!PASSWORD_SYMBOL_CHECK_PATTERN.matcher(password).matches()){
           passwordLayout.setError(getString(R.string.password_error_symbol));
           return false;
        }

        if(!PASSWORD_NUMBER_CHECK_PATTERN.matcher(password).matches()){
           passwordLayout.setError(getString(R.string.password_error_number));
           return false;
        }

        if(!PASSWORD_LENGTH_CHECK_PATTERN.matcher(password).matches()){
            passwordLayout.setError(getString(R.string.password_error_length));
            return false;
        }

        return true;
    }


    private boolean validateUserInput(){
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();

        emailLayout.setError(null);
        passwordLayout.setError(null);
        passwordLayout.setErrorEnabled(false);
        emailLayout.setErrorEnabled(false);

        if(loginIsActive){
        if(!USERNAME_PATTERN.matcher(email).matches()){
            emailLayout.setError(getString(R.string.username_error_login));
            return false;
          }
        }


        if(!loginIsActive){

            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailLayout.setError(getString(R.string.email_error));
                return false;
            }

           String confirmPassword = edtConfirmPassword.getText().toString();
           String username = edtUsername.getText().toString();

           confirmPasswordLayout.setError(null);
           usernameLayout.setError(null);
           usernameLayout.setErrorEnabled(false);
           confirmPasswordLayout.setErrorEnabled(false);



           if(!USERNAME_PATTERN.matcher(username).matches()){
              usernameLayout.setError(getString(R.string.username_error));
              return false;
           }

            if(!validatePassword(password)){
                return false;
            }

           if(!confirmPassword.equals(password)){
               confirmPasswordLayout.setError(getString(R.string.confirm_password_error));
               return false;
           }

           return true;
        }

        if(password.equals("")){
            passwordLayout.setError(getString(R.string.password_error));
            return false;
        }


        return true;
    }

    /**
     * Signs Up the user to the back4app Parse server with email,password,and username
     * @param  username parseUser username
     * @param  password parseUser password
     * @param  email parseUser email
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void signUpUser(String username, String password, String email){

        ParseUser newUser = new ParseUser();
        newUser.setPassword(password);
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.put(FOLLOWING,new ArrayList<ParseObject>());
        newUser.put(FOLLOWERS,new ArrayList<ParseObject>());
        newUser.put(POSTS,new ArrayList<ParseObject>());
        HelperMethods.closeKeyboard(this,AuthActivity.this);
        showLoadingState();
        newUser.signUpInBackground(exception -> {
        hideLoadingState();
        boolean noErrors = exception == null;
        AlertDialog dialog = createAlert(noErrors ?getString(R.string.success_signup) :exception.getMessage(),
                    noErrors?getString(R.string.success):
                            getString(R.string.error),!noErrors);
          dialog.show();

          ParseUser.logOut();
        });


    }

    /**
     * Signs in the user with email and password
     * @param password parseUser password
     * @param username parseUser username
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void signInUser (String password, String username){
       HelperMethods.closeKeyboard(this,AuthActivity.this);
       showLoadingState();
       ParseUser.logInInBackground(username, password, (user, exception) -> {
           hideLoadingState();
           if(exception != null){
               AlertDialog dialog = createAlert(getString(R.string.email_error_login,exception.getMessage(),""),
                       getString(R.string.error),true);
               dialog.show();
               ParseUser.logOut();
               return;

           }
           if(user.getBoolean("emailVerified")){
              Intent intent = new Intent(AuthActivity.this,MainActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
           }else{
               AlertDialog dialog = createAlert(getString(R.string.email_error_login),
                       getString(R.string.error),true);
               dialog.show();
               ParseUser.logOut();
           }

       });

    }



   private void showLoadingState (){
        //LAUNCH LOADING SCREEN

        anim_layout.setVisibility(View.VISIBLE);
        txt_anim_left.startAnimation(AnimationUtils.loadAnimation(AuthActivity.this,R.anim.move_left));
        txt_anim_right.startAnimation(AnimationUtils.loadAnimation(this,R.anim.move_right));
        txt_anim_round.startAnimation(AnimationUtils.loadAnimation(this,R.anim.move_around));
    }

    private void hideLoadingState (){
        //HIDE LOADING SCREEN

       anim_layout.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private AlertDialog createAlert (String message, String title, boolean isError){
     //SHOW ALERT WITH MESSAGE AND TITLE

     AlertDialog.Builder dialog = new AlertDialog.Builder(this);
     TextView txt_message = new TextView(this);
     txt_message.setText(message);
     txt_message.setTextColor(isError ? Color.RED:getColor(custom_primary_accent_color));

     txt_message.setTextSize(20f);
     txt_message.setPadding(4,10,4,10);
     txt_message.setGravity(Gravity.CENTER);
     dialog.setView(txt_message);
     dialog.setTitle(title);
     dialog.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialogInterface, int i) {
             dialogInterface.dismiss();
         }
     });
     return dialog.create();
    }


}