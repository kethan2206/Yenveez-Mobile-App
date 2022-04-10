package com.example.yenveez_mobile_app.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.MainActivity;
import com.example.yenveez_mobile_app.R;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(800);
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(Splash_Screen.this, Login.class));
                }
            }
        };thread.start();

    }
}