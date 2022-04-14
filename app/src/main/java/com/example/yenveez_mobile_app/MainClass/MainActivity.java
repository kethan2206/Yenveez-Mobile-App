package com.example.yenveez_mobile_app.MainClass;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kkmcn.kbeaconlib2.KBeaconsMgr;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_COARSE_LOCATION = 10;
    private static final int PERMISSION_FINE_LOCATION = 20;
    private static final String TAG = "Beacon";

    //onClick Logout Button
    public void Logout(View view){

        //Alert dialogue box on pressing log out button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to Log out?")
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

    //onClick Edit Profile pic button
    public void EditProfilePic(View view){
        launcher.launch("image/*");
    }

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    FirebaseDatabase firebaseDatabase;

    ProgressBar progressBarMain, progressProfilePic;
    ImageView profile_image;
    TextView profile_name;

    KBeaconsMgr mBeaconsMgr; //KBeaconsMgr instance

    ActivityResultLauncher<String> launcher; //launcher is used to open gallery

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        progressBarMain = findViewById(R.id.progressBarMain);
        progressProfilePic = findViewById(R.id.progressProfilePic);

        firebaseUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);


        //Beacon Functions on start Main Activity!!

        //Scanning Beacon in background
        mBeaconsMgr = KBeaconsMgr.sharedBeaconManager(this);
        if (mBeaconsMgr == null)
        {
            Toast.makeText(this, "Device does not have bluetooth support!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Scanning requires permission
        //for android6, the app need corse location permission for BLE scanning
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_COARSE_LOCATION);
        }
        //for android10, the app need fine location permission for BLE scanning
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }


        //Start Scanning method!!
        KBeaconsMgr.KBeaconMgrDelegate beaconMgrDeletate = null;
        mBeaconsMgr.delegate = beaconMgrDeletate;
        int nStartScan = mBeaconsMgr.startScanning();
        if (nStartScan == 0)
        {
            Log.v(TAG, "start scan success");
        }
        else if (nStartScan == KBeaconsMgr.SCAN_ERROR_BLE_NOT_ENABLE) {
            Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
        }
        else if (nStartScan == KBeaconsMgr.SCAN_ERROR_NO_PERMISSION) {
            Toast.makeText(this, "Bluetooth scanning has no location permission", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Bluetooth scanning error!", Toast.LENGTH_SHORT).show();
        }


        //Other Functions!!

        //function for opening gallery on clicking edit button
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                profile_image.setImageURI(result);
                progressBarMain.setVisibility(View.VISIBLE);

                String userId = mAuth.getCurrentUser().getUid();

                final StorageReference storageReference = storage.getReference() //Storing the image in the firebase storage
                        .child("ProfilePic").child(userId);

                storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        progressBarMain.setVisibility(View.GONE);

                        //Copying the image link from firebase storage to real time database
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                firebaseDatabase.getInstance().getReference().child("Users").child(userId)
                                        .child("imageUrl").setValue(result.toString());
                            }
                        });
                    }
                });
            }
        });

        progressProfilePic.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        //fetching user data to the profile page from database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserData userData = snapshot.getValue(UserData.class);
                    assert userData != null;
                    profile_name.setText(userData.getUserName());
                    if (userData.getImageUrl().equals("default")){
                        profile_image.setImageResource(R.drawable.profile_default_pic);
                    } else {
                        Glide.with(getApplicationContext()).load(userData.getImageUrl()).into(profile_image);
                    }
                    progressProfilePic.setVisibility(View.INVISIBLE);
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

    //logout method
    private void LoggedOut(){
        progressBarMain.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth.signOut();
                Toast.makeText(MainActivity.this, "logged out Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, Login.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        },1000);
    }
}