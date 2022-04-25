package com.example.yenveez_mobile_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.MainClass.MainActivity;
import com.example.yenveez_mobile_app.MainClass.UserData;
import com.example.yenveez_mobile_app.Splash.Splash_Screen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkmcn.kbeaconlib2.KBeacon;
import com.kkmcn.kbeaconlib2.KBeaconsMgr;

public class FindBeacon extends AppCompatActivity implements KBeaconsMgr.KBeaconMgrDelegate, SensorEventListener {

    TextView beaconScanStatusText,stepsCount;
    ImageView profile_image_small;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    ProgressBar progressBarBeacon;
    DatabaseReference databaseReference;

    SensorManager sensorManager; //Used for accessing acceleration sensor
    Sensor mSensor;
    boolean isCounterSensorPresent;

    private KBeaconsMgr mBeaconsMgr;

    //onClick profile
    public void ProfilePage(View view){
        progressBarBeacon.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(FindBeacon.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                progressBarBeacon.setVisibility(View.GONE);
            }
        },900);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beacon);

        //Accessing permission for Physical Sensor of the device
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, Sensor.TYPE_STEP_COUNTER);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //used for keeping the screen awake

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        profile_image_small = (ImageView) findViewById(R.id.profile_image_small);
        progressBarBeacon = (ProgressBar) findViewById(R.id.progressBarBeacon);
        stepsCount = (TextView) findViewById(R.id.stepsCount);

        //setting the Profile icon
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        //fetching user profile pic to the profile page from database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserData userData = snapshot.getValue(UserData.class);
                    assert userData != null;
                    if (userData.getImageUrl().equals("default")){
                        profile_image_small.setImageResource(R.drawable.profile_default_pic);
                    } else {
                        Glide.with(getApplicationContext()).load(userData.getImageUrl()).into(profile_image_small);
                    }
                } else {
                    profile_image_small.setImageResource(R.drawable.profile_default_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Exception error
            }
        });


        //Getting footStep
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            Toast.makeText(this, "Device does not support any counter Sensor", Toast.LENGTH_SHORT).show();
            isCounterSensorPresent = false;
        }


        //Checking Beacon
        mBeaconsMgr = KBeaconsMgr.sharedBeaconManager(this);
        if (mBeaconsMgr == null)
        {
            Toast.makeText(this, "Make sure the phone has support ble function", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mBeaconsMgr.delegate = this;
        mBeaconsMgr.setScanMinRssiFilter(-40);
        mBeaconsMgr.setScanMode(KBeaconsMgr.SCAN_MODE_LOW_LATENCY);


        //Defining references
        beaconScanStatusText = findViewById(R.id.BeaconScanStatusText);
    }


    //override methods for KBeaconsMgr.KBeaconMgrDelegate interface
    @Override
    public void onBeaconDiscovered(KBeacon[] beacons) {

    }

    @Override
    public void onCentralBleStateChang(int nNewState) {

    }

    @Override
    public void onScanFailed(int errorCode) {

    }


    //Override method for SensorEventListener interface

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        stepsCount.setText(String.valueOf(sensorEvent.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCounterSensorPresent){
            sensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCounterSensorPresent){
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager = null;
        mSensor = null;
    }
}