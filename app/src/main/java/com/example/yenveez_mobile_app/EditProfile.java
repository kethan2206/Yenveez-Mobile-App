package com.example.yenveez_mobile_app;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.Beacon.FindBeacon;
import com.example.yenveez_mobile_app.MainClass.MainActivity;
import com.example.yenveez_mobile_app.MainClass.UserData;
import com.example.yenveez_mobile_app.Redeem.Redeem_Activity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

public class EditProfile extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    FirebaseDatabase firebaseDatabase;


    ActivityResultLauncher<String> launcher; //launcher is used to open gallery
    ImageView editProfilePhoto;
    EditText editProfileEditName,editProfileEditEmail, editProfileEditBio;
    ProgressBar editProfilePicProgress;

    /** onClick Edit Profile pic button */
    public void EditProfilePic(View view){
        launcher.launch("image/*");
    }

    public void CloseEditProfile(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        editProfilePhoto = (ImageView) findViewById(R.id.editProfilePhoto);
        editProfileEditName = (EditText) findViewById(R.id.editProfileEditName);
        editProfileEditEmail = (EditText) findViewById(R.id.editProfileEditEmail);
        editProfilePicProgress = (ProgressBar) findViewById(R.id.editProfilePicProgress);

        /** Bottom Navigation */
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.navEditProfile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.navHome:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.navEditProfile:
                        return true;
                    case R.id.navMenu:
                        startActivity(new Intent(getApplicationContext(), About.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.navActivity:
                        startActivity(new Intent(getApplicationContext(), FindBeacon.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.navCoupons:
                        startActivity(new Intent(getApplicationContext(), Redeem_Activity.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                }
                return false;
            }
        });

        /** function for opening gallery on clicking edit button */
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                editProfilePhoto.setImageURI(result);

                String userId = mAuth.getCurrentUser().getUid();

                final StorageReference storageReference = storage.getReference() //Storing the image in the firebase storage
                        .child("ProfilePic").child(userId);

                storageReference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(EditProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();

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

        editProfilePicProgress.setVisibility(View.VISIBLE);
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        /** fetching user data to the profile page from database */
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserData userData = snapshot.getValue(UserData.class);
                    assert userData != null;
                    editProfileEditName.setText(userData.getUserName());
                    editProfileEditEmail.setText(userData.getUserEmail());
                    if (userData.getImageUrl().equals("default")){
                        editProfilePhoto.setImageResource(R.drawable.profile_default_pic);
                    } else {
                        Glide.with(getApplicationContext()).load(userData.getImageUrl()).into(editProfilePhoto);
                    }
                    editProfilePicProgress.setVisibility(View.INVISIBLE);
                } else {
                    editProfilePhoto.setImageResource(R.drawable.profile_default_pic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Exception error
            }
        });
    }
}