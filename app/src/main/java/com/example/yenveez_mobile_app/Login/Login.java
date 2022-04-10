package com.example.yenveez_mobile_app.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.yenveez_mobile_app.R;
import com.example.yenveez_mobile_app.Regisration.Registration;
import com.example.yenveez_mobile_app.Reset_Password;
import com.example.yenveez_mobile_app.Splash.Splash_Screen;

public class Login extends AppCompatActivity {

    public void login(View view){

    }

    public void ForgotPass(View view){
        progressBarLog.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, Reset_Password.class));
                finish();
            }
        },1000);
    }

    public void CreateAcc(View view){
        progressBarLog.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, Registration.class));
                finish();
            }
        },1000);
    }

    EditText editText_EmailLog, editText_PasswordLog;
    ProgressBar progressBarLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_EmailLog = findViewById(R.id.editText_EmailLog);
        editText_PasswordLog = findViewById(R.id.editText_PasswordLog);
        progressBarLog = findViewById(R.id.progressBarLog);

    }
}