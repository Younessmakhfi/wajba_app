package com.wajeba.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wajeba.asyncTask.LoadAddMenu;
import com.wajeba.asyncTask.LoadDeleteMenu;
import com.wajeba.fooddelivery.CartActivity;
import com.wajeba.fooddelivery.R;
import com.wajeba.interfaces.SuccessListener;
import com.wajeba.items.ItemCart;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterCart extends RecyclerView.Adapter<AdapterCart.MyViewHolder> {

    private Context context;
    private ArrayList<ItemCart> arrayList;
    private ProgressDialog progressDialog;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_title, textView_quantity, textView_price, textView_minus, textView_plus, tv_currency;

        MyViewHolder(View view) {
            super(view);
            textView_title = view.findViewById(R.id.tv_cart_menu_name);
            textView_quantity = view.findViewById(R.id.tv_menu_qty);
            textView_price = view.findViewById(R.id.tv_cart_price);
            textView_minus = view.findViewById(R.id.tv_cart_minus);
            textView_plus = view.findViewById(R.id.tv_cart_plus);
            tv_currency = view.findViewById(R.id.tv);

            tv_currency.setTypeface(null, Typeface.BOLD);
        }
    }

    public AdapterCart(Context context, ArrayList<ItemCart> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        methods = new Methods(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.loading));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cart, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        float tot = Float.parseFloat(arrayList.get(position).getMenuPrice()) * Integer.parseInt(arrayList.get(position).getMenuQty());

        holder.textView_title.setText(arrayList.get(position).getMenuName());
        holder.textView_quantity.setText(arrayList.get(position).getMemuTempQty());
        holder.textView_price.setText(String.valueOf(tot));

        holder.textView_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(holder.textView_quantity.getText().toString());
                if (count > 1) {
                    count = count - 1;
                    holder.textView_quantity.setText(String.valueOf(count));
                    arrayList.get(holder.getAdapterPosition()).setMemuTempQty(String.valueOf(count));
                    loadAddMenu(holder.getAdapterPosition());
                } else if (count == 1) {
                    openDeleteDialog(holder.getAdapterPosition());
                }
            }
        });

        holder.textView_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = Integer.parseInt(holder.textView_quantity.getText().toString());
                if (count < 30) {
                    count = count + 1;
                    holder.textView_quantity.setText(String.valueOf(count));
                    arrayList.get(holder.getAdapterPosition()).setMemuTempQty(String.valueOf(count));
                    loadAddMenu(holder.getAdapterPosition());
                } else {
                    methods.showToast(context.getString(R.string.max_quantity_reached));
                }
            }
        });
    }

    private void openDeleteDialog(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ThemeDialog);
        builder.setTitle(arrayList.get(pos).getMenuName());
        builder.setMessage(context.getString(R.string.remove_menu_from_cart));
        builder.setPositiveButton(context.getString(R.string.remove), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMenu(pos);
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void loadAddMenu(final int pos) {
        if (methods.isNetworkAvailable()) {
            LoadAddMenu loadAddMenu = new LoadAddMenu(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String isWorkSuccess, String message) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (success.equals("1")) {
                        methods.showToast(message);
                    } else {
                        methods.showToast(context.getString(R.string.error_server_conneting));
                    }

                    arrayList.get(pos).setMenuQty(arrayList.get(pos).getMemuTempQty());
                    notifyItemChanged(pos);

                    changeTotal();
                }
            }, methods.getAPIRequest(Constant.METHOD_CART_ADD_ITEM, 0, arrayList.get(pos).getRestId(), "", "", arrayList.get(pos).getMenuId(), "", "", "", arrayList.get(pos).getMenuPrice(), arrayList.get(pos).getMemuTempQty(), "", "", arrayList.get(pos).getMenuName(), "", "", "", Constant.itemUser.getId(), "", null));
            loadAddMenu.execute();
        } else {
            methods.showToast(context.getString(R.string.error_net_not_conn));
        }
    }

    private void deleteMenu(final int pos) {
        if (methods.isNetworkAvailable()) {
            LoadDeleteMenu loadDeleteMenu = new LoadDeleteMenu(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String isWorkSucces, String message) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }

                    if (success.equals("1")) {
                        if (isWorkSucces.equals("1")) {
                            arrayList.remove(pos);
                            notifyDataSetChanged();
                            Constant.menuCount = Constant.menuCount - 1;
                            changeTotal();
                        }
                        methods.showToast(message);
                    } else {
                        methods.showToast(context.getString(R.string.error_server_conneting));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_CART_DELETE_ITEM, 0, "", arrayList.get(pos).getId(), "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadDeleteMenu.execute();
        } else {
            methods.showToast(context.getString(R.string.error_net_not_conn));
        }
    }

    private void changeTotal() {
        float total = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            total = total + (Float.parseFloat(arrayList.get(i).getMenuPrice()) * Float.parseFloat(arrayList.get(i).getMenuQty()));
        }
        ((CartActivity)context).textView_total.setText(String.valueOf(total));
        if (arrayList.size() == 0) {
            ((CartActivity) context).hideView();
        }
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