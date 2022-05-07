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

import com.example.yenveez_mobile_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Reset_Password extends AppCompatActivity {

    public void Reset(View view){
        String resetEmail = editText_EmailReset.getText().toString();

        /** String expression for validating input email */

        String Expn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        if (resetEmail.isEmpty()){
            editText_EmailReset.setError("Please provide an Email");
            editText_EmailReset.requestFocus();
        } else if (!resetEmail.matches(Expn)){
            editText_EmailReset.setError("Provide a valid Email");
            editText_EmailReset.requestFocus();
        }
        else {
            progressBarReset.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //pre define function for sending reset link to the input email
                    mAuth.sendPasswordResetEmail(resetEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Reset_Password.this, "Reset link has been successfully sent", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Reset_Password.this, Login.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            } else {
                                Toast.makeText(Reset_Password.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }, 1000);
        }
    }

    EditText editText_EmailReset;
    ProgressBar progressBarReset;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        editText_EmailReset = (EditText) findViewById(R.id.editText_EmailReset);
        progressBarReset = (ProgressBar) findViewById(R.id.progressBarReset);
        mAuth = FirebaseAuth.getInstance();
    }
}