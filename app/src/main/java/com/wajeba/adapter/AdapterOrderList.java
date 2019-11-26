package com.wajeba.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wajeba.asyncTask.LoadOrderCancel;
import com.wajeba.fooddelivery.OrderDetailsActivity;
import com.wajeba.fooddelivery.R;
import com.wajeba.interfaces.SuccessListener;
import com.wajeba.items.ItemOrderList;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterOrderList extends RecyclerView.Adapter<AdapterOrderList.MyViewHolder> {

    private Context context;
    private Methods methods;
    private ArrayList<ItemOrderList> arrayList;
    private ArrayList<ItemOrderList> filteredArrayList;
    private NameFilter filter;
    private SimpleDateFormat dateFormat;
    private ProgressDialog progressDialog;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView_hotename, textView_unique_id, textView_qty, textView_totalprice, textView_date, textView_address,
                textView_status, textView_time, textView_currency, textView_cancel;
        private LinearLayout ll;

        MyViewHolder(View view) {
            super(view);
            textView_hotename = view.findViewById(R.id.tv_orderlist_hotelname);
            textView_unique_id = view.findViewById(R.id.tv_orderlist_uniqueid);
            textView_qty = view.findViewById(R.id.tv_orderlist_quantity);
            textView_totalprice = view.findViewById(R.id.tv_orderlist_total);
            textView_date = view.findViewById(R.id.tv_orderlist_date);
            textView_time = view.findViewById(R.id.tv_orderlist_time);
            textView_address = view.findViewById(R.id.tv_orderlist_address);
            textView_status = view.findViewById(R.id.tv_orderlist_status);
            textView_currency = view.findViewById(R.id.tv);
            textView_cancel = view.findViewById(R.id.tv_orderlist_cancel);
            ll = view.findViewById(R.id.ll_orderlist);

            textView_unique_id.setTypeface(textView_unique_id.getTypeface(), Typeface.BOLD);
            textView_totalprice.setTypeface(textView_totalprice.getTypeface(), Typeface.BOLD);
            textView_currency.setTypeface(null, Typeface.BOLD);
        }
    }

    public AdapterOrderList(Context context, ArrayList<ItemOrderList> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        methods = new Methods(context);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.cancelling_order));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_orderlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if (position % 2 == 0) {
            holder.ll.setBackgroundColor(ContextCompat.getColor(context, R.color.info_1));
        } else {
            holder.ll.setBackgroundColor(ContextCompat.getColor(context, R.color.info_2));
        }

        holder.textView_hotename.setText(arrayList.get(position).getArrayListOrderMenu().get(0).getRestName());
        holder.textView_unique_id.setText(arrayList.get(position).getUniqueId());
        holder.textView_qty.setText(context.getString(R.string.qty) + " " + arrayList.get(position).getTotalQuantity());
        holder.textView_totalprice.setText(arrayList.get(position).getTotalBill());
        holder.textView_address.setText(arrayList.get(position).getAddress());
        holder.textView_status.setText(arrayList.get(position).getStatus());

        holder.textView_date.setText(arrayList.get(position).getDate().split(" ")[0]);
        holder.textView_time.setText(arrayList.get(position).getDate().split(" ")[1]);

        switch (arrayList.get(position).getStatus()) {

            case Constant.TAG_PENDING:
                holder.textView_status.setBackgroundResource(R.drawable.bg_round_pending);
                break;
            case Constant.TAG_PROCESS:
                holder.textView_status.setBackgroundResource(R.drawable.bg_round_processing);
                break;
            case Constant.TAG_COMPLETE:
                holder.textView_status.setBackgroundResource(R.drawable.bg_round_completed);
                break;
            case Constant.TAG_CANCEL:
                holder.textView_status.setBackgroundResource(R.drawable.bg_round_cancel);
                break;
        }

        Date d = null;
        try {
            d = dateFormat.parse(arrayList.get(holder.getAdapterPosition()).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((d.getTime() + 600000) < System.currentTimeMillis() || arrayList.get(holder.getAdapterPosition()).getStatus().equals(Constant.TAG_CANCEL) || arrayList.get(holder.getAdapterPosition()).getStatus().equals(Constant.TAG_PROCESS) || arrayList.get(holder.getAdapterPosition()).getStatus().equals(Constant.TAG_COMPLETE)) {
            holder.textView_cancel.setVisibility(View.GONE);
        }

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetailsActivity.class);
                Constant.itemOrderList = arrayList.get(holder.getAdapterPosition());
                context.startActivity(intent);
            }
        });

        holder.textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCancelDialog(position);
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

    private void loadCancelOrder(final int pos) {
        if (methods.isNetworkAvailable()) {
            LoadOrderCancel loadOrderCancel = new LoadOrderCancel(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String isWorkSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (isWorkSuccess.equals("1")) {
                            arrayList.get(pos).setStatus(Constant.TAG_CANCEL);
                            notifyItemChanged(pos);
                        }
                        methods.showToast(message);
                    } else {
                        methods.showToast(context.getString(R.string.error_order_cancel));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_REST_LIST, 0, "", arrayList.get(pos).getUniqueId(), "", "", "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
            loadOrderCancel.execute();
        } else {
            methods.showToast(context.getString(R.string.error_net_not_conn));
        }
    }

    private void openCancelDialog(final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.ThemeDialog);
        builder.setMessage(context.getString(R.string.sure_cancel_order));
        builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadCancelOrder(pos);
            }
        });
        builder.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
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

            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemOrderList> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String uniqueId = filteredArrayList.get(i).getUniqueId();
//                    String date = filteredArrayList.get(i).getDate();
//                    String hotel = "";
//                    if (i < filteredArrayList.size() && filteredArrayList.get(i).getArrayListOrderMenu().size() > 0) {
//                        hotel = filteredArrayList.get(i).getArrayListOrderMenu().get(0).getRestName();
//                    }
                    if (uniqueId.contains(constraint))
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

            arrayList = (ArrayList<ItemOrderList>) results.values;
            notifyDataSetChanged();
        }
    }
}