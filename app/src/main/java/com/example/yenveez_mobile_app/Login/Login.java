package com.example.yenveez_mobile_app.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yenveez_mobile_app.Beacon.FindBeacon;
import com.example.yenveez_mobile_app.MainClass.MainActivity;
import com.example.yenveez_mobile_app.R;
import com.example.yenveez_mobile_app.Regisration.Registration;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class Login extends AppCompatActivity {

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

    /** onClick Login Button */

    public void login(View view){
        String Email = editText_EmailLog.getText().toString();
        String Password = editText_PasswordLog.getText().toString();

        /** String expression for verifying the pattern of an email */

        String Expn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        /** Checking all conditions for email and password */

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

    /** onClick Forgot password button */

    public void ForgotPass(View view){
        progressBarLog.setVisibility(View.VISIBLE);
        //Handler is used for making a delay of 1 sec once user click on Forgot Password button
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, Reset_Password.class));
                progressBarLog.setVisibility(View.GONE);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        },1000);
    }

    /** onClick Register button */

    public void CreateAcc(View view){
        progressBarLog.setVisibility(View.VISIBLE);
        //Handler is used for making a delay of 1 sec
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Login.this, Registration.class));
                progressBarLog.setVisibility(View.GONE);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        },1000);
    }

    /** Google Button SignIn functions */

    public void GoogleButton(View view){
        progressBarLog.setVisibility(View.VISIBLE);
        SignInGoogle();
    }

    /** facebook Button SignIn functions */

    public void facebookButton(View view){
        LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile"));
    }

    /** Twitter Button SignIn functions */

    public void TwitterButton(View view){
        progressBarLog.setVisibility(View.VISIBLE);
        SignInTwitter();
    }

    EditText editText_EmailLog, editText_PasswordLog;
    ProgressBar progressBarLog;

    FirebaseAuth mAuth; //Creating reference for Firebase Authentication

    GoogleSignInClient mGoogleSignInClient;

    DatabaseReference databaseReference;

    CallbackManager callbackManager; //Facebook callBackManager

    int RedeemCoin = 0; //Veez coin


    FirebaseStorage firebaseStorage;

    ImageView hide_show_password;


    private static final int RC_SIGN_IN = 100; // Can be any integer unique to the Activity.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        editText_EmailLog = (EditText) findViewById(R.id.editText_EmailLog);
        editText_PasswordLog = (EditText) findViewById(R.id.editText_PasswordLog);
        progressBarLog = (ProgressBar) findViewById(R.id.progressBarLog);

        mAuth = FirebaseAuth.getInstance(); //getting the instance for Firebase Authentication


        /** Configure Google Sign in */

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Facebook login config
//        callbackManager = CallbackManager.Factory.create();
//        LoginManager.getInstance().registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        handleFacebookAccessToken(loginResult.getAccessToken());
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                    }
//                });
    }

    /** Login function with email */
    private void UserLogin(String Email, String Password) {
        progressBarLog.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBarLog.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Intent intent = new Intent(Login.this, FindBeacon.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK); //Used to kill the login activity once it go to the MainActivity
                    startActivity(intent);
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Toast.makeText(Login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /** Sign In method for Google */
    private void SignInGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        progressBarLog.setVisibility(View.GONE);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                progressBarLog.setVisibility(View.VISIBLE);
                fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                //Google Sign in Failed
                Toast.makeText(this, "Log in failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Initialising fireBaseAuthWithGoogle(account) */
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
                            assert account != null;
                            String name = account.getDisplayName();
                            String email = account.getEmail();
                            Uri googleProfileImage = account.getPhotoUrl();
                            databaseReference = FirebaseDatabase.getInstance().getReference("Users") //Creating database path for storing data
                                    .child(userId);
                            HashMap<String, String> hashMap = new HashMap<>(); //HashMap is used for storing the users required data
                            hashMap.put("userId",userId);
                            hashMap.put("userName",name);
                            hashMap.put("userEmail",email);
                            hashMap.put("imageUrl",googleProfileImage.toString());
                            //hashMap.put("RedeemCoin",Integer.toString(RedeemCoin));
                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        startActivity(new Intent(Login.this,FindBeacon.class));
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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


    /**handler method for facebook login*/
//    private void handleFacebookAccessToken(AccessToken token) {
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            assert user != null;
//                            String userId = user.getUid();
//                            String email = user.getEmail();
//                            String name = user.getDisplayName();
//                            String imageUrl = user.getPhotoUrl().toString();
//                            databaseReference = FirebaseDatabase.getInstance().getReference("Users") //Creating database path for storing data
//                                    .child(userId);
//                            HashMap<String, String> hashMap = new HashMap<>(); //HashMap is used for storing the users required data
//                            hashMap.put("userId",userId);
//                            hashMap.put("userName",name);
//                            hashMap.put("userEmail",email);
//                            hashMap.put("imageUrl",imageUrl);
//                            hashMap.put("RedeemCoin",Integer.toString(RedeemCoin));
//                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        startActivity(new Intent(Login.this,FindBeacon.class));
//                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                                        progressBarLog.setVisibility(View.GONE);
//                                        finish();
//                                        Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    /** Twitter SignIn */
    private void SignInTwitter(){
        OAuthProvider.Builder provider = OAuthProvider.newBuilder("twitter.com");
        // Target specific email with login hint.
        provider.addCustomParameter("lang", "fr");

        Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    assert user != null;
                                    String userId = user.getUid();
                                    String name = user.getDisplayName();
                                    String email = user.getEmail();
                                    String imageUrl = user.getPhotoUrl().toString();
                                    databaseReference = FirebaseDatabase.getInstance().getReference("Users") //Creating database path for storing data
                                            .child(userId);
                                    HashMap<String, String> hashMap = new HashMap<>(); //HashMap is used for storing the users required data
                                    hashMap.put("userId",userId);
                                    hashMap.put("userName",name);
                                    hashMap.put("userEmail",email);
                                    hashMap.put("imageUrl","default");
                                    //hashMap.put("RedeemCoin",Integer.toString(RedeemCoin));
                                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                startActivity(new Intent(Login.this,FindBeacon.class));
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                progressBarLog.setVisibility(View.GONE);
                                                finish();
                                                Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    progressBarLog.setVisibility(View.GONE);
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
        } else {
            // There's no pending result so we need to start the sign-in flow
            mAuth.startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    assert user != null;
                                    String userId = user.getUid();
                                    String name = user.getDisplayName();
                                    String imageUrl = user.getPhotoUrl().toString();
                                    String email = user.getEmail();
                                    databaseReference = FirebaseDatabase.getInstance().getReference("Users") //Creating database path for storing data
                                            .child(userId);
                                    HashMap<String, String> hashMap = new HashMap<>(); //HashMap is used for storing the users required data
                                    hashMap.put("userId",userId);
                                    hashMap.put("userName",name);
                                    hashMap.put("userEmail",email);
                                    hashMap.put("imageUrl","default");
                                    //hashMap.put("RedeemCoin",Integer.toString(RedeemCoin));
                                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                startActivity(new Intent(Login.this,FindBeacon.class));
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                progressBarLog.setVisibility(View.GONE);
                                                finish();
                                                Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    progressBarLog.setVisibility(View.GONE);
                                    Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
        }
    }
}