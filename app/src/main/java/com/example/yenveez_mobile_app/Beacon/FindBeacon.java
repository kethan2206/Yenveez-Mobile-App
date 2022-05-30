package com.example.yenveez_mobile_app.Beacon;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.MainClass.MainActivity;
import com.example.yenveez_mobile_app.MainClass.UserData;
import com.example.yenveez_mobile_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketBase;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketIBeacon;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBeacon;
import com.kkmcn.kbeaconlib2.KBeaconsMgr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class FindBeacon extends AppCompatActivity implements KBeaconsMgr.KBeaconMgrDelegate, SensorEventListener {

    private static final String TAG = "Beacon";
    public static final  int REQUEST_ENABLE_BT = 1;
    TextView beaconScanStatusText,stepsCount, rssiText, energyTextView;
    ImageView profile_image_small;
    ProgressBar progressBarBeacon,progressBarBeaconScan;
    Button stop;

    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;

    DatabaseReference databaseReferenceProfilePic, databaseReferenceUuid;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;

    SensorManager sensorManager; //Used for accessing acceleration sensor
    Sensor mSensor;
    boolean isCounterSensorPresent;
    public static float mStepCounterAndroid = 0;
    public static float mInitialStepCount = 0;
    public static float energyGenerated = 0;

    private KBeaconsMgr mBeaconsMgr;
    private int mScanFailedContinueNum = 0;
    private final static int  MAX_ERROR_SCAN_NUMBER = 2;
    int beaconRssi;

    public final ArrayList<String> UuidList = new ArrayList<>();


    /**onClick profile*/

    public void ProfilePage(View view){
        progressBarBeacon.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(FindBeacon.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                progressBarBeacon.setVisibility(View.GONE);
                finish();
            }
        },900);
    }


    /**stop button for testing*/

    public void Stop(View view){
        sensorManager.unregisterListener(this,mSensor);
        mStepCounterAndroid = 0;
        mInitialStepCount = 0;
        stepsCount.setText(String.valueOf(mStepCounterAndroid - mInitialStepCount));
        energyGenerated = (mStepCounterAndroid - mInitialStepCount) * 5;
        energyTextView.setText(String.valueOf(energyGenerated));
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beacon);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //used for keeping the screen awake

        mAuth = (FirebaseAuth) FirebaseAuth.getInstance();
        firebaseUser = (FirebaseUser) mAuth.getCurrentUser();

        bluetoothManager = (BluetoothManager) getSystemService(BluetoothManager.class);
        bluetoothAdapter = (BluetoothAdapter) bluetoothManager.getAdapter();

        stop = (Button) findViewById(R.id.stop);

        profile_image_small = (ImageView) findViewById(R.id.profile_image_small);
        progressBarBeacon = (ProgressBar) findViewById(R.id.progressBarBeacon);
        progressBarBeaconScan = (ProgressBar) findViewById(R.id.progressBarBeaconScan);
        stepsCount = (TextView) findViewById(R.id.stepsCount);
        beaconScanStatusText = (TextView) findViewById(R.id.BeaconScanStatusText);
        rssiText = (TextView) findViewById(R.id.rssiText);
        energyTextView = (TextView) findViewById(R.id.energyTextView);


        /**Enabling the bluetooth if it is not enable*/

        if (!bluetoothAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        /**Accessing permission for Physical Sensor of the device*/

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, Sensor.TYPE_STEP_COUNTER);
        }


        databaseReferenceProfilePic = (DatabaseReference) FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        /**fetching user profile pic to the profile page from database*/

        databaseReferenceProfilePic.addValueEventListener(new ValueEventListener() {
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

        /** Retrieve all the UUID in a Array List */
        databaseReferenceUuid = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Malls");
        databaseReferenceUuid.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UuidList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    UuidList.add(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /**Getting footStep*/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorPresent = true;
        } else {
            Toast.makeText(this, "Device does not support any Accelerometer Sensor", Toast.LENGTH_SHORT).show();
            isCounterSensorPresent = false;
        }


        /**Checking Location enable or not*/

        mBeaconsMgr = KBeaconsMgr.sharedBeaconManager(this);
        if (mBeaconsMgr == null)
        {
            Toast.makeText(this, "Make sure that the phone has bluetooth support", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mBeaconsMgr.delegate = this;
        mBeaconsMgr.setScanMinRssiFilter(-80);
        mBeaconsMgr.setScanMode(KBeaconsMgr.SCAN_MODE_LOW_LATENCY);
        mBeaconsMgr.setScanAdvTypeFilter(KBAdvType.EddyTLM | KBAdvType.Sensor | KBAdvType.IBeacon);

        /**start scanning the beacon*/
        ScanBeacon();

    }

    /** Start Pedometer function */
    public void StartPedometer(){
        sensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    /** Stop Pedometer function */
    public void StopPedometer(){
        sensorManager.unregisterListener(this, mSensor);
        mStepCounterAndroid = 0;
        mInitialStepCount = 0;
        stepsCount.setText(String.valueOf(mStepCounterAndroid - mInitialStepCount));
        energyGenerated = (mStepCounterAndroid - mInitialStepCount) * 5;
        energyTextView.setText(String.valueOf(energyGenerated));
    }

    /**Beacon Scan function*/
    public void ScanBeacon(){
        int nStartScan = mBeaconsMgr.startScanning();
        if (nStartScan == 0)
        {
            Log.v(TAG, "start scan success");
        }
        else if (nStartScan == KBeaconsMgr.SCAN_ERROR_BLE_NOT_ENABLE) {
            Toast.makeText(this, "Bluetooth function is not enable", Toast.LENGTH_SHORT).show();
        }
        else if (nStartScan == KBeaconsMgr.SCAN_ERROR_NO_PERMISSION) {
            Toast.makeText(this, "Bluetooth scanning has no location permission", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Bluetooth scanning unknown error", Toast.LENGTH_SHORT).show();
        }
    }


    /** override methods for KBeaconsMgr.KBeaconMgrDelegate interface */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBeaconDiscovered(KBeacon[] beacons) {
        beaconScanStatusText.setText("Beacon found");
        progressBarBeaconScan.setVisibility(View.GONE);

        for (KBeacon beacon: beacons) {

            /** get beacon adv common info*/

            Log.v(TAG, "beacon mac:" + beacon.getMac());
            Log.v(TAG, "beacon name:" + beacon.getName());
            Log.v(TAG, "beacon rssi:" + beacon.getRssi());

            /** get ADV packet */
            for (KBAdvPacketBase advPacketBase : beacon.allAdvPackets()){
                switch (advPacketBase.getAdvType()){
                    case KBAdvType.IBeacon:
                        KBAdvPacketIBeacon advPacketIBeacon = (KBAdvPacketIBeacon) advPacketBase;
                        String Uuid = advPacketIBeacon.getUuid();
                        Log.v(TAG,"IBeacon UUID:" + Uuid);
                        if (UuidList.contains(Uuid)){
                            StartPedometer();
                        } else {
                            StopPedometer();
                        }
                }
            }
            //Clear all scanned packet
            beacon.removeAdvPacket();

            beaconRssi = beacon.getRssi();
            rssiText.setText(String.valueOf(beaconRssi));
        }
    }

    @Override
    public void onCentralBleStateChang(int nNewState) {
        Log.e(TAG, "centralBleStateChang：" + nNewState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onScanFailed(int errorCode) {
        Log.e(TAG, "Start N scan failed：" + errorCode);
        if (mScanFailedContinueNum >= MAX_ERROR_SCAN_NUMBER){
            Toast.makeText(this, "scan encounter error, error time:" + mScanFailedContinueNum, Toast.LENGTH_SHORT).show();
        }
        mScanFailedContinueNum++;
        beaconScanStatusText.setText("Scan failed");
        progressBarBeaconScan.setVisibility(View.GONE);
    }


    /**override methods for SensorEven Listener*/
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mInitialStepCount == 0.0){
            mInitialStepCount = sensorEvent.values[0];
        }
        mStepCounterAndroid = sensorEvent.values[0];

        stepsCount.setText(String.valueOf(mStepCounterAndroid - mInitialStepCount));
        energyGenerated = (mStepCounterAndroid - mInitialStepCount) * 5;
        energyTextView.setText(String.valueOf(energyGenerated));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    /**other*/
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}