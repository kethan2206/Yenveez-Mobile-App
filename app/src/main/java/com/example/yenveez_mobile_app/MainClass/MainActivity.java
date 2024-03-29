package com.example.yenveez_mobile_app.MainClass;

/** Home Screen */

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.About;
import com.example.yenveez_mobile_app.Beacon.FindBeacon;
import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;
import com.example.yenveez_mobile_app.Redeem.Redeem_Activity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Beacon";
    private static final int PERMISSION_COARSE_LOCATION = 0;
    private static final int PERMISSION_FINE_LOCATION = 1;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    FirebaseDatabase firebaseDatabase;

    ProgressBar progressBarMain, progressProfilePic;
    ImageView profile_image;
    TextView profile_name,profile_email, homepageStat, profile_bio,homepageEnergyScore, homePageCoinsScore;
    public static float energyGenerated;

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

    /** onClick Logout Button */

    public void Logout(View view){

        //Alert dialogue box on pressing log out button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LoggedOut();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void FindBeacon(View view){
        startActivity(new Intent(MainActivity.this, FindBeacon.class));
        finish();
    }

    public void RedeemOnClick(View view){
        startActivity(new Intent(MainActivity.this, Redeem_Activity.class));
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mAuth = FirebaseAuth.getInstance();
        progressBarMain = (ProgressBar) findViewById(R.id.progressBarMain);
        progressProfilePic = (ProgressBar) findViewById(R.id.progressProfilePic);

        firebaseUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_name = (TextView) findViewById(R.id.profile_name);
        profile_email = (TextView) findViewById(R.id.profile_email);
        homepageStat = (TextView) findViewById(R.id.homepageStat);
        profile_bio = (TextView) findViewById(R.id.profile_bio);
        homepageEnergyScore = (TextView) findViewById(R.id.homepageEnergyScore);
        homePageCoinsScore = (TextView) findViewById(R.id.homePageCoinsScore);


        /** Bottom Navigation */
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.navHome);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navHome:
                        return true;
                    case R.id.navEditProfile:
                        startActivity(new Intent(getApplicationContext(), EditProfile.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.navMenu:
                        startActivity(new Intent(getApplicationContext(), About.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.navActivity:
                        startActivity(new Intent(getApplicationContext(),FindBeacon.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.navCoupons:
                        startActivity(new Intent(getApplicationContext(),Redeem_Activity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        //Other Functions!!

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        /** fetching user data to the profile page from database */
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    energyGenerated = Float.parseFloat(snapshot.child("EnergyGenerated").getValue().toString());
                    homepageEnergyScore.setText(Float.toString(energyGenerated));
                    homePageCoinsScore.setText(snapshot.child("RedeemCoin").getValue().toString());
                    UserData userData = snapshot.getValue(UserData.class);
                    assert userData != null;
                    profile_name.setText(userData.getUserName());
                    profile_email.setText(userData.getUserEmail());
                    profile_bio.setText(userData.getUserBio());
                    int BulbGlowTime = (int) energyGenerated / 10;
                    homepageStat.setText("Congrats " + userData.getUserName() + " !!! You have energized a 10W bulb for " + BulbGlowTime + " seconds.");
                    if (userData.getImageUrl().equals("default")){
                        profile_image.setImageResource(R.drawable.profile_default_pic);
                    } else {
                        Glide.with(getApplicationContext()).load(userData.getImageUrl()).into(profile_image);
                    }
                    progressProfilePic.setVisibility(View.GONE);
                    progressBarMain.setVisibility(View.GONE);
                } else {
                    profile_image.setImageResource(R.drawable.profile_default_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Exception error
            }
        });


    }

    /** logout method */

    private void LoggedOut(){
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
}