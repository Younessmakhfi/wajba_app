package com.wajeba.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.wajeba.fooddelivery.R;
import com.wajeba.items.ItemReview;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterReview extends RecyclerView.Adapter<AdapterReview.MyViewHolder> {

    private Context context;
    private ArrayList<ItemReview> arrayList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_username, textView_msg;
        private LinearLayout linearLayout;
        private RatingBar ratingBar;

        MyViewHolder(View view) {
            super(view);
            textView_username = view.findViewById(R.id.tv_reviewlist_username);
            textView_msg = view.findViewById(R.id.tv_reviewlist_msg);
            linearLayout = view.findViewById(R.id.ll_reviewlist);
            ratingBar = view.findViewById(R.id.rb_reviewlist);

        }
    }

    public AdapterReview(Context context, ArrayList<ItemReview> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_review_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        if (position % 2 == 0) {
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.menu_list_30));
        } else {
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.menu_list_10));
        }

        holder.textView_username.setText(arrayList.get(position).getUserName());
        holder.textView_msg.setText(arrayList.get(position).getMessage());
        holder.ratingBar.setRating(Float.parseFloat(arrayList.get(position).getRating()));
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}