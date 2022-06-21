package com.example.yenveez_mobile_app.Redeem;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yenveez_mobile_app.R;
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


    public void RedeemItemOnClick(View view){

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
        redeemItemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        redeemItemRecyclerView.setAdapter(redeemAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Redeem Items");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                redeemDataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    redeemDataList.add(dataSnapshot.getValue(RedeemData.class));
                }
                redeemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}