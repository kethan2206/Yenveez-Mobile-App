package com.example.yenveez_mobile_app.Regisration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Registration extends AppCompatActivity {

    EditText editText_name, editText_EmailReg, editText_passReg, editText_ConfirmPassReg;
    ProgressBar progressBarReg;
    FirebaseAuth mAuth; //Creating reference for Firebase Authentication
    DatabaseReference databaseReference; //Creating reference for Database

    int RedeemCoin = 0;
    float EnergyGenerated = 0;

    public static int TIME_INTERVAL = 2000;
    private long backPressed;

    @Override
    public void onBackPressed() {
        if (backPressed + TIME_INTERVAL > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        } else
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        backPressed = System.currentTimeMillis();
    }


    /** onClick Register Button */

    public void Register(View view){
        String name = editText_name.getText().toString();
        String email = editText_EmailReg.getText().toString();
        String password = editText_passReg.getText().toString();
        String confirmPass = editText_ConfirmPassReg.getText().toString();

        /** String expression for verifying the pattern of an email */

        String Expn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        /** Checking all conditions for all required field */

        if(name.isEmpty()){
            editText_name.setError("Please provide a Name");
            editText_name.requestFocus();
            return;
        } else if(email.isEmpty()){
            editText_EmailReg.setError("Please provide an Email");
            editText_EmailReg.requestFocus();
            return;
        } else if (password.isEmpty()){
            editText_passReg.setError("Please provide a Password");
            editText_passReg.requestFocus();
            return;
        } else if (password.length() < 8){
            editText_passReg.setError("Minimum Password length should be 8 character");
            editText_passReg.requestFocus();
            return;
        } else if(confirmPass.isEmpty()){
            editText_ConfirmPassReg.setError("Please confirm your password");
            editText_ConfirmPassReg.requestFocus();
            return;
        } else if (!email.matches(Expn)){
            editText_EmailReg.setError("Provide a valid Email");
            editText_EmailReg.requestFocus();
            return;
        } else if (!confirmPass.equals(password)){
            editText_ConfirmPassReg.setError("Password is not matched");
            editText_ConfirmPassReg.requestFocus();
        }
        else {
            CreateUser(name,email,password);
        }
    }

    /** onClick 'Already registered' button */

    public void ClickToLogin(View view){
        progressBarReg.setVisibility(View.VISIBLE);
        //Handler is used for making a delay of 1 sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Registration.this,Login.class));
                //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        },1000);
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_EmailReg = (EditText) findViewById(R.id.editText_EmailReg);
        editText_passReg = (EditText) findViewById(R.id.editText_passReg);
        editText_ConfirmPassReg = (EditText) findViewById(R.id.editText_ConfirmPassReg);
        progressBarReg = (ProgressBar) findViewById(R.id.progressBarReg);

        mAuth = FirebaseAuth.getInstance(); //getting the instance for Firebase Authentication
    }

    /** Function for creating new User */
    private void CreateUser(String name, String email, String password){
        progressBarReg.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    String userId = user.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference("Users") //Creating database path for storing data
                            .child(userId);
                    HashMap<String, String> hashMap = new HashMap<>(); //HashMap is used for storing the users required data
                    hashMap.put("userId",userId);
                    hashMap.put("userName",name);
                    hashMap.put("userEmail",email);
                    hashMap.put("imageUrl","default");
                    hashMap.put("EnergyGenerated",Float.toString(EnergyGenerated));
                    hashMap.put("userBio","Bio");
                    hashMap.put("RedeemCoin",Integer.toString(RedeemCoin));

                    /** setting the value of database as the hashMap to get all the data stored in the Hashmap */
                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(Registration.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Toast.makeText(Registration.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressBarReg.setVisibility(View.GONE);
                    Toast.makeText(Registration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}