package com.wajeba.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.wajeba.fooddelivery.R;
import com.wajeba.interfaces.ClickListener;
import com.wajeba.items.ItemRestaurant;
import com.wajeba.utils.Constant;
import com.wajeba.utils.DBHelper;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterHotelList extends RecyclerView.Adapter<AdapterHotelList.MyViewHolder> {

    private ArrayList<ItemRestaurant> arrayList;
    private ArrayList<ItemRestaurant> filteredArrayList;
    private NameFilter filter;
    private DBHelper dbHelper;
    private ClickListener clickListener;
    private Animation anim;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_title, textView_address, textView_tot_rating;
        RoundedImageView imageView;
        ImageView imageView_hotel_type, imageView_fav;
        RatingBar rating;
        LinearLayout ll_home_main;

        MyViewHolder(View view) {
            super(view);
            textView_title = view.findViewById(R.id.tv_list_name);
            textView_address = view.findViewById(R.id.tv_list_address);
            textView_tot_rating = view.findViewById(R.id.tv_latest_list_tot_rating);
            imageView = view.findViewById(R.id.iv_list);
            imageView_hotel_type = view.findViewById(R.id.iv_hotel_type);
            rating = view.findViewById(R.id.rating_list_latest);
            ll_home_main = view.findViewById(R.id.ll_main);
            imageView_fav = view.findViewById(R.id.iv_fav);
        }
    }

    public AdapterHotelList(Context context, ArrayList<ItemRestaurant> arrayList, ClickListener clickListener) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.clickListener = clickListener;
        dbHelper = new DBHelper(context);
        anim = AnimationUtils.loadAnimation(context, R.anim.scale_up_fav);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_hotel, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        if (dbHelper.checkIsFav(arrayList.get(holder.getAdapterPosition()).getId())) {
            holder.imageView_fav.setImageResource(R.mipmap.fav_hover);
        } else {
            holder.imageView_fav.setImageResource(R.mipmap.fav);
        }

        if (arrayList.get(position).getType().equals(Constant.TAG_VEG)) {
            holder.imageView_hotel_type.setImageResource(R.mipmap.veg);
        } else if (arrayList.get(position).getType().equals(Constant.TAG_NONVEG)) {
            holder.imageView_hotel_type.setImageResource(R.mipmap.nonveg);
        }

        holder.textView_title.setText(arrayList.get(position).getName());
        holder.textView_address.setText(arrayList.get(position).getAddress());
        holder.textView_tot_rating.setText("(" + arrayList.get(position).getTotalRating() + ")");
        holder.rating.setRating(arrayList.get(position).getAvgRatings());
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(R.drawable.placeholder_hotel)
                .into(holder.imageView);

        holder.imageView_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imageView_fav.startAnimation(anim);
                if (dbHelper.addtoFavourite(arrayList.get(holder.getAdapterPosition()))) {
                    holder.imageView_fav.setImageResource(R.mipmap.fav_hover);
                } else {
                    holder.imageView_fav.setImageResource(R.mipmap.fav);
                }
            }
        });

        holder.ll_home_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getID(int pos) {
        return arrayList.get(pos).getId();
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemRestaurant> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemRestaurant>) results.values;
            notifyDataSetChanged();
        }
    }
}