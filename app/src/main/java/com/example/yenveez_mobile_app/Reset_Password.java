package com.example.yenveez_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.Regisration.Registration;

public class Reset_Password extends AppCompatActivity {

    public void Reset(View view){
        progressBarReset.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Reset_Password.this, Login.class));
                finish();
            }
        },1000);
    }

    EditText editText_EmailReset;
    ProgressBar progressBarReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editText_EmailReset = findViewById(R.id.editText_EmailReset);
        progressBarReset = findViewById(R.id.progressBarReset);
    }
}