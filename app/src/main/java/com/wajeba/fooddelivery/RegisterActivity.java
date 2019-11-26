package com.wajeba.fooddelivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wajeba.asyncTask.LoadRegister;
import com.wajeba.interfaces.SuccessListener;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;

import androidx.appcompat.app.AppCompatActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    EditText editText_name, editText_email, editText_pass, editText_cpass, editText_phone;
    TextView textView_signin;
    Button button_register;
    ProgressDialog progressDialog;
    Methods methods;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow(), null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.register));
        progressDialog.setCancelable(false);

        textView_signin = findViewById(R.id.tv_regis_signin);
        button_register = findViewById(R.id.button_register);
        editText_name = findViewById(R.id.et_regis_name);
        editText_email = findViewById(R.id.et_regis_email);
        editText_pass = findViewById(R.id.et_regis_password);
        editText_cpass = findViewById(R.id.et_regis_cpassword);
        editText_phone = findViewById(R.id.et_regis_phone);

        TextView tv_welcome = findViewById(R.id.tv);

        tv_welcome.setTypeface(tv_welcome.getTypeface(), Typeface.BOLD);
        textView_signin.setTypeface(textView_signin.getTypeface(), Typeface.BOLD);
        button_register.setTypeface(button_register.getTypeface(), Typeface.BOLD);

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    loadRegister();
                }
            }
        });

        textView_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private Boolean validate() {
        if (editText_name.getText().toString().trim().isEmpty()) {
            editText_name.setError(getResources().getString(R.string.enter_name));
            editText_name.requestFocus();
            return false;
        } else if (editText_email.getText().toString().trim().isEmpty()) {
            editText_email.setError(getResources().getString(R.string.enter_email));
            editText_email.requestFocus();
            return false;
        } else if (!isEmailValid(editText_email.getText().toString().trim())) {
            editText_email.setError(getString(R.string.error_invalid_email));
            editText_email.requestFocus();
            return false;
        } else if (editText_pass.getText().toString().trim().isEmpty()) {
            editText_pass.setError(getResources().getString(R.string.enter_password));
            editText_pass.requestFocus();
            return false;
        } else if (editText_cpass.getText().toString().trim().isEmpty()) {
            editText_cpass.setError(getResources().getString(R.string.enter_cpassword));
            editText_cpass.requestFocus();
            return false;
        } else if (!editText_cpass.getText().toString().equals(editText_cpass.getText().toString())) {
            editText_cpass.setError(getResources().getString(R.string.pass_nomatch));
            editText_cpass.requestFocus();
            return false;
        } else if (editText_phone.getText().toString().trim().isEmpty()) {
            editText_phone.setError(getResources().getString(R.string.enter_phone));
            editText_phone.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void loadRegister() {
        if (methods.isNetworkAvailable()) {
            LoadRegister loadRegister = new LoadRegister(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        switch (registerSuccess) {
                            case "1":
                                methods.showToast(message);
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("from", "");
                                startActivity(intent);
                                finish();
                                break;
                            case "-1":
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                break;
                            default:
                                if (message.contains("already") || message.contains("Invalid email format")) {
                                    editText_email.setError(message);
                                    editText_email.requestFocus();
                                } else {
                                    methods.showToast(message);
                                }
                                break;
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_REGISTER, 0, "", "", "", "", "", "","","","", editText_email.getText().toString(), editText_pass.getText().toString(), editText_name.getText().toString(), editText_phone.getText().toString(), "", "", "", "", null));
            loadRegister.execute();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }
    }
}