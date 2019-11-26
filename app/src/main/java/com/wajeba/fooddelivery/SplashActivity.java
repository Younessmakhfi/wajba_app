package com.wajeba.fooddelivery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.wajeba.asyncTask.LoadAbout;
import com.wajeba.asyncTask.LoadLogin;
import com.wajeba.helper.LocalHelper;
import com.wajeba.interfaces.AboutListener;
import com.wajeba.interfaces.LoginListener;
import com.wajeba.items.ItemUser;
import com.wajeba.utils.SharedPref;
import com.wajeba.utils.Constant;
import com.wajeba.utils.DBHelper;
import com.wajeba.utils.Methods;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    SharedPref sharedPref;
    Methods methods;
    DBHelper dbHelper;
    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            // TODO This is a new install (or the user cleared the shared preferences)
            Intent i = new Intent(getBaseContext(), ChangeLanguage.class);
            startActivity(i);

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkFirstRun();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Paper.init(this);
        String language = Paper.book().read("language");
        if(language == null)
            Paper.book().write("language","en");
        updateView((String)Paper.book().read("language"));
        hideStatusBar();

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        dbHelper = new DBHelper(this);

        if (sharedPref.getIsFirst()) {
            loadAboutData();
        } else {
            if (sharedPref.getIsFirst()) {
                openLoginActivity();
            } else {
                if (!sharedPref.getIsAutoLogin()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openMainActivity();
                        }
                    }, 2000);
                } else {
                    if (methods.isNetworkAvailable()) {
                        loadLogin();
                    } else {
                        openMainActivity();
                    }
                }
            }
        }
    }
    private void updateView(String lang) {
        Context context = LocalHelper.setLocale(this,lang);
        Resources resources = context.getResources();
    }
    private void loadLogin() {
        if (methods.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name) {

                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Constant.itemUser = new ItemUser(user_id, user_name, sharedPref.getEmail(), sharedPref.getMobile(), sharedPref.getCity(), sharedPref.getAddress());

                            Constant.isLogged = true;
                            openMainActivity();
                        } else {
                            openMainActivity();
                        }
                    } else {
                        openMainActivity();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_LOGIN, 0, "", "", "", "", "", "","", "","", sharedPref.getEmail(), sharedPref.getPassword(), "", "", "", "", "", "", null));
            loadLogin.execute();
        } else {
            methods.showToast(getString(R.string.error_net_not_conn));
        }
    }

    public void loadAboutData() {
        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            dbHelper.addtoAbout();
                            if (sharedPref.getIsFirst()) {
                                sharedPref.setIsFirst(false);
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                intent.putExtra("from","");
                                startActivity(intent);
                                finish();
                            } else {
                                openMainActivity();
                            }
                        } else {
                            errorDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.error_server), getString(R.string.error_server_conneting));
                    }
                }
            });
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.error_net_not_conn), getString(R.string.error_connect_net_tryagain));
        }
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.error_net_not_conn)) || title.equals(getString(R.string.error_server))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        intent.putExtra("from","");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}