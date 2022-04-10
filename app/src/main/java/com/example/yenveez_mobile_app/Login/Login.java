package com.example.yenveez_mobile_app.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yenveez_mobile_app.MainActivity;
import com.example.yenveez_mobile_app.R;
import com.example.yenveez_mobile_app.Regisration.Registration;
import com.example.yenveez_mobile_app.Reset_Password;
import com.example.yenveez_mobile_app.Splash.Splash_Screen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    public void login(View view){
        String Email = editText_EmailLog.getText().toString();
        String Password = editText_PasswordLog.getText().toString();

        String Expn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        if (Email.isEmpty()) {
            editText_EmailLog.setError("Please provide an Email");
            editText_EmailLog.requestFocus();
            return;
        } else if (Password.isEmpty()) {
            editText_PasswordLog.setError("Please provide a Password");
            editText_PasswordLog.requestFocus();
            return;
        } else if (Password.length() < 8) {
            editText_PasswordLog.setError("Minimum Password length should be 8 character");
            editText_PasswordLog.requestFocus();
            return;
        } else if (!Email.matches(Expn)){
            editText_EmailLog.setError("Provide a valid Email");
            editText_EmailLog.requestFocus();
            return;
        } else {
            UserLogin(Email, Password);
        }
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

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_EmailLog = findViewById(R.id.editText_EmailLog);
        editText_PasswordLog = findViewById(R.id.editText_PasswordLog);
        progressBarLog = findViewById(R.id.progressBarLog);

        mAuth = FirebaseAuth.getInstance();
    }

    private void UserLogin(String Email, String Password) {
        progressBarLog.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarLog.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}