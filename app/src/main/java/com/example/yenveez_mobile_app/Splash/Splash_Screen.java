package com.example.yenveez_mobile_app.Splash;

/** Splash Screen */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.example.yenveez_mobile_app.Beacon.FindBeacon;
import com.example.yenveez_mobile_app.Intro.Intro1;
import com.example.yenveez_mobile_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    protected void onStart() {
        super.onStart();
        /** Handler is used for making a delay of 0.8 sec for displaying the splash screen */

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser(); //checking if user is already exist or not
                if (user == null){
                    startActivity(new Intent(Splash_Screen.this, Intro1.class));
                } else {
                    startActivity(new Intent(Splash_Screen.this, FindBeacon.class));
                }
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        },800);
    }
}