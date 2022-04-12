package com.example.yenveez_mobile_app.MainClass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.Login.Login;
import com.example.yenveez_mobile_app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //onClick Logout Button
    public void Logout(View view){
        LoggedOut();
    }

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    ProgressBar progressBarMain;

    ImageView profile_image;
    TextView profile_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        progressBarMain = findViewById(R.id.progressBarMain);

        firebaseUser = mAuth.getCurrentUser();

        profile_image = findViewById(R.id.profile_image);
        profile_name = findViewById(R.id.profile_name);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData userData = snapshot.getValue(UserData.class);
                assert userData!=null;
               profile_name.setText(userData.getUserName());
               if (userData.getImageUrl().equals("default")){
                   profile_image.setImageResource(R.drawable.profile_default_pic);
               } else {
                   Glide.with(getApplicationContext()).load(userData.getImageUrl()).into(profile_image);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Exception error
            }
        });
    }

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