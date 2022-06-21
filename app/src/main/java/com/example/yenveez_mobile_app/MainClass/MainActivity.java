package com.example.yenveez_mobile_app.MainClass;

/** Home Screen */

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.Beacon.FindBeacon;
import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;
import com.example.yenveez_mobile_app.Redeem.Redeem_Activity;
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
    TextView profile_name;

    ActivityResultLauncher<String> launcher; //launcher is used to open gallery

    /** onClick Logout Button */

    public void Logout(View view){

        //Alert dialogue box on pressing log out button
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("If you have logged in with Google or Facebook or Twitter then your all saved data will be deleted.\n\nDo you want to log out?")
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

    /** onClick Edit Profile pic button */

    public void EditProfilePic(View view){
        launcher.launch("image/*");
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

        //Other Functions!!

        /** function for opening gallery on clicking edit button */
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

        /** fetching user data to the profile page from database */
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

    /** logout method */

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