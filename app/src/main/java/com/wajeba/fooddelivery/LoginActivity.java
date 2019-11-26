package com.wajeba.fooddelivery;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wajeba.asyncTask.LoadLogin;
import com.wajeba.interfaces.LoginListener;
import com.wajeba.items.ItemUser;
import com.wajeba.utils.Constant;
import com.wajeba.utils.Methods;
import com.wajeba.utils.SharedPref;

import androidx.appcompat.app.AppCompatActivity;
import cn.refactor.library.SmoothCheckBox;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    SharedPref sharedPref;
    EditText et_email, et_password;
    Button button_login, button_skip;
    TextView textView_register, textView_forgotpass;
    Methods methods;
    ProgressDialog progressDialog;
    LinearLayout ll_checkbox;
    SmoothCheckBox cb_rememberme;

    private String from = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow(), null);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.login_in));
        progressDialog.setCancelable(false);

        ll_checkbox = findViewById(R.id.ll_checkbox);
        cb_rememberme = findViewById(R.id.cb_rememberme);
        et_email = findViewById(R.id.et_login_email);
        et_password = findViewById(R.id.et_login_password);
        button_login = findViewById(R.id.button_login);
        button_skip = findViewById(R.id.button_skip);
        textView_register = findViewById(R.id.tv_login_signup);
        textView_forgotpass = findViewById(R.id.tv_forgotpass);

        TextView tv_welcome = findViewById(R.id.tv);

        tv_welcome.setTypeface(tv_welcome.getTypeface(), Typeface.BOLD);
        textView_forgotpass.setTypeface(textView_forgotpass.getTypeface(), Typeface.BOLD);
        textView_register.setTypeface(textView_register.getTypeface(), Typeface.BOLD);
        button_login.setTypeface(button_login.getTypeface(), Typeface.BOLD);
        button_skip.setTypeface(button_skip.getTypeface(), Typeface.BOLD);

        ll_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_rememberme.setChecked(!cb_rememberme.isChecked(), true);
            }
        });

        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });

        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        textView_forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (methods.isNetworkAvailable()) {
                    attemptLogin();
                } else {
                    methods.showToast(getString(R.string.error_net_not_conn));
                }
            }
        });

        if (sharedPref.isRemember()) {
            et_email.setText(sharedPref.getEmail());
            et_password.setText(sharedPref.getPassword());
        }
    }

    private void attemptLogin() {
        et_email.setError(null);
        et_password.setError(null);

        // Store values at the time of the login attempt.
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !methods.isPasswordValid(password)) {
            et_password.setError(getString(R.string.error_invalid_password));
            focusView = et_password;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            et_email.setError(getString(R.string.error_field_required));
            focusView = et_email;
            cancel = true;
        } else if (!methods.isEmailValid(email)) {
            et_email.setError(getString(R.string.error_invalid_email));
            focusView = et_email;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            loadLogin();
        }
    }

    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void loadLogin() {
        if (methods.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Constant.itemUser = new ItemUser(user_id, user_name, et_email.getText().toString(), "", "", "");
                            if (cb_rememberme.isChecked()) {
                                sharedPref.setLoginDetails(Constant.itemUser, cb_rememberme.isChecked(), et_password.getText().toString());
                            } else {
                                sharedPref.setRemeber(false);
                            }
                            sharedPref.setIsAutoLogin(true);
                            Constant.isLogged = true;
                            methods.showToast(getString(R.string.login_success));

                            if (from.equals("app")) {
                                finish();
                            } else {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            methods.showToast(message);
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_LOGIN, 0, "", "", "", "", "", "", "", "", "", et_email.getText().toString(), et_password.getText().toString(), "", "", "", "", "", "", null));
            loadLogin.execute();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }
    }
}