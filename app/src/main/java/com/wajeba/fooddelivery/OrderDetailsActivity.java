package com.wajeba.fooddelivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wajeba.adapter.AdapterOrderDetailsMenu;
import com.wajeba.asyncTask.LoadOrderCancel;
import com.wajeba.interfaces.SuccessListener;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderDetailsActivity extends AppCompatActivity {

    Methods methods;
    Toolbar toolbar;
    RecyclerView recyclerView;
    AdapterOrderDetailsMenu adapter;
    LinearLayout ll_comment;
    private SimpleDateFormat dateFormat;
    TextView textView_hotelname, textView_unqid, textView_date, textView_address, textView_comment, textView_qty,
            textView_price, textView_status, textView_time, textView_currency, textView_cancel;
    private ProgressDialog progressDialog;
    Date d;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        toolbar = findViewById(R.id.toolbar_orderdetails);
        toolbar.setTitle(Constant.itemOrderList.getUniqueId());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods = new Methods(this);
        LinearLayout ll_adView = findViewById(R.id.ll_adView_order_details);
        methods.showBannerAd(ll_adView);

        progressDialog = new ProgressDialog(OrderDetailsActivity.this);
        progressDialog.setMessage(getString(R.string.cancelling_order));

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ll_comment = findViewById(R.id.ll_comment);
        textView_hotelname = findViewById(R.id.tv_orderdetails_hotelname);
        textView_unqid = findViewById(R.id.tv_orderdetails_uniqueid);
        textView_date = findViewById(R.id.tv_orderdetails_date);
        textView_time = findViewById(R.id.tv_orderdetails_time);
        textView_address = findViewById(R.id.tv_orderdetails_address);
        textView_comment = findViewById(R.id.tv_orderdetails_comment);
        textView_qty = findViewById(R.id.tv_orderdetails_qty);
        textView_price = findViewById(R.id.tv_orderdetails_totalprice);
        textView_status = findViewById(R.id.tv_orderlist_status);
        textView_cancel = findViewById(R.id.tv_orderlist_cancel);
        textView_currency = findViewById(R.id.tv);

        textView_price.setTypeface(textView_price.getTypeface(), Typeface.BOLD);
        textView_unqid.setTypeface(textView_unqid.getTypeface(), Typeface.BOLD);
        textView_currency.setTypeface(null, Typeface.BOLD);

        try {
            d = dateFormat.parse(Constant.itemOrderList.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((d.getTime() + 600000) < System.currentTimeMillis() || Constant.itemOrderList.getStatus().equals(Constant.TAG_CANCEL) || Constant.itemOrderList.getStatus().equals(Constant.TAG_PROCESS) || Constant.itemOrderList.getStatus().equals(Constant.TAG_COMPLETE)) {
            textView_cancel.setVisibility(View.GONE);
        } else {
            textView_cancel.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager llm_latest = new LinearLayoutManager(OrderDetailsActivity.this);
        recyclerView = findViewById(R.id.rv_orderdetails_menu);
        recyclerView.setLayoutManager(llm_latest);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        if (Constant.itemOrderList.getComment().trim().isEmpty()) {
            ll_comment.setVisibility(View.GONE);
        } else {
            textView_comment.setText(Constant.itemOrderList.getComment());
        }

        textView_hotelname.setText(Constant.itemOrderList.getArrayListOrderMenu().get(0).getRestName());
        textView_unqid.setText(Constant.itemOrderList.getUniqueId());
        textView_status.setText(Constant.itemOrderList.getStatus());
        textView_address.setText(Constant.itemOrderList.getAddress());

        textView_qty.setText(getString(R.string.qty) + " " + Constant.itemOrderList.getTotalQuantity());
        textView_price.setText(Constant.itemOrderList.getTotalBill());

        textView_date.setText(Constant.itemOrderList.getDate().split(" ")[0]);
        textView_time.setText(Constant.itemOrderList.getDate().split(" ")[1]);

        adapter = new AdapterOrderDetailsMenu(OrderDetailsActivity.this, Constant.itemOrderList.getArrayListOrderMenu());
        recyclerView.setAdapter(adapter);

        switch (Constant.itemOrderList.getStatus()) {
            case Constant.TAG_PENDING:
                textView_status.setBackgroundResource(R.drawable.bg_round_pending);
                break;
            case Constant.TAG_PROCESS:
                textView_status.setBackgroundResource(R.drawable.bg_round_processing);
                break;
            case Constant.TAG_COMPLETE:
                textView_status.setBackgroundResource(R.drawable.bg_round_completed);
                break;
            case Constant.TAG_CANCEL:
                textView_status.setBackgroundResource(R.drawable.bg_round_cancel);
                break;
        }

        textView_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCancelDialog();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCancelOrder() {
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
                            Constant.isCancelOrder = true;
                            textView_cancel.setVisibility(View.GONE);
                            textView_status.setText(Constant.TAG_CANCEL);
                            textView_status.setBackgroundResource(R.drawable.bg_round_cancel);
                            Constant.itemOrderList.setStatus(Constant.TAG_CANCEL);
                            methods.showToast(message);
                        }
                        methods.showToast(message);
                    } else {
                        methods.showToast(getString(R.string.error_order_cancel));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_ORDER_CANCEL, 0, "", Constant.itemOrderList.getUniqueId(), "", "", "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
            loadOrderCancel.execute();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }
    }

    private void openCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this, R.style.ThemeDialog);
        builder.setMessage(getString(R.string.sure_cancel_order));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadCancelOrder();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        if (textView_cancel != null) {
            if ((d.getTime() + 600000) < System.currentTimeMillis() || Constant.itemOrderList.getStatus().equals(Constant.TAG_CANCEL) || Constant.itemOrderList.getStatus().equals(Constant.TAG_PROCESS) || Constant.itemOrderList.getStatus().equals(Constant.TAG_COMPLETE)) {
                textView_cancel.setVisibility(View.GONE);
            } else {
                textView_cancel.setVisibility(View.VISIBLE);
            }
        }
        super.onResume();
    }
}