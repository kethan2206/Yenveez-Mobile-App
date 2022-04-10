package com.example.yenveez_mobile_app.Regisration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;

public class Registration extends AppCompatActivity {

    public void Register(View view){

    }

    public void ClickToLogin(View view){
        progressBarReg.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Registration.this,Login.class));
                finish();
            }
        },1000);
    }

    EditText editText_name, editText_EmailReg, editText_passReg, editText_Phone;
    ProgressBar progressBarReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        editText_name = findViewById(R.id.editText_name);
        editText_EmailReg = findViewById(R.id.editText_EmailLog);
        editText_passReg = findViewById(R.id.editText_PasswordLog);
        editText_Phone = findViewById(R.id.editText_Phone);
        progressBarReg = findViewById(R.id.progressBarReg);
    }
}