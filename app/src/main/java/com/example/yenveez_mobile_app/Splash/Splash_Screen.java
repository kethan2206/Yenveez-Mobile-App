package com.example.yenveez_mobile_app.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.MainActivity;
import com.example.yenveez_mobile_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null){
                    startActivity(new Intent(Splash_Screen.this, Login.class));
                } else {
                    startActivity(new Intent(Splash_Screen.this, MainActivity.class));
                }
                finish();
            }
        },800);
    }
}