package com.example.yenveez_mobile_app.Redeem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RedeemAdapter extends RecyclerView.Adapter<RedeemAdapter.ViewHolder> {

    Context context;
    List<RedeemData> redeemDataList;

    public RedeemAdapter(Context context, List<RedeemData> redeemDataList) {
        this.context = context;
        this.redeemDataList = redeemDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.redeem_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RedeemData redeemData = redeemDataList.get(position);
        holder.redeemItemTitle.setText(redeemData.getRedeemItemName());
        holder.redeemItemDescription.setText(redeemData.getRedeemItemDescription());
        holder.RedeemCardView.setBackgroundColor(holder.itemView.getResources().getColor(getRandomBackgroundColour(),null));

        String imageUri = redeemData.getRedeemItemIconUrl();
        Glide.with(context.getApplicationContext()).load(imageUri).into(holder.redeemItemPic);
    }

    private Integer getRandomBackgroundColour(){
        List<Integer> backgroundColour = new ArrayList<>();
        backgroundColour.add(R.color.coupon_blue);
        backgroundColour.add(R.color.coupon_green);
        backgroundColour.add(R.color.coupon_orange);
        backgroundColour.add(R.color.coupon_pink);
        backgroundColour.add(R.color.coupon_purple);
        backgroundColour.add(R.color.coupon_yellow);

        Random randomBackground = new Random();
        int number = randomBackground.nextInt(backgroundColour.size());
        return backgroundColour.get(number);
    }

    @Override
    public int getItemCount() {
        return redeemDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView redeemItemTitle, redeemItemDescription;
        ImageView redeemItemPic;
        CardView RedeemCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            redeemItemTitle = (TextView) itemView.findViewById(R.id.redeemItemTitle);
            redeemItemDescription = (TextView) itemView.findViewById(R.id.redeemItemDescription);
            redeemItemPic = (ImageView) itemView.findViewById(R.id.redeemItemPic);
            RedeemCardView = (CardView) itemView.findViewById(R.id.RedeemCardView);
        }
    }
}
