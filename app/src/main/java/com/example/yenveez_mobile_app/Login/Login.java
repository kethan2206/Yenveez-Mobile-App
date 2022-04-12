package com.example.yenveez_mobile_app.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yenveez_mobile_app.MainActivity;
import com.example.yenveez_mobile_app.R;
import com.example.yenveez_mobile_app.Regisration.Registration;
import com.example.yenveez_mobile_app.Reset_Password;
import com.example.yenveez_mobile_app.Splash.Splash_Screen;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    //onClick Login Button
    public void login(View view){
        String Email = editText_EmailLog.getText().toString();
        String Password = editText_PasswordLog.getText().toString();

        //String expression for verifying the pattern of an email
        String Expn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        //Checking all conditions for email and password
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

    //onClick Forgot password button
    public void ForgotPass(View view){
        progressBarLog.setVisibility(View.VISIBLE);
        //Handler is used for making a delay of 1 sec once user click on Forgot Password button
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, Reset_Password.class));
                progressBarLog.setVisibility(View.GONE);
            }
        },1000);
    }

    //onClick Register button
    public void CreateAcc(View view){
        progressBarLog.setVisibility(View.VISIBLE);
        //Handler is used for making a delay of 1 sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, Registration.class));
                progressBarLog.setVisibility(View.GONE);
                finish();
            }
        },1000);
    }

    //onClick google Sign in Button

    EditText editText_EmailLog, editText_PasswordLog;
    ProgressBar progressBarLog;

    FirebaseAuth mAuth; //Creating reference for Firebase Authentication
    SignInButton signInButton;

    GoogleSignInClient mGoogleSignInClient;

    DatabaseReference databaseReference;

    private static final int RC_SIGN_IN = 100;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText_EmailLog = findViewById(R.id.editText_EmailLog);
        editText_PasswordLog = findViewById(R.id.editText_PasswordLog);
        progressBarLog = findViewById(R.id.progressBarLog);

        mAuth = FirebaseAuth.getInstance(); //getting the instance for Firebase Authentication

        signInButton = findViewById(R.id.signInbutton);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        //Configure Google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.signInbutton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBarLog.setVisibility(View.VISIBLE);
                SignIn();
            }
        });

    }

    //Login function
    private void UserLogin(String Email, String Password) {
        progressBarLog.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarLog.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK); //Used to kill the login activity once it go to the MainActivity
                    startActivity(intent);
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SignIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                //Google Sign in Failed
                Toast.makeText(this, "Log in failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct){
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String userId = user.getUid();
                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Login.this);
                            String name = account.getDisplayName();
                            String email = account.getEmail();
                            databaseReference = FirebaseDatabase.getInstance().getReference("Users") //Creating database path for storing data
                                    .child(userId);
                            HashMap<String, String> hashMap = new HashMap<>(); //HashMap is used for storing the users required data
                            hashMap.put("userId",userId);
                            hashMap.put("userName",name);
                            hashMap.put("userEmail",email);
                            hashMap.put("userPhone","null");
                            hashMap.put("imageUrl","default");
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressBarLog.setVisibility(View.GONE);
                                        startActivity(new Intent(Login.this,MainActivity.class));
                                        finish();
                                        progressBarLog.setVisibility(View.GONE);
                                        Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}