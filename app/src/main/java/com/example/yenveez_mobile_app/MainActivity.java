package com.example.yenveez_mobile_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.Regisration.Registration;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    public void Logout(View view){
        progressBarMain.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "logged out Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        },1000);
    }

    FirebaseAuth mAuth;
    ProgressBar progressBarMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        progressBarMain = findViewById(R.id.progressBarMain);
    }
}