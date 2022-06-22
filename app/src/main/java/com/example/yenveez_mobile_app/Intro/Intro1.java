package com.example.yenveez_mobile_app.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;

public class Intro1 extends AppCompatActivity {

    public void OnClickIntro1(View view){
        startActivity(new Intent(this,Intro2.class));
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        finish();
    }

    public void ClickToLogin(View view){
        startActivity(new Intent(this, Login.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro1);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}