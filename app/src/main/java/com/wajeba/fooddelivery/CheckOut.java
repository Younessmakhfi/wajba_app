package com.wajeba.fooddelivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wajeba.adapter.AdapterCheckOut;
import com.wajeba.asyncTask.LoadCheckOut;
import com.wajeba.interfaces.SuccessListener;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import java.net.URLEncoder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckOut extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    ProgressDialog progressDialog;
    AppCompatButton button_checkout;
    EditText editText_address, editText_comment;
    TextView textView_total, textView_hotel_name, textView_currency;
    String comment, address, cart_ids, total, rest_name = "", from = "";
    CardView cardView_edit;
    RecyclerView recyclerView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        methods = new Methods(this);
        progressDialog = new ProgressDialog(CheckOut.this);
        progressDialog.setMessage(getString(R.string.loading));

        toolbar = findViewById(R.id.toolbar_checkout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        methods.setStatusColor(getWindow(), toolbar);

        from = getIntent().getStringExtra("from");
        rest_name = getIntent().getStringExtra("rest_name");
        cart_ids = getIntent().getStringExtra("cart_ids");
        total = getIntent().getStringExtra("total");

        cardView_edit = findViewById(R.id.cv_checkout_edit);
        editText_address = findViewById(R.id.et_checkout_address);
        editText_comment = findViewById(R.id.et_checkout_comment);
        textView_hotel_name = findViewById(R.id.tv_checkout_hotel_name);
        textView_total = findViewById(R.id.tv_checkout_total);
        textView_currency = findViewById(R.id.tv);
        button_checkout = findViewById(R.id.button_checkout);

        recyclerView = findViewById(R.id.rv_checkout);
        recyclerView.setLayoutManager(new LinearLayoutManager(CheckOut.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        AdapterCheckOut adapterCart = new AdapterCheckOut(CheckOut.this, Constant.arrayList_cart);
        recyclerView.setAdapter(adapterCart);

        textView_currency.setTypeface(null, Typeface.BOLD);
        textView_hotel_name.setText(rest_name);
        textView_hotel_name.setTypeface(textView_hotel_name.getTypeface(), Typeface.BOLD);
        textView_total.setTypeface(textView_hotel_name.getTypeface(), Typeface.BOLD);
        float amount=Float.parseFloat(total);
        amount = amount+7;
        total = Float.toString(amount);
        textView_total.setText(total);

        editText_address.setText(Constant.itemUser.getAddress());

        button_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    address = URLEncoder.encode(editText_address.getText().toString());
                    comment = URLEncoder.encode(editText_comment.getText().toString());
                    loadCheckOutApi();
                }
            }
        });

        cardView_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals("home")) {
                    Intent intent = new Intent(CheckOut.this, CartActivity.class);
                    startActivity(intent);
                }
                finish();
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

    private Boolean validate() {
        if (editText_address.getText().toString().trim().isEmpty()) {
            methods.showToast(getResources().getString(R.string.address_empty));
            return false;
        } else {
            return true;
        }
    }

    private void loadCheckOutApi() {
        if (methods.isNetworkAvailable()) {
            LoadCheckOut loadCheckOut = new LoadCheckOut(new SuccessListener() {
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
                        if (isWorkSuccess.equals("1")) {
                            Constant.isCartRefresh = true;
                            Constant.menuCount = 0;
                            Constant.arrayList_cart.clear();
                            Constant.isFromCheckOut = true;
                            openOrderSuccessDialog();
                        } else {
                            openErrorDialog(getString(R.string.error_order));
                        }
                        methods.showToast(message);
                    } else {
                        methods.showToast(getString(R.string.error_server_conneting));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_ORDER_CHECKOUT, 0, "", cart_ids, "", "", "", "", "", "", "", "", "", "", "", "", address, Constant.itemUser.getId(), comment, null));
            loadCheckOut.execute();
        } else {
            openErrorDialog(getString(R.string.error_net_not_conn));
        }
    }

    private void openOrderSuccessDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_pay_suc, null);
        dialogBuilder.setView(dialogView);

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.scale_up);
        anim.setInterpolator(new OvershootInterpolator());

        ImageView imageView = dialogView.findViewById(R.id.iv_pay_suc);
        Button button_close = dialogView.findViewById(R.id.button_close);
        imageView.startAnimation(anim);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.show();

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().getFragments().clear();
                Intent intent = new Intent(CheckOut.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void openErrorDialog(String message) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_dialog_pay_suc, null);
        dialogBuilder.setView(dialogView);

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.scale_up);
        anim.setInterpolator(new OvershootInterpolator());

        ImageView imageView = dialogView.findViewById(R.id.iv_pay_suc);
        TextView textView = dialogView.findViewById(R.id.tv_dialog_suc);
        textView.setText(message);
        imageView.setImageResource(R.drawable.close);
        Button button_close = dialogView.findViewById(R.id.button_close);
        imageView.startAnimation(anim);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.show();

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}