package com.example.yenveez_mobile_app.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;
import com.example.yenveez_mobile_app.Regisration.Registration;

public class Intro3 extends AppCompatActivity {

    public void ClickToLogin(View view){
        startActivity(new Intent(this, Login.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro3);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}