package com.example.yenveez_mobile_app.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;

public class Intro3 extends AppCompatActivity {

    public void onClickIntro3(View view){
        startActivity(new Intent(this, Login.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro3);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}