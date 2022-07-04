package com.example.yenveez_mobile_app.Redeem;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yenveez_mobile_app.About;
import com.example.yenveez_mobile_app.Beacon.FindBeacon;
import com.example.yenveez_mobile_app.EditProfile;
import com.example.yenveez_mobile_app.MainClass.MainActivity;
import com.example.yenveez_mobile_app.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Redeem_Activity extends AppCompatActivity {

    RecyclerView redeemItemRecyclerView;
    DatabaseReference databaseReference;
    RedeemAdapter redeemAdapter;
    List<RedeemData> redeemDataList;
    ProgressBar progressBarRedeem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        redeemDataList = new ArrayList<RedeemData>();
        redeemAdapter = new RedeemAdapter(this,redeemDataList);

        redeemItemRecyclerView = findViewById(R.id.redeemItemRecyclerView);
        redeemItemRecyclerView.setHasFixedSize(true);
        redeemItemRecyclerView.setAdapter(redeemAdapter);
        progressBarRedeem = (ProgressBar) findViewById(R.id.progressBarRedeem);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        redeemItemRecyclerView.setLayoutManager(gridLayoutManager);

        /** Bottom Navigation */
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.navCoupons);

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
                        startActivity(new Intent(getApplicationContext(), FindBeacon.class));
                        overridePendingTransition(0,0);
                        finish();
                        return true;
                    case R.id.navCoupons:
                        return true;
                }
                return false;
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Redeem Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                redeemDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    redeemDataList.add(dataSnapshot.getValue(RedeemData.class));
                    progressBarRedeem.setVisibility(View.INVISIBLE);
                }
                redeemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}