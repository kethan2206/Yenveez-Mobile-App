package com.example.yenveez_mobile_app.Redeem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yenveez_mobile_app.R;

import java.util.List;

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

        String imageUri = redeemData.getRedeemItemIconUrl();
        Glide.with(context.getApplicationContext()).load(imageUri).into(holder.redeemItemPic);
    }

    @Override
    public int getItemCount() {
        return redeemDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView redeemItemTitle, redeemItemDescription;
        ImageView redeemItemPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            redeemItemTitle = (TextView) itemView.findViewById(R.id.redeemItemTitle);
            redeemItemDescription = (TextView) itemView.findViewById(R.id.redeemItemDescription);
            redeemItemPic = (ImageView) itemView.findViewById(R.id.redeemItemPic);
        }
    }
}
