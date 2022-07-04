package com.example.yenveez_mobile_app.Beacon;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.yenveez_mobile_app.About;
import com.example.yenveez_mobile_app.EditProfile;
import com.example.yenveez_mobile_app.MainClass.MainActivity;
import com.example.yenveez_mobile_app.MainClass.UserData;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvType;
import com.kkmcn.kbeaconlib2.KBeacon;
import com.kkmcn.kbeaconlib2.KBeaconsMgr;

import java.util.ArrayList;
import java.util.List;

public class FindBeacon extends AppCompatActivity implements KBeaconsMgr.KBeaconMgrDelegate, SensorEventListener {

    private static final String TAG = "Beacon";
    public static final int REQUEST_ENABLE_BT = 1;
    TextView energyTextView, redeemPointsText;
    ImageView closeAdBanner, activityBulb;
    ImageSlider AdsImage;
    CardView AdsBanner;

    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;

    DatabaseReference databaseReferenceProfilePic, databaseReferenceMacId, databaseReferenceAdsBanner, databaseReference, databaseReferenceBulbShine;

    BluetoothAdapter bluetoothAdapter;

    SensorManager sensorManager; //Used for accessing acceleration sensor
    Sensor mSensor;
    boolean isCounterSensorPresent;
    public static float mStepCounterAndroid = 0;
    public static float mInitialStepCount = 0;
    public static float energyGenerated = 0;
    public static int redeemPointsGenerated = 0;

    private KBeaconsMgr mBeaconsMgr;
    private int mScanFailedContinueNum = 0;
    private final static int MAX_ERROR_SCAN_NUMBER = 2;
    int beaconRssi;
    public static final int BLUETOOTH_REQ_CODE = 1;
    public static  boolean CHECK_ALL_PERMISSION = false;
    public static boolean IS_BEACON_CONNECTED = false;

    final ArrayList<String> UuidList = new ArrayList<>();
    final ArrayList<SlideModel> AdsBannerUrlList = new ArrayList<>();

    float Energy;
    int RedeemPoints, BulbCounter;
    public static float mEnergyGenerated = 0, steps = 1;
    public static int mRedeemPointsGenerated = 0;

    NotificationManager notificationManager;
    private static final String NOTIFICATION_CHANNEL_ID = "notification_channel";
    private static final int NOTIFICATION_ID = 100;
    public static String notificationTitle = "";
    public static String notificationContent = "";
    String UserName;
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

    @SuppressLint({"SetTextI18n", "MissingPermission"})
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beacon);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //used for keeping the screen awake

        mAuth = (FirebaseAuth) FirebaseAuth.getInstance();
        firebaseUser = (FirebaseUser) mAuth.getCurrentUser();

        energyTextView = (TextView) findViewById(R.id.energyTextView);
        redeemPointsText = (TextView) findViewById(R.id.redeemPointsText);

        AdsBanner = (CardView) findViewById(R.id.AdsBanner);
        AdsImage = (ImageSlider) findViewById(R.id.AdsImage);
        closeAdBanner = (ImageView) findViewById(R.id.closeAdBanner);
        activityBulb = (ImageView) findViewById(R.id.activityBulb);

        /** Bottom Navigation */
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.navActivity);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navHome:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navEditProfile:
                        startActivity(new Intent(getApplicationContext(), EditProfile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navMenu:
                        startActivity(new Intent(getApplicationContext(), About.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navActivity:
                        return true;
                    case R.id.navCoupons:
                        startActivity(new Intent(getApplicationContext(), Redeem_Activity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        closeAdBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AdsBanner.setVisibility(View.GONE);
            }
        });

        /**Enabling the bluetooth if it is not enable*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null){
            Toast.makeText(this, "Device does not support Bluetooth function", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()){
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent,BLUETOOTH_REQ_CODE);
        }

        //Calling permissions check function
        CheckAllPermissions();


        databaseReferenceProfilePic = (DatabaseReference) FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        /** Retrieve all the UUID in a Array List */
        databaseReferenceMacId = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Malls");
        databaseReferenceMacId.addValueEventListener(new ValueEventListener() {
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

        /**  getting the value of energy and redeem points from database */
        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Energy = Float.parseFloat(snapshot.child("EnergyGenerated").getValue().toString());
                    RedeemPoints = Integer.parseInt(snapshot.child("RedeemCoin").getValue().toString());
                    UserData userData = snapshot.getValue(UserData.class);
                    assert userData != null;
                    UserName = userData.getUserName();
                    notificationTitle = "Welcome " + UserName;
                    notificationContent = "Tap to view your activity";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceBulbShine = (DatabaseReference) FirebaseDatabase.getInstance().getReference();
        databaseReferenceBulbShine.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    BulbCounter = Integer.parseInt(snapshot.child("Bulb Shine Counter").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void CreateNotification(){

        Notification notification;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this,FindBeacon.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            notification = new Notification.Builder(this)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationContent)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setOngoing(false)
                    .build();
            notificationManager.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID,"New channel",
                    NotificationManager.IMPORTANCE_HIGH));
        }
        else {
            notification = new Notification.Builder(this)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationContent)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)
                    .setOngoing(false)
                    .build();
        }

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            finish();
            startActivity(getIntent());
        } else if (resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Turn on bluetooth and restart the app to Scan Beacon", Toast.LENGTH_LONG).show();
        }
    }

    /** checking all permissions */
    public void CheckAllPermissions(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACTIVITY_RECOGNITION).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (!multiplePermissionsReport.areAllPermissionsGranted()){
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",getPackageName(),null);
                            intent.setData(uri);
                            startActivity(intent);
                        } else {
                            CHECK_ALL_PERMISSION = true;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
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
        energyGenerated = (mStepCounterAndroid - mInitialStepCount) * 5;
        redeemPointsGenerated = (int) (mStepCounterAndroid - mInitialStepCount) * 10;
        energyTextView.setText(String.valueOf(energyGenerated));
        redeemPointsText.setText(String.valueOf(redeemPointsGenerated));
    }

    /**Beacon Scan function*/
    public void ScanBeacon(){
        int nStartScan = mBeaconsMgr.startScanning();
        if (nStartScan == 0)
        {
            Log.v(TAG, "start scan success");
            System.out.println("is beacon " + IS_BEACON_CONNECTED);
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

        IS_BEACON_CONNECTED = true;

        for (KBeacon beacon: beacons) {

            /** get beacon adv common info*/

            Log.v(TAG, "beacon mac:" + beacon.getMac());
            Log.v(TAG, "beacon name:" + beacon.getName());
            Log.v(TAG, "beacon rssi:" + beacon.getRssi());

            /** Checking whether the current mac exist in database */
            if (UuidList.contains(beacon.getMac())){
                StartPedometer();

                //Collecting Ads Data from the Data Base
                databaseReferenceAdsBanner = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Ads Banner").child(beacon.getMac());
                databaseReferenceAdsBanner.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        AdsBannerUrlList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            AdsBannerUrlList.add(new SlideModel(dataSnapshot.getValue().toString(),ScaleTypes.FIT)); //Adding into the AdsBanner Url List
                        }
                        AdsImage.setImageList(AdsBannerUrlList); //setting all the Ads Image as a slider
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                StopPedometer();
                float finalEnergy = Energy + mEnergyGenerated;
                int finalPoints = RedeemPoints + mRedeemPointsGenerated;
                mEnergyGenerated = 0;
                mRedeemPointsGenerated = 0;
                BulbCounter = 0;
                databaseReferenceBulbShine.child("Bulb Shine Counter").setValue(BulbCounter);
                ///activityBulb.setImageResource(R.drawable.activity1bulbshinningprogress);

                if (finalEnergy != 0 && finalPoints != 0){
                    databaseReference.child("EnergyGenerated").setValue(finalEnergy);
                    databaseReference.child("RedeemCoin").setValue(finalPoints);
                }
            }

            beaconRssi = beacon.getRssi();
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
    }


    /**override methods for SensorEven Listener*/
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (mInitialStepCount == 0.0){
            mInitialStepCount = sensorEvent.values[0];
        }
        mStepCounterAndroid = sensorEvent.values[0];

        steps = mStepCounterAndroid - mInitialStepCount;
        System.out.println("steps" + steps);
        energyGenerated = steps * 5; //calculating energy on basis of steps
        redeemPointsGenerated = (int) steps * 10; //calculating redeem Points on basis of steps
        energyTextView.setText(String.valueOf(energyGenerated));
        redeemPointsText.setText(String.valueOf(redeemPointsGenerated));

        System.out.println("bulb" + BulbCounter);

        /** setting up the Bulb Lighting Bar */
        if (steps % 5 == 0){
            BulbCounter++;
            databaseReferenceBulbShine.child("Bulb Shine Counter").setValue(BulbCounter);
            if (BulbCounter == 1){
                activityBulb.setImageResource(R.drawable.activity1bulbshinningprogress);
            } else if (BulbCounter == 2){
                activityBulb.setImageResource(R.drawable.activitybulbshinningprogress2);
            } else if (BulbCounter == 3){
                activityBulb.setImageResource(R.drawable.activitybulbshinningprogress3);
            } else if (BulbCounter == 4){
                activityBulb.setImageResource(R.drawable.activitybulbshinningprogress4);
            } else if (BulbCounter == 5){
                activityBulb.setImageResource(R.drawable.activitybulbshinningprogress5);
            } else if (BulbCounter > 5){
                BulbCounter = 0;
                databaseReferenceBulbShine.child("Bulb Shine Counter").setValue(BulbCounter);
            }
        }

        mEnergyGenerated = (mEnergyGenerated * 0) + energyGenerated; //Calculating net energy
        mRedeemPointsGenerated = (mRedeemPointsGenerated * 0) + redeemPointsGenerated; //calculating net points

        //Showing Ad Banner
        if (steps >=0 && steps <= 2){
            AdsBanner.setVisibility(View.VISIBLE);
            CreateNotification();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    /**other*/
    @Override
    protected void onResume() {
        super.onResume();
        databaseReferenceBulbShine.child("Bulb Shine Counter").setValue(BulbCounter);
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