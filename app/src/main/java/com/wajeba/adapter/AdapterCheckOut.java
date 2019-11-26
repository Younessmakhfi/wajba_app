package com.wajeba.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wajeba.fooddelivery.R;
import com.wajeba.items.ItemCart;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterCheckOut extends RecyclerView.Adapter<AdapterCheckOut.MyViewHolder> {

    private Context context;
    private ArrayList<ItemCart> arrayList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_title, textView_quantity, textView_price, tv_currency;

        MyViewHolder(View view) {
            super(view);
            textView_title = view.findViewById(R.id.tv_checkout_menu_name);
            textView_quantity = view.findViewById(R.id.tv_checkout_menu_qty);
            textView_price = view.findViewById(R.id.tv_checkout_menu_price);
            tv_currency = view.findViewById(R.id.tv);
            tv_currency.setTypeface(null, Typeface.BOLD);
        }
    }

    public AdapterCheckOut(Context context, ArrayList<ItemCart> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_checkout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        float tot = Float.parseFloat(arrayList.get(position).getMenuPrice()) * Integer.parseInt(arrayList.get(position).getMenuQty());

        holder.textView_title.setText(arrayList.get(position).getMenuName());
        holder.textView_quantity.setText(context.getString(R.string.x) + arrayList.get(position).getMemuTempQty());
        holder.textView_price.setText(String.valueOf(tot));
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