package com.wajeba.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wajeba.fooddelivery.R;
import com.wajeba.items.ItemOrderMenu;
import com.wajeba.utils.Constant;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterOrderDetailsMenu extends RecyclerView.Adapter<AdapterOrderDetailsMenu.MyViewHolder> {

    private Context context;
    private ArrayList<ItemOrderMenu> arrayList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_title, textView_quantity, textView_price, textView_currency;
        private LinearLayout linearLayout;
        private ImageView imageView, imageView_type;

        MyViewHolder(View view) {
            super(view);
            textView_title = view.findViewById(R.id.tv_orderdetails_menu_name);
            textView_quantity = view.findViewById(R.id.tv_orderdetails_menu_qty);
            textView_price = view.findViewById(R.id.tv_orderdetails_menu_price);
            textView_currency = view.findViewById(R.id.tv);
            linearLayout = view.findViewById(R.id.ll_orderdetails_cart);
            imageView = view.findViewById(R.id.iv_orderdetails_image);
            imageView_type = view.findViewById(R.id.iv_orderdetails_type);

            textView_currency.setTypeface(null, Typeface.BOLD);
        }
    }

    public AdapterOrderDetailsMenu(Context context, ArrayList<ItemOrderMenu> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_orderdetails_menulist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.info_1));
        } else {
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.info_2));
        }

        if (arrayList.get(position).getMenuType().equals(Constant.TAG_VEG)) {
            holder.imageView_type.setImageResource(R.mipmap.veg);
        } else if (arrayList.get(position).getMenuType().equals(Constant.TAG_NONVEG)) {
            holder.imageView_type.setImageResource(R.mipmap.nonveg);
        }

        holder.textView_title.setText(arrayList.get(position).getMenuName());
        holder.textView_quantity.setText(context.getString(R.string.qty) + " " + arrayList.get(position).getMenuQty());
        holder.textView_price.setText(arrayList.get(position).getMenuPrice());

        Picasso.get()
                .load(arrayList.get(position).getMenuImage())
                .placeholder(R.drawable.placeholder_menu)
                .into(holder.imageView);
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