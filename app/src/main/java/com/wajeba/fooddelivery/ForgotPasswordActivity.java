package com.wajeba.fooddelivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wajeba.asyncTask.LoadForgotPass;
import com.wajeba.interfaces.SuccessListener;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgotPasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    Button button_send;
    EditText editText_email;
    ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        toolbar = findViewById(R.id.toolbar_forgostpass);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow(), toolbar);

        toolbar.setTitle(getString(R.string.forgot_password));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        TextView tv = findViewById(R.id.tv);
        TextView tv1 = findViewById(R.id.tv1);
        button_send = findViewById(R.id.button_forgot_send);
        editText_email = findViewById(R.id.et_forgot_email);

        tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
        tv1.setTypeface(tv1.getTypeface(), Typeface.BOLD);
        button_send.setTypeface(button_send.getTypeface(), Typeface.BOLD);

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (methods.isNetworkAvailable()) {
                    if (!editText_email.getText().toString().trim().isEmpty()) {
                        loadForgotPass(editText_email.getText().toString());
                    } else {
                        methods.showToast(getString(R.string.enter_email));
                    }
                } else {
                    methods.showToast(getString(R.string.error_net_not_conn));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void loadForgotPass(String email) {
        LoadForgotPass loadForgotPass = new LoadForgotPass(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                progressDialog.dismiss();
                if (success.equals("1")) {
                    methods.showToast(message);
                } else {
                    methods.showToast(getString(R.string.error_server));
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_FORGOT_PASSWORD, 0, "", "", "", "", "", "", "", "", "", email, "", "", "", "", "", "", "", null));
        loadForgotPass.execute();
    }
}